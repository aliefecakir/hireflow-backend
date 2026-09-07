package com.hireflow.backend.service.impl;

import com.hireflow.backend.dto.CreateQuestionRequest;
import com.hireflow.backend.dto.QuestionResponse;
import com.hireflow.backend.dto.QuestionTypeResponse;
import com.hireflow.backend.dto.QuestionUsageResponse;
import com.hireflow.backend.dto.UpdateQuestionRequest;
import com.hireflow.backend.entity.Form;
import com.hireflow.backend.entity.FormQuestionRel;
import com.hireflow.backend.entity.GeneralType;
import com.hireflow.backend.entity.Question;
import com.hireflow.backend.entity.QuestionChoice;
import com.hireflow.backend.exception.BadRequestException;
import com.hireflow.backend.repository.FormQuestionRelRepository;
import com.hireflow.backend.repository.GeneralTypeRepository;
import com.hireflow.backend.repository.QuestionAnswerRepository;
import com.hireflow.backend.repository.QuestionChoiceRepository;
import com.hireflow.backend.repository.QuestionRepository;
import com.hireflow.backend.service.QuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class QuestionServiceImpl implements QuestionService {

    private static final UUID SYSTEM_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final Short DEFAULT_FLAG_OFF = 0;
    private static final Short ACTIVE = 1;
    private static final Set<String> QUESTION_TYPE_CODES = Set.of(
            "SNGL", "SINGLE", "MULT", "MULTI", "OPEN", "TEXT", "TSS", "CSS", "AU", "FILE", "CV", "DATE", "DT"
    );

    private final QuestionRepository questionRepository;
    private final QuestionChoiceRepository questionChoiceRepository;
    private final GeneralTypeRepository generalTypeRepository;
    private final FormQuestionRelRepository formQuestionRelRepository;
    private final QuestionAnswerRepository questionAnswerRepository;

    public QuestionServiceImpl(
            QuestionRepository questionRepository,
            QuestionChoiceRepository questionChoiceRepository,
            GeneralTypeRepository generalTypeRepository,
            FormQuestionRelRepository formQuestionRelRepository,
            QuestionAnswerRepository questionAnswerRepository
    ) {
        this.questionRepository = questionRepository;
        this.questionChoiceRepository = questionChoiceRepository;
        this.generalTypeRepository = generalTypeRepository;
        this.formQuestionRelRepository = formQuestionRelRepository;
        this.questionAnswerRepository = questionAnswerRepository;
    }

    @Override
    public List<QuestionResponse> getActiveQuestions() {
        Map<Long, Long> formCounts = formQuestionRelRepository.countGroupedByQuestionId().stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> ((Number) row[1]).longValue()
                ));
        return questionRepository.findAllWithChoices().stream()
                .map(question -> toResponse(question, formCounts.getOrDefault(question.getQuestionId(), 0L)))
                .toList();
    }

    @Override
    public List<QuestionTypeResponse> getActiveQuestionTypes() {
        List<GeneralType> activeTypes = generalTypeRepository.findByIsActvOrderByNameAsc(ACTIVE);
        List<QuestionTypeResponse> questionTypes = activeTypes.stream()
                .filter(this::isQuestionType)
                .map(this::toQuestionTypeResponse)
                .toList();

        if (!questionTypes.isEmpty()) {
            return questionTypes;
        }

        return activeTypes.stream()
                .map(this::toQuestionTypeResponse)
                .toList();
    }

    private QuestionTypeResponse toQuestionTypeResponse(GeneralType type) {
        return new QuestionTypeResponse(
                type.getGnlTpId(),
                type.getName(),
                type.getShrtCode(),
                type.getEntCodeName()
        );
    }

    private boolean isQuestionType(GeneralType type) {
        String ent = type.getEntCodeName() == null ? "" : type.getEntCodeName().toUpperCase(Locale.ROOT);
        String name = type.getName() == null ? "" : type.getName().toLowerCase(Locale.ROOT);
        String code = type.getShrtCode() == null ? "" : type.getShrtCode().toUpperCase(Locale.ROOT);

        if (ent.contains("QUESTION") || ent.contains("QTYPE")) {
            return true;
        }
        if (name.contains("seçmeli") || name.contains("uçlu") || name.contains("single")
                || name.contains("multi") || name.contains("open") || name.contains("text")
                || name.contains("cv") || name.contains("dosya") || name.contains("file")
                || name.contains("tarih") || name.contains("date")) {
            return true;
        }
        return QUESTION_TYPE_CODES.contains(code);
    }

    @Override
    @Transactional
    public QuestionResponse createQuestion(CreateQuestionRequest request) {
        Question question = new Question();
        question.setQuestionText(request.questionText());
        question.setTpId(request.tpId());
        question.setMinScore(request.minScore());
        question.setMaxScore(request.maxScore());
        question.setIsAssmt(request.isAssmt() != null ? request.isAssmt() : DEFAULT_FLAG_OFF);
        question.setCuser(SYSTEM_USER_ID);

        Question savedQuestion = questionRepository.save(question);

        List<QuestionChoice> choices = new ArrayList<>();
        if (request.choices() != null) {
            for (CreateQuestionRequest.Choice choiceRequest : request.choices()) {
                QuestionChoice choice = new QuestionChoice();
                choice.setQuestion(savedQuestion);
                choice.setChoiceText(choiceRequest.choiceText());
                choice.setScore(choiceRequest.score() != null ? choiceRequest.score() : 0);
                choice.setOrdNo(choiceRequest.ordNo());
                choice.setIsOther(choiceRequest.isOther() != null ? choiceRequest.isOther() : DEFAULT_FLAG_OFF);
                choice.setCuser(SYSTEM_USER_ID);
                choices.add(choice);
            }
        }

        List<QuestionChoice> savedChoices = questionChoiceRepository.saveAll(choices);
        savedQuestion.setQuestionChoices(savedChoices);
        return toResponse(savedQuestion, 0L);
    }

    @Override
    public QuestionUsageResponse getQuestionUsage(Long questionId) {
        List<String> formTitles = formQuestionRelRepository.findByQuestion_QuestionId(questionId).stream()
                .map(FormQuestionRel::getForm)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        Form::getFormId,
                        form -> (form.getTitle() == null || form.getTitle().isBlank())
                                ? ("Form #" + form.getFormId())
                                : form.getTitle(),
                        (first, ignored) -> first,
                        LinkedHashMap::new
                ))
                .values()
                .stream()
                .toList();
        long formCount = formTitles.size();
        long answerCount = questionAnswerRepository.countByQuestion_QuestionId(questionId);

        boolean isUsed = formCount > 0 || answerCount > 0;

        return new QuestionUsageResponse(
                isUsed,
                !isUsed,
                !isUsed,
                formCount,
                answerCount,
                formTitles
        );
    }

    @Override
    @Transactional
    public QuestionResponse updateQuestion(Long questionId, UpdateQuestionRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NoSuchElementException("Soru bulunamadı: " + questionId));
        
        QuestionUsageResponse usage = getQuestionUsage(questionId);
        
        if (usage.isUsed()) {
            // Kullanımdaysa: mevcut şıkların puanı + yeni şık ekleme. Mevcut şık silinmez / metni değişmez.
            if (request.choices() != null) {
                for (UpdateQuestionRequest.ChoiceUpdate choiceUpdate : request.choices()) {
                    if (choiceUpdate.id() != null) {
                        QuestionChoice choice = questionChoiceRepository.findById(choiceUpdate.id())
                                .orElseThrow(() -> new BadRequestException("Şık bulunamadı: " + choiceUpdate.id()));

                        if (!questionId.equals(choice.getQuestion().getQuestionId())) {
                            throw new BadRequestException("Şık bu soruya ait değil: " + choiceUpdate.id());
                        }

                        if (choiceUpdate.score() != null) {
                            choice.setScore(choiceUpdate.score());
                        }
                        if (choiceUpdate.ordNo() != null) {
                            choice.setOrdNo(choiceUpdate.ordNo());
                        }
                        choice.setUuser(SYSTEM_USER_ID);
                        questionChoiceRepository.save(choice);
                    } else {
                        if (choiceUpdate.choiceText() == null || choiceUpdate.choiceText().isBlank()) {
                            throw new BadRequestException("Yeni şık için metin girin.");
                        }
                        QuestionChoice choice = new QuestionChoice();
                        choice.setQuestion(question);
                        choice.setChoiceText(choiceUpdate.choiceText().trim());
                        choice.setScore(choiceUpdate.score() != null ? choiceUpdate.score() : 0);
                        choice.setOrdNo(choiceUpdate.ordNo() != null ? choiceUpdate.ordNo() : 0);
                        choice.setIsOther(choiceUpdate.isOther() != null ? choiceUpdate.isOther() : DEFAULT_FLAG_OFF);
                        choice.setCuser(SYSTEM_USER_ID);
                        questionChoiceRepository.save(choice);
                    }
                }
            }

            if (request.maxScore() != null) {
                question.setMaxScore(request.maxScore());
            }
            if (request.minScore() != null) {
                question.setMinScore(request.minScore());
            }

            List<QuestionChoice> allChoices = questionChoiceRepository
                    .findByQuestion_QuestionIdOrderByOrdNoAsc(questionId);
            if (!allChoices.isEmpty()) {
                int minScore = allChoices.stream()
                        .mapToInt(c -> c.getScore() != null ? c.getScore() : 0)
                        .min().orElse(0);
                int maxScore = allChoices.stream()
                        .mapToInt(c -> c.getScore() != null ? c.getScore() : 0)
                        .max().orElse(0);
                question.setMinScore(Math.min(0, minScore));
                question.setMaxScore(Math.max(0, maxScore));
            }
            
        } else {
            // Kullanımda değilse her şeyi güncelle
            if (request.questionText() != null) {
                question.setQuestionText(request.questionText());
            }
            if (request.tpId() != null) {
                question.setTpId(request.tpId());
            }
            if (request.minScore() != null) {
                question.setMinScore(request.minScore());
            }
            if (request.maxScore() != null) {
                question.setMaxScore(request.maxScore());
            }
            if (request.isAssmt() != null) {
                question.setIsAssmt(request.isAssmt());
            }
            
            // Şıkları güncelle: mevcut olanları sil, yenilerini ekle
            if (request.choices() != null) {
                List<QuestionChoice> existingChoices = questionChoiceRepository
                        .findByQuestion_QuestionIdOrderByOrdNoAsc(questionId);
                questionChoiceRepository.deleteAll(existingChoices);
                questionChoiceRepository.flush();
                
                List<QuestionChoice> newChoices = new ArrayList<>();
                for (UpdateQuestionRequest.ChoiceUpdate choiceUpdate : request.choices()) {
                    QuestionChoice choice = new QuestionChoice();
                    choice.setQuestion(question);
                    choice.setChoiceText(choiceUpdate.choiceText());
                    choice.setScore(choiceUpdate.score() != null ? choiceUpdate.score() : 0);
                    choice.setOrdNo(choiceUpdate.ordNo());
                    choice.setIsOther(choiceUpdate.isOther() != null ? choiceUpdate.isOther() : DEFAULT_FLAG_OFF);
                    choice.setCuser(SYSTEM_USER_ID);
                    newChoices.add(choice);
                }
                questionChoiceRepository.saveAll(newChoices);
            }
        }
        
        question.setUuser(SYSTEM_USER_ID);
        Question saved = questionRepository.save(question);
        
        // Güncel şıklarla birlikte yükle
        Question refreshed = questionRepository.findWithChoices(saved.getQuestionId())
                .orElse(saved);
        
        return toResponse(refreshed, formQuestionRelRepository.countByQuestion_QuestionId(saved.getQuestionId()));
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NoSuchElementException("Soru bulunamadı: " + questionId));
        
        QuestionUsageResponse usage = getQuestionUsage(questionId);
        
        if (!usage.canDelete()) {
            throw new BadRequestException(
                    String.format("Bu soru kullanımda olduğu için silinemez. %d formda, %d cevap kaydında var.",
                            usage.formCount(), usage.answerCount())
            );
        }
        
        // Şıkları sil
        List<QuestionChoice> choices = questionChoiceRepository
                .findByQuestion_QuestionIdOrderByOrdNoAsc(questionId);
        questionChoiceRepository.deleteAll(choices);
        
        // Soruyu sil
        questionRepository.delete(question);
    }

    private QuestionResponse toResponse(Question question, long formCount) {
        List<QuestionChoice> sourceChoices = question.getQuestionChoices() == null
                ? List.of()
                : question.getQuestionChoices();
        List<QuestionResponse.Choice> choices = sourceChoices.stream()
                .sorted(Comparator.comparing(QuestionChoice::getOrdNo, Comparator.nullsLast(Integer::compareTo)))
                .map(choice -> new QuestionResponse.Choice(
                        choice.getQuestionChoiceId(),
                        choice.getChoiceText(),
                        choice.getScore(),
                        choice.getOrdNo(),
                        choice.getIsOther()
                ))
                .toList();

        return new QuestionResponse(
                question.getQuestionId(),
                question.getQuestionText(),
                question.getTpId(),
                question.getMinScore(),
                question.getMaxScore(),
                question.getIsAssmt(),
                choices,
                formCount
        );
    }
}
