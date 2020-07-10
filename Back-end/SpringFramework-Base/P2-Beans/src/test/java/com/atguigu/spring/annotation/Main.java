package com.atguigu.spring.annotation;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    @Test
    public void main() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("beans-annotation.xml");
        UserAction userAction = ctx.getBean(UserAction.class);
        userAction.execute();
    }

}
