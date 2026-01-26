package com.taskflow.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super("Task with ID " + id + " not found.");
    }

    public TaskNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskNotFoundException(Long id, Throwable cause) {
        super("Task with ID " + id + " not found.", cause);
    }
}
