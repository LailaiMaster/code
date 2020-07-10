package me.ztiany.mybatis.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

import me.ztiany.mybatis.pojo.Employee;


public interface EmployeeMapper {

    /**
     * 添加以一条记录，返回影响记录数。
     */
    Long addEmp(Employee employee);

    /**
     * 更新记录。
     */
    boolean updateEmp(Employee employee);

    /**
     * 根据id删除记录。
     */
    void deleteEmpById(Integer id);

    /**
     * 根据主键查询，传单个参数。
     */
    Employee getEmpById(Integer id);

    /**
     * 根据主键和名称查询：测试多参数传递，命名参数。
     */
    Employee getEmpByIdAndLastName(@Param("id") Integer id, @Param("lastName") String lastName);

    /**
     * 测试 Map 传参。
     */
    Employee getEmpByMap(Map<String, Object> map);

    /**
     * 多条记录封装一个map：Map<Integer,Employee>：键是这条记录的主键，值是记录封装后的javaBean。
     * `@MapKey`：告诉 mybatis 封装这个 map 的时候使用哪个属性作为 map 的 key。
     */
    @MapKey("lastName")
    Map<String, Employee> getEmpByLastNameLikeReturnMap(String lastName);

    /**
     * 返回一条记录的map：key就是列名，值就是对应的属性值。
     */
    Map<String, Object> getEmpByIdReturnMap(Integer id);

    /**
     * 测试查询列表。
     */
    List<Employee> getEmpsByLastNameLike(String lastName);

}
