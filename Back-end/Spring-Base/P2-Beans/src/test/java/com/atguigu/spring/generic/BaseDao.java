package com.atguigu.spring.generic;

public class BaseDao<T> {

    public void save(T entity) {
        System.out.println("Save:" + entity);
    }
}
