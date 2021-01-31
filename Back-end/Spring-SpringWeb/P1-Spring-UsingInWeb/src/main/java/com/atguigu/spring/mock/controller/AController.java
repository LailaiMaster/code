package com.atguigu.spring.mock.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/28 14:16
 */
public class AController implements Controller{

    @Override
    public void handleRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        System.out.println("AController do handleRequest");
        try {
            httpResponse.getWriter().write("AController");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String url() {
        return "/a";
    }

}
