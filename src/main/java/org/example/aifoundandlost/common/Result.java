package org.example.aifoundandlost.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    // 原有方法（保留）
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "成功", data);
    }

    public static <T> Result<T> success() {
        return new Result<>(200, "成功", null);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<>(400, msg, null);
    }

    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "未登录", null);
    }

    // 新增：支持自定义错误码+提示的fail方法（适配BusinessException）
    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    // 新增：服务器异常方法（替换你代码中的serverError）
    public static <T> Result<T> error() {
        return new Result<>(500, "服务器繁忙，请稍后重试", null);
    }

    // 新增：支持自定义提示的服务器异常方法
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }
}