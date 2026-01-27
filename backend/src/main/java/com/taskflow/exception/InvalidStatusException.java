package com.taskflow.exception;

import com.taskflow.entity.Status;

public class InvalidStatusException extends RuntimeException {
    public InvalidStatusException(Status status) {
        super("Status " + status + " is invalid");
    }

    public InvalidStatusException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public InvalidStatusException(Status status, Throwable throwable) {
        super("Status " + status + " is invalid", throwable);
    }
}
