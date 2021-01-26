package com.lin.sleeve.exception.http;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/23 23:04
 */
public class ServerErrorException  extends HttpException{

    private static final int SERVER_ERROR = 9999;

    public ServerErrorException(int code) {
        this.code = code;
        this.httpStatusCode = SERVER_ERROR;
    }

}
