package com.atguigu.jpa.test;

import com.atguigu.jpa.entities.base.Customer;

import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/11 20:31
 */
public class JPAUsageTest {

    /**
     * 基础流程演示
     */
    @Test
    public void base() {
        //1. 创建 EntityManagerFactory
        String persistenceUnitName = "jpa-base";
        Map<String, Object> properties = new HashMap<>();
        //配置属性
        properties.put("hibernate.show_sql", true);
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName, properties);

        //2. 创建 EntityManager. 类似于 Hibernate 的 SessionFactory
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //3. 开启事务
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        //4. 进行持久化操作
        Customer customer = new Customer();
        customer.setAge(12);
        customer.setEmail("tom@atguigu.com");
        customer.setLastName("Tom");
        customer.setBirth(new Date());
        customer.setCreatedTime(new Date());

        entityManager.persist(customer);

        //5. 提交事务
        transaction.commit();

        //6. 关闭 EntityManager
        entityManager.close();

        //7. 关闭 EntityManagerFactory
        entityManagerFactory.close();
    }

}
