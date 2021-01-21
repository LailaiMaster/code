package com.lin.sleeve.core;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/19 17:25
 */
public class UnifyResponse {

    private int code;
    private String message;
    private String request;

    public UnifyResponse(int code, String message, String request) {
        this.code = code;
        this.message = message;
        this.request = request;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getRequest() {
        return request;
    }

}
