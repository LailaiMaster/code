package com.ztiany.annotation.ioc.config;

import com.ztiany.annotation.ioc.beans.Person;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/18 14:12
 */
@Configuration
public class IocBaseConfig {

    /*bean id 默认是方法名*/
    @Bean
    public Person providePerson() {
        return new Person();
    }

}
