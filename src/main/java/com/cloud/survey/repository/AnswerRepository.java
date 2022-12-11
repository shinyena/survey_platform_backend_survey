package com.cloud.survey.repository;

import com.cloud.survey.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    @Query(value = "select a.ans_id, a.que_id, a.type, a.content, qo.option_name, date_format(a.reg_dt, '%Y-%m-%d %H:%i:%S') reg_dt, a.reg_ip, q.sur_id, q.q_type, q.content question" +
            " from answer a left outer join question_option qo on a.que_id = qo.que_id and a.content = qo.option_order " +
            "left outer join question q on a.que_id = q.que_id WHERE a.reg_id =:userId and q.sur_id = :surId and a.del_yn=0", nativeQuery=true)
    List<Map<String,Object>> findByRegIdAndSurId(@Param("userId") String userId, @Param("surId") int surId);

    @Query("select a from Answer a where a.regId = :regId and a.question.survey.surId = :surId and a.delYn = 0 ")
    List<Answer> findAnswerByRegIdAndSurId(String regId, Integer surId);

    @Modifying
    @Query(value = "UPDATE answer a JOIN question q ON a.que_id = q.que_id SET a.del_yn = 1 " +
            "WHERE a.reg_id = :regId AND q.sur_id = :surId", nativeQuery = true)
    void updateDeleteYn(Integer surId, String regId);

    @Query(value = "select s.sur_id, a.que_id, a.reg_dt, a.reg_id " +
            "from survey s left outer join question q on s.sur_id = q.sur_id left join answer a on q.que_id = a.que_id and q.sur_id = s.sur_id " +
            "where s.sur_id = :surId and s.status <> 'D' and a.del_yn = 0 and a.que_id = (SELECT min(que_id) FROM question qu where sur_id=s.sur_id)", nativeQuery=true)
    List<Map<String,Object>> findBySurId( @Param("surId") int surId);


}
