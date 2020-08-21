package me.ztiany.mybatis.test.basic.s04_sqlmap;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import me.ztiany.mybatis.mapper.EmployeeMapperAssociation;
import me.ztiany.mybatis.pojo.Employee;

/**
 * 学习：sql 映射文件的详细配置，关联查询。
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/6/14 23:55
 */
public class SqlMapDetailEmployeeAssociationTest {

    //创建SqlSession
    private SqlSession newSqlSession() {
        //加载核心配置文件
        InputStream in = null;
        try {
            in = Resources.getResourceAsStream("MyBatisConfig03_SQLMapDetail.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //创建SqlSessionFactory
        return new SqlSessionFactoryBuilder().build(in).openSession();
    }

    @Test
    public void testGetEmpById() {
        try (SqlSession openSession = newSqlSession()) {
            EmployeeMapperAssociation mapper = openSession.getMapper(EmployeeMapperAssociation.class);
            Employee employee = mapper.getEmpById(1);
            System.out.println(employee);
            System.out.println(employee.getDept());
        }
    }

    @Test
    public void testGetEmpAndDept() {
        try (SqlSession openSession = newSqlSession()) {
            EmployeeMapperAssociation mapper = openSession.getMapper(EmployeeMapperAssociation.class);
            Employee employee = mapper.getEmpAndDept(1);
            System.out.println(employee);
            System.out.println(employee.getDept());
        }
    }

    @Test
    public void testGetEmpsByDeptId() {
        try (SqlSession openSession = newSqlSession()) {
            EmployeeMapperAssociation mapper = openSession.getMapper(EmployeeMapperAssociation.class);
            mapper.getEmpsByDeptId(1).forEach(employee -> {
                System.out.println(employee);
                System.out.println(employee.getDept());
            });
        }
    }

    @Test
    public void testGetEmpByIdAssociatedLazy() {
        try (SqlSession openSession = newSqlSession()) {
            EmployeeMapperAssociation mapper = openSession.getMapper(EmployeeMapperAssociation.class);
            Employee employee = mapper.getEmpByIdAssociatedLazy(3);
            System.out.println(employee);
            System.out.println(employee.getDept());
        }
    }

}
