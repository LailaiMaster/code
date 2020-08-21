package com.atguigu.spring.transaction;

public class BookStockException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BookStockException() {
        super();
    }

    public BookStockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BookStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookStockException(String message) {
        super(message);
    }

    public BookStockException(Throwable cause) {
        super(cause);
    }

}