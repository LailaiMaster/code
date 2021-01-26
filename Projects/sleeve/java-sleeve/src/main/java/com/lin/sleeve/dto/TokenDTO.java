package com.lin.sleeve.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/26 19:13
 */
@Setter
@Getter
@ToString
public class TokenDTO {

    @NotBlank(message = "token 不允许为空")
    private String token;

}
