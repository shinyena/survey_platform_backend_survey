package com.cloud.survey.controller;

import com.cloud.survey.dto.PageRequestDTO;
import com.cloud.survey.dto.question.QuestionDTO;
import com.cloud.survey.dto.survey.SurveyDTO;
import com.cloud.survey.dto.survey.SurveyRequestDTO;
import com.cloud.survey.dto.survey.SurveyTargetDTO;
import com.cloud.survey.dto.vulgarism.VulgarismDTO;
import com.cloud.survey.entity.IsYn;
import com.cloud.survey.entity.Survey;
import com.cloud.survey.entity.SurveyStatus;
import com.cloud.survey.entity.SurveyVulgarism;
import com.cloud.survey.service.QuestionService;
import com.cloud.survey.service.SurveyService;
import com.cloud.survey.service.SurveyTargetService;
import com.cloud.survey.service.SurveyVulgarismService;
import com.cloud.survey.service.kafka.producer.KafkaProducer;
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
    private SurveyTargetService surveyTargetService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private KafkaProducer kafkaProducer;



    @Autowired
    private SurveyVulgarismService surveyVulgarismService;

    // 설문 리스트 조회
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<List<SurveyDTO>> getSurveyList(
            @RequestParam (value = "status") SurveyStatus status, @RequestParam (value = "is_private") IsYn isPrivateYn) {
        return new ResponseEntity<>(surveyService.getSurveyList(status, isPrivateYn), HttpStatus.OK);
    }

    // 설문 검색리스트 조회
    @RequestMapping(value = "/search_list", method = RequestMethod.GET)
    public ResponseEntity< Page<Map<String,Object>>> getSearchList(  Principal principal,
                                                                     @RequestParam (value = "category_id", required = false) Integer categoryId,
                                                                     @RequestParam (value = "status", required = false) SurveyStatus status,
                                                                     PageRequestDTO pageRequestDTO) {

        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;

        String userId = null;
        if(token != null){
            userId = token.getTokenAttributes().get("preferred_username").toString();
        }
        Page<Map<String,Object>> list = surveyService.getSurveySearchList(categoryId, status, pageRequestDTO, userId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // 설문 참여리스트 조회
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/ptcp_list", method = RequestMethod.GET)
    public ResponseEntity<Page<SurveyDTO>> getParticipateList(
                                                    @RequestParam (value = "category") Integer[] categoryId,
                                                    @RequestParam (value = "status", required = false) SurveyStatus status,
                                                    @RequestParam (value = "title", required = false) String title,
                                                    Principal principal, PageRequestDTO pageRequestDTO) {

        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        String userId = token.getTokenAttributes().get("preferred_username").toString();

        Page<SurveyDTO> list = surveyService.getSurveyParticipateList(title, userId, categoryId, status, pageRequestDTO);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    // 설문 생성 리스트 조회
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/make_list", method = RequestMethod.GET)
    public ResponseEntity<Page<SurveyDTO>> getMakeList(
                                            @RequestParam (value = "category", required = false) Integer[] categoryId,
                                            @RequestParam (value = "status", required = false) SurveyStatus status,
                                            @RequestParam (value = "title", required = false) String title,
                                            Principal principal, PageRequestDTO pageRequestDTO) {

        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        String userId = token.getTokenAttributes().get("preferred_username").toString();

        Page<SurveyDTO> list =  surveyService.getSurveyMakeList(title, userId, categoryId, status, pageRequestDTO);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // 설문 조사 생성
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    @PostMapping("/reg")
    public ResponseEntity<String> registerSurvey(Principal principal, @RequestBody SurveyRequestDTO surveyRequestDTO) {

        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        String userId = token.getTokenAttributes().get("preferred_username").toString();

        // 기본정보
        SurveyDTO surveyDTO = surveyRequestDTO.getSurvey();
        Survey survey = surveyService.insertSurvey(surveyDTO, userId);

        // 질문
        List<QuestionDTO> questionDTOList = surveyRequestDTO.getQuestionDTOList();
        questionService.insertSurveyQuestion(questionDTOList, survey, userId);

        // 발송
        if(surveyRequestDTO.getSend_yn().equals("Y")){ // 바로 배포
            List<SurveyTargetDTO> surveyTargetDTOList = surveyRequestDTO.getSurveyTargetDTOList();
            surveyTargetService.insertSurveyTarget(surveyTargetDTOList, survey);
        }

        // 설문 생성 카프카 토픽
        Map<String, Object> surveyMap = new HashMap<>();
        surveyMap.put("survey_info", survey);
        surveyMap.put("question_List", questionDTOList);
        kafkaProducer.sendObjetMap("SURVEY_REG",surveyMap);

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }




    // 설문 상세정보, 질문 조회
    @Transactional
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getSurveyDetail(@RequestParam (value = "sur_id") int surId) {

        // 조회수 업데이트
        surveyService.updateSurveyHits(surId);

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

    // 비속어 DB 저장
    @Transactional
    @RequestMapping(value="/vulgarismInsert", method = RequestMethod.POST)
    public void vulgarismInsert(@RequestBody VulgarismDTO vulgarismDTO){
        surveyVulgarismService.InsertVulgarism(vulgarismDTO.getSurId(), vulgarismDTO.isInfoYn(), vulgarismDTO.isQuestionYn());
    }


    @RequestMapping(value = "/vulgarismList", method = RequestMethod.GET)
    public List<VulgarismDTO> getVulgarismList() {
        List<VulgarismDTO> vulgarismList = surveyVulgarismService.getVulgarismList();
        return vulgarismList;
    }

}
