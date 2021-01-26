package com.lin.sleeve.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/23 22:47
 */
@Getter
@Setter
@ToString
public class Spec implements Serializable {

    private static final long serialVersionUID = 422L;

    @JsonProperty("key_id")
    private Long keyId;

    private String key;

    @JsonProperty("value_id")
    private Long valueId;

    private String value;

}
