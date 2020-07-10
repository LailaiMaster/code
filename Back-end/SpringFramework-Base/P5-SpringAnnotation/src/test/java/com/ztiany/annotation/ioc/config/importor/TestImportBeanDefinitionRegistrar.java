package com.ztiany.annotation.ioc.config.importor;

import com.ztiany.annotation.ioc.beans.Fish;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class TestImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    /*
     * AnnotationMetadata：当前类的注解信息
     * BeanDefinitionRegistry：BeanDefinition注册类；
     * 所有需要添加到容器中的bean；调用 BeanDefinitionRegistry.registerBeanDefinition手工注册进来
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        boolean hasCatDefinition = registry.containsBeanDefinition("com.ztiany.annotation.ioc.beans.Cat");
        if (hasCatDefinition) {
            RootBeanDefinition beanDefinition = new RootBeanDefinition(Fish.class);
            //注册一个Bean，指定bean名
            registry.registerBeanDefinition("fish", beanDefinition);
        }
    }

}
