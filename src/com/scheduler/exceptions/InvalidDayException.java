package com.scheduler.exceptions;

public class InvalidDayException extends Exception {
    public InvalidDayException(String errorMessage) {
        super(errorMessage);
    }
}
