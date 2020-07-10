package com.ztiany.annotation.ioc.config;

import com.ztiany.annotation.ioc.beans.Person;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/18 15:45
 */
@Configuration
public class ProfileConfig {

    @Bean
    @Profile("default")
    public Person provideDefaultPerson() {
        Person person = new Person();
        person.setName("default...");
        return person;
    }

    @Bean
    @Profile("dev")
    public Person provideDevPerson() {
        Person person = new Person();
        person.setName("Dev...");
        return person;
    }

    @Bean
    @Profile("test")
    public Person provideTestPerson() {
        Person person = new Person();
        person.setName("Test...");
        return person;
    }

    @Bean
    @Profile("prod")
    public Person provideProdPerson() {
        Person person = new Person();
        person.setName("Prod...");
        return person;
    }

}
