package com.mcmp.cost.ncp.collector.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 공통 코드
 * <a href="https://api.ncloud-docs.com/docs/common-vapidatatype-commoncode">CommonCode</a>
 */
@Getter
@Setter
@NoArgsConstructor
public class CommonCode {

    /**
     * 5 자리 이내의 코드.
     */
    private String code;

    /**
     * 코드에 해당하는 코드 이름.
     */
    private String codeName;
}
