package com.ztiany.annotation.ioc.config;

import com.ztiany.annotation.ioc.beans.Person;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/18 14:53
 */
@Configuration
public class ScopeConfig {

    @Bean("person forever")
    public Person providePerson1() {
        System.out.println("===>call person forever");
        return new Person();
    }

    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean("person many")
    public Person providePerson2() {
        System.out.println("===>call person many");
        return new Person();
    }

    @Lazy
    @Bean("person forever lazy")
    public Person providePerson3() {
        System.out.println("===>call person forever lazy");
        return new Person();
    }

}
