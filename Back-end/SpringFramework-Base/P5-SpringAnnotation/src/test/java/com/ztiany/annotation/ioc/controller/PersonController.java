package com.ztiany.annotation.ioc.controller;

import com.ztiany.annotation.ioc.dao.PersonDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/18 15:40
 */
@Component("Person-Controller")
public class PersonController {

    @Autowired
    private PersonDao mPersonDao;

    public void doSomething() {
        System.out.println("===>mPersonDao = " + mPersonDao);
    }

}
