package com.processor.costprocessor.model.abnormal;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AbnormalItemModel {

    private LocalDate collect_dt;
    private String abnormal_rating;
    private double percentage_point;
    private String product_cd;
    private double standard_cost;
    private double subject_cost;
    private String project_cd;
    private String workspace_cd;
    private String csp_type;
}
