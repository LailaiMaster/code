package com.atguigu.spring.hibernate.exceptions;

public class UserAccountException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public UserAccountException() {
        super();
    }

    public UserAccountException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UserAccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAccountException(String message) {
        super(message);
    }

    public UserAccountException(Throwable cause) {
        super(cause);
    }

}