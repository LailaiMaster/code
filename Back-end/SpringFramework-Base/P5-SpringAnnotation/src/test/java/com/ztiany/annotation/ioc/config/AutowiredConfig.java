package com.ztiany.annotation.ioc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/18 15:38
 */
@Configuration
@ComponentScan(
        value = {
                "com.ztiany.annotation.ioc.dao",
                "com.ztiany.annotation.ioc.beans",
                "com.ztiany.annotation.ioc.controller",
        }
)
public class AutowiredConfig {

}
