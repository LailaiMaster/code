package com.lin.sleeve.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 参考 <a href='https://www.ruanyifeng.com/blog/2018/07/json_web_token-tutorial.html'>JSON Web Token 入门教程</a>。
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/26 17:32
 */
@Component
public class JwtToken {

    private static String JWT_KEY;
    private static Integer EXPIRED_TIME;

    public static final String UID_KEY = "uid";
    public static final String SCOPE_KEY = "scope";

    /**
     * 用户的 scope，用于控制权限。
     */
    private static final Integer DEFAULT_SCOPE = 8;

    public static boolean verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_KEY);
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            return false;
        }
        return true;
    }

    @Value("${sleeve.security.jwt-key}")
    public void setJwtKey(String jwtKey) {
        JwtToken.JWT_KEY = jwtKey;
    }

    @Value("${sleeve.security.token-expired-in}")
    public void setExpiredTime(Integer expiredTime) {
        JwtToken.EXPIRED_TIME = expiredTime;
    }

    /**
     * @param uid 用户的 id。
     * @return jwt令牌
     */
    public static String makeToken(Long uid) {
        return getToken(uid, DEFAULT_SCOPE);
    }

    /**
     * @param uid   用户的 id。
     * @param scope 用户的 scope，用于控制权限。
     * @return jwt令牌
     */
    public static String makeToken(Long uid, Integer scope) {
        return getToken(uid, scope);
    }

    private static String getToken(Long uid, Integer scope) {
        Algorithm algorithm = Algorithm.HMAC256(JWT_KEY);
        Map<String, Date> stringDateMap = calculateExpiredIssues();
        return JWT.create()
                .withClaim(UID_KEY, uid)
                .withClaim(SCOPE_KEY, scope)
                .withExpiresAt(stringDateMap.get("expiredTime"))
                .withIssuedAt(stringDateMap.get("now"))
                .sign(algorithm);
    }

    private static Map<String, Date> calculateExpiredIssues() {
        HashMap<String, Date> map = new HashMap<>();
        Calendar calender = Calendar.getInstance();
        Date now = calender.getTime();
        calender.add(Calendar.SECOND, EXPIRED_TIME);
        map.put("now", now);
        map.put("expiredTime", calender.getTime());
        return map;
    }

    public static Optional<Map<String, Claim>> getClaims(String token) {
        Algorithm algorithm = Algorithm.HMAC256(JWT_KEY);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            return Optional.of(jwtVerifier.verify(token).getClaims());
        } catch (JWTVerificationException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

}

