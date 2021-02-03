package com.lin.sleeve.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lin.sleeve.exception.ExceptionCodes;
import com.lin.sleeve.exception.http.ServerErrorException;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 作用同 MapAndJson，用于处理列表字段，列表类型必须声明为 List<Object>，运行时，Object 的实际的类型是 Map。
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/23 23:00
 */
@Converter
public class ListAndJson implements AttributeConverter<List<Object>, String> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(List<Object> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            e.printStackTrace();//记录日志
            throw new ServerErrorException(ExceptionCodes.C_9999);
        }
    }

    @Override
    public List<Object> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, List.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ServerErrorException(ExceptionCodes.C_9999);
        }
    }

}
