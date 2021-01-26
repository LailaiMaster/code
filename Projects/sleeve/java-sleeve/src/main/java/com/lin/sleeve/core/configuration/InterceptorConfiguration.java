package com.lin.sleeve.core.configuration;

import com.lin.sleeve.core.interceptors.PermissionInterceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/26 18:58
 */
@Component
public class InterceptorConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PermissionInterceptor());
    }

}
