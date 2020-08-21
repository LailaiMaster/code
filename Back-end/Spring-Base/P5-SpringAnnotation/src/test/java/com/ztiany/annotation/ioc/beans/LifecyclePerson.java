package com.ztiany.annotation.ioc.beans;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/18 14:59
 */
public class LifecyclePerson implements InitializingBean, DisposableBean {

    public void initMethod() {
        System.out.println("===>LifecyclePerson initMethod");
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("===>LifecyclePerson postConstruct");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("===>LifecyclePerson preDestroy");
    }

    public void destroyMethod() {
        System.out.println("===>LifecyclePerson destroyMethod");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("===>LifecyclePerson afterPropertiesSet");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("===>LifecyclePerson destroy");
    }

}
