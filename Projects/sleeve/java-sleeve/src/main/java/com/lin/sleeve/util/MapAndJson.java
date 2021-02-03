package com.lin.sleeve.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lin.sleeve.exception.ExceptionCodes;
import com.lin.sleeve.exception.http.ServerErrorException;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 通用的实体转换器【方案1】：某些实体中存在有其他实体字段，希望在存储数据库时将其序列化成 json，可以在字段上注解  @Convert 并指定 AttributeConverter。
 * 这个实现是通用的实现，可以处理所有类型，缺点是必须将实体字段的类型声明为 Map。
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/23 23:00
 */
@Converter
public class MapAndJson implements AttributeConverter<Map<String, Object>, String> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            e.printStackTrace();//记录日志
            throw new ServerErrorException(ExceptionCodes.C_9999);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, HashMap.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ServerErrorException(ExceptionCodes.C_9999);
        }
    }

}
