# MyBatis 入门学习

## 1 MyBatis-Base 基础学习

### 1.1  MyBatis 基础学习

1 MyBatisBasicTest：MyBatis 的基本用法，配置文件包括：

- sqlMapConfig_Basic 配置文件
- sqlmap/User.xml 数据库映射文件
    
2 MyBatisDaoTest：MyBatis dao 模式

3 MyBatisBasicTest：MyBatis 自动 Mapper 映射
    
4 SqlMapDetailEmployeeTest：sql映射文件的传参细节

5 SqlMapDetailDepartmentTest：sql映射文件的传参细节

6 SqlMapDetailEmployeeAssociationTest：sql映射文件的传参细节
 
### 1.2 Spring 集成 MyBatis

- SpringDaoTest：MyBatis 整合 Spring，dao模式
- SpringMapperTest：MyBatis 整合 Spring，手动配置mapper模式 + MyBatis事务演示
- SpringMapperScannerTest：MyBatis 整合 Spring，自动扫描Mapper
- SpringTxTest：MyBatis 整合 Spring，使用 Spring 事务管理 MyBatis

##  2 MyBatis-Plugin

根据 SQL 生成 POJO、Mapper、和 xml 映射文件，支持单表查询。

- 在 gradle 中添加依赖和配置插件，生成的 task 是`other/mbGenerator`
- 使用 `src/main/resource/generatorConfig.xml`配置插件
- 生成的代码配置在根目录 generate 目录下
- 运行 task (如果没有生成则手动创建生成路径)即可生成代码
