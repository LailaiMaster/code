package com.ztiany.basic.generic.erasure;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/26 17:49
 */
public class Test {

    /*通过反射可以拿到 list1 上的类型信息*/
    private List<String> list1 = new ArrayList<>();

    /*通过反射可以拿到 list2 上的类型信息*/
    private List<String> list2 = new ArrayList<String>() {
    };

    /*通过反射不可以拿到 list3 上的类型信息*/
    private List list3 = new ArrayList<String>();

    public static void main(String... args) {
        printTypeInfo("list1");
        printTypeInfo("list2");
        printTypeInfo("list3");
    }

    private static void printTypeInfo(String name) {
        System.out.println(name + "：--------------------------------------");
        try {
            Field field = Test.class.getDeclaredField(name);
            Type genericType = field.getGenericType();
            System.out.println(genericType);
            System.out.println(((ParameterizedType) genericType).getRawType());
            System.out.println(Arrays.toString(((ParameterizedType) genericType).getActualTypeArguments()));
            System.out.println(((ParameterizedType) genericType).getOwnerType());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}
