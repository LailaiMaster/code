package com.atguigu.spring.mock.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/28 14:18
 */
public interface Controller {

    void handleRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse);

    String url();

}
