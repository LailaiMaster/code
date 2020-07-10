package com.ztiany.basic.classloader.timing;

public class SubClass extends SuperClass {

    static {
        System.out.println("SubClass init!");
    }

}