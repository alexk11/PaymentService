package com.iprody.exception;


public class NoSuchPaymentException extends RuntimeException {
    private String message;

    public NoSuchPaymentException() {
        this.message = "There is no such payment";
    }

    public NoSuchPaymentException(String msg) {
        super(msg);
        this.message = msg;
    }
}
