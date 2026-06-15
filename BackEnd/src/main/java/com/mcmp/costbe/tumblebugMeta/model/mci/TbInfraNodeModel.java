package com.mcmp.costbe.tumblebugMeta.model.mci;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * One entry of the "node" array returned by GET /ns/{ns}/infra/{mci}?nodeId={vm}.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbInfraNodeModel {
    private TbInfraNodeSpecModel spec;
}
