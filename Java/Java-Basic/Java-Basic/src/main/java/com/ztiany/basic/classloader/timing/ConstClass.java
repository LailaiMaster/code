package com.ztiany.basic.classloader.timing;

public class ConstClass {

    static {
        System.out.println("ConstClass init!");
    }

    public static final String HELLOWORLD = "hello world";

}