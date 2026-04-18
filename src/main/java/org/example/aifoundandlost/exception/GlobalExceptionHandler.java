package org.example.aifoundandlost.exception;

import org.example.aifoundandlost.common.Result;
import org.example.aifoundandlost.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result<?> handle(Exception e) {
        return Result.fail(e.getMessage());
    }
}