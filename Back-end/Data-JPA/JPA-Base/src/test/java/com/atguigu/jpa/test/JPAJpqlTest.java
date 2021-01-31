package com.atguigu.jpa.test;

import com.atguigu.jpa.entities.twoway.many2one.Customer;
import com.atguigu.jpa.entities.twoway.many2one.Order;

import org.hibernate.jpa.QueryHints;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/4/11 22:37
 */
public class JPAJpqlTest {

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

    //可以使用 JPQL 完成 UPDATE 和 DELETE 操作。
    @Test
    public void testExecuteUpdate() {
        String jpql = "UPDATE CUSTOMER_TWO_WAY_MANY_TO_ONE c SET c.lastName = ?1 WHERE c.id = ?2";
        Query query = entityManager.createQuery(jpql).setParameter(1, "aa").setParameter(2, 57);
        query.executeUpdate();
    }

    //使用 jpql 内建的函数
    @Test
    public void testJpqlFunction() {
        String jpql = "SELECT lower(c.email) FROM CUSTOMER_TWO_WAY_MANY_TO_ONE c";
        List<String> emails = entityManager.createQuery(jpql).getResultList();
        System.out.println(emails);
    }

    /*
    子查询
     */
    @Test
    public void testSubQuery() {
        //查询所有 Customer 的 lastName 为 YY 的 Order
        String jpql = "SELECT o FROM ORDER_TWO_WAY_MANY_TO_ONE o  WHERE o.customer = (SELECT c FROM CUSTOMER_TWO_WAY_MANY_TO_ONE c WHERE c.lastName = ?1)";
        Query query = entityManager.createQuery(jpql).setParameter(1, "bb");
        List<Order> orders = query.getResultList();
        System.out.println(orders.size());
    }

    /*
     * JPQL 的关联查询同 HQL 的关联查询。
     */
    @Test
    public void testLeftOuterJoinFetch() {
        //不加 FETCH 得到 Object 类型的结果
        //String jpql1 = "FROM CUSTOMER_TWO_WAY_MANY_TO_ONE c LEFT OUTER JOIN c.orders WHERE c.id = ?1";
        //List resultList = entityManager.createQuery(jpql1).setParameter(1, 51).getResultList();
        //System.out.println(resultList);
        //[[Ljava.lang.Object;@66f0548d, [Ljava.lang.Object;@2e6f610d]
        String jpql2 = "FROM CUSTOMER_TWO_WAY_MANY_TO_ONE c LEFT OUTER JOIN FETCH c.orders WHERE c.id = ?1";
        List<Object[]> result = entityManager.createQuery(jpql2).setParameter(1, 51).getResultList();
        System.out.println(result);
    }

    //查询 order 数量大于 2 的那些 Customer
    @Test
    public void testGroupBy2() {
        String jpql = "SELECT o.customer FROM ORDER_TWO_WAY_MANY_TO_ONE o  GROUP BY o.customer  HAVING count(o.id) >= 2";
        List<Customer> customers = entityManager.createQuery(jpql).getResultList();
        System.out.println(customers);
    }

    @Test
    public void testGroupBy1() {
        String jpql = "SELECT c FROM CUSTOMER_TWO_WAY_MANY_TO_ONE c  GROUP BY c.lastName";
        List<Customer> customers = entityManager.createQuery(jpql).getResultList();
        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }

    @Test
    public void testOrderBy() {
        String jpql = "FROM CUSTOMER_TWO_WAY_MANY_TO_ONE c WHERE c.age > ?1 ORDER BY c.age DESC";
        Query query = entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);
        //占位符的索引是从 1 开始
        query.setParameter(1, 16);
        List<Customer> customers = query.getResultList();
        System.out.println(customers.size());
    }

    //使用 hibernate 的查询缓存，前提是配置文件启用了缓存。【注意区分查询缓存与二级缓存】
    @Test
    public void testQueryCache() {
        String jpql = "FROM CUSTOMER_TWO_WAY_MANY_TO_ONE c WHERE c.age > ?1";
        Query query = entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);//使用缓存

        //占位符的索引是从 1 开始
        query.setParameter(1, 16);
        List<Customer> customers = query.getResultList();
        System.out.println(customers.size());

        query = entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);//使用缓存

        //占位符的索引是从 1 开始
        query.setParameter(1, 16);
        customers = query.getResultList();
        System.out.println(customers.size());
    }

    //createNativeQuery 适用于本地 SQL
    @Test
    public void testNativeQuery() {
        String sql = "SELECT age FROM jpa_customers_two_way_many_to_one WHERE id = ?1";
        Query query = entityManager.createNativeQuery(sql).setParameter(1, 16);
        Object result = query.getSingleResult();
        System.out.println(result);
    }

    //createNamedQuery 适用于在实体类前使用 @NamedQuery 标记的查询语句
    @Test
    public void testNamedQuery() {
        Query query = entityManager.createNamedQuery("testNamedQuery").setParameter(1, 16);
        Customer customer = (Customer) query.getSingleResult();
        System.out.println(customer);
    }

    //默认情况下，若只查询部分属性，则将返回 Object[] 类型的结果，或者 Object[] 类型的 List。
    //返回的 Object 使用很麻烦，此时可以在实体类中创建对应的构造器，然后在 JPQL 语句中利用对应的构造器返回实体类的对象。
    @Test
    public void testPartlyProperties() {
        //String jpql1 = "SELECT c.lastName, c.age FROM CUSTOMER_TWO_WAY_MANY_TO_ONE c WHERE c.id > ?1";
        //List result1 = entityManager.createQuery(jpql1).setParameter(1, 1).getResultList();
        //System.out.println(result1);
        //[[Ljava.lang.Object;@6c6333cd, [Ljava.lang.Object;@3e47a03, [Ljava.lang.Object;@7d9ba6c, [Ljava.lang.Object;@8deb645]

        String jpql2 = "SELECT new CUSTOMER_TWO_WAY_MANY_TO_ONE(c.lastName, c.age) FROM CUSTOMER_TWO_WAY_MANY_TO_ONE c WHERE c.id > ?1";
        List result2 = entityManager.createQuery(jpql2).setParameter(1, 1).getResultList();
        System.out.println(result2);
    }

    @Test
    public void testHelloJPQL() {
        String jpql = "FROM CUSTOMER_TWO_WAY_MANY_TO_ONE c WHERE c.age > ?1";
        Query query = entityManager.createQuery(jpql);
        //占位符的索引是从 1 开始
        query.setParameter(1, 16);
        List<Customer> customers = query.getResultList();
        System.out.println(customers.size());
    }

}
