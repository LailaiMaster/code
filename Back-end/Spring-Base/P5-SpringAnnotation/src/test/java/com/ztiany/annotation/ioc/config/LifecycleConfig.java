package com.ztiany.annotation.ioc.config;

import com.ztiany.annotation.ioc.beans.LifecyclePerson;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/18 14:58
 */
@Configuration
@ComponentScan(value = "com.ztiany.annotation.ioc.beans")
public class LifecycleConfig {

    @Bean(
            initMethod = "initMethod", destroyMethod = "destroyMethod"
    )
    public LifecyclePerson provideLifecyclePerson() {
        return new LifecyclePerson();
    }

}
