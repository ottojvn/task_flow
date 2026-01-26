package com.taskflow.exception;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.taskflow.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception e, HttpServletRequest req) {
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
