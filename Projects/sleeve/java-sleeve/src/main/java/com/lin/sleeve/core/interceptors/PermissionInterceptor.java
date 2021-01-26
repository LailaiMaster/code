package com.lin.sleeve.core.interceptors;

import com.auth0.jwt.interfaces.Claim;
import com.lin.sleeve.exception.http.ForbiddenException;
import com.lin.sleeve.exception.http.UnAuthenticatedException;
import com.lin.sleeve.util.JwtToken;

import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.lin.sleeve.exception.ExceptionCodes.UN_AUTHENTICATED_ERROR;
import static com.lin.sleeve.exception.ExceptionCodes.UN_AUTHORIZED_ERROR;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/26 18:31
 */
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("PermissionInterceptor preHandle with: " + request.getRequestURL());

        Optional<ScopeLevel> scopeLevel = getScopeLevel(handler);
        if (!scopeLevel.isPresent()) {
            return true;
        }
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization) || !authorization.startsWith("Bearer")) {
            throw new UnAuthenticatedException(UN_AUTHENTICATED_ERROR);
        }
        String token = authorization.split(" ")[1];
        Optional<Map<String, Claim>> optionalMap = JwtToken.getClaims(token);
        Map<String, Claim> claimMap = optionalMap.orElseThrow(() -> new UnAuthenticatedException(UN_AUTHENTICATED_ERROR));
        return hasPermission(scopeLevel.get(), claimMap);
    }

    private boolean hasPermission(ScopeLevel scopeLevel, Map<String, Claim> claimMap) {
        int level = scopeLevel.value();
        Integer scope = claimMap.get("scope").asInt();
        System.out.printf("hasPermission method level = %d, user scope = %d%n", level, scope);
        if (level > scope) {
            throw new ForbiddenException(UN_AUTHORIZED_ERROR);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    private Optional<ScopeLevel> getScopeLevel(Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            ScopeLevel annotation = handlerMethod.getMethod().getAnnotation(ScopeLevel.class);
            if (annotation == null) {
                return Optional.empty();
            }
            return Optional.of(annotation);
        }
        return Optional.empty();
    }

}
