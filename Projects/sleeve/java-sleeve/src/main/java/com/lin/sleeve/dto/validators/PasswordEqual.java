package com.lin.sleeve.dto.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/20 15:46
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Constraint(validatedBy = PasswordValidator.class)
public @interface PasswordEqual {

    int min() default 4;

    int max() default 20;

    /**规范要求的模板方法，message 自动被拿来做消息*/
    String message() default "passwords are not equal";

    /*规范要求的模板方法，加上即可。*/
    Class<?>[] groups() default {};

    /*规范要求的模板方法，加上即可。*/
    Class<? extends Payload>[] payload() default {};

}


