package com.lin.sleeve.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lin.sleeve.exception.ExceptionCodes;
import com.lin.sleeve.exception.http.ServerErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 通用的实体转换器【方案2】：某些实体中存在有其他实体字段，希望在存储数据库时将其序列化成 json，于是手动添加 Setter 和 Getter 方法，并利用 GenericAndJson 工具类手动转换。
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/25 13:53
 */
@Component
public class GenericAndJson {

    private static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        GenericAndJson.objectMapper = objectMapper;
    }

    public static String objectToJson(Object object) {
        try {
            return GenericAndJson.objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();//记录日志
            throw new ServerErrorException(ExceptionCodes.C_9999);
        }
    }

    public static <T> T jsonToObject(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ServerErrorException(ExceptionCodes.C_9999);
        }
    }

    public static <T> T jsonToObject(String json, TypeReference<T> tTypeReference) {
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, tTypeReference);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ServerErrorException(ExceptionCodes.C_9999);
        }
    }

    /**
     * 改方法错误，在改方法内，无法获取到 T 的实际类型。
     */
    @Deprecated
    public static <T> List<T> jsonToList(String json) {
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<T>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ServerErrorException(ExceptionCodes.C_9999);
        }
    }

}
