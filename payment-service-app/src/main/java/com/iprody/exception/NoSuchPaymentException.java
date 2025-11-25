package com.iprody.exception;


public class NoSuchPaymentException extends RuntimeException {
    private String message;

    public NoSuchPaymentException() {}

    public NoSuchPaymentException(String msg) {
        super(msg);
        this.message = msg;
    }
}
