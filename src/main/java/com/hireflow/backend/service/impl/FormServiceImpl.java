package com.hireflow.backend.service.impl;

import com.hireflow.backend.dto.CreateFormRequest;
import com.hireflow.backend.dto.FormDetailResponse;
import com.hireflow.backend.dto.FormQuestionResponse;
import com.hireflow.backend.dto.FormResponse;
import com.hireflow.backend.entity.Form;
import com.hireflow.backend.entity.FormQuestionRel;
import com.hireflow.backend.entity.Organization;
import com.hireflow.backend.entity.Question;
import com.hireflow.backend.entity.QuestionChoice;
import com.hireflow.backend.exception.BadRequestException;
import com.hireflow.backend.repository.FormQuestionRelRepository;
import com.hireflow.backend.repository.FormRepository;
import com.hireflow.backend.repository.OrganizationRepository;
import com.hireflow.backend.repository.QuestionRepository;
import com.hireflow.backend.service.FormService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class FormServiceImpl implements FormService {

    private static final UUID SYSTEM_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final Short ACTIVE = 1;
    private static final Short CANDIDATE_QUESTION = 0;
    private static final Short DEFAULT_REQUIRED = 1;

    private final FormRepository formRepository;
    private final FormQuestionRelRepository formQuestionRelRepository;
    private final OrganizationRepository organizationRepository;
    private final QuestionRepository questionRepository;

    public FormServiceImpl(
            FormRepository formRepository,
            FormQuestionRelRepository formQuestionRelRepository,
            OrganizationRepository organizationRepository,
            QuestionRepository questionRepository
    ) {
        this.formRepository = formRepository;
        this.formQuestionRelRepository = formQuestionRelRepository;
        this.organizationRepository = organizationRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public List<FormResponse> getForms(boolean includeInactive) {
        List<Form> forms = includeInactive
                ? formRepository.findAllByOrderBySdateDesc()
                : formRepository.findByIsActvOrderBySdateDesc(ACTIVE);
        return forms.stream().map(this::toFormResponse).toList();
    }

    @Override
    public FormDetailResponse getFormDetail(Long formId) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new NoSuchElementException("Form bulunamadı."));

        List<FormQuestionResponse> questions = formQuestionRelRepository
                .findByForm_FormIdOrderByOrdNoAsc(formId)
                .stream()
                .map(this::toFormQuestionResponse)
                .toList();

        FormResponse summary = toFormResponse(form);
        return new FormDetailResponse(
                summary.formId(),
                summary.title(),
                summary.descr(),
                summary.organizationId(),
                summary.organizationName(),
                summary.isActv(),
                summary.sdate(),
                summary.edate(),
                questions
        );
    }

    @Override
    public List<FormQuestionResponse> getCandidateQuestions(Long formId) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new NoSuchElementException("Form bulunamadı."));
        if (form.getIsActv() == null || !ACTIVE.equals(form.getIsActv())) {
            throw new NoSuchElementException("Form bulunamadı.");
        }

        return formQuestionRelRepository
                .findByForm_FormIdAndQuestion_IsAssmtOrderByOrdNoAsc(formId, CANDIDATE_QUESTION)
                .stream()
                .map(this::toFormQuestionResponse)
                .toList();
    }

    @Override
    @Transactional
    public FormResponse createForm(CreateFormRequest request) {
        Form form = new Form();
        form.setCuser(SYSTEM_USER_ID);
        applyFormFields(form, request);
        Form savedForm = formRepository.save(form);
        replaceQuestions(savedForm, request.questions());
        return toFormResponse(savedForm);
    }

    @Override
    @Transactional
    public FormResponse updateForm(Long formId, CreateFormRequest request) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new NoSuchElementException("Form bulunamadı."));
        applyFormFields(form, request);
        form.setUuser(SYSTEM_USER_ID);
        Form savedForm = formRepository.save(form);
        replaceQuestions(savedForm, request.questions());
        return toFormResponse(savedForm);
    }

    private void applyFormFields(Form form, CreateFormRequest request) {
        Organization organization = organizationRepository.findById(request.organizationId())
                .orElseThrow(() -> new BadRequestException("Organizasyon bulunamadı."));

        form.setOrganization(organization);
        form.setTitle(request.title());
        form.setDescr(request.descr());
        form.setSdate(request.sdate());
        form.setEdate(request.edate());
        form.setIsActv(request.isActv() != null ? request.isActv() : ACTIVE);
    }

    private void replaceQuestions(Form form, List<CreateFormRequest.FormQuestionRequest> items) {
        formQuestionRelRepository.deleteByForm_FormId(form.getFormId());
        formQuestionRelRepository.flush();
        if (items == null || items.isEmpty()) {
            return;
        }

        List<FormQuestionRel> relations = new ArrayList<>();
        int ordNo = 1;
        for (CreateFormRequest.FormQuestionRequest item : items) {
            Question question = questionRepository.findById(item.questionId())
                    .orElseThrow(() -> new BadRequestException("Soru bulunamadı: " + item.questionId()));

            FormQuestionRel relation = new FormQuestionRel();
            relation.setForm(form);
            relation.setQuestion(question);
            relation.setOrdNo(ordNo++);
            relation.setIsReq(item.isReq() != null ? item.isReq() : DEFAULT_REQUIRED);
            relation.setCuser(SYSTEM_USER_ID);
            relations.add(relation);
        }
        formQuestionRelRepository.saveAll(relations);
    }

    private FormResponse toFormResponse(Form form) {
        Organization organization = form.getOrganization();
        return new FormResponse(
                form.getFormId(),
                form.getTitle(),
                form.getDescr(),
                organization == null ? null : organization.getOrganizationId(),
                organization == null ? null : organization.getName(),
                form.getIsActv(),
                form.getSdate(),
                form.getEdate()
        );
    }

    private FormQuestionResponse toFormQuestionResponse(FormQuestionRel relation) {
        Question question = relation.getQuestion();
        List<QuestionChoice> sourceChoices = question.getQuestionChoices() == null
                ? List.of()
                : question.getQuestionChoices();

        List<FormQuestionResponse.Choice> choices = sourceChoices.stream()
                .sorted(Comparator.comparing(QuestionChoice::getOrdNo, Comparator.nullsLast(Integer::compareTo)))
                .map(choice -> new FormQuestionResponse.Choice(
                        choice.getQuestionChoiceId(),
                        choice.getChoiceText(),
                        choice.getScore(),
                        choice.getOrdNo(),
                        choice.getIsOther()
                ))
                .toList();

        return new FormQuestionResponse(
                question.getQuestionId(),
                question.getQuestionText(),
                question.getTpId(),
                question.getMinScore(),
                question.getMaxScore(),
                relation.getOrdNo(),
                relation.getIsReq(),
                question.getIsAssmt(),
                choices
        );
    }
}
