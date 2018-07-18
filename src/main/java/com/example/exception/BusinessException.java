package com.example.exception;

public class BusinessException  extends RuntimeException {
    public BusinessException(String s) {
        super(s);
    }
}
