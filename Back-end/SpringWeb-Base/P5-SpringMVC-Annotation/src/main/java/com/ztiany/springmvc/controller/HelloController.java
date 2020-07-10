package com.ztiany.springmvc.controller;


import com.ztiany.springmvc.service.HelloService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class HelloController {

    @Autowired
    private HelloService helloService;

    @ResponseBody
    @RequestMapping("/hello")
    public String hello() {
        return helloService.sayHello("tomcat..");
    }

    //  /WEB-INF/views/success.jsp
    @RequestMapping("/success")
    public String success() {
        return "success";
    }

}
