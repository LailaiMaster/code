package com.ztiany.basic.generic.reflect;

/**
 * 获取泛型类型
 */
public class GetGenericTypeSample {

    public static void main(String[] args) throws Exception {
        //这里是BeanCallBack的子类，并且指定父类BeanCallBack的类型参数为String类型
        new BeanCallBack<String>() {
        }.parseNetworkResponse("");
    }

}
