package com.cloud.survey.service;

import com.cloud.survey.dto.survey.SurveyTargetDTO;
import com.cloud.survey.entity.Survey;
import com.cloud.survey.repository.SurveyTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyTargetServiceImpl implements SurveyTargetService {
    @Autowired
    private final SurveyTargetRepository surveyTargetRepository;


    public void insertSurveyTarget(List<SurveyTargetDTO> targetList, Survey survey){
        targetList.forEach(targetDto -> {
            surveyTargetRepository.save(dtoToEntity(targetDto, survey));
        });
    }


}
