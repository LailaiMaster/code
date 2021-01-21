package com.lin.sleeve.core.hack;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/19 21:45
 */
@Component
public class AutoPrefixUrlMapping extends RequestMappingHandlerMapping {

    @Value(("${sleeve.api-package}"))
    private String apkPackagePath;

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo mappingInfo = super.getMappingForMethod(method, handlerType);
        if (mappingInfo != null && handlerType.getPackage().getName().contains(apkPackagePath)) {
            String prefix = getPrefix(handlerType);
            mappingInfo = RequestMappingInfo.paths(prefix).build().combine(mappingInfo);
        }
        return mappingInfo;
    }

    private String getPrefix(Class<?> handlerType) {
        String name = handlerType.getPackage().getName();
        String dotPath = name.replaceAll(this.apkPackagePath, "");
        return dotPath.replace(".", "/");
    }

}
