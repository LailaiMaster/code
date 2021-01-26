package com.lin.sleeve.exception.http;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/26 17:10
 */
public class ParameterException extends HttpException {

    public ParameterException(int code) {
        this.httpStatusCode = 400;
        this.code = code;
    }

}
