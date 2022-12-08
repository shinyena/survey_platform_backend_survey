package com.cloud.survey.dto.survey;

import com.cloud.survey.entity.IsYn;
import com.cloud.survey.entity.SurveyStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
//@NoArgsConstructor
//@AllArgsConstructor
@Builder
public class SurveyDTO {

    private int surId;
    private String title;
    private String description;
    private int categoryId;
    private String categoryContent;
    private int version;
    private SurveyStatus status;
    private LocalDateTime dueDt;
    private IsYn isLoginYn;
    private IsYn isPrivateYn;
    private IsYn isModifyYn;
    private IsYn isAnnoyYn;
    private String regId;
    private LocalDateTime regDt;
    private Integer views;
    private String statusName;

    @QueryProjection
    public SurveyDTO(int surId, String title, String description, int categoryId, String categoryContent, int version, SurveyStatus status, LocalDateTime dueDt, IsYn isLoginYn, IsYn isPrivateYn, IsYn isModifyYn, IsYn isAnnoyYn, String regId, LocalDateTime regDt, Integer views, String statusName) {
        this.surId = surId;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.categoryContent = categoryContent;
        this.version = version;
        this.status = status;
        this.dueDt = dueDt;
        this.isLoginYn = isLoginYn;
        this.isPrivateYn = isPrivateYn;
        this.isModifyYn = isModifyYn;
        this.isAnnoyYn = isAnnoyYn;
        this.regId = regId;
        this.regDt = regDt;
        this.views = views;
        this.statusName = statusName;
    }
}
