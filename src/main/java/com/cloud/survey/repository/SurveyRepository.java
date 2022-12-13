package com.cloud.survey.repository;

import com.cloud.survey.entity.IsYn;
import com.cloud.survey.entity.Survey;
import com.cloud.survey.entity.SurveyStatus;
//import com.cloud.survey.querydsl.SurveyRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface SurveyRepository extends JpaRepository<Survey, Integer> {

    // 설문 리스트 조회
    List<Survey> findByStatusAndIsPrivateYn(SurveyStatus status, IsYn isPrivateYn);


    // 설문 검색 리스트 조회
    @Query(value =
            "SELECT sc.content, s.*, " +
//            "(select count(*) " +
//            "from question q left join answer a on q.que_id = a.que_id and q.sur_id = s.sur_id " +
//            "where a.del_yn <> 1 " +
//            "and a.que_id = (SELECT min(q.que_id) FROM question qu where sur_id=s.sur_id)) answer_cnt " +
            "FROM survey s left join survey_category sc on sc.sur_cat_id = s.category_id " +
            "WHERE 1=1 " +
            "and s.status = 'I' " +
            "and s.is_private = 'N' "
//            "and s.category_id = :categoryId "
            , nativeQuery = true)
    Page<Map<String,Object>> findByCategoryIdAndStatus(@Param("categoryId") int categoryId, Pageable pageable);

    // 대상자 포함 설문 검색 리스트 조회

    @Query(value =
            "SELECT sc.content, s.*, " +
//                    "(select count(*) " +
//                    "from question q left join answer a on q.que_id = a.que_id and q.sur_id = s.sur_id " +
//                    "where a.del_yn <> 1 " +
//                    "and a.que_id = (SELECT min(q.que_id) FROM question qu where sur_id=s.sur_id)) answer_cnt " +
                    "FROM survey s left join survey_category sc on sc.sur_cat_id = s.category_id " +
                    "WHERE 1=1 " +
                    "and s.status = 'I' " +
                    "and (s.is_private = 'N' ||(s.is_private = 'Y' and (select count(st.sur_id) from survey_target st where st.sur_id = s.sur_id and st.target_id = ':regId') > 0))"
            , nativeQuery = true)
    Page<Map<String,Object>> findByCategoryIdAndStatusAndTarget(@Param("categoryId") int categoryId, Pageable pageable, @Param("regId") String regId);





    // 설문 상세 조회
    Survey findBySurId(int surId);


    // 카테고리별 베스트 설문 조회
    @Query(value = "SELECT * FROM survey " +
            "WHERE category_id=:surCatId " +
            "ORDER BY views DESC, reg_dt DESC LIMIT 1;", nativeQuery = true)
    Survey findBestSurveyByCategory(Integer surCatId);


    // 카테고리별 설문 리스트 조회
    @Query("select s.surId from Survey s where s.surveyCategory.surCatId = :surCatId")
    List<Integer> findSurIdBySurCatId(Integer surCatId);


    // 설문 조회수 업데이트
    @Query(value="UPDATE survey s SET s.views = s.views + 1 WHERE s.sur_id =:surId", nativeQuery = true)
    void updateSurveyHits(@Param("surId") Integer surId);

}
