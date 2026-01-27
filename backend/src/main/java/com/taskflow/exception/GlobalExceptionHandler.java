package com.taskflow.exception;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.taskflow.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException e, HttpServletRequest req) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                400,
                "Validation Error",
                "One or more fields are invalid",
                req.getRequestURI());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<?> handleNotFound(TaskNotFoundException e, HttpServletRequest req) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                404,
                "Not found",
                "Task not found",
                req.getRequestURI());

        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<?> handleInvalidStatus(InvalidStatusException e, HttpServletRequest req) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                400,
                "Bad request",
                "Invalid format",
                req.getRequestURI());

        return ResponseEntity.status(400).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception e, HttpServletRequest req) {
        logger.error("Unexpected error: ", e);
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                500,
                "Internal Server Error",
                "Error processing request",
                req.getRequestURI());

        e.printStackTrace();
        return ResponseEntity.status(500).body(error);
    }
}
