package com.lin.sleeve.dto;

import com.lin.sleeve.dto.validators.ConfirmPassword;
import com.lin.sleeve.dto.validators.PasswordEqual;

import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/20 0:27
 */
@Getter
@Setter
@ToString
@PasswordEqual
public class PersonDTO implements ConfirmPassword {

    @Length(min = 2, max = 10)
    private String name;

    private Integer age;

    @Valid
    private SchoolDTO school;

    private String password;

    private String confirmPassword;

}

