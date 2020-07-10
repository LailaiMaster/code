package me.ztiany.mybatis.test.basic.s04_sqlmap;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import me.ztiany.mybatis.mapper.DepartmentMapper;
import me.ztiany.mybatis.pojo.Department;

/**
 * 学习：sql 映射文件的详细配置，关联查询。
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/6/14 23:56
 */
public class SqlMapDetailDepartmentTest {

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

    /**
     * 普通查询
     */
    @Test
    public void testEetDeptById() {
        try (SqlSession openSession = newSqlSession()) {
            DepartmentMapper mapper = openSession.getMapper(DepartmentMapper.class);
            Department deptByIdStep = mapper.getDeptById(1);
            System.out.println(deptByIdStep.getDepartmentName());
        }
    }

    /**
     * 关联查询
     */
    @Test
    public void testEetDeptByIdAssociated() {
        try (SqlSession openSession = newSqlSession()) {
            DepartmentMapper mapper = openSession.getMapper(DepartmentMapper.class);
            Department deptByIdStep = mapper.getDeptByIdAssociated(1);
            System.out.println(deptByIdStep.getDepartmentName());
        }
    }

    /**
     * 关联查询（延迟加载）
     */
    @Test
    public void testEetDeptByIdAssociatedLazy() {
        try (SqlSession openSession = newSqlSession()) {
            DepartmentMapper mapper = openSession.getMapper(DepartmentMapper.class);
            Department deptByIdStep = mapper.getDeptByIdAssociatedLazy(1);
            System.out.println(deptByIdStep.getDepartmentName());
            System.out.println(deptByIdStep.getEmps());
        }
    }

}
