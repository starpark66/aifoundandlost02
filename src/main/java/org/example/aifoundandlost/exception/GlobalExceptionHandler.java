package org.example.aifoundandlost.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.aifoundandlost.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 精准拦截业务异常（保留自定义错误码）
    @ExceptionHandler(BusinessException.class)
    public Result<?> businessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        // 适配你补全的 Result.fail(code, msg) 方法，保留自定义码
        return Result.fail(e.getCode(), e.getMessage());
    }

    // 2. 兜底拦截其他运行时异常（如空指针、参数绑定失败等）
    @ExceptionHandler(RuntimeException.class)
    public Result<?> runtimeException(RuntimeException e) {
        log.error("运行时异常：{}", e.getMessage());
        return Result.fail(e.getMessage());
    }

    // 3. 系统未知异常（绝不暴露堆栈信息）
    @ExceptionHandler(Exception.class)
    public Result<?> exception(Exception e) {
        log.error("系统异常", e); // 日志打印完整堆栈，方便排查
        return Result.error(); // 返回通用500提示，不暴露细节
    }
}