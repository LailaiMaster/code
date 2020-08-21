package com.atguigu.spring.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class UserAction {

    @Autowired
    private UserService mUserService;

    public void execute() {
        System.out.println("接受请求");
        mUserService.addNew();
    }

}
