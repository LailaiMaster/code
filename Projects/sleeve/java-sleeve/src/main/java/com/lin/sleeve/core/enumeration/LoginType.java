package com.lin.sleeve.core.enumeration;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/26 15:23
 */
public enum LoginType {

    USER_WX(0, "微信登录"),
    USER_Email(1, "邮箱登录");

    private final Integer value;
    private final String description;

    LoginType(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public String toString() {
        return "LoginType{" +
                "value=" + value +
                ", description='" + description + '\'' +
                '}';
    }

}
