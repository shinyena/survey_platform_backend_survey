package com.cloud.survey.controller;

import com.cloud.survey.dto.PageRequestDTO;
import com.cloud.survey.dto.question.QuestionDTO;
import com.cloud.survey.dto.survey.SurveyDTO;
import com.cloud.survey.dto.survey.SurveyRequestDTO;
import com.cloud.survey.entity.IsYn;
import com.cloud.survey.entity.Survey;
import com.cloud.survey.entity.SurveyStatus;
import com.cloud.survey.service.QuestionService;
import com.cloud.survey.service.SurveyService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="v1/survey")
@Log4j2
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private QuestionService questionService;

    // 설문 리스트 조회
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<List<SurveyDTO>> getSurveyList(
            @RequestParam (value = "status") SurveyStatus status, @RequestParam (value = "is_private") IsYn isPrivateYn) {
        return new ResponseEntity<>(surveyService.getSurveyList(status, isPrivateYn), HttpStatus.OK);
    }

    // 설문 검색리스트 조회
    @RequestMapping(value = "/search_list", method = RequestMethod.GET)
    public ResponseEntity< Page<Map<String,Object>>> getSearchList(
            @RequestParam (value = "category_id", required = false) Integer categoryId, @RequestParam (value = "status", required = false) SurveyStatus status, PageRequestDTO pageRequestDTO) {

        Page<Map<String,Object>> list = surveyService.getSurveySearchList(categoryId, status, pageRequestDTO);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // 설문 참여리스트 조회
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/ptcp_list", method = RequestMethod.GET)
    public ResponseEntity<Page<Map<String,Object>>> getParticipateList(
                                                    @RequestParam (value = "category_id", required = false) Integer categoryId,
                                                    @RequestParam (value = "status", required = false) SurveyStatus status,
                                                    @RequestParam (value = "title", required = false) String title,
                                                    Principal principal, PageRequestDTO pageRequestDTO) {

        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        String userId = token.getTokenAttributes().get("preferred_username").toString();

//        List<Tuple> list
        Page<Map<String, Object>> list =
                surveyService.getSurveyParticipateList(title, userId, categoryId, status, pageRequestDTO);

//        Map<String, Object> returnMap = new HashMap<>();
//        for (Tuple tuple : list) {
//            returnMap.put(tuple.get(0,String.class), tuple.get(1,Object.class));
//        }
        return new ResponseEntity<>(list, HttpStatus.OK);

    }


    // 설문 생성 리스트 조회
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/make_list", method = RequestMethod.GET)
    public ResponseEntity<Page<Map<String,Object>>> getMakeList(
                                            @RequestParam (value = "category_id", required = false) Integer categoryId,
                                            @RequestParam (value = "status", required = false) SurveyStatus status,
                                            @RequestParam (value = "title", required = false) String title,
                                            Principal principal, PageRequestDTO pageRequestDTO) {

        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        String userId = token.getTokenAttributes().get("preferred_username").toString();

//        List<Tuple> list =
        Page<Map<String, Object>> list =
                surveyService.getSurveyMakeList(title, userId, categoryId, status, pageRequestDTO);
        return new ResponseEntity<>(list, HttpStatus.OK);

    }

    // 설문 조사 생성
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    @PostMapping("/reg")
    public ResponseEntity<String> registerSurvey(Principal principal, @RequestBody SurveyRequestDTO surveyRequestDTO) {

        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        String userId = token.getTokenAttributes().get("preferred_username").toString();

        List<QuestionDTO> questionDTOList = surveyRequestDTO.getQuestionDTOList();

        Survey survey = surveyService.insertSurvey(surveyRequestDTO, userId);
        questionService.insertSurveyQuestion(questionDTOList, survey, userId);


        return new ResponseEntity<>("Success", HttpStatus.OK);
    }




    // 설문 상세정보, 질문 조회
    @Transactional
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getSurveyDetail(@RequestParam (value = "sur_id") int surId) {

        Map<String, Object> map = new HashMap<>();
        map.put("info", surveyService.getSurveyDetail(surId));
        map.put("question_list", questionService.getSurveyQuestion(surId));

        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    // 카테고리별 인기 설문조사 조회
    @GetMapping("/best")
    public ResponseEntity<List<Survey>> getBestSurveyList() {
        List<Survey> bestSurveyList = surveyService.getBestSurvey();
        return new ResponseEntity<>(bestSurveyList, HttpStatus.OK);
    }

}
