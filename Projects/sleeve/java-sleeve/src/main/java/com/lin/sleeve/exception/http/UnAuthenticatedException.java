package com.lin.sleeve.exception.http;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/26 18:47
 */
public class UnAuthenticatedException extends HttpException {

    public UnAuthenticatedException(int code) {
        this.httpStatusCode = 401;
        this.code = code;
    }

}
