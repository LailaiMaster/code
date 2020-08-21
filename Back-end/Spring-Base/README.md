# Spring Framework4 入门

## P1-GetStarted：Spring IOC 入门

在test中演示 Spring Framework 基础功能

- bean：Spring容器中Bean的配置
  - ConfigTest：Spring 手动加载配置文件，手动从容器中获取对象。
  - JUnitSpringTest：使用 Spring 提供的测试框架，简化Spring 单元测试
  - ProxyTest：演示 Proxy 和 CGLib 代理

- aop ：Spring 切面功能
  - AOPTest：xml方式配置aop
  - AOPAnnotationTest：注解方式开启aop

- jdbc：Spring 的 JDBC 模板
  - JDBCTest：jdbc模板的使用
  
- transaction：spring 事务
  - Tx1Test：手动调用 TransactionTemplate 使用事务
  - Tx2Test：Spring AOP 事务配置
  - Tx3Test：Spring AOP 事务注解式配置

## P2-Beans：Bean 注入详解

- getstarted：入门
- annotation：注解注入
- auto：自动注入，`byName` 和 `byTypoe`
- generic：Spring4 泛型注入

## P3-AOP：AOP 详解

- aop：注解配置aop
- aop.xml：xml方式配置aop

## P4-SpringJDBC：Spring对JDBC的支持，以及 Spring 事务

- jdbc：jdbc
- transaction：事务
- transaction.xml：事务的xml配置方式

## P5-SpringAnnotation：Spring 纯注解开发

- ioc：ioc 容器注解开发
- aop：aop 注解开发
- tx：声明式事务
