package org.example.aifoundandlost.util;

import org.example.aifoundandlost.exception.BusinessException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * 通用参数校验工具类，统一抛业务异常
 */
public class ValidateUtil {

    /**
     * 校验字符串非空
     */
    public static void notBlank(String str, String message) {
        if (!StringUtils.hasText(str)) {
            throw new BusinessException(400, message);
        }
    }

    /**
     * 校验对象非空
     */
    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new BusinessException(400, message);
        }
    }

    /**
     * 校验集合非空
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException(400, message);
        }
    }

    /**
     * 校验Map 非空
     */
    public static void notEmpty(Map<?, ?> map, String message) {
        if (CollectionUtils.isEmpty(map)) {
            throw new BusinessException(400, message);
        }
    }

    /**
     * 校验布尔条件，不满足则抛异常
     */
    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new BusinessException(400, message);
        }
    }
}