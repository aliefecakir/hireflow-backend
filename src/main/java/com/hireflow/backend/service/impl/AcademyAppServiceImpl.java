package com.hireflow.backend.service.impl;

import com.hireflow.backend.dto.AcademyAppStatusResponse;
import com.hireflow.backend.dto.AcademyApplyRequest;
import com.hireflow.backend.dto.AcademyApplyResponse;
import com.hireflow.backend.dto.FormApplicationResponse;
import com.hireflow.backend.dto.UpdateAcademyAppStatusRequest;
import com.hireflow.backend.entity.AcademyApp;
import com.hireflow.backend.entity.Department;
import com.hireflow.backend.entity.Form;
import com.hireflow.backend.entity.GnlSt;
import com.hireflow.backend.entity.Question;
import com.hireflow.backend.entity.QuestionAnswer;
import com.hireflow.backend.entity.QuestionChoice;
import com.hireflow.backend.entity.University;
import com.hireflow.backend.exception.BadRequestException;
import com.hireflow.backend.repository.AcademyAppRepository;
import com.hireflow.backend.repository.DepartmentRepository;
import com.hireflow.backend.repository.FormQuestionRelRepository;
import com.hireflow.backend.repository.FormRepository;
import com.hireflow.backend.repository.GnlStRepository;
import com.hireflow.backend.repository.QuestionAnswerRepository;
import com.hireflow.backend.repository.QuestionChoiceRepository;
import com.hireflow.backend.repository.QuestionRepository;
import com.hireflow.backend.repository.UniversityRepository;
import com.hireflow.backend.service.AcademyAppService;
import com.hireflow.backend.service.FormService;
import com.hireflow.backend.util.FormWindow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Akademi başvurusu: form penceresi, uni/bölüm puanı, cevap kaydı, total score. */
@Service
@Transactional(readOnly = true)
public class AcademyAppServiceImpl implements AcademyAppService {

    private static final UUID SYSTEM_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111"); // anonim başvuru CUSER
    private static final String ACADEMY_APP_ENTITY = "ACADEMY_APP";
    private static final String PENDING_SCORE_STATUS_NAME = "Puanlanacak";
    private static final Short OTHER_CHOICE = 1;
    private static final Short CANDIDATE_QUESTION = 0;

    private final FormService formService;
    private final FormRepository formRepository;
    private final FormQuestionRelRepository formQuestionRelRepository;
    private final UniversityRepository universityRepository;
    private final DepartmentRepository departmentRepository;
    private final AcademyAppRepository academyAppRepository;
    private final GnlStRepository gnlStRepository;
    private final QuestionRepository questionRepository;
    private final QuestionChoiceRepository questionChoiceRepository;
    private final QuestionAnswerRepository questionAnswerRepository;

    public AcademyAppServiceImpl(
            FormService formService,
            FormRepository formRepository,
            FormQuestionRelRepository formQuestionRelRepository,
            UniversityRepository universityRepository,
            DepartmentRepository departmentRepository,
            AcademyAppRepository academyAppRepository,
            GnlStRepository gnlStRepository,
            QuestionRepository questionRepository,
            QuestionChoiceRepository questionChoiceRepository,
            QuestionAnswerRepository questionAnswerRepository
    ) {
        this.formService = formService;
        this.formRepository = formRepository;
        this.formQuestionRelRepository = formQuestionRelRepository;
        this.universityRepository = universityRepository;
        this.departmentRepository = departmentRepository;
        this.academyAppRepository = academyAppRepository;
        this.gnlStRepository = gnlStRepository;
        this.questionRepository = questionRepository;
        this.questionChoiceRepository = questionChoiceRepository;
        this.questionAnswerRepository = questionAnswerRepository;
    }

    @Override
    public List<FormApplicationResponse> getFormApplications(Long formId) {
        // Form yoksa 404; başvurular + GNL_ST isimleri tek seferde map'lenir
        if (!formRepository.existsById(formId)) {
            throw new NoSuchElementException("Form bulunamadı.");
        }

        List<AcademyApp> applications = academyAppRepository.findByForm_FormIdOrderByAcademyAppIdDesc(formId);
        Map<Long, GnlSt> statuses = loadStatuses(applications);

        return applications.stream()
                .map(app -> toFormApplicationResponse(app, statuses.get(app.getStId())))
                .toList();
    }

