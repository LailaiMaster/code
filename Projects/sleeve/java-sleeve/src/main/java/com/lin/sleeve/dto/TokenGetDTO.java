package com.lin.sleeve.dto;

import com.lin.sleeve.core.enumeration.LoginType;
import com.lin.sleeve.dto.validators.TokenPassword;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/26 15:10
 */
@Setter
@Getter
@ToString
public class TokenGetDTO {

    @NotBlank(message = "account 不允许为空")
    private String account;

    @TokenPassword(message = "{token.password}")
    private String password;

    private LoginType loginType;

}
