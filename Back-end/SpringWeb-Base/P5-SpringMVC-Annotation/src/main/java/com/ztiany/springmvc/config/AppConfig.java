package com.ztiany.springmvc.config;


import com.ztiany.springmvc.interceptor.FirstInterceptor;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.stereotype.Controller;

//SpringMVC只扫描Controller，子容器。
//useDefaultFilters=false 禁用默认的过滤规则。
@ComponentScan(
        value = "com.ztiany.springmvc",
        includeFilters = {
                @Filter(type = FilterType.ANNOTATION, classes = {Controller.class})
        },
        useDefaultFilters = false)
/*EnableWebMvc 相当于：<mvc:annotation-driven />*/
@EnableWebMvc
public class AppConfig extends WebMvcConfigurerAdapter {

    //视图解析器
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        //registry.jsp();//默认所有的页面都从 /WEB-INF/xxx .jsp
        registry.jsp("/WEB-INF/views/", ".jsp");//默认所有的页面都从 /WEB-INF/views/xxx .jsp
    }

    //静态资源访问
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    	/*
    	将SpringMVC处理不了的请求交给tomcat；静态资源 就可以访问
    			<mvc:default-servlet-handler/>
    	 */
        configurer.enable();
    }

    //拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new FirstInterceptor()).addPathPatterns("/**");
    }

}