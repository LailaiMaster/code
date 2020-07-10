package com.atguigu.jpa.dao;

import com.atguigu.jpa.spring.entities.Person;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class PersonDao {

    //如何获取到和当前事务关联的 EntityManager 对象呢 ? 通过 @PersistenceContext 注解来标记成员变量。
    @PersistenceContext
    private EntityManager entityManager;

    public void save(Person person) {
        entityManager.persist(person);
    }

}
