package com.ztiany.annotation.ioc.config.importor;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

//自定义逻辑返回需要导入的组件
public class TestImportSelector implements ImportSelector {

    //返回值，就是到导入到容器中的组件全类名
    //AnnotationMetadata：当前标注@Import注解的类的所有注解信息
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        //方法不要返回null值
        return new String[]{"com.ztiany.annotation.ioc.beans.Cat"};
    }

}
