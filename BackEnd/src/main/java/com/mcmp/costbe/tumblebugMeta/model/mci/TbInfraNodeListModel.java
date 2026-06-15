package com.mcmp.costbe.tumblebugMeta.model.mci;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Response of GET /ns/{ns}/infra/{mci}?nodeId={vm}.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbInfraNodeListModel {
    private List<TbInfraNodeModel> node;
}
