package com.ztiany.annotation.ioc.beans;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/18 15:34
 */
public class PropertiesBean {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    //使用@Value赋值；
    //1、基本数值
    //2、可以写SpEL； #{}
    //3、可以写${}；取出配置文件【properties】中的值（在运行环境变量里面的值）
    @Value("${name1}")
    private String name;

    @Value("${age1}")
    private int age;

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

}
