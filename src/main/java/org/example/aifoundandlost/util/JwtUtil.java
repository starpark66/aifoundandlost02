package org.example.aifoundandlost.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    // 固定密钥（不配置化，不碰pom/yml，纯代码）
    private static final String SECRET = "aifoundandlost_2026_32bit_secret_key";
    private static final long EXPIRE = 7 * 24 * 60 * 60 * 1000L;
    private final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // 生成Token
    public String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析Token（健壮异常处理）
    public Long getUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("userId", Long.class);
        } catch (ExpiredJwtException ex) {
            throw new RuntimeException("登录已过期");
        } catch (JwtException ex) {
            throw new RuntimeException("无效令牌");
        }
    }
}