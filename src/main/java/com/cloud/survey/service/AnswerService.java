package com.cloud.survey.service;

import com.cloud.survey.dto.answer.AnswerDTO;
import com.cloud.survey.dto.answer.AnswerQuestionDTO;
import com.cloud.survey.entity.Answer;
import com.cloud.survey.entity.Question;

import java.util.List;
import java.util.Map;

public interface AnswerService {

    List<Map<String,Object>> getUserAnswer (String userId, int surId); // 사용자의 답변 조회
    Map<String,Object> getSurveyAnswerAnalysis(int surId); // 설문 응답시간, 응답자 데이터 조회

    List<AnswerQuestionDTO> getAnswerList(Integer surId, String regId);
    void insertAnswer(Integer surId, List<AnswerDTO> answerList);
    void updateAnswer(Integer surId, List<AnswerDTO> answerList);
    void deleteAnswer(Integer surId, String regId);

    default Answer dtoToEntity(AnswerDTO dto, Question question) {
        Answer answer = Answer.builder()
                .question(question)
                .type(dto.getType())
                .content(dto.getContent())
                .regId(dto.getRegId())
                .delYn(false)
                .build();
        return answer;
    }

    default AnswerDTO entityToDTO(Answer answer) {
        AnswerDTO dto = AnswerDTO.builder()
                .ansId(answer.getAnsId())
                .queId(answer.getQuestion().getQueId())
                .type(answer.getType())
                .content(answer.getContent())
                .regId(answer.getRegId())
                .build();
        return dto;
    }
}