    @Override
    public List<AcademyAppStatusResponse> getApplicationStatuses() {
        // ACADEMY_APP entity'sine ait tüm durum kodları
        return gnlStRepository.findByEntCodeNameIgnoreCaseOrderByNameAsc(ACADEMY_APP_ENTITY).stream()
                .map(status -> new AcademyAppStatusResponse(
                        status.getGnlStId(),
                        status.getName(),
                        status.getDescr(),
                        status.getShrtCode()
                ))
                .toList();
    }

    @Override
    @Transactional
    public FormApplicationResponse updateApplicationStatus(
            Long appId,
            UpdateAcademyAppStatusRequest request,
            UUID evaluatorId
    ) {
        // ST_ID + STATUS_DESCR + evaluatedBy
        AcademyApp app = academyAppRepository.findDetailedById(appId)
                .orElseThrow(() -> new NoSuchElementException("Başvuru bulunamadı."));
        GnlSt status = resolveAcademyStatus(request.stId());

        app.setStId(status.getGnlStId());
        app.setStatusDescr(normalizeStatusDescr(request.statusDescr()));
        app.setEvaluatedBy(evaluatorId);
        app.setUuser(evaluatorId);
        app.setUdate(LocalDateTime.now());

        return toFormApplicationResponse(academyAppRepository.save(app), status);
    }

    @Override
    @Transactional
    public AcademyApplyResponse applyToForm(Long formId, AcademyApplyRequest request) {
        formService.deactivateExpiredForms(); // önce süresi bitenleri kapat
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new NoSuchElementException("Form bulunamadı."));

        LocalDateTime now = LocalDateTime.now();
        if (!FormWindow.isActive(form)) {
            throw new BadRequestException(
                    FormWindow.isExpired(form, now)
                            ? "Başvuru süresi sona erdi."
                            : "Yalnızca aktif formlara başvurulabilir."
            );
        }
        if (!FormWindow.hasStarted(form, now)) {
            throw new BadRequestException("Başvurular henüz başlamadı.");
        }

