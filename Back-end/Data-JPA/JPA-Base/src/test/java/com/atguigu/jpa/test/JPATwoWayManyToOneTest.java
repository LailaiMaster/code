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
 * Date 2020/4/11 21:52
 */
public class JPATwoWayManyToOneTest {

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
    public void testUpdate() {
        Customer customer = entityManager.find(Customer.class, 10);

        customer.getOrders().iterator().next().setOrderName("O-XXX-10");
    }

    //默认情况下, 若删除 1 的一端, 则会先把关联的 n 的一端的外键置空, 然后进行删除.
    //可以通过 @OneToMany 的 cascade 属性来修改默认的删除策略.
    @Test
    public void testManyToOneRemove() {
        Customer customer = entityManager.find(Customer.class, 8);
        entityManager.remove(customer);
    }

    //默认对关联的多的一方使用懒加载的加载策略.
    //可以使用 @ManyToOne 的 fetch 属性来修改默认的加载策略
    @Test
    public void testManyToOneFind() {
        Customer customer = entityManager.find(Customer.class, 9);
        System.out.println(customer.getLastName());

        System.out.println(customer.getOrders().size());
    }

    /*
    若是双向 1-n 的关联关系，执行保存时
        若先保存 n 的一端, 再保存 1 的一端, 默认情况下, 会多出 n 条 UPDATE 语句.
        若先保存 1 的一端, 则会多出 n 条 UPDATE 语句
        在进行双向 1-n 关联关系时，建议使用 n 的一方来维护关联关系，而 1 的一方不维护关联系，这样会有效的减少 SQL 语句（即在 OneToMany 中添加 mappedBy 属性，属性值为 n 端时态的字段名）。
        注意：若在 1 的一端的 @OneToMany 中使用 mappedBy 属性, 则 @OneToMany 端就不能再使用 @JoinColumn 属性了.
     */
    @Test
    public void testManyToOnePersist() {
        Customer customer = new Customer();
        customer.setAge(19);
        customer.setBirth(new Date());
        customer.setCreatedTime(new Date());
        customer.setEmail("cc@163.com");
        customer.setLastName("cc");

        Order order1 = new Order();
        order1.setOrderName("CC-MM-1");
        Order order2 = new Order();
        order2.setOrderName("CC-MM-2");

        //建立关联关系
        customer.getOrders().add(order1);
        customer.getOrders().add(order2);

        order1.setCustomer(customer);
        order2.setCustomer(customer);

        //执行保存操作
        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.persist(customer);
    }

}
