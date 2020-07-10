package com.ztiany.annotation.ioc.beans;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/18 15:29
 */
@Component
public class TestBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("===>LifecyclePerson postProcessBeforeInitialization bean = " + bean + ", beanName = " + beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("===>LifecyclePerson postProcessAfterInitialization bean = " + bean + ", beanName = " + beanName);
        return bean;
    }

}
