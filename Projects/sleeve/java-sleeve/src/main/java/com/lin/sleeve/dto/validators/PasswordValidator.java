package com.lin.sleeve.dto.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 自定义参数校验
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/20 15:51
 */
public class PasswordValidator implements ConstraintValidator<PasswordEqual/*注解类型*/, ConfirmPassword/*目标类型*/> {

    private int min;
    private int max;

    @Override
    public void initialize(PasswordEqual constraintAnnotation) {
        this.max = constraintAnnotation.max();
        this.min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(ConfirmPassword value, ConstraintValidatorContext context) {
        String password = value.getPassword();
        String confirmPassword = value.getConfirmPassword();
        return password.equals(confirmPassword);
    }

}
