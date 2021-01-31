package com.atguigu.springmvc.crud.handlers.test;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class SpringMVCTestExceptionHandler {

    @ExceptionHandler({ArithmeticException.class})
    public ModelAndView handleArithmeticException(Exception ex) {
        System.out.println("----> called in SpringMVCTestExceptionHandler 出异常了: " + ex);
        ModelAndView mv = new ModelAndView("error");
        mv.addObject("exception", ex);
        return mv;
    }

}
