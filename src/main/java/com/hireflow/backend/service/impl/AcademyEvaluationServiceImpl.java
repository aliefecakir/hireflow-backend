package com.hireflow.backend.service.impl;

import com.hireflow.backend.dto.AcademyAppDetailsResponse;
import com.hireflow.backend.dto.AcademyEvaluateResponse;
import com.hireflow.backend.dto.EvaluateAcademyAppRequest;
import com.hireflow.backend.entity.AcademyApp;
import com.hireflow.backend.entity.FormQuestionRel;
import com.hireflow.backend.entity.GeneralType;
import com.hireflow.backend.entity.GnlSt;
import com.hireflow.backend.entity.Question;
import com.hireflow.backend.entity.QuestionAnswer;
import com.hireflow.backend.entity.QuestionChoice;
import com.hireflow.backend.exception.BadRequestException;
import com.hireflow.backend.repository.AcademyAppRepository;
import com.hireflow.backend.repository.FormQuestionRelRepository;
import com.hireflow.backend.repository.GeneralTypeRepository;
import com.hireflow.backend.repository.GnlStRepository;
import com.hireflow.backend.repository.QuestionAnswerRepository;
import com.hireflow.backend.repository.QuestionChoiceRepository;
import com.hireflow.backend.repository.QuestionRepository;
import com.hireflow.backend.service.AcademyEvaluationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AcademyEvaluationServiceImpl implements AcademyEvaluationService {

    private static final Short CANDIDATE_QUESTION = 0;
    private static final Short ASSESSMENT_QUESTION = 1;

    private final AcademyAppRepository academyAppRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final FormQuestionRelRepository formQuestionRelRepository;
    private final QuestionRepository questionRepository;
    private final QuestionChoiceRepository questionChoiceRepository;
    private final GeneralTypeRepository generalTypeRepository;
    private final GnlStRepository gnlStRepository;

    public AcademyEvaluationServiceImpl(
            AcademyAppRepository academyAppRepository,
            QuestionAnswerRepository questionAnswerRepository,
            FormQuestionRelRepository formQuestionRelRepository,
            QuestionRepository questionRepository,
            QuestionChoiceRepository questionChoiceRepository,
            GeneralTypeRepository generalTypeRepository,
            GnlStRepository gnlStRepository
    ) {
        this.academyAppRepository = academyAppRepository;
        this.questionAnswerRepository = questionAnswerRepository;
        this.formQuestionRelRepository = formQuestionRelRepository;
        this.questionRepository = questionRepository;
        this.questionChoiceRepository = questionChoiceRepository;
        this.generalTypeRepository = generalTypeRepository;
        this.gnlStRepository = gnlStRepository;
    }

    @Override
    public AcademyAppDetailsResponse getApplicationDetails(Long appId) {
        AcademyApp app = academyAppRepository.findDetailedById(appId)
                .orElseThrow(() -> new NoSuchElementException("Başvuru bulunamadı."));

        // TÜM CEVAPLARI TEK SORGUDA ÇEK (Candidate + Assessment)
        List<QuestionAnswer> allAnswers = questionAnswerRepository.findByAcademyApp_AcademyAppId(appId);
        
        // Candidate ve Assessment cevaplarını ayır
        List<QuestionAnswer> candidateAnswers = allAnswers.stream()
                .filter(answer -> answer.getQuestion() != null && 
                        CANDIDATE_QUESTION.equals(answer.getQuestion().getIsAssmt()))
                .toList();
        
        Map<Long, QuestionAnswer> assessmentAnswers = allAnswers.stream()
                .filter(answer -> answer.getQuestion() != null && 
                        ASSESSMENT_QUESTION.equals(answer.getQuestion().getIsAssmt()) &&
                        answer.getQuestion().getQuestionId() != null)
                .collect(Collectors.toMap(
                        answer -> answer.getQuestion().getQuestionId(),
                        answer -> answer,
                        (first, ignored) -> first,
                        LinkedHashMap::new
                ));

        // TÜM QUESTION ID'LERİNİ TOPLA
        List<Long> allQuestionIds = allAnswers.stream()
                .map(QuestionAnswer::getQuestion)
                .filter(Objects::nonNull)
                .map(Question::getQuestionId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Long formId = app.getForm() == null ? null : app.getForm().getFormId();
        
        // Form sorularını da ekle
        List<Long> formQuestionIds = formId == null ? List.of() :
                formQuestionRelRepository
                        .findByForm_FormIdAndQuestion_IsAssmtOrderByOrdNoAsc(formId, ASSESSMENT_QUESTION)
                        .stream()
                        .map(rel -> rel.getQuestion() == null ? null : rel.getQuestion().getQuestionId())
                        .filter(Objects::nonNull)
                        .toList();

        // Tüm question ID'leri birleştir
        List<Long> combinedQuestionIds = new java.util.ArrayList<>(allQuestionIds);
        combinedQuestionIds.addAll(formQuestionIds);
        combinedQuestionIds = combinedQuestionIds.stream().distinct().toList();

        // TÜM CHOICE'LARI TEK SORGUDA ÇEK (N+1 Problemi Çözüldü!)
        Map<Long, List<QuestionChoice>> choicesByQuestionId = combinedQuestionIds.isEmpty() 
                ? Map.of()
                : questionChoiceRepository.findByQuestion_QuestionIdIn(combinedQuestionIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                choice -> choice.getQuestion().getQuestionId(),
                                LinkedHashMap::new,
                                Collectors.toList()
                        ));

        List<AcademyAppDetailsResponse.CandidateAnswer> answers = toCandidateAnswers(
                candidateAnswers, 
                choicesByQuestionId
        );

        List<AcademyAppDetailsResponse.InterviewCriterion> criteria = formId == null
                ? List.of()
                : formQuestionRelRepository
                .findByForm_FormIdAndQuestion_IsAssmtOrderByOrdNoAsc(formId, ASSESSMENT_QUESTION)
                .stream()
                .map(relation -> toInterviewCriterion(
                        relation,
                        assessmentAnswers.get(relation.getQuestion() == null ? null : relation.getQuestion().getQuestionId()),
                        choicesByQuestionId
                ))
                .toList();

        GnlSt status = app.getStId() == null
                ? null
                : gnlStRepository.findById(app.getStId()).orElse(null);

        return new AcademyAppDetailsResponse(
                app.getAcademyAppId(),
                app.getName(),
                app.getSurname(),
                app.getEmail(),
                app.getPhone(),
                app.getUniversity() == null ? null : app.getUniversity().getName(),
                app.getDepartment() == null ? null : app.getDepartment().getName(),
                app.getUniScore(),
                app.getDepScore(),
                app.getTotalScore(),
                app.getInterviewScore(),
                app.getStId(),
                status == null ? null : status.getName(),
                app.getStatusDescr(),
                answers,
                criteria
        );
    }

    @Override
    @Transactional
    public AcademyEvaluateResponse evaluateApplication(Long appId, EvaluateAcademyAppRequest request, UUID evaluatorId) {
        if (evaluatorId == null) {
            throw new BadRequestException("Değerlendiren kullanıcı bulunamadı.");
        }

        AcademyApp app = academyAppRepository.findById(appId)
                .orElseThrow(() -> new NoSuchElementException("Başvuru bulunamadı."));

        // BÖLÜM 1: Açık uçlu sorulara yöneticinin verdiği manuel puanları güncelle
        if (request.manualScores() != null && !request.manualScores().isEmpty()) {
            for (EvaluateAcademyAppRequest.ManualScoreRequest manualScore : request.manualScores()) {
                Question question = questionRepository.findById(manualScore.questionId())
                        .orElseThrow(() -> new BadRequestException("Soru bulunamadı: " + manualScore.questionId()));
                
                // Sadece aday sorularına (isAssmt = 0) manuel puan verilebilir
                if (!CANDIDATE_QUESTION.equals(question.getIsAssmt())) {
                    throw new BadRequestException("Yalnızca aday sorularına manuel puan verilebilir: " + manualScore.questionId());
                }

                // Max score kontrolü
                Integer maxScore = question.getMaxScore() != null ? question.getMaxScore() : 10;
                if (manualScore.score() < 0 || manualScore.score() > maxScore) {
                    throw new BadRequestException(
                        String.format("Puan 0 ile %d arasında olmalıdır: %d", maxScore, manualScore.questionId())
                    );
                }

                // İlgili QUESTION_ANSWER kaydını bul ve puanı güncelle
                List<QuestionAnswer> existingAnswers = questionAnswerRepository
                        .findByAcademyApp_AcademyAppIdAndQuestion_QuestionId(appId, manualScore.questionId());
                
                if (!existingAnswers.isEmpty()) {
                    for (QuestionAnswer answer : existingAnswers) {
                        answer.setScore(manualScore.score());
                        answer.setUuser(evaluatorId);
                        answer.setUdate(java.time.LocalDateTime.now());
                    }
                    questionAnswerRepository.saveAll(existingAnswers);
                }
            }
        }

        // BÖLÜM 2: Mülakat sorularını kaydet ve puanla
        questionAnswerRepository.deleteByAcademyApp_AcademyAppIdAndQuestion_IsAssmt(appId, ASSESSMENT_QUESTION);

        int interviewScore = 0;
        List<QuestionAnswer> evaluationAnswers = new ArrayList<>();
        if (request.answers() != null) {
            for (EvaluateAcademyAppRequest.EvaluationAnswerRequest item : request.answers()) {
                if (item.questionChoiceId() == null && !StringUtils.hasText(item.answerText())) {
                    continue;
                }

                Question question = questionRepository.findById(item.questionId())
                        .orElseThrow(() -> new BadRequestException("Soru bulunamadı: " + item.questionId()));
                if (!ASSESSMENT_QUESTION.equals(question.getIsAssmt())) {
                    throw new BadRequestException("Yalnızca mülakat soruları puanlanabilir: " + item.questionId());
                }

                QuestionAnswer answer = new QuestionAnswer();
                answer.setAcademyApp(app);
                answer.setQuestion(question);
                answer.setCuser(evaluatorId);

                if (item.questionChoiceId() != null) {
                    QuestionChoice choice = questionChoiceRepository.findById(item.questionChoiceId())
                            .orElseThrow(() -> new BadRequestException(
                                    "Soru şıkkı bulunamadı: " + item.questionChoiceId()));
                    if (choice.getQuestion() == null
                            || !question.getQuestionId().equals(choice.getQuestion().getQuestionId())) {
                        throw new BadRequestException("Seçilen şık bu soruya ait değil.");
                    }

                    int choiceScore = choice.getScore() != null ? choice.getScore() : 0;
                    interviewScore += choiceScore;
                    answer.setQuestionChoice(choice);
                    answer.setScore(choiceScore);
                    if (StringUtils.hasText(item.answerText())) {
                        answer.setAnswerText(item.answerText().trim());
                    }
                } else if (StringUtils.hasText(item.answerText())) {
                    answer.setQuestionChoice(null);
                    answer.setAnswerText(item.answerText().trim());
                    answer.setScore(0);
                } else {
                    throw new BadRequestException("Mülakat cevabı eksik: " + item.questionId());
                }

                evaluationAnswers.add(answer);
            }
        }

        questionAnswerRepository.saveAll(evaluationAnswers);

        // BÖLÜM 3: Final Total Score Hesapla
        // Üniversite + Bölüm + (Tüm QUESTION_ANSWER kayıtlarının güncel toplam puanı)
        int uniScore = app.getUniScore() != null ? app.getUniScore() : 0;
        int depScore = app.getDepScore() != null ? app.getDepScore() : 0;
        
        // Tüm cevapların (aday + mülakat) puanlarını topla
        int totalAnswerScore = questionAnswerRepository
                .findByAcademyApp_AcademyAppId(appId)
                .stream()
                .mapToInt(answer -> answer.getScore() != null ? answer.getScore() : 0)
                .sum();

        int finalTotalScore = uniScore + depScore + totalAnswerScore;

        app.setTotalScore(finalTotalScore);
        app.setInterviewScore(interviewScore);
        app.setEvaluatedBy(evaluatorId);
        app.setUuser(evaluatorId);
        app.setUdate(LocalDateTime.now());
        AcademyApp saved = academyAppRepository.save(app);

        return new AcademyEvaluateResponse(
                saved.getAcademyAppId(),
                saved.getInterviewScore(),
                saved.getStatusDescr()
        );
    }

    private List<AcademyAppDetailsResponse.CandidateAnswer> toCandidateAnswers(
            List<QuestionAnswer> answers,
            Map<Long, List<QuestionChoice>> choicesByQuestionId) {
        Map<Long, List<QuestionAnswer>> grouped = new LinkedHashMap<>();
        for (QuestionAnswer answer : answers) {
            Question question = answer.getQuestion();
            if (question == null || question.getQuestionId() == null) {
                continue;
            }
            grouped.computeIfAbsent(question.getQuestionId(), key -> new ArrayList<>()).add(answer);
        }

        return grouped.values().stream()
                .map(group -> toCandidateAnswer(group, choicesByQuestionId))
                .toList();
    }

    private AcademyAppDetailsResponse.CandidateAnswer toCandidateAnswer(
            List<QuestionAnswer> answers,
            Map<Long, List<QuestionChoice>> choicesByQuestionId) {
        QuestionAnswer first = answers.get(0);
        Question question = first.getQuestion();

        List<Long> selectedChoiceIds = answers.stream()
                .map(QuestionAnswer::getQuestionChoice)
                .filter(choice -> choice != null && choice.getQuestionChoiceId() != null)
                .map(QuestionChoice::getQuestionChoiceId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Long selectedChoiceId = selectedChoiceIds.isEmpty() ? null : selectedChoiceIds.get(0);

        String answerText = answers.stream()
                .map(QuestionAnswer::getAnswerText)
                .filter(text -> text != null && !text.isBlank())
                .findFirst()
                .orElse(null);

        Integer score = answers.stream()
                .map(QuestionAnswer::getScore)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(0);

        // CACHE'TEN CHOICE'LARI AL (DB'ye gitme!)
        List<QuestionChoice> sourceChoices = choicesByQuestionId.getOrDefault(
                question.getQuestionId(), 
                List.of()
        );

        List<AcademyAppDetailsResponse.CandidateAnswer.Choice> choices = uniqueChoices(sourceChoices).stream()
                .sorted(Comparator.comparing(QuestionChoice::getOrdNo, Comparator.nullsLast(Integer::compareTo)))
                .map(choice -> new AcademyAppDetailsResponse.CandidateAnswer.Choice(
                        choice.getQuestionChoiceId(),
                        choice.getChoiceText(),
                        choice.getScore(),
                        choice.getOrdNo(),
                        choice.getIsOther()
                ))
                .toList();

        return new AcademyAppDetailsResponse.CandidateAnswer(
                question.getQuestionId(),
                question.getQuestionText(),
                selectedChoiceId,
                selectedChoiceIds,
                answerText,
                question.getTpId(),
                question.getMinScore(),
                question.getMaxScore(),
                score,
                choices
        );
    }

    private List<QuestionChoice> uniqueChoices(List<QuestionChoice> source) {
        if (source == null || source.isEmpty()) {
            return List.of();
        }
        Map<Long, QuestionChoice> unique = new LinkedHashMap<>();
        for (QuestionChoice choice : source) {
            if (choice == null || choice.getQuestionChoiceId() == null) {
                continue;
            }
            unique.putIfAbsent(choice.getQuestionChoiceId(), choice);
        }
        return new ArrayList<>(unique.values());
    }

    private AcademyAppDetailsResponse.InterviewCriterion toInterviewCriterion(
            FormQuestionRel relation,
            QuestionAnswer existingAnswer,
            Map<Long, List<QuestionChoice>> choicesByQuestionId
    ) {
        Question question = relation.getQuestion();
        
        // CACHE'TEN CHOICE'LARI AL (DB'ye gitme!)
        List<QuestionChoice> sourceChoices = choicesByQuestionId.getOrDefault(
                question.getQuestionId(),
                List.of()
        );

        List<AcademyAppDetailsResponse.InterviewCriterion.Choice> choices = uniqueChoices(sourceChoices).stream()
                .sorted(Comparator.comparing(QuestionChoice::getOrdNo, Comparator.nullsLast(Integer::compareTo)))
                .map(choice -> new AcademyAppDetailsResponse.InterviewCriterion.Choice(
                        choice.getQuestionChoiceId(),
                        choice.getChoiceText(),
                        choice.getScore(),
                        choice.getOrdNo()
                ))
                .toList();

        Long selectedChoiceId = existingAnswer == null || existingAnswer.getQuestionChoice() == null
                ? null
                : existingAnswer.getQuestionChoice().getQuestionChoiceId();

        String tpShrtCode = null;
        if (question.getTpId() != null) {
            tpShrtCode = generalTypeRepository.findById(question.getTpId())
                    .map(GeneralType::getShrtCode)
                    .orElse(null);
        }

        return new AcademyAppDetailsResponse.InterviewCriterion(
                question.getQuestionId(),
                question.getQuestionText(),
                question.getMinScore(),
                question.getMaxScore(),
                relation.getOrdNo(),
                question.getTpId(),
                tpShrtCode,
                existingAnswer == null ? null : existingAnswer.getAnswerText(),
                selectedChoiceId,
                choices
        );
    }
}
