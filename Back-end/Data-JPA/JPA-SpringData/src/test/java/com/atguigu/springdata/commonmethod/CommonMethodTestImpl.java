package com.atguigu.springdata.commonmethod;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

import javax.persistence.EntityManager;

@NoRepositoryBean
public class CommonMethodTestImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements CommonMethodTest<T, ID> {

    private EntityManager entityManager;

    public CommonMethodTestImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.entityManager = em;
    }

    @Override
    public void method() {
        System.out.println("...METHOD TEST...");
    }

}
