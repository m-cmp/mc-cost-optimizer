/**
 * ErrorModel.java
 *
 * @author   soojungkim
 * @version  1.0, Jun 13, 2019
 * @description Error Model
 *
 * copyright BESPIN GLOBAL
 */

package com.mcmp.dummybe.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorModel {

    /** The Error Code. */
    @JsonProperty("code")
    private String code = "";

    /** The Error Message. */
    @JsonProperty("message")
    private String message = "";

}