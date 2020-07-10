package com.atguigu.test;

import com.atguigu.util.JDBCUtils;

import org.junit.Test;

import java.sql.Connection;


public class JDBCUtilTest {

    @Test
    public void testGetConnection() {
        Connection conn = JDBCUtils.getConnection();
        System.out.println(conn);
        JDBCUtils.closeConnection(conn);
    }

}
