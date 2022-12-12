package com.cloud.survey.service;

import com.cloud.survey.dto.answer.AnswerDTO;
import com.cloud.survey.dto.question.QuestionDTO;
import com.cloud.survey.entity.Answer;
import com.cloud.survey.entity.Question;
import com.cloud.survey.entity.QuestionOption;
import com.cloud.survey.entity.Survey;

import java.util.ArrayList;
import java.util.List;

public interface QuestionService {
    List<QuestionDTO> getSurveyQuestion (int surId); // 설문 질문 조회

    void insertSurveyQuestion(List<QuestionDTO> questionDTOList, Survey survey, String regId); // 설문 질문 생성

    void insertSurveyQuestionOption(List<QuestionOption> questionOptionList, Question question); // 설문 질문옵션 생성

     List<String> getSurveyQuestionContentList (int surId); // 설문 질문내용 조회

    default Question dtoToEntity(QuestionDTO dto, Survey survey, String regId) {
        Question question = Question.builder()
                .survey(survey)
                .qType(dto.getQuestionType())
                .content(dto.getContent())
                .regId(regId)
                .build();

        return question;
    }

    default QuestionDTO entityToDto(Question question, List<QuestionOption> questionOptionList) {
        QuestionDTO questionDTO = QuestionDTO.builder()
                .queId(question.getQueId())
                .surId(question.getSurvey().getSurId())
                .questionType(question.getQType())
                .content(question.getContent())
                .optionList(new ArrayList<>())
                .build();
        questionOptionList.forEach(questionOption -> {
            questionDTO.addOptionList(questionOption);
        });
        return questionDTO;
    }
}
