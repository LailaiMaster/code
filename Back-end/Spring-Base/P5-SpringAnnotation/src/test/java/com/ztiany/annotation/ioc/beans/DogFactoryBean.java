package com.ztiany.annotation.ioc.beans;

import org.springframework.beans.factory.FactoryBean;

//创建一个Spring定义的FactoryBean
public class DogFactoryBean implements FactoryBean<Dog> {

    //返回一个Color对象，这个对象会添加到容器中
    @Override
    public Dog getObject() throws Exception {
        System.out.println("===>DogFactoryBean...getObject...");
        return new Dog();
    }

    @Override
    public Class<?> getObjectType() {
        return Dog.class;
    }

    //是单例？
    //true：这个bean是单实例，在容器中保存一份
    //false：多实例，每次获取都会创建一个新的bean；
    @Override
    public boolean isSingleton() {
        return false;
    }

}
