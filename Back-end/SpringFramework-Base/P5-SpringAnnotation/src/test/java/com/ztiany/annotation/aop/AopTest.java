package com.ztiany.annotation.aop;

import com.ztiany.annotation.Utils;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/19 17:37
 */
public class AopTest {

    @Test
    public void testAopConfig() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AopConfig.class);
        Utils.printBeansValue("testAopConfig", applicationContext);
        applicationContext.getBean(MathCalculator.class).div(1, 1);
        applicationContext.close();
    }

}
