package com.atguigu.spring.mock.listeners;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class SpringServletContextListener implements ServletContextListener {

    public SpringServletContextListener() {
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        //1. 获取 Spring 配置文件的名称.
        ServletContext servletContext = arg0.getServletContext();
        String config = servletContext.getInitParameter("configLocation");
        //1. 创建 IOC 容器
        ApplicationContext ctx = new ClassPathXmlApplicationContext(config);
        //2. 把 IOC 容器放在 ServletContext 的一个属性中.
        servletContext.setAttribute("ApplicationContext", ctx);
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
    }

}
