package com.atguigu.spring.aop.xml;

import org.aspectj.lang.JoinPoint;

import java.util.Arrays;

public class ValidationAspect {

    public void validateArgs(JoinPoint joinPoint) {
        System.out.println("-->validate:" + Arrays.asList(joinPoint.getArgs()));
    }

}
