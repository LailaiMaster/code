package com.lin.sleeve.core.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/19 18:11
 */
@ConfigurationProperties(prefix = "lin")
@PropertySource("classpath:config/exception-code.properties")
@Component
public class ExceptionCodeConfiguration {

    private Map<Integer, String> codes = new HashMap<>();

    public void setCodes(Map<Integer, String> codes) {
        this.codes = codes;
    }

    public String getMessage(int code) {
        return codes.get(code);
    }

}
