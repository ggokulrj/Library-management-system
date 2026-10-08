package com.library.exception;

// Thrown when a library rule is broken (e.g. no copies available)
public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}
