package com.mcmp.collector.model.cur;

import lombok.Data;

import java.util.List;

@Data
public class AwsCurDetailModel {
    String suffix;
    List<AwsCurModel> batchList;
}
