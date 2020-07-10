package com.ztiany.servlet3;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServletContextSampleListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("ServletContextSampleListener.contextInitialized ServletContext实例化了");

        ServletContext ctx = sce.getServletContext();

        //注册组件  ServletRegistration
        ServletRegistration.Dynamic servlet = ctx.addServlet("testRegisterServlet", new TestRegisterServlet());
        //配置servlet的映射信息
        servlet.addMapping("/testRegister");

        //注册Filter  FilterRegistration
        FilterRegistration.Dynamic filter = ctx.addFilter("testRegisterFilter", TestRegisterFilter.class);
        //配置Filter的映射信息
        filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        //注册Listener
        ctx.addListener(TestRegisterListener.class);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("ServletContextSampleListener.contextDestroyed  ServletContext销毁了");
    }

}