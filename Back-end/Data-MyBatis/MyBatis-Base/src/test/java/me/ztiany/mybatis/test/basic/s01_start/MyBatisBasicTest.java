package me.ztiany.mybatis.test.basic.s01_start;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import me.ztiany.mybatis.pojo.User;

/**
 * 测试MyBatis基础功能：
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 18.5.26 22:16
 */
public class MyBatisBasicTest {


    /*
     * 注意：
     * 1. SqlSession代表和数据库的一次会话，用完必须关闭。
     * 2. SqlSession和connection一样都是非线程安全。每次使用都应该去获取新的对象。
     * 3. 全局配置文件用于配置Mybatis，也可以使用注解的方式而不需要使用xml。
     * 4. selectOne(sql) 这种方式下 sql 映射文件是必须的。
     *
     * 使用步骤：
     * 1、根据xml配置文件（全局配置文件）创建一个SqlSessionFactory对象 有数据源一些运行环境信息
     * 2、sql映射文件；配置了每一个sql，以及sql的封装规则等。
     * 3、将sql映射文件注册在全局配置文件中
     * 4、写代码：
     *      1）、根据全局配置文件得到SqlSessionFactory；
     *      2）、使用sqlSession工厂，获取到sqlSession对象使用他来执行增删改查，一个sqlSession就是代表和数据库的一次会话，用完关闭
     *      3）、使用sql的唯一标志来告诉MyBatis执行哪个sql，sql都是保存在sql映射文件中的。
     */

    private SqlSessionFactory sqlSessionFactory;

    @Before
    public void before() throws IOException {
        //加载核心配置文件
        InputStream in = Resources.getResourceAsStream("MyBatisConfig01_Basic.xml");
        //创建SqlSessionFactory
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(in);
    }

    //创建SqlSession，这里创建的Session为 org.apache.ibatis.session.defaults.DefaultSqlSession
    private SqlSession newSqlSession() {
        return sqlSessionFactory.openSession();
    }


    @Test
    public void testMybatis() {
        //执行Sql语句：命名空间.sqlID
        SqlSession sqlSession = newSqlSession();
        User user = sqlSession.selectOne("test.findUserById", 10);
        System.out.println(user);
        //关闭session
        sqlSession.close();
    }

    //根据用户名称模糊查询用户列表
    @Test
    public void testFindUserByUsername() {
        //执行Sql语句
        SqlSession sqlSession = newSqlSession();
        List<User> users = sqlSession.selectList("test.findUserByUsername", "五");
        for (User user : users) {
            System.out.println(user);
        }
        //关闭session
        sqlSession.close();
    }

    //添加用户：insert操作已经做了，但是MyBatis并不会帮我们自动提交事物，必须手动通过SqlSession的commit()方法提交事务
    @Test
    public void testInsertUser() {
        //执行Sql语句
        User user = new User();
        user.setUsername("小马哥");
        user.setBirthday(new Date());
        user.setAddress("深圳");
        user.setSex("男");
        SqlSession sqlSession = newSqlSession();
        //返回插入后，影响的行数
        int insert = sqlSession.insert("test.insertUser", user);
        //打印结果
        System.out.println("insert: " + insert);
        System.out.println("last insert user: " + user.getId());
        //提交事务
        sqlSession.commit();
        //关闭
        sqlSession.close();
    }

    //更新用户
    @Test
    public void testUpdateUserById() {
        //执行Sql语句
        User user = new User();
        user.setId(27);
        user.setUsername("何炅哥");
        user.setAddress("湖南岳阳");
        SqlSession sqlSession = newSqlSession();
        int i = sqlSession.update("test.updateUserById", user);
        //提交事务
        sqlSession.commit();
        //关闭
        sqlSession.close();
        System.out.println(i);
    }

    //删除
    @Test
    public void testDelete() {
        SqlSession sqlSession = newSqlSession();
        sqlSession.delete("test.deleteUserById", 27);
        //提交事务
        sqlSession.commit();
        //关闭
        sqlSession.close();
    }

}