package com.atguigu.shiro.services;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.session.Session;

import java.util.Date;

public class ShiroService {

    /**真实开发中，我们一般在 Controller 上配置 RequiresRoles*/
    @RequiresRoles({"admin"})
    public void testMethod() {
        System.out.println("testMethod, time: " + new Date());
        Session session = SecurityUtils.getSubject().getSession();
        Object val = session.getAttribute("key");
        System.out.println("Service SessionVal: " + val);
    }

}
 