package me.ztiany.mybatis.test.basic.s04_sqlmap;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.ztiany.mybatis.mapper.EmployeeMapper;
import me.ztiany.mybatis.mapper.EmployeeMapperAnnotation;
import me.ztiany.mybatis.pojo.Employee;

/**
 * 学习：sql 映射文件的详细配置，主要的传参方式。
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/6/14 19:16
 */
public class SqlMapDetailEmployeeTest {

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

    ///////////////////////////////////////////////////////////////////////////
    // 注解配置方式
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 测试使用注解配置Sql映射
     */
    @Test
    public void testAnnotationSQLMap() {
        try (SqlSession session = newSqlSession()) {
            EmployeeMapperAnnotation mapper = session.getMapper(EmployeeMapperAnnotation.class);
            //测试添加
            Employee employee = mapper.getEmpById(1);
            //获取到主键
            System.out.println(employee);
            //2、手动提交数据
            session.commit();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 获取自增主键
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 插入后获取新插入记录的主键。
     */
    @Test
    public void testGetIdAfterInsert() {
        try (SqlSession session = newSqlSession()) {
            EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
            //测试添加
            Employee employee = new Employee(null, "jerry4", null, "1");
            System.out.println(mapper.addEmp(employee));
            //获取到主键
            System.out.println(employee.getId());
            //2、手动提交数据
            session.commit();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 传参方式
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetEmpById() {
        try (SqlSession openSession = newSqlSession()) {
            EmployeeMapper mapper = openSession.getMapper(EmployeeMapper.class);
            Employee employee = mapper.getEmpById(1);
            System.out.println(employee);
        }
    }

    @Test
    public void testGetEmpByIdAndLastName() {
        try (SqlSession openSession = newSqlSession()) {
            EmployeeMapper mapper = openSession.getMapper(EmployeeMapper.class);
            Employee employee = mapper.getEmpByIdAndLastName(1, "jerry4");
            System.out.println(employee);
        }
    }

    @Test
    public void testGetEmpByMap() {
        try (SqlSession openSession = newSqlSession()) {
            EmployeeMapper mapper = openSession.getMapper(EmployeeMapper.class);
            Map<String, Object> map = new HashMap<>();
            map.put("id", 1);
            map.put("lastName", "jerry4");
            map.put("tableName", "tbl_employee");
            Employee employee = mapper.getEmpByMap(map);
            System.out.println(employee);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 返回复杂类型
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void testGetEmpsByLastNameLike() {
        try (SqlSession openSession = newSqlSession()) {
            EmployeeMapper mapper = openSession.getMapper(EmployeeMapper.class);
            List<Employee> like = mapper.getEmpsByLastNameLike("%e%");
            for (Employee employee : like) {
                System.out.println(employee);
            }
        }
    }

    @Test
    public void testEetEmpByIdReturnMap() {
        try (SqlSession openSession = newSqlSession()) {
            EmployeeMapper mapper = openSession.getMapper(EmployeeMapper.class);
            Map<String, Object> map = mapper.getEmpByIdReturnMap(1);
            System.out.println(map);
        }
    }

    @Test
    public void testGetEmpByLastNameLikeReturnMap() {
        try (SqlSession openSession = newSqlSession()) {
            EmployeeMapper mapper = openSession.getMapper(EmployeeMapper.class);
            Map<String, Employee> map = mapper.getEmpByLastNameLikeReturnMap("%r%");
            System.out.println(map);
        }
    }

}