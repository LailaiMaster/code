package com.lin.sleeve.core;

import com.lin.sleeve.model.User;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/3 20:00
 */
public class LocalUser {

    private final static ThreadLocal<Map<String, Object>> THREAD_LOCAL = new ThreadLocal<>();

    public static User getUser() {
        return (User) THREAD_LOCAL.get().get("user");
    }

    public static Integer getScope() {
        return (Integer) THREAD_LOCAL.get().get("scope");
    }

    public static void setUser(User user, Integer scope) {
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("scope", scope);
        THREAD_LOCAL.set(map);
    }

    public static void clear() {
        THREAD_LOCAL.remove();
    }

}
