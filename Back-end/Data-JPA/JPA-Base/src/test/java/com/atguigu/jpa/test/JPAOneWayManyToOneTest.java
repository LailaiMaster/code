package com.atguigu.jpa.test;


import com.atguigu.jpa.entities.twoway.many2one.Customer;
import com.atguigu.jpa.entities.twoway.many2one.Order;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/11 20:30
 */
public class JPAOneWayManyToOneTest {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private EntityTransaction transaction;

    @Before
    public void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("jpa-base");
        entityManager = entityManagerFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        transaction.begin();
    }

    @After
    public void destroy() {
        transaction.commit();
        entityManager.close();
        entityManagerFactory.close();
    }

    @Test
    public void testManyToOneUpdate() {
        Order order = entityManager.find(Order.class, 2);
        order.getCustomer().setLastName("FFF");
    }

    //多对一的查询，不能直接删除 1 的一端，因为有外键约束。
    @Test
    public void testManyToOneRemove() {
        // Order 随便删
        // Order order = entityManager.find(Order.class, 1);
        // entityManager.remove(order);
        Customer customer = entityManager.find(Customer.class, 7);
        entityManager.remove(customer);
    }

    //默认情况下，多对一的查询，使用左外连接的方式来获取 n 的一端的对象和其关联的 1 的一端的对象。
    //可使用 @ManyToOne 的 fetch 属性来修改默认的关联属性的加载策略
    @Test
    public void testManyToOneFind() {
        Order order = entityManager.find(Order.class, 1);
        System.out.println(order.getOrderName());
        System.out.println(order.getCustomer().getLastName());
    }

    /*
     * 保存多对一时，建议先保存 1 的一端，后保存 n 的一端，这样不会多出额外的 UPDATE 语句。
     */
    @Test
    public void testManyToOnePersist() {
        Customer customer = new Customer();
        customer.setAge(18);
        customer.setBirth(new Date());
        customer.setCreatedTime(new Date());
        customer.setEmail("gg@163.com");
        customer.setLastName("GG");

        Order order1 = new Order();
        order1.setOrderName("G-GG-1");

        Order order2 = new Order();
        order2.setOrderName("G-GG-2");

        //设置关联关系
        order1.setCustomer(customer);
        order2.setCustomer(customer);

        //执行保存操作（先保存order，再保存customer也可以，但是会多出维护外键的数据）
        entityManager.persist(customer);
        entityManager.persist(order1);
        entityManager.persist(order2);
    }

}
