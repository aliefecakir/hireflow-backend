package com.hireflow.backend.service.impl;

import com.hireflow.backend.dto.CreateQuestionRequest;
import com.hireflow.backend.dto.QuestionResponse;
import com.hireflow.backend.dto.QuestionTypeResponse;
import com.hireflow.backend.entity.GeneralType;
import com.hireflow.backend.entity.Question;
import com.hireflow.backend.entity.QuestionChoice;
import com.hireflow.backend.repository.GeneralTypeRepository;
import com.hireflow.backend.repository.QuestionChoiceRepository;
import com.hireflow.backend.repository.QuestionRepository;
import com.hireflow.backend.service.QuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

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

    public QuestionServiceImpl(
            QuestionRepository questionRepository,
            QuestionChoiceRepository questionChoiceRepository,
            GeneralTypeRepository generalTypeRepository
    ) {
        this.questionRepository = questionRepository;
        this.questionChoiceRepository = questionChoiceRepository;
        this.generalTypeRepository = generalTypeRepository;
    }

    @Override
    public List<QuestionResponse> getActiveQuestions() {
        return questionRepository.findAllWithChoices().stream()
                .map(this::toResponse)
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
        return toResponse(savedQuestion);
    }

    private QuestionResponse toResponse(Question question) {
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
                choices
        );
    }
}
