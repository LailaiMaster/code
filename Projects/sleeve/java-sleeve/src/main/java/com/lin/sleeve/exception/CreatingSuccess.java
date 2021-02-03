package com.lin.sleeve.exception;

import com.lin.sleeve.exception.http.HttpException;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/3 21:11
 */
public class CreatingSuccess extends HttpException {

    public CreatingSuccess(int code) {
        this.httpStatusCode = 201;
        this.code = code;
    }

}
