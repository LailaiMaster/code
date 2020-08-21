package com.atguigu.springdata.commonmethod;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/25 21:22
 */
public class CommonRepositoryMethodTest {

    @Test
    public void testCommonCustomRepositoryMethod() {
        ApplicationContext ctx2 = new ClassPathXmlApplicationContext("classpath:applicationContext_commonmethod.xml");
//        AddressRepository addressRepository = ctx2.getBean(AddressRepository.class);
//        addressRepository.method();
    }

}