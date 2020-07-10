package com.ztiany.annotation.ioc;

import com.ztiany.annotation.Utils;
import com.ztiany.annotation.ioc.config.AutowiredConfig;
import com.ztiany.annotation.ioc.config.ConditionConfig;
import com.ztiany.annotation.ioc.config.ImportConfig;
import com.ztiany.annotation.ioc.config.IocBaseConfig;
import com.ztiany.annotation.ioc.config.IocScanFilterConfig;
import com.ztiany.annotation.ioc.config.LifecycleConfig;
import com.ztiany.annotation.ioc.config.ProfileConfig;
import com.ztiany.annotation.ioc.config.PropertiesAssignmentConfig;
import com.ztiany.annotation.ioc.config.ScopeConfig;
import com.ztiany.annotation.ioc.controller.PersonController;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/18 14:11
 */
public class IocMainTest {

    @Test
    public void testBaseConfig() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(IocBaseConfig.class);
        Utils.printBeansName("testBaseConfig", applicationContext);
        applicationContext.close();
    }

    @Test
    public void testScanConfig() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(IocScanFilterConfig.class);
        Utils.printBeansName("testScanConfig", applicationContext);
        applicationContext.close();
    }

    @Test
    public void testImportConfig() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ImportConfig.class);
        Utils.printBeansValue("testImportConfig", applicationContext);
        applicationContext.close();
    }

    @Test
    public void testConditionConfig() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ConditionConfig.class);
        Utils.printBeansValue("testConditionConfig", applicationContext);
        applicationContext.close();
    }

    @Test
    public void testScopeConfig() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ScopeConfig.class);
        Utils.printBeansValue("testScopeConfig", applicationContext);
        applicationContext.close();
    }

    @Test
    public void testLifecycleConfig() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(LifecycleConfig.class);
        Utils.printBeansValue("testLifecycleConfig", applicationContext);
        applicationContext.close();
    }

    @Test
    public void testPropertiesAssignmentConfig() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(PropertiesAssignmentConfig.class);
        Utils.printBeansValue("testPropertiesAssignmentConfig", applicationContext);
        applicationContext.close();
    }

    @Test
    public void testAutowiredConfig() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AutowiredConfig.class);
        Utils.printBeansValue("testAutowiredConfig", applicationContext);
        applicationContext.getBean(PersonController.class).doSomething();
        applicationContext.close();
    }

    /*
    -Dspring.profiles.active=test
     */
    @Test
    public void testProfileConfig1() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ProfileConfig.class);
        Utils.printBeansValue("testProfileConfig", applicationContext);
        applicationContext.close();
    }

    @Test
    public void testProfileConfig2() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.getEnvironment().setActiveProfiles("prod");
        applicationContext.register(ProfileConfig.class);
        applicationContext.refresh();
        Utils.printBeansValue("testProfileConfig", applicationContext);
        applicationContext.close();
    }

}