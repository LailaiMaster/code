package me.ztiany.mybatis.mapper;

import me.ztiany.mybatis.pojo.Department;

public interface DepartmentMapper {

    /**
     * 仅查询部门
     */
    Department getDeptById(Integer id);

    /**
     * 关联查询
     */
    Department getDeptByIdAssociated(Integer id);

    /**
     * 关联查询（懒加载）
     */
    Department getDeptByIdAssociatedLazy(Integer id);

}