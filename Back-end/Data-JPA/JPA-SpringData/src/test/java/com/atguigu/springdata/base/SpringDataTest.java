package com.atguigu.springdata.base;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.sql.DataSource;

public class SpringDataTest {

    private ApplicationContext ctx;
    private PersonRepository mPersonRepository;
    private PersonService personService;

    {
        ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        mPersonRepository = ctx.getBean(PersonRepository.class);
        personService = ctx.getBean(PersonService.class);
    }

    @Test
    public void testCustomRepositoryMethod() {
        mPersonRepository.test();
    }

    /*
     * 目标：实现带查询条件的分页. id > 5 的条件
     *
     * 调用 JpaSpecificationExecutor 的 Page<T> findAll(Specification<T> spec, Pageable pageable);
     *
     *      Specification： 封装了 JPA Criteria 查询的查询条件。
     *      Pageable:：封装了请求分页的信息，例如 pageNo, pageSize, Sort。
     */
    @Test
    public void testJpaSpecificationExecutor() {
        int pageNo = 3 - 1;
        int pageSize = 5;
        PageRequest pageable = PageRequest.of(pageNo, pageSize);

        /*
         * 通常使用 Specification 的匿名内部类
         *      root: 代表查询的实体类。
         *      query: 可以从中可到 Root 对象, 即告知 JPA Criteria 查询要查询哪一个实体类，还可以来添加查询条件，还可以结合 EntityManager 对象得到最终查询的 TypedQuery 对象。
         *      CriteriaBuilder 对象，用于创建 Criteria 相关对象的工厂，当然可以从中获取到 Predicate 对象
         *
         * 该方法需要返回 Predicate 类型, 代表一个查询条件。
         */
        Specification<Person> specification = (Specification<Person>) (root, query, cb) -> {
            //找到 id 属性
            Path path = root.get("id");
            //gt 表示 greater than，要求属性 id 大于 5。
            Predicate predicate = cb.gt(path, 5);
            return predicate;
        };

        Page<Person> page = mPersonRepository.findAll(specification, pageable);

        System.out.println("总记录数: " + page.getTotalElements());
        System.out.println("当前第几页: " + (page.getNumber() + 1));
        System.out.println("总页数: " + page.getTotalPages());
        System.out.println("当前页面的 List: " + page.getContent());
        System.out.println("当前页面的记录数: " + page.getNumberOfElements());
    }

    @Test
    public void testJpaRepository() {
        Person person = new Person();
        person.setBirth(new Date());
        person.setEmail("xy@atguigu.com");
        person.setLastName("xyz");
        person.setId(28);

        Person person2 = mPersonRepository.saveAndFlush(person);
        System.out.println(person == person2);
    }

    @Test
    public void testPagingAndSortingRepository() {
        //pageNo 从 0 开始.
        int pageNo = 6 - 1;
        int pageSize = 5;
        //Pageable 接口通常使用的其 PageRequest 实现类，其中封装了需要分页的信息
        //排序相关的. Sort 封装了排序的信息
        //Order 是具体针对于某一个属性进行升序还是降序.
        Sort.Order order1 = new Sort.Order(Sort.Direction.DESC, "id");
        Sort.Order order2 = new Sort.Order(Sort.Direction.ASC, "email");
        Sort sort = Sort.by(order1, order2);

        PageRequest pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Person> page = mPersonRepository.findAll(pageable);

        System.out.println("总记录数: " + page.getTotalElements());
        System.out.println("当前第几页: " + (page.getNumber() + 1));
        System.out.println("总页数: " + page.getTotalPages());
        System.out.println("当前页面的 List: " + page.getContent());
        System.out.println("当前页面的记录数: " + page.getNumberOfElements());
    }

    @Test
    public void testCrudRepository() {
        List<Person> persons = new ArrayList<>();

        for (int i = 'a'; i <= 'z'; i++) {
            Person person = new Person();
            person.setAddressId(i + 1);
            person.setBirth(new Date());
            person.setEmail((char) i + "" + (char) i + "@atguigu.com");
            person.setLastName((char) i + "" + (char) i);
            persons.add(person);
        }

        personService.savePersons(persons);
    }

    @Test
    public void testModifying() {
        personService.updatePersonEmail("mmmm@atguigu.com", 1);
    }

    @Test
    public void testNativeQuery() {
        long count = mPersonRepository.getTotalCount();
        System.out.println(count);
    }

    @Test
    public void testQueryAnnotationLikeParam() {
        List<Person> persons = mPersonRepository.testQueryAnnotationLikeParam2("bb", "A");
        System.out.println(persons.size());
    }

    @Test
    public void testQueryAnnotationParams2() {
        List<Person> persons = mPersonRepository.testQueryAnnotationParams2("aa@atguigu.com", "AA");
        System.out.println(persons);
    }

    @Test
    public void testQueryAnnotationParams1() {
        List<Person> persons = mPersonRepository.testQueryAnnotationParams1("AA", "aa@atguigu.com");
        System.out.println(persons);
    }

    @Test
    public void testQueryAnnotation() {
        Person person = mPersonRepository.getMaxIdPerson();
        System.out.println(person);
    }

    @Test
    public void testKeyWords2() {
        List<Person> persons = mPersonRepository.getByAddress_IdGreaterThan(1);
        System.out.println(persons);
    }

    @Test
    public void testKeyWords() {
        List<Person> persons = mPersonRepository.getByLastNameStartingWithAndIdLessThan("X", 10);
        System.out.println(persons);

        persons = mPersonRepository.getByLastNameEndingWithAndIdLessThan("X", 10);
        System.out.println(persons);

        persons = mPersonRepository.getByEmailInAndBirthLessThan(Arrays.asList("AA@atguigu.com", "FF@atguigu.com", "SS@atguigu.com"), new Date());
        System.out.println(persons.size());
    }

    @Test
    public void testHelloWorldSpringData() {
        System.out.println(mPersonRepository.getClass().getName());
        Person person = mPersonRepository.getByLastName("AA");
        System.out.println(person);
    }

    /**
     * 测试自动建表
     */
    @Test
    public void testJpa() {

    }

    /**
     * 测试容器配置
     */
    @Test
    public void testDataSource() throws SQLException {
        DataSource dataSource = ctx.getBean(DataSource.class);
        System.out.println(dataSource.getConnection());
    }

}
