package com.ztiany.annotation.ioc.config;

import com.ztiany.annotation.ioc.beans.PropertiesBean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/18 15:32
 */
@Configuration
//使用@PropertySource读取外部配置文件中的k/v保存到运行的环境变量中;加载完外部的配置文件以后使用${}取出配置文件的值
@PropertySource(value = {"classpath:/config.properties"})
public class PropertiesAssignmentConfig {

    @Bean
    public PropertiesBean providePropertiesBean() {
        return new PropertiesBean();
    }

}
