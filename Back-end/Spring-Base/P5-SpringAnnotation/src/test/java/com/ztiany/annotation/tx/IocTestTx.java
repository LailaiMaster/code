package com.ztiany.annotation.tx;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IocTestTx {

    @Test
    public void test01() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(TxConfig.class);
        UserService userService = applicationContext.getBean(UserService.class);
        userService.insertUser();
        applicationContext.close();
    }

}
