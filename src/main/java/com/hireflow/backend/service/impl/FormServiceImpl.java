package com.hireflow.backend.service.impl;

import com.hireflow.backend.dto.CreateFormRequest;
import com.hireflow.backend.dto.FormDetailResponse;
import com.hireflow.backend.dto.FormQuestionResponse;
import com.hireflow.backend.dto.FormResponse;
import com.hireflow.backend.entity.Form;
import com.hireflow.backend.entity.FormQuestionRel;
import com.hireflow.backend.entity.GeneralType;
import com.hireflow.backend.entity.Organization;
import com.hireflow.backend.entity.Question;
import com.hireflow.backend.entity.QuestionChoice;
import com.hireflow.backend.exception.BadRequestException;
import com.hireflow.backend.repository.AcademyAppRepository;
import com.hireflow.backend.repository.FormQuestionRelRepository;
import com.hireflow.backend.repository.FormRepository;
import com.hireflow.backend.repository.GeneralTypeRepository;
import com.hireflow.backend.repository.OrganizationRepository;
import com.hireflow.backend.repository.QuestionRepository;
import com.hireflow.backend.service.FormService;
import com.hireflow.backend.util.FormWindow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

/** Form CRUD, soru bağlama, süresi dolanları pasife çekme. */
@Service
@Transactional(readOnly = true)
public class FormServiceImpl implements FormService {

    private static final UUID SYSTEM_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final Short ACTIVE = FormWindow.ACTIVE;
    private static final Short CANDIDATE_QUESTION = 0; // isAssmt=0 aday sorusu
    private static final Short DEFAULT_REQUIRED = 1;

    private final FormRepository formRepository;
    private final FormQuestionRelRepository formQuestionRelRepository;
    private final OrganizationRepository organizationRepository;
    private final QuestionRepository questionRepository;
    private final GeneralTypeRepository generalTypeRepository;
    private final AcademyAppRepository academyAppRepository;

    public FormServiceImpl(
            FormRepository formRepository,
            FormQuestionRelRepository formQuestionRelRepository,
            OrganizationRepository organizationRepository,
            QuestionRepository questionRepository,
            GeneralTypeRepository generalTypeRepository,
            AcademyAppRepository academyAppRepository
    ) {
        this.formRepository = formRepository;
        this.formQuestionRelRepository = formQuestionRelRepository;
        this.organizationRepository = organizationRepository;
        this.questionRepository = questionRepository;
        this.generalTypeRepository = generalTypeRepository;
        this.academyAppRepository = academyAppRepository;
    }

    @Override
    @Transactional
    public List<FormResponse> getForms(boolean includeInactive) {
        deactivateExpiredForms();
        List<Form> forms = includeInactive
                ? formRepository.findAllByOrderBySdateDesc()
                : formRepository.findByIsActvOrderBySdateDesc(ACTIVE);
        Map<Long, Long> applicationCounts = applicationCountByFormId();
        return forms.stream()
                .map((form) -> toFormResponse(form, applicationCounts.getOrDefault(form.getFormId(), 0L)))
                .toList();
    }

    @Override
    @Transactional
    public FormDetailResponse getFormDetail(Long formId) {
        deactivateExpiredForms();
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
    @Transactional
    public List<FormQuestionResponse> getCandidateQuestions(Long formId) {
        deactivateExpiredForms();
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new NoSuchElementException("Form bulunamadı."));
        LocalDateTime now = LocalDateTime.now();
        if (!FormWindow.isVisibleToCandidates(form, now)) {
            throw new NoSuchElementException("Form bulunamadı.");
        }
        if (!FormWindow.hasStarted(form, now)) {
            throw new BadRequestException("Başvurular henüz başlamadı.");
        }

        return formQuestionRelRepository
                .findByForm_FormIdAndQuestion_IsAssmtOrderByOrdNoAsc(formId, CANDIDATE_QUESTION)
                .stream()
                .map(this::toFormQuestionResponse)
                .toList();
    }

    @Override
    @Transactional
    public FormResponse createForm(CreateFormRequest request, UUID currentUserId) {
        Form form = new Form();
        form.setCuser(currentUserId);
        applyFormFields(form, request);
        Form savedForm = formRepository.save(form);
        replaceQuestions(savedForm, request.questions(), currentUserId);
        return toFormResponse(savedForm);
    }

    @Override
    @Transactional
    public FormResponse updateForm(Long formId, CreateFormRequest request, UUID currentUserId) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new NoSuchElementException("Form bulunamadı."));
        applyFormFields(form, request);
        form.setUuser(currentUserId);
        Form savedForm = formRepository.save(form);
        replaceQuestions(savedForm, request.questions(), currentUserId);
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
        form.setIsActv(FormWindow.resolveActiveFlag(request.isActv(), request.edate(), LocalDateTime.now()));
    }

    private void replaceQuestions(
            Form form,
            List<CreateFormRequest.FormQuestionRequest> items,
            UUID currentUserId
    ) {
        formQuestionRelRepository.deleteByForm_FormId(form.getFormId()); // eski bağları sil
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
            relation.setCuser(currentUserId);
            relations.add(relation);
        }
        formQuestionRelRepository.saveAll(relations);
    }

    @Override
    @Transactional
    public void deactivateExpiredForms() {
        LocalDateTime now = LocalDateTime.now();
        List<Form> expired = formRepository.findByIsActvAndEdateBefore(ACTIVE, now); // aktif + EDATE geçmiş
        if (expired.isEmpty()) {
            return;
        }
        for (Form form : expired) {
            form.setIsActv(FormWindow.PASSIVE);
            form.setUuser(SYSTEM_USER_ID);
            form.setUdate(now);
        }
        formRepository.saveAll(expired);
    }

    private Map<Long, Long> applicationCountByFormId() {
        Map<Long, Long> counts = new HashMap<>();
        for (Object[] row : academyAppRepository.countGroupedByFormId()) {
            if (row == null || row.length < 2 || row[0] == null || row[1] == null) continue;
            counts.put(((Number) row[0]).longValue(), ((Number) row[1]).longValue());
        }
        return counts;
    }

    private FormResponse toFormResponse(Form form) {
        long applicationCount = form.getFormId() == null
                ? 0L
                : academyAppRepository.countByForm_FormId(form.getFormId());
        return toFormResponse(form, applicationCount);
    }

    private FormResponse toFormResponse(Form form, long applicationCount) {
        Organization organization = form.getOrganization();
        return new FormResponse(
                form.getFormId(),
                form.getTitle(),
                form.getDescr(),
                organization == null ? null : organization.getOrganizationId(),
                organization == null ? null : organization.getName(),
                form.getIsActv(),
                form.getSdate(),
                form.getEdate(),
                applicationCount
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

        GeneralType questionType = question.getTpId() == null
                ? null
                : generalTypeRepository.findById(question.getTpId()).orElse(null);

        return new FormQuestionResponse(
                question.getQuestionId(),
                question.getQuestionText(),
                question.getTpId(),
                questionType == null ? null : questionType.getShrtCode(),
                questionType == null ? null : questionType.getName(),
                question.getMinScore(),
                question.getMaxScore(),
                relation.getOrdNo(),
                relation.getIsReq(),
                question.getIsAssmt(),
                choices
        );
    }
}
