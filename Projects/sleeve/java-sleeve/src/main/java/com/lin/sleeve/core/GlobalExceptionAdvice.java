package com.lin.sleeve.core;

import com.lin.sleeve.core.configuration.ExceptionCodeConfiguration;
import com.lin.sleeve.exception.http.HttpException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/19 16:48
 */
@ControllerAdvice
public class GlobalExceptionAdvice {

    @Autowired
    private ExceptionCodeConfiguration mExceptionCodeConfiguration;

    private static final int SERVER_ERROR = 9999;
    private static final int ARGUMENTS_ERROR = 10001;

    /**
     * 未知错误的处理入口
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public UnifyResponse handleException(HttpServletRequest request, Exception exception) {
        System.out.println("------------------------------------------------handleException------------------------------------------------>");
        exception.printStackTrace();
        return new UnifyResponse(SERVER_ERROR, mExceptionCodeConfiguration.getMessage(SERVER_ERROR), request.getMethod() + " " + request.getRequestURI());
    }

    /**
     * HTTP 错误的处理入口
     */
    @ExceptionHandler(HttpException.class)
    public ResponseEntity<UnifyResponse> handleHttpException(HttpServletRequest request, HttpException exception) {
        System.out.println("------------------------------------------------handleHttpException------------------------------------------------>");
        exception.printStackTrace();

        UnifyResponse unifyResponse = new UnifyResponse(exception.getCode(), mExceptionCodeConfiguration.getMessage(exception.getCode()), request.getMethod() + " " + request.getRequestURI());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpStatus httpStatus = HttpStatus.resolve(exception.getHttpStatusCode());

        return new ResponseEntity<>(unifyResponse, httpHeaders, httpStatus);
    }

    /**
     * 参数校验错误的处理入口
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public UnifyResponse handleArgumentsException(HttpServletRequest request, MethodArgumentNotValidException exception) {
        System.out.println("------------------------------------------------handleArgumentsException------------------------------------------------>");
        String message = this.formatAllErrorMessages(exception.getBindingResult().getAllErrors());
        return new UnifyResponse(ARGUMENTS_ERROR, message, request.getMethod() + " " + request.getRequestURI());
    }

    /**
     * 参数校验错误的处理入口
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public UnifyResponse handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException exception) {
        System.out.println("------------------------------------------------handleConstraintViolationException------------------------------------------------>");
        return new UnifyResponse(ARGUMENTS_ERROR, exception.getMessage(), request.getMethod() + " " + request.getRequestURI());
    }

    private String formatAllErrorMessages(List<ObjectError> allErrors) {
        StringBuilder sb = new StringBuilder();
        allErrors.forEach(objectError -> {
            sb.append(objectError.getDefaultMessage()).append(";");
        });
        return sb.toString();
    }

}
