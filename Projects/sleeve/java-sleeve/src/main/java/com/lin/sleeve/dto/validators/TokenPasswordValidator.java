package com.lin.sleeve.dto.validators;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 自定义参数校验
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/20 15:51
 */
public class TokenPasswordValidator implements ConstraintValidator<TokenPassword/*注解类型*/, String/*目标类型*/> {

    private int min;
    private int max;

    @Override
    public void initialize(TokenPassword constraintAnnotation) {
        this.max = constraintAnnotation.max();
        this.min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(password)) {
            return true;
        }
        return password.length() >= min && password.length() <= max;
    }

}
