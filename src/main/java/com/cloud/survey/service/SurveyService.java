package com.cloud.survey.service;


import com.cloud.survey.dto.PageRequestDTO;
import com.cloud.survey.dto.survey.SurveyDTO;
import com.cloud.survey.entity.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface SurveyService {

    // 설문조사 리스트 조회
    List<SurveyDTO> getSurveyList(SurveyStatus status, IsYn isPrivateYn);

    // 설문조사 검색 리스트 조회
    Page<Map<String,Object>> getSurveySearchList(Integer category_id, SurveyStatus status, PageRequestDTO requestDTO);

    // 설문조사 참여 리스트 조회
    Page<SurveyDTO> getSurveyParticipateList(String title, String regId, Integer[] category_id, SurveyStatus status, PageRequestDTO requestDTO);

    // 설문조사 생성 리스트 조회
    Page<SurveyDTO> getSurveyMakeList(String title, String regId, Integer[] category_id, SurveyStatus status, PageRequestDTO requestDTO);

    // 설문조사 생성
    Survey insertSurvey(SurveyDTO surveyDTO, String userId);

    // 설문조사 타겟 생성
    void insertSurveyTarget(List<SurveyTarget> targetList);

    // 설문 상세정보 조회
    SurveyDTO getSurveyDetail (int surId);

    // 카테고리별 인기 설문조사 조회
    List<Survey> getBestSurvey();


    default Survey dtoToEntity(SurveyDTO dto, SurveyCategory surveyCategory, String userId) {
        Survey survey = Survey.builder()
                .surId(dto.getSurId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .surveyCategory(surveyCategory)
                .version(dto.getVersion())
                .status(dto.getStatus())
                .dueDt(dto.getDueDt())
                .isLoginYn(dto.getIsLoginYn())
                .isPrivateYn(dto.getIsPrivateYn())
                .isModifyYn(dto.getIsModifyYn())
                .isAnnoyYn(dto.getIsAnnoyYn())
//                .regId(dto.getRegId())
                .regId(userId)
//                .regDt(dto.getRegDt())
                .build();
        return survey;
    }


    default SurveyDTO entityToDTO(Survey survey) {
        SurveyDTO dto = SurveyDTO.builder()
                .surId(survey.getSurId())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .categoryId(survey.getSurveyCategory().getSurCatId())
                .categoryContent(survey.getSurveyCategory().getContent())
                .version(survey.getVersion())
                .status(survey.getStatus())
                .dueDt(survey.getDueDt())
                .isLoginYn(survey.getIsLoginYn())
                .isPrivateYn(survey.getIsPrivateYn())
                .isModifyYn(survey.getIsModifyYn())
                .isAnnoyYn(survey.getIsAnnoyYn())
                .regId(survey.getRegId())
                .regDt(survey.getRegDt())
                .build();
        return dto;
    }
}
