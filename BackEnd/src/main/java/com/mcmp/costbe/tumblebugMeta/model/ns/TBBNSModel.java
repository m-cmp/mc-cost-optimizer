package com.mcmp.costbe.tumblebugMeta.model.ns;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TBBNSModel {
    private List<TBBNSItemModel> ns;
    private List<String> output;
}
