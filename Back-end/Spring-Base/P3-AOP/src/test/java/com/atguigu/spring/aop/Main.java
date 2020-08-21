package com.atguigu.spring.aop;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 使用注解配置AOP
 */
public class Main {

    @Test
    public void noAop() {
        ArithmeticCalculator arithmeticCalculator = new ArithmeticCalculatorLoggingImpl();

        int result = arithmeticCalculator.add(11, 12);
        System.out.println("result:" + result);

        result = arithmeticCalculator.div(21, 3);
        System.out.println("result:" + result);
    }

    public void manualAop() {
        ArithmeticCalculator arithmeticCalculator = new ArithmeticCalculatorImpl();
        arithmeticCalculator = new ArithmeticCalculatorLoggingProxy(arithmeticCalculator).getLoggingProxy();

        int result = arithmeticCalculator.add(11, 12);
        System.out.println("result:" + result);

        result = arithmeticCalculator.div(21, 3);
        System.out.println("result:" + result);
    }

    @Test
    public void springAop() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext-aop.xml");
        ArithmeticCalculator arithmeticCalculator = (ArithmeticCalculator) ctx.getBean("arithmeticCalculator");

        System.out.println(arithmeticCalculator.getClass().getName());

        int result = arithmeticCalculator.add(11, 12);
        System.out.println("result:" + result);

        result = arithmeticCalculator.div(21, 3);
        System.out.println("result:" + result);
    }


}
