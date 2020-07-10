package com.ztiany.servlet3;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/19 16:04
 */
public class TestRegisterFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("TestRegisterFilter.doFilter");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}
