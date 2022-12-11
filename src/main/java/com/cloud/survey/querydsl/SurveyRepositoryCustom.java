package com.cloud.survey.querydsl;

import com.cloud.survey.dto.survey.QSurveyDTO;
import com.cloud.survey.dto.survey.SurveyDTO;
import com.cloud.survey.entity.*;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.mysema.commons.lang.Assert.assertThat;

@RequiredArgsConstructor
@Repository
public class SurveyRepositoryCustom {


    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;
    QSurvey qSurvey = QSurvey.survey;
    QQuestion qQuestion = QQuestion.question;
    QAnswer qAnswer = QAnswer.answer;

    QSurveyCategory qSurveyCategory = QSurveyCategory.surveyCategory;


    public List<Tuple> findByCategoryIdAndTitle( // 검색리스트
            String title, Integer[] categoryId, Pageable pageable) {

        List<Tuple> results = queryFactory
                .select(qSurvey, qSurveyCategory.content
//                        ExpressionUtils.as(
//                                JPAExpressions.select(m.team.count())
//                                    .from(m)
//                                    .where(m.team.eq(t)),
//                                "memberCount")
                )
                .from(qSurvey)
                .leftJoin(qSurveyCategory)
                .on(qSurvey.surveyCategory.surCatId.eq(qSurveyCategory.surCatId))
                .where(
                        inCategoryId(categoryId),
                        qSurvey.title.like("%"+title+"%")
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return results;
    }

    public Page<SurveyDTO> findByRegIdAndCategoryIdAndStatusAndTitle( //생성목록
            String title, String regId, Integer[] categoryId, SurveyStatus status, Pageable pageable) {


        StringExpression caseStatusDeudateStr = new CaseBuilder()
                .when(qSurvey.dueDt.before(LocalDateTime.now())).then("배포")
                .when(qSurvey.dueDt.after(LocalDateTime.now())).then("마감").otherwise("");


        List<SurveyDTO> list = queryFactory
                .select(new QSurveyDTO(
                         qSurvey.surId
                        ,qSurvey.title
                        ,qSurvey.description
                        ,qSurvey.surveyCategory.surCatId
                        ,qSurveyCategory.content.as("categoryContent")
                        ,qSurvey.version
                        ,qSurvey.status
                        ,qSurvey.dueDt
                        ,qSurvey.isLoginYn
                        ,qSurvey.isPrivateYn
                        ,qSurvey.isModifyYn
                        ,qSurvey.isAnnoyYn
                        ,qSurvey.regId
                        ,qSurvey.regDt
                        ,qSurvey.views
                        , new CaseBuilder()
                        .when(qSurvey.status.eq(SurveyStatus.valueOf("P"))).then("제작")
                        .when(qSurvey.status.eq(SurveyStatus.valueOf("I"))).then(caseStatusDeudateStr)
                        .otherwise("").as("statusName")
                        )
                )
                .from(qSurvey)
                .leftJoin(qSurveyCategory)
                .on(qSurvey.surveyCategory.surCatId.eq(qSurveyCategory.surCatId))
                .where(
                        eqRegId(regId),
                        inCategoryId(categoryId),
                        likeTitle(title)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long count = queryFactory
                .select(qSurvey.count())
                .from(qSurvey)
                .leftJoin(qSurveyCategory)
                .on(qSurvey.surveyCategory.surCatId.eq(qSurveyCategory.surCatId))
                .where(
                        eqRegId(regId),
                        inCategoryId(categoryId),
                        likeTitle(title)
                )
                .fetchOne();


        return  new PageImpl<>(list, pageable, count);
    }

    public Page<SurveyDTO> findByCategoryIdAndStatusAndTitle( // 참여목록
            String title, String regId, Integer[] categoryId, SurveyStatus status, Pageable pageable) {

        StringExpression caseStatusDeudateStr = new CaseBuilder()
                .when(qSurvey.dueDt.before(LocalDateTime.now())).then("배포")
                .when(qSurvey.dueDt.after(LocalDateTime.now())).then("마감").otherwise("");


        List<SurveyDTO> list = queryFactory
                .select(new QSurveyDTO(
                                qSurvey.surId
                                ,qSurvey.title
                                ,qSurvey.description
                                ,qSurvey.surveyCategory.surCatId
                                ,qSurveyCategory.content.as("categoryContent")
                                ,qSurvey.version
                                ,qSurvey.status
                                ,qSurvey.dueDt
                                ,qSurvey.isLoginYn
                                ,qSurvey.isPrivateYn
                                ,qSurvey.isModifyYn
                                ,qSurvey.isAnnoyYn
                                ,qSurvey.regId
                                ,qSurvey.regDt
                                ,qSurvey.views
                                , new CaseBuilder()
                                .when(qSurvey.status.eq(SurveyStatus.valueOf("P"))).then("제작")
                                .when(qSurvey.status.eq(SurveyStatus.valueOf("I"))).then(caseStatusDeudateStr)
                                .otherwise("").as("statusName")
                        )
                )
                .from(qSurvey)
                .leftJoin(qSurveyCategory)
                .on(qSurvey.surveyCategory.surCatId.eq(qSurveyCategory.surCatId))
                .leftJoin(qQuestion)
                .on(qSurvey.surId.eq(qQuestion.survey.surId))
                .join(qAnswer)
                .on(qQuestion.queId.eq(qAnswer.question.queId).and(qAnswer.regId.eq((regId))))
                .where(
                        inCategoryId(categoryId),
                        likeTitle(title)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .groupBy(qSurvey.surId)
                .fetch();


        Long count = queryFactory
                .select(qSurvey.count())
                .from(qSurvey)
                .leftJoin(qSurveyCategory)
                .on(qSurvey.surveyCategory.surCatId.eq(qSurveyCategory.surCatId))
                .leftJoin(qQuestion)
                .on(qSurvey.surId.eq(qQuestion.survey.surId))
                .join(qAnswer)
                .on(qQuestion.queId.eq(qAnswer.question.queId).and(qAnswer.regId.eq((regId))))
                .where(
                        inCategoryId(categoryId),
                        likeTitle(title)
                )
                .fetchOne();


        return  new PageImpl<>(list, pageable, count);

    }


    private BooleanExpression eqRegId(String regId) {
        if (StringUtils.isEmpty(regId)) {
            return null;
        }
        return qSurvey.regId.eq(regId);
    }

    private BooleanExpression inCategoryId(Integer[] cateId) {
        if (cateId == null || cateId.length <0) {
            return null;
        }
        return qSurvey.surveyCategory.surCatId.in(cateId);
    }

    private BooleanExpression eqStatus(SurveyStatus status) {
        if (StringUtils.isEmpty(String.valueOf(status))) {
            return null;
        }
        return qSurvey.status.eq(status);
    }

    private BooleanExpression likeTitle(String title) {
        if (title == null || StringUtils.isEmpty(title)) {
            return null;
        }
        return qSurvey.title.like("%"+title+"%");
    }



}
