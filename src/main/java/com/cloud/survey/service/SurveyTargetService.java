package com.cloud.survey.service;

import com.cloud.survey.dto.survey.SurveyTargetDTO;
import com.cloud.survey.entity.Survey;
import com.cloud.survey.entity.SurveyTarget;

import java.util.List;

public interface SurveyTargetService {

    void insertSurveyTarget(List<SurveyTargetDTO> targetList, Survey survey);

    default SurveyTarget dtoToEntity(SurveyTargetDTO dto, Survey survey) {
        SurveyTarget surveyTarget = SurveyTarget.builder()
                .targetId(dto.getTargetId())
                .survey(survey)
                .build();
        return surveyTarget;
    }
}
