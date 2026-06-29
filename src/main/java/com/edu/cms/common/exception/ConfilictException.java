package com.edu.cms.common.exception;

import org.springframework.http.HttpStatus;

public class ConfilictException extends  AppException {
    public ConfilictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
