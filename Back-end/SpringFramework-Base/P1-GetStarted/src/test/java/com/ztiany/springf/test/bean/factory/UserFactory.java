package com.ztiany.springf.test.bean.factory;

import com.ztiany.springf.test.bean.domain.User;

public class UserFactory {

    public static User createUser() {
        System.out.println("静态工厂创建User");
        return new User();
    }

    public User createUser2() {
        System.out.println("实例工厂创建User");
        return new User();
    }

}
