package com.atguigu.spring.generic;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    @Test
    @SuppressWarnings("unchecked")
    public void main() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("beans-annotation-generic.xml");

        BaseService<User> userService = (BaseService<User>) ctx.getBean("userService");
        userService.addNew(new User());

        BaseService<Role> roleService = (BaseService<Role>) ctx.getBean("roleService");
        roleService.addNew(new Role());
    }

}
