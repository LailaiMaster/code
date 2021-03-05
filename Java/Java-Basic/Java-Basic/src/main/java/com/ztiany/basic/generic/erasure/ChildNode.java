package com.ztiany.basic.generic.erasure;

import java.lang.reflect.Type;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/26 15:58
 */
public class ChildNode extends Node<Number> {

    @Override
    public void setData(Number number) {
        super.setData(number);
    }

    public static void main(String... args) {
        Type genericSuperclass = ChildNode.class.getGenericSuperclass();
        System.out.println(genericSuperclass);
    }

}
