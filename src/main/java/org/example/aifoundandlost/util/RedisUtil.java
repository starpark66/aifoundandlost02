package org.example.aifoundandlost.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis工具类，封装缓存操作，增加防穿透/击穿处理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    // 空值占位符（防缓存穿透）
    private static final String NULL_VALUE = "NULL";

    /**
     * 获取缓存（防穿透：空值也缓存）
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            Object value = ops.get(key);
            if (value == null) {
                return null;
            }
            // 空值占位符，返回null
            if (NULL_VALUE.equals(value)) {
                return null;
            }
            return clazz.cast(value);
        } catch (Exception e) {
            log.error("Redis获取缓存失败，key:{}", key, e);
            // 缓存异常不影响主流程，返回null
            return null;
        }
    }

    /**
     * 设置缓存（防击穿：设置过期时间）
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            // 空值用占位符缓存
            Object cacheValue = value == null ? NULL_VALUE : value;
            ops.set(key, cacheValue, timeout, unit);
        } catch (Exception e) {
            log.error("Redis设置缓存失败，key:{}", key, e);
            // 缓存异常不抛业务异常，仅日志记录
        }
    }

    /**
     * 删除缓存
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Redis删除缓存失败，key:{}", key, e);
        }
    }
}