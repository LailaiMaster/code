package com.lin.sleeve.exception.http;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/19 17:18
 */
public class NotFoundException extends HttpException {

    public NotFoundException(int code) {
        this.httpStatusCode = 404;
        this.code = code;
    }

}
