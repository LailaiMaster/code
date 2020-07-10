package com.ztiany.annotation.ioc.config;

import com.ztiany.annotation.ioc.beans.DogFactoryBean;
import com.ztiany.annotation.ioc.beans.Person;
import com.ztiany.annotation.ioc.config.importor.TestImportBeanDefinitionRegistrar;
import com.ztiany.annotation.ioc.config.importor.TestImportSelector;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/18 14:36
 */
@Configuration
@Import({
        Person.class, TestImportSelector.class, TestImportBeanDefinitionRegistrar.class
})
public class ImportConfig {

    @Bean
    public DogFactoryBean dogFactoryBean() {
        return new DogFactoryBean();
    }

}
