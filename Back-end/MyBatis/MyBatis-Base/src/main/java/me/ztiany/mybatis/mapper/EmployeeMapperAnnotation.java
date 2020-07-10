package me.ztiany.mybatis.mapper;

import org.apache.ibatis.annotations.Select;

import me.ztiany.mybatis.pojo.Employee;

/**
 * 以注解的方式配置 sql 隐射 ，不是很复杂的情况可以使用
 */
public interface EmployeeMapperAnnotation {

    @Select("select * from tbl_employee where id=#{id}")
    Employee getEmpById(Integer id);

}
