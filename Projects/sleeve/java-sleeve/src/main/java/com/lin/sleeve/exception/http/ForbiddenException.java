package com.lin.sleeve.exception.http;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/19 17:18
 */
public class ForbiddenException extends HttpException{

    public ForbiddenException(int code) {
        this.httpStatusCode = 403;
        this.code = code;
    }

}
