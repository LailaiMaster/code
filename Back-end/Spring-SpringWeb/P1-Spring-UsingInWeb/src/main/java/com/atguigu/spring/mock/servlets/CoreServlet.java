package com.atguigu.spring.mock.servlets;

import com.atguigu.spring.mock.controller.Controller;

import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TestServlet
 */
public class CoreServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1. 从 application 域对象中得到 IOC 容器的引用
        ServletContext servletContext = getServletContext();
        ApplicationContext ctx = (ApplicationContext) servletContext.getAttribute("ApplicationContext");
        //2. 从 IOC 容器中得到需要的 bean
        Map<String, Controller> beansOfType = ctx.getBeansOfType(Controller.class);
        String uri = request.getRequestURI().replace(request.getContextPath(),"");
        Controller target = null;
        for (Controller controller : beansOfType.values()) {
            if (uri.equals(controller.url())) {
                target = controller;
                break;
            }
        }
        //3. 调用组件，处理请求
        if (target != null) {
            target.handleRequest(request, response);
        }else {
            PrintWriter writer = response.getWriter();
            writer.write("not found");
        }
    }

}