        // ÜNİVERSİTE VE BÖLÜM PUANLARINI VERİTABANINDAN ÇEK
        University university = universityRepository.findById(request.universityId())
                .orElseThrow(() -> new BadRequestException("Üniversite bulunamadı."));
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new BadRequestException("Bölüm bulunamadı."));

        int uniScore = university.getScore() != null ? university.getScore() : 0;
        int depScore = department.getScore() != null ? department.getScore() : 0;

        AcademyApp academyApp = new AcademyApp();
        academyApp.setForm(form);
        academyApp.setName(request.name());
        academyApp.setSurname(request.surname());
        academyApp.setEmail(request.email());
        academyApp.setPhone(request.phone());
        academyApp.setUniversity(university);
        academyApp.setDepartment(department);
        academyApp.setGradDate(request.gradDate());
        academyApp.setUniScore(uniScore);
        academyApp.setDepScore(depScore);
        // Frontend'den gelen totalScore'u YANITSIZ BIRAK - Puanı backend hesaplayacak
        academyApp.setTotalScore(0); // Geçici olarak 0, aşağıda hesaplanacak
        academyApp.setInterviewScore(0);
        GnlSt defaultStatus = resolveDefaultAcademyStatus();
        academyApp.setStId(defaultStatus.getGnlStId());
        academyApp.setStatusDescr(null);
        academyApp.setCuser(SYSTEM_USER_ID);

        AcademyApp savedApp = academyAppRepository.save(academyApp);

        Set<Long> allowedQuestionIds = formQuestionRelRepository
                .findByForm_FormIdAndQuestion_IsAssmtOrderByOrdNoAsc(formId, CANDIDATE_QUESTION)
                .stream()
                .map(rel -> rel.getQuestion().getQuestionId())
                .collect(Collectors.toSet());

        // CEVAPLARI KAYDET VE ŞIKLI SORULARIN PUANLARINI TOPLA
        int answersScore = 0;
        List<QuestionAnswer> answers = new ArrayList<>();
        if (request.answers() != null) {
            for (AcademyApplyRequest.AnswerRequest answerRequest : request.answers()) {
                if (answerRequest.questionId() == null || !allowedQuestionIds.contains(answerRequest.questionId())) {
                    throw new BadRequestException("Yalnızca bu formun aday soruları yanıtlanabilir.");
                }
                QuestionAnswer answer = toQuestionAnswer(savedApp, answerRequest);
                answers.add(answer);
                // Şıklı soruların puanlarını topla (açık uçlu sorular 0 puan)
                answersScore += (answer.getScore() != null ? answer.getScore() : 0);
            }
        }
        questionAnswerRepository.saveAll(answers);

        // İLK TOTAL SCORE HESAPLA: Üniversite + Bölüm + Şıklı Sorular
        int totalScore = uniScore + depScore + answersScore;
        savedApp.setTotalScore(totalScore);
        academyAppRepository.save(savedApp);

        return new AcademyApplyResponse(
                savedApp.getAcademyAppId(),
                form.getFormId(),
                savedApp.getStatusDescr()
        );
    }

    private QuestionAnswer toQuestionAnswer(AcademyApp academyApp, AcademyApplyRequest.AnswerRequest answerRequest) {
        // Şık skoru otomatik; açık uçlu / "diğer" 0 (yönetici sonra puanlar)
        Question question = questionRepository.findById(answerRequest.questionId())
                .orElseThrow(() -> new BadRequestException(
                        "Soru bulunamadı: " + answerRequest.questionId()));

        QuestionChoice choice = null;
        if (answerRequest.questionChoiceId() != null) {
            choice = questionChoiceRepository.findById(answerRequest.questionChoiceId())
                    .orElseThrow(() -> new BadRequestException(
                            "Soru şıkkı bulunamadı: " + answerRequest.questionChoiceId()));
            if (choice.getQuestion() == null
                    || !question.getQuestionId().equals(choice.getQuestion().getQuestionId())) {
                throw new BadRequestException("Seçilen şık bu soruya ait değil.");
            }
        }

        boolean openEnded = choice == null;
        boolean otherSelected = choice != null && OTHER_CHOICE.equals(choice.getIsOther());

        QuestionAnswer answer = new QuestionAnswer();
        answer.setAcademyApp(academyApp);
        answer.setQuestion(question);
        answer.setQuestionChoice(choice);
        if (openEnded || otherSelected) {
            answer.setScore(0);
        } else {
            answer.setScore(choice != null && choice.getScore() != null ? choice.getScore() : 0);
        }
        if (openEnded || otherSelected || answerRequest.answerText() != null) {
            answer.setAnswerText(answerRequest.answerText());
        }
        answer.setCuser(SYSTEM_USER_ID);
        return answer;
    }

    private Map<Long, GnlSt> loadStatuses(List<AcademyApp> applications) {
        Set<Long> statusIds = applications.stream()
                .map(AcademyApp::getStId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (statusIds.isEmpty()) {
            return Map.of();
        }
        return gnlStRepository.findAllById(statusIds).stream()
                .collect(Collectors.toMap(GnlSt::getGnlStId, Function.identity()));
    }

    private GnlSt resolveAcademyStatus(Long stId) {
        if (stId == null) {
            throw new BadRequestException("Durum bilgisi zorunludur.");
        }
        return gnlStRepository.findById(stId)
                .filter(status -> ACADEMY_APP_ENTITY.equalsIgnoreCase(status.getEntCodeName()))
                .orElseThrow(() -> new BadRequestException("Geçersiz akademi başvuru durumu."));
    }

    private GnlSt resolveDefaultAcademyStatus() {
        return gnlStRepository
                .findFirstByEntCodeNameIgnoreCaseAndNameIgnoreCase(ACADEMY_APP_ENTITY, PENDING_SCORE_STATUS_NAME)
                .orElseThrow(() -> new BadRequestException(
                        "ACADEMY_APP / Puanlanacak başvuru durumu bulunamadı."));
    }

    private FormApplicationResponse toFormApplicationResponse(AcademyApp app, GnlSt status) {
        University university = app.getUniversity();
        Department department = app.getDepartment();
        return new FormApplicationResponse(
                app.getAcademyAppId(),
                app.getName(),
                app.getSurname(),
                university == null ? null : university.getName(),
                department == null ? null : department.getName(),
                app.getGradDate(),
                app.getTotalScore(),
                app.getInterviewScore(),
                app.getStId(),
                status == null ? null : status.getName(),
                status == null ? null : status.getShrtCode(),
                app.getStatusDescr()
        );
    }

    private String normalizeStatusDescr(String statusDescr) {
        if (!StringUtils.hasText(statusDescr)) {
            return null;
        }
        return statusDescr.trim();
    }
}
