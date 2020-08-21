package com.atguigu.spring.aop.xml;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 使用 Xml 配置 AOP。
 */
public class Main {

    @Test
    public void xmlAop() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext-aop-xml.xml");
        ArithmeticCalculator arithmeticCalculator = (ArithmeticCalculator) ctx.getBean("arithmeticCalculator");

        System.out.println(arithmeticCalculator.getClass().getName());

        int result = arithmeticCalculator.add(1, 2);
        System.out.println("result:" + result);

        result = arithmeticCalculator.div(1000, 0);
        System.out.println("result:" + result);
    }

}
