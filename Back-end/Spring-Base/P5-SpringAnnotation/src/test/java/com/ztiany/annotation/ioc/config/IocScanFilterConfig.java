package com.ztiany.annotation.ioc.config;

import com.ztiany.annotation.ioc.beans.Person;
import com.ztiany.annotation.ioc.config.typefilter.TestTypeFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/18 14:25
 */
@Configuration
@ComponentScan(
        value = "com.ztiany.annotation.ioc.dao",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.CUSTOM, classes = TestTypeFilter.class)
        }, useDefaultFilters = false)
public class IocScanFilterConfig {

    /*bean id 默认是方法名*/
    @Bean
    public Person providePerson() {
        return new Person();
    }

}

