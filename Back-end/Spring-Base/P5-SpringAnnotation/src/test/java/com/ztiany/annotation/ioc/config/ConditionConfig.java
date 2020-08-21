package com.ztiany.annotation.ioc.config;

import com.ztiany.annotation.ioc.beans.Person;
import com.ztiany.annotation.ioc.config.condition.LinuxCondition;
import com.ztiany.annotation.ioc.config.condition.WindowsCondition;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/18 14:47
 */
@Configuration
@Conditional(value = {WindowsCondition.class})
public class ConditionConfig {

    @Bean
    @Conditional(value = {LinuxCondition.class})
    public Person provideLinuxPerson() {
        Person person = new Person();
        person.setName("Linus");
        return person;
    }

    @Bean
    @Conditional(value = {WindowsCondition.class})
    public Person provideWindowsPerson() {
        Person person = new Person();
        person.setName("Bill Gates");
        return person;
    }

}
