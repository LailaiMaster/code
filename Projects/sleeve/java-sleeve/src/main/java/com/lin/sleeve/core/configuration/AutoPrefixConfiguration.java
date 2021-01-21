package com.lin.sleeve.core.configuration;

import com.lin.sleeve.core.hack.AutoPrefixUrlMapping;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/19 21:49
 */
@Component
public class AutoPrefixConfiguration implements WebMvcRegistrations {

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new AutoPrefixUrlMapping();
    }

}
