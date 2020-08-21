package com.ztiany.annotation.tx;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@EnableTransactionManagement
@ComponentScan("com.ztiany.annotation.tx")
@Configuration
public class TxConfig {

    //数据源
    @Bean
    public DataSource dataSource() throws Exception {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setUser("root");
        dataSource.setPassword("201314");
        dataSource.setDriverClass("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql:///spring4_learning?useSSL=false&serverTimezone=Asia/Shanghai");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() throws Exception {
        //Spring对@Configuration类会特殊处理，给容器中加组件的方法，多次调用都只是从容器中找组件。
        //所以这里调用 dataSource 方法也不会导致创建新的 DataSource。
        return new JdbcTemplate(dataSource());
    }

    //注册事务管理器在容器中
    @Bean
    public PlatformTransactionManager transactionManager() throws Exception {
        return new DataSourceTransactionManager(dataSource());
    }

}