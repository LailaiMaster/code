package com.atguigu.jpa.test;

import com.atguigu.jpa.entities.twoway.one2one.Department;
import com.atguigu.jpa.entities.twoway.one2one.Manager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/11 22:17
 */
public class JPATwoWayOneToOneTest {

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

    /*
    默认情况下，若获取不维护关联关系的一方，则也会通过左外连接获取其关联的对象，但可以通过 @OntToOne 的 fetch 属性来修改加载策略。
    但依然会再发送 SQL 语句来初始化其关联的对象，这说明在不维护关联关系的一方，不建议修改 fetch 属性.。
     */
    @Test
    public void testOneToOneFind2() {
        Manager mgr = entityManager.find(Manager.class, 29);
        System.out.println(mgr.getMgrName());
        System.out.println(mgr.getDept().getClass().getName());
    }

    /*
    默认情况下，若获取维护关联关系的一方，则会通过左外连接获取其关联的对象。但可以通过 @OntToOne 的 fetch 属性来修改加载策略.
     */
    @Test
    public void testOneToOneFind() {
        Department dept = entityManager.find(Department.class, 29);
        System.out.println(dept.getDeptName());
        System.out.println(dept.getMgr().getClass());
        //这里再次触发sql查询
        System.out.println(dept.getMgr());
    }

    //双向 1-1 的关联关系,，建议先保存不维护关联关系的一方,，即没有外键的一方，这样不会多出 UPDATE 语句。
    @Test
    public void testOneToOnePersistence() {
        Manager mgr = new Manager();
        mgr.setMgrName("N-BB");

        Department dept = new Department();
        dept.setDeptName("A-BB");

        //设置关联关系
        mgr.setDept(dept);
        dept.setMgr(mgr);
        //执行保存操作
        entityManager.persist(mgr);
        entityManager.persist(dept);
    }

}
