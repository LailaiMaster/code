package com.ztiany.serbase.servlets.response;

import com.ztiany.serbase.utils.LogUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理注册的 Servlet，演示中文编码问题。
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 18.4.15 23:49
 */
public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        @SuppressWarnings("unchecked")
        Map<String, String[]> parameterMap = request.getParameterMap();
        parameterMap.forEach((key, value) -> LogUtils.LOG.info("登录参数：" + key + " = " + Arrays.toString(value)));
        LogUtils.LOG.debug(Charset.defaultCharset().toString());
        System.out.println("defaultCharset = " + Charset.defaultCharset().displayName());
        System.out.println("file.encoding = " + System.getProperty("file.encoding"));
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("注册成功，20秒后自动转向登录页面");
        response.setHeader("Refresh", "20;URL=http://www.google.cn");
    }

}