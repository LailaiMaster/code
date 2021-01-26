package com.lin.sleeve.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lin.sleeve.exception.http.ServerErrorException;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * * 通用的实体转换器【方案3】：某些实体中存在有其他实体字段，希望在存储数据库时将其序列化成 json，为了解决方案 1 的缺点，必须为每种类型指定 AttributeConverter，
 * 但是可以抽象出一个 SuperGenericAndJson，以省去重复的逻辑，子类只需要指定泛型类型即可。
 */
@Converter
public class SuperGenericAndJson<T> implements AttributeConverter<T, String> {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public String convertToDatabaseColumn(T t) {
        System.out.println("convertToDatabaseColumn run by " + this.getClass());
        try {
            return mapper.writeValueAsString(t);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerErrorException(9999);
        }
    }

    @Override
    public T convertToEntityAttribute(String s) {
        System.out.println("convertToEntityAttribute run by " + this.getClass());
        try {
            if (s == null) {
                return null;
            }

            T t = mapper.readValue(s, new TypeReference<T>() {
            });
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerErrorException(9999);
        }
    }

}
