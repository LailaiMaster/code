package com.atguigu.spring.mock.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/28 14:16
 */
public class BController implements Controller{

    @Override
    public void handleRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        System.out.println("BController do handleRequest");
        try {
            httpResponse.getWriter().write("BController");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String url() {
        return "/b";
    }

}
