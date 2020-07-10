package com.atguigu.spring.auto;

import com.atguigu.spring.getstarted.User;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class MyBeanPostProcessor implements BeanPostProcessor {

    //该方法在 init 方法之后被调用
    @Override
    public Object postProcessAfterInitialization(Object arg0, String arg1)
            throws BeansException {
        if (arg1.equals("boy")) {
            System.out.println("postProcessAfterInitialization..." + arg0 + "," + arg1);
            User user = (User) arg0;
            user.setUserName("李大齐");
        }
        return arg0;
    }

    //该方法在 init 方法之前被调用

    /**
     * @param arg0: 实际要返回的对象
     * @param arg1: bean 的 id 值
     */
    @Override
    public Object postProcessBeforeInitialization(Object arg0, String arg1) throws BeansException {
        if (arg1.equals("boy")) {
            System.out.println("postProcessBeforeInitialization..." + arg0 + "," + arg1);
        }
        return arg0;
    }

}
