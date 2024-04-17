package com.mcmp.dummybe.model.widget;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WidgetProductPortionPortionModel {
    private String cost;
    private String familyCode;
    private List<WidgetProductPortionItemsModel> familyItems;
    private String order;
    private Boolean serviceGroup;
    private Boolean isOther;
    private Integer numberOfOthers;
    private String item;
    private String itemAlias;
}
