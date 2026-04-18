package org.example.aifoundandlost.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    // 密码加密（注册用）
    public static String encode(String raw) {
        return ENCODER.encode(raw);
    }

    // 密码校验（登录用）
    public static boolean match(String raw, String encoded) {
        return ENCODER.matches(raw, encoded);
    }
}