package com.cloud.survey.service;

import com.cloud.survey.dto.PageRequestDTO;
import com.cloud.survey.dto.survey.SurveyDTO;
import com.cloud.survey.entity.*;
import com.cloud.survey.entity.Survey;
import com.cloud.survey.entity.SurveyCategory;
import com.cloud.survey.entity.SurveyStatus;
import com.cloud.survey.querydsl.SurveyRepositoryCustom;
import com.cloud.survey.repository.SurveyCategoryRepository;
import com.cloud.survey.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class SurveyServiceImpl implements SurveyService{
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final SurveyCategoryRepository surveyCategoryRepository;
    @Autowired
    private final SurveyRepository surveyRepository;
    @Autowired
    private final SurveyRepositoryCustom surveyRepositoryCustom;
    @Autowired
    private final ModelMapper mapper;

    @Override
    public List<SurveyDTO> getSurveyList(SurveyStatus status, IsYn isPrivateYn){

        List<SurveyDTO> surveyDtoList = new ArrayList<>();
        List<Survey> surveyList = surveyRepository.findByStatusAndIsPrivateYn(status, isPrivateYn);

        surveyList.forEach(survey -> {
            SurveyDTO surveydto = mapper.map(survey, SurveyDTO.class);
            surveyDtoList.add(surveydto);
        });
        return surveyDtoList;
    }

    @Override
    public  Page<Map<String,Object>> getSurveySearchList(Integer category_id, SurveyStatus status, PageRequestDTO requestDTO, String regId){
        Pageable pageable = requestDTO.getPageable(Sort.by("reg_dt").descending());
        Page<Map<String,Object>> list = null;
        if(regId == null){
            list =  surveyRepository.findByCategoryIdAndStatus(category_id, pageable);
        }else{
            list = surveyRepository. findByCategoryIdAndStatusAndTarget(category_id, pageable, regId);
        }
        return list;
    }

    public Page<SurveyDTO> getSurveyParticipateList(String title, String regId, Integer[] category_id, SurveyStatus status, PageRequestDTO requestDTO){
        requestDTO.setSize(4);
        Pageable pageable = requestDTO.getPageable(Sort.by("reg_dt").descending());

        Page<SurveyDTO> tuplePageList = surveyRepositoryCustom.findByCategoryIdAndStatusAndTitle(title, regId, category_id, status, pageable);
        List<SurveyDTO> list = tuplePageList.getContent();

        return new PageImpl<>(list, pageable, tuplePageList.getTotalElements());

    }

    public Page<SurveyDTO> getSurveyMakeList(String title, String regId, Integer[] category_id, SurveyStatus status, PageRequestDTO requestDTO){
        requestDTO.setSize(4);
        Pageable pageable = requestDTO.getPageable(Sort.by("reg_dt").descending());

        Page<SurveyDTO> tuplePageList = surveyRepositoryCustom.findByRegIdAndCategoryIdAndStatusAndTitle(title, regId, category_id, status, pageable);
        List<SurveyDTO> list = tuplePageList.getContent();

        return new PageImpl<>(list, pageable, tuplePageList.getTotalElements());
    }

    public Survey insertSurvey(SurveyDTO surveyDTO, String userId){
        SurveyCategory surveyCategory = surveyCategoryRepository.findBySurCatId(surveyDTO.getCategoryId());
        Survey save = surveyRepository.save(dtoToEntity(surveyDTO, surveyCategory, userId));
        return save;
    }

    public SurveyDTO getSurveyDetail (int surId){
        Survey survey = surveyRepository.findBySurId(surId);
        return entityToDTO(survey);
    }

    public List<Survey> getBestSurvey() {
        List<Survey> bestSurveyList = new ArrayList<>();
        List<SurveyCategory> surveyCategoryList = surveyCategoryRepository.findAll();
        surveyCategoryList.forEach(surveyCategory -> {
            Survey survey = surveyRepository.findBestSurveyByCategory(surveyCategory.getSurCatId());
            bestSurveyList.add(survey);
        });
        return bestSurveyList;
    }

    public void updateSurveyHits(Integer surId){
        surveyRepository.updateSurveyHits(surId);
    }


}
