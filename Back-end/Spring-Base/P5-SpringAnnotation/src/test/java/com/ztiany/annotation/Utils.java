package com.ztiany.annotation;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/18 14:21
 */
public class Utils {

    public static void printBeansName(String tag, AnnotationConfigApplicationContext applicationContext) {
        System.out.println();
        System.out.println(tag + "-----------------------------printBeansName--------------------------------start");
        String[] definitionNames = applicationContext.getBeanDefinitionNames();
        for (String name : definitionNames) {
            System.out.println(name);
        }
        System.out.println(tag + "-----------------------------printBeansName--------------------------------end");
        System.out.println();
    }

    public static void printBeansValue(String tag, AnnotationConfigApplicationContext applicationContext) {
        System.out.println();
        System.out.println(tag + "-----------------------------printBeansValue--------------------------------start");
        String[] definitionNames = applicationContext.getBeanDefinitionNames();
        for (String name : definitionNames) {
            System.out.println(name + " ==> " + applicationContext.getBean(name));
        }
        System.out.println(tag + "-----------------------------printBeansValue--------------------------------end");
        System.out.println();
    }

}
