package com.lin.sleeve.exception.http;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/19 17:18
 */
public class HttpException extends RuntimeException{

    protected Integer code;
    protected Integer httpStatusCode = 500;

    public Integer getCode() {
        return code;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

}
