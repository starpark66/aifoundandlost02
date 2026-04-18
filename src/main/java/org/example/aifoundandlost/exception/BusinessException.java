package org.example.aifoundandlost.exception;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 业务异常类（和原项目保持一致）
 */
@Getter
@NoArgsConstructor
public class BusinessException extends RuntimeException {
    private Integer code;
    private String message;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message){
        this(400,message);
    }
}