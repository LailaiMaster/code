package com.ztiany.servlet3;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/19 16:06
 */
public class TestRegisterListener implements /*ServletContextListener*/ServletRequestListener {

    //监听ServletContext销毁（无法启动应用）
    //@Override
    public void contextDestroyed(ServletContextEvent arg0) {
        System.out.println("TestRegisterListener...contextDestroyed...");
    }

    //监听ServletContext启动初始化（无法启动应用）
    //@Override
    public void contextInitialized(ServletContextEvent arg0) {
        ServletContext servletContext = arg0.getServletContext();
        System.out.println("TestRegisterListener...contextInitialized...");
    }

    //@Override
    public void requestDestroyed(ServletRequestEvent sre) {
        System.out.println("TestRegisterListener...requestDestroyed...");
    }

    //@Override
    public void requestInitialized(ServletRequestEvent sre) {
        System.out.println("TestRegisterListener...requestInitialized...");
    }

}
