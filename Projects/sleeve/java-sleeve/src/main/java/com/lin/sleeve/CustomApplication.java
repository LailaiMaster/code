package com.lin.sleeve;

import com.lin.sleeve.api.v1.BannerController;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Predicate;

/**
 * 模拟 SpringBootApplication
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/18 18:11
 */
@CustomApplication.EnableCustomConfiguration
public class CustomApplication {

    public static void main(String... args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(CustomApplication.class)
                .web(WebApplicationType.NONE)//不启动服务器
                .run(args);

        BannerController bean = context.getBean(BannerController.class);
        System.out.println(bean);
    }

    public static class CustomConfigurationSelector implements ImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{BannerController.class.getName()};
        }

        @Override
        public Predicate<String> getExclusionFilter() {
            return null;
        }

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Documented
    @Import(CustomConfigurationSelector.class)
    public @interface EnableCustomConfiguration {

    }

}

