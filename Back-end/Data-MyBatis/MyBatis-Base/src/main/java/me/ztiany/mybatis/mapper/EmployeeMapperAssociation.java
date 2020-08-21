package me.ztiany.mybatis.mapper;

import java.util.List;

import me.ztiany.mybatis.pojo.Employee;

public interface EmployeeMapperAssociation {

    Employee getEmpById(Integer id);

    Employee getEmpAndDept(Integer id);

    Employee getEmpByIdAssociatedLazy(Integer id);

    List<Employee> getEmpsByDeptId(Integer deptId);

}
