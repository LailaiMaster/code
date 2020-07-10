# 第二章：装配 Bean

创建应用对象之间协作关系的行为通常称为装配（wiring），这也是依赖注入（DI）的本质，在 Spring 中装配 bean 有多种方式。

---
## 1 Spring 配置的可选方案

- 在 XML 中进行显式配置。
- 在 Java 中进行显式配置。
- 隐式的 bean 发现机制和自动装配。

这些配置方案是可以混用的。但是建议尽可能地使用**自动配置的机制**，显式配置越少越好。其次对于某些第三方库中对象的配置，建议使用 JavaConfig，而 XML 是最后的选择。

---
## 2 自动化装配 bean

Spring从两个角度来实现自动化装配：

- 组件扫描（component scanning）：Spring 会自动发现应用上下文中所创建的 bean。
- 自动装配（autowiring）：Spring 自动满足 bean 之间的依赖。

### 创建可自动装配的Bean

-  `@Component`注解用于表明该类会作为组件类
- 组件扫描默认是不启用的，应用 `@ComponentScan` 注解能够在 Spring 中启用组件扫描
- 如果没有其他配置的话，`@ComponentScan` 默认会扫描与配置类相同的包（包括子包）
- 在 XML 中配置 Spring context 命名空间的 `<context:component-scan>` 也可以开启 Spring 自动扫描组件

### 设置 Bean 的 ID

Spring 应用上下文中所有的 bean 都会给定一个 ID，如果没有显示的指定，Spring 会根据类名为其指定一个 ID（将类名的第一个字母变为小写）。

为 bean 命名的两种方式：

-  `@Component` 的 value 属性
- 使用 Java 依赖注入规范（Java Dependency Injection）中所提供的 `@Named` 注解

设置组件扫描的基础包，可以使用 `@ComponentScan` 属性设置不同的属性来指定要扫描的包：

- 使用 value 属性中指明包的名称
- 使用 basePackages 指定基础包
- 使用 basePackageClasses 指定类，这些类所在的包将会作为组件扫描的基础包

### 通过为 bean 添加注解实现自动装配

自动装配就是让 Spring 自动满足 bean 依赖的一种方法，在满足依赖的过程中，会在 Spring 应用上下文中寻找匹配某个 bean 需求的其他 bean。

-  `@Autowired注解` 可以用来声明 bean 的依赖
 -  `@Autowired` 注解不仅能够用在构造器上，还能用在属性的Setter方法上
 -  `@Autowired` 的属性可以指定依赖对象的 id，否则如果有多个 bean 都能满足依赖关系的话，Spring 将会抛出一个异常，表明没有明确指定要选择哪个 bean 进行自动装配
-  `@Inject` 也可以用来声明 bean 的依赖，`@Inject` 注解来源于 Java 依赖注入规范

---
## 3 过Java代码装配bean

想要将第三方库中的组件装配到你的应用中，在这种情况下，是没有办法在它的类上添加 `@Component` 和 `@Autowired` 注解的，因此就不能使用自动化装配的方案了。在这种情况下必须要采用显式装配的方式: JavaConfig 和 XML。JavaConfig 是更好的方案，它更为强大、类型安全并且对重构友好。但是在概念上，JavaConfig 与应用程序中的业务逻辑和领域代码是不同的。它不应该包含任何业务逻辑，也不应该侵入到业务逻辑代码之中，通常会将  JavaConfig 放到单独的包中，使它与其他的应用程序逻辑分离开来。

### 创建配置类

`@Configuration` 注解：一个类添加`@Configuration` 注解，则表明这个类是一个配置类，该类应该用于指定 Spring 应用上下文如何创建 bean。

### 声明简单的 bean

- `@Bean` 注解：在一个方法上添加`@Bean`注解，用于告诉 Spring 这个方法将会返回一个对象
- 默认情况下，Spring 中的 bean 都是单例的
- 添加 `@Bean` 注解的方法可以声明参数，这个参数由 Spring 注入，当然，容器中应该存在该类型的对象

---
## 4 通过 XML 装配 bean

在 Spring 刚刚出现的时候，XML 是描述配置的主要方式。

- 创建一个XML文件，并且要以 `<beans>` 元素为根
`<bean>` 元素类似于 JavaConfig 中的 `@Bean` 注解

### 构造器注入初始化 bean

有两种基本的配置方案可供选择

-  `<constructor-arg>` 元素使用
- Spring 3.0 所引入的 `c-命名空间`

#### c-命名空间

- 使用属性名注入：如果构建时优化构建过程，将调试标志移除掉，那么这种方式可能就无法正常执行了。
- 使用参数索引注入
- ref 用于注入引用类型
- value 用于注入基本类型和字符串类型
- 引用类型可以使用 `<null/>` 注入 null

#### constructor-arg

`<constructor-arg>` 和 `c-命名空间` 的功能是相同的。但是某些情况下，`<constructor-arg>`功能更加强大，比如在装配列表时，只能使用`<constructor-arg>`

### 属性注入

- 使用 property 注入属性
- 使用 p 名称空间注入属性

### utils 命名空间

`util-命名空间` 用于在容器中创建一些集合，比如所提供的功能之一 `<util:list>` 元素，它会创建一个列表的 bean。Spring util-命名空间中的元素如下：

- `<util:constant>` 引用某个类型的 public static 域，并将其暴露为 bean
- `util:list` 创建一个 java.util.List 类型的 bean，其中包含值或引用
- `util:map` 创建一个 java.util.Map 类型的bean，其中包含值或引用
- `util:properties` 创建一个 java.util.Properties 类型的
- `beanutil:property-path` 引用一个 bean 的属性（或内嵌属性），并将其暴露为 bean
- `util:set` 创建一个 java.util.Set 类型的 bean，其中包含值或引用

---
## 5 混合配置

Spring 中各种配置方案都不是互斥的，完全可以将 JavaConfig 的组件扫描和自动装配和/或 XML 配置混合在一起。

- 在 JavaConfig 中引用 XML 配置：
  - `@Import` 可以导入其他的 JavaConfig 配置
  - `@ImportResource`用于在 JavaConfig 中引用 XML 配置
- 在 XML 配置中引用 JavaConfig：
  - `<import>`元素只能导入其他的XML配置文件，不能导入 JavaConfig 类配置
  - 直接在 XML 中声明一个 bean，设置类型为 JavaConfig 类，即可在 XML 配置中引用 JavaConfig
