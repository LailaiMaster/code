package com.atguigu.filter;


import com.atguigu.util.LogUtils;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SetAllCharacterEncodingFilter implements Filter {

    private FilterConfig filterConfig;
    private ServletContext mServletContext;

    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        mServletContext = filterConfig.getServletContext();
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        LogUtils.info("EncodeInfo: =================================================================");
        LogUtils.info("Charset.defaultCharset().name(): " + Charset.defaultCharset().name());
        LogUtils.info("file.encoding: " + System.getProperty("file.encoding"));
        LogUtils.info("EncodeInfo: =================================================================");
        LogUtils.info("");
        LogUtils.info("Server: =================================================================");
        System.out.println("getMajorVersion: " + mServletContext.getMajorVersion());//服务器支持的Servlet主版本号
        System.out.println("getMinorVersion: " + mServletContext.getMinorVersion());//服务器支持的Servlet次版本号
        LogUtils.info("Server: =================================================================");

        HttpServletRequest request;
        HttpServletResponse response;

        try {
            request = (HttpServletRequest) req;
            response = (HttpServletResponse) resp;
        } catch (Exception e) {
            throw new RuntimeException("non http request");
        }


        String encoding = filterConfig.getInitParameter("encoding");

        if (encoding == null) {
            encoding = "UTF-8";
        }

        request.setCharacterEncoding(encoding);//POST方式，同时也可以给后面过滤器或Servlet使用
        response.setCharacterEncoding(encoding);//给后面过滤器或Servlet使用

        response.setContentType("text/html;charset=" + encoding);//输出字符流使用的编码，告知客户端用什么编码解码

        chain.doFilter(request, response);
    }

    public void destroy() {

    }

}
