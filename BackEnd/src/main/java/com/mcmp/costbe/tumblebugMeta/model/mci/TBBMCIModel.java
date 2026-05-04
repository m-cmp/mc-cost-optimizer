package com.mcmp.costbe.tumblebugMeta.model.mci;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TBBMCIModel {
    @JsonProperty("infra")
    private List<TBBMCIItemModel> mci;
}
