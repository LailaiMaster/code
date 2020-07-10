package com.ztiany.register.utils;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;

public class MD5Util {

    public static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");//指定加密算法
            byte[] encrypted = md.digest(str.getBytes());//不一定对应着字符串
            return Base64.encodeBase64String(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
