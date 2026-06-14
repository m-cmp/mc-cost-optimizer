package com.mcmp.costbe.tumblebugMeta.model.mci;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * spec block of a node returned by GET /ns/{ns}/infra/{mci}?nodeId={vm}.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbInfraNodeSpecModel {
    private String cspSpecName;

    // Lombok's getter for "vCPU" is getVCPU(), which Jackson would otherwise
    // map back to "VCPU" (consecutive-uppercase getter names aren't
    // decapitalized), missing the actual "vCPU" JSON field.
    @JsonProperty("vCPU")
    private Double vCPU;

    private Double memoryGiB;
}
