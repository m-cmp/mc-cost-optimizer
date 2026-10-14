package com.mcmp.costbe.tumblebugMeta.model.mci;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Node(VM) object returned by GET /ns/{ns}/infra/{mci}/node/{vm}. Only the nested spec is mapped.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbInfraNodeModel {
    private TbInfraNodeSpecModel spec;
}
