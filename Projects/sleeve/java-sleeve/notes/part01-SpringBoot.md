# Part01：SpringBoot【8-13周】

## 1 SpringBoot 项目介绍

**开发环境**：

1. 开发工具：IDEA
2. 构建工具：Maven

**SpringBoot 项目的初始化**：

- 方式1：在 <https://start.spring.io/> 配置项目【最少选择 web 和 devtools 依赖】。
- 方式2：使用 IDEA 的插件初始化 SpringBoot 项目。

**SpringBoot 的版本号介绍**：以 `2.3.9 (SNAPSHOT)` 为例

1. 第一个数组为主版本号。【跨主版本，不一定保证兼容】
2. 第二个数组为次版本号。【用于发布一些新特新，原则上保证兼容性】
3. 第三个数组为主版本号。【增量版本，主要用于 bug 修复，原则上保证兼容性】
4. 最后的字母被称为里程碑版本，名称由开发者自己指定，用于描述版本发布计划或状态，常见的有：
    1. `GA`：General Availability 稳定版本。
    2. `CURRENT`：当前版本。
    3. `ALPHA`：内部测试。
    4. `BATE`：外部测试。
    6. `SNAPSHOT`：不如 GA 稳定。

**SpringBoot 自动热重启配置**：

- 参考 [SpringBoot 配置 devtools 实现热部署](https://zhuanlan.zhihu.com/p/62811195)

**uber jar**：

- SpringBoot 打的包是 uber jar 包，里面已经包含了一个 tomcat 服务器。

## 2 面向可以维护性编程

### 开闭原则

6 大原则里面最终用的就是 **开闭原则**，修改已有的业务引发 bug 的几率很大，而重新写一个 class 引发 bug 的记录相对较小。

### 代码的演进

阶段1：混乱代码。

1. 没有设计模式。
2. 命令式的编程

阶段2：使用接口。接口的作用：

1. 接口可以统一方法的调用，却无法统一对象的创建。
2. 面向对象主要就是：实例化对象、调用方法（逻辑）。

阶段3：解决创建对象的问题。

1. 只有一段代码中没有 new 的出现，才能保持相对稳定型，才能逐步实现 ==OCP【开放原则】==。
2. 上面这句话只是表象，实质是一段代码如果要保持稳定，就不应该负责对象的实例化。
3. 对象的实例化是不可消除的。
4. 把对象的实例化过程，转移到其他代码片段里，比如说通过==工厂模式==。

阶段4：隔离不稳定性。

1. 代码中总是会存在不稳定，隔离这些不稳定的代码，保证其他代码的稳定性。

### IOC、DI 与 DIP

1. 配置文件不应该归属于系统本身，而是外部输入，因此配置的修改并不违反 OCP。
2. 为什么有了抽象工厂，还需要 IOC、DI 呢？虽然有了抽象工厂，但我们还是需要手动地去调用工厂，而通过 IOC、DI，我们甚至不在需要调用工厂，而只需要声明需要的对象，由 ICO、DI 框架是实现对象的初始化，同时这个框架还可以帮助我们管理对象之间的关心。==这就由主动地要变成了被动的给==。

## 3 Spring 与 SpringBoot 理论

1. 狭义的 Spring 指的是 SpringFramework。
2. 广义的 Spring 指的是 Spring 全家桶。

SSM = Spring + SpringMVC + MyBatis，其中 SpringMVC 只是 Spring 全家桶中用于 Web 开发的模块。

**把握 Spring 的几个重要目的**：

1. IOC 实现：容器——加入容器，然后注入。
2. 目的：
    1. 抽象的意义：控制器交给用户。
    2. 实现灵活的 OCP。

**SpringBoot**：

1. SpringBoot 是利用 Spring 开发出来的一套更易用，更上层的一套框架。
2. SpringBoot 的核心优势是自动配置。

## 4 Spring IOC 的核心机制：实例化与注入

将对象加入容器的方式：

1. xml 配置的方式。
2. 注解的方式。

### stereotype annotations（模式注解）

最基础的注解：

1. `@Component`：将组件/类/bean加入到容器。
2. `@Autowired`：声明依赖。

与 Component 作用一致的注解，只是用不同的名称表示不同的作用。

2. `@Service`
3. `@Repository`
4. `@Controller`

其他类型的注解：

1. Configuration：更加灵活的注解，可以一次性加入多个 Bean。

### 实例化和依赖注入时机与延迟实例化

1. 默认情况下，如果容器中没有满足注入条件的 bean，启动时就会报错。此时可以设置 `@Autowired` 中 required = false。
2. 默认情况下，容器中的类是立即初始化的，使用 `@Lazy` 可以开启该 bean 的延迟初始化。如果一个立即初始化的组件依赖另一个延迟初始化的组件，则该延迟初始化的组件也会在容器启动时被立即初始化。

### 注入组件的方式

1. 属性注入方式。【必须加 Autowired 注解】
2. 构造函数注入方式。【推荐，可以不加 Autowired 注解，可以认为是被动注入】
3. setter 注入方式。【推荐，必须加 Autowired 注解】

### 单个接口，多个实现的情况

如果容器中，单个接口有多个实现，如果没有明确指定注入哪个实现，会怎么样？

Autowired 的注入方式有两种：

1. by type【默认方式】
2. by name

具体的规则：

1. 先按类型匹配，找不到就按名称匹配。
2. 按名称匹配有一个默认的规则，如果根据`字段名`能匹配到`类名`，则注入该类型。
3. 找不到任何一个 bean 则报错。

其次，可以使用 `@Qualifier` 明确指定注入需要的实现。

### 应对变化的解决方案

1. 指定一个 interface，用多个类实现这个 interface。【策略模式】
2. 只有一个类，通过修改类的属性来应对变化。【属性配置】

## 5 SpringBoot 基本配置原理

### Configuration 配置类的使用

1. `@Configuration` 注解表明这个类是一个配置类，其内部的添加了 `@Bean` 的方法将会作为作为Bean的工厂方法。
2. 配合 `@ComponentScan(basePackages = "me.ztiany.bean.profile")` 可以指定要扫描的包。
3. 配合 `@ImportResource(locations = "classpath:xml_profile.xml")` 可以指定要使用的资源。

```java
@Configuration
public class JavaConfig {

    @Bean
    public Car createCar() {
        return new BMWCar();
    }

    @Bean
    public Driver createDriver(Car car) {
        NormalDriver normalDriver = new NormalDriver();
        normalDriver.setCar(car);
        return normalDriver;
    }

}
```

应对变化的一个策略是将变化隔离到配置文件中，我们应该将 Configuration 看作是配置，是老式 xml 的替代，所以 Configuration 的变化不应该认为是破坏了 OCP 原则。

为什么要将变化隔离到配置文件中：

1. 配置文件具有集中性。
2. 配置文件不含有复杂的业务逻辑，相对较为清晰。

### Configuration 的意义

1. 使用 Configuration 更加灵活，可以对应很多的注入情况，比如单个接口的多个实现，条件注入等。
2. 使用 Configuration 具有更多的控制权。
3. Configuration 是一种编程模式，以配置的方式应对变化。

### ComponentScan 包描述机制

SpringBoot 默认只扫描入口类（`@SpringBootApplication`）同级或子级下的包。如果要改变默认扫描的包位置，则可以使用 `@ComponentScan` 注解。

### 应对变化与策略模式

对于一个 interface，有多个类实现这个 interface 的情况，考虑使用策略模式应对这种情况。主要的方式为：

- 切换注入方式为 by name。
- 使用 `@Qualifer` 注解
- 有选择地只注入一个 bean，注释掉某个 bean 上的 `@Component` 注解。
- 使用 `@Primary` 注解。【推荐】

### 条件注解 `@Conditional`

**自定义条件注解**：

通过 `@Conditional` 和实现 Condition 接口，可以实现自定义的条件注解。

**成品条件组件**：定义在 `org.springframework.boot.autoconfigure.condition` 包下。

- `@ConditionalOnProperty`
- `@ConditionalOnClass`
- `@ConditionalOnBean`
- `@ConditionalOnMissingBean`
- ...

## 6 自动配置/装配

问题：

1. 自动配置的原理是什么?
2. 为什么要有自动配置?

### 自动配置的原理是什么

1. SpringBootApplication 是一个超级大的配置类。
2. EnableAutoConfiguration 的目的去加载用 maven/gradle 导入的第三方库。`spring.factories` 中定义了所有的候选类。核心还是去加载 `@Configuration`，一般 Configuration 对应一个 SDK。
3. `SpringBootApplication = SpringBootConfiguration + EnableAutoConfiguration + ComponentScan`

`spring.factories` 就是隔离变化点。`@Primary` 条件关注的是小粒度（类/对象）的变化。而 `spring.factories` 中隔离的是整体解决方案的变化。

### 自动配置其实是 SPI 机制的应用

SPI 即 Service Provider Interface，SPI 用于因对各种变化。

- 调用方：标准服务接口。
- 提供方：
    - 方案 A
    - 方案 B
    - 方案 C

基于 interface + 策略模式 + 配置文件。

### 自动配置指的是什么

自动配置真正解决的不是自动引入第三方库到项目，而是自动发现第三方库，并将它们加入到 IOC 容易中。

## 7 Java 异常分类剖析与自定义异常

### 统一异常反馈

对于接口，后台应该统一格式，比如：

```json
{
    "code" : 100,
    "message" : "what",
    "data" : {}
}
```

### Spring 统一处理异常

Spring 使用 ControllerAdvice 和 ExceptionHandler 注解统一异常处理。

**怎么区分 RuntimeException 和 CheckedException**？

1. CheckedException 一般对应的是 bug，比如去读配置文件，因为配置文件错误导致了异常，这个异常是可以修复的，所以应该是 CheckedException。
2. RuntimeException 一般对应程序无法处理的情况，比如数据库中不存在对应的数据。

**APIException 应该继承自 RuntimeException 还是 CheckedException 呢**？

- 如果类似 Spring 这种有全局异常处理器的，可以选择集成 RuntimeException。

**已知异常和未知异常**：

- 未知异常即在编程中，因为各种原因没有考虑到的异常。比如 `int a = b / c`，其中 c 可能为 0，而开发者没有考录到这种情况，就可能发生除零异常。
- 未知异常对于前端开发者和用户都是无意义的，服务端开发者的代码逻辑问题。

## 8 参考校验与 LomBok 项目

参考校验的作用

1. 提高前后端协同开发效率。
2. 对保护内部机要数据很重要。
3. 控制器仅用于桥接视图层和模型层，不要写业务逻辑。

### 接收参数

使用 `@PathVariable` 接收路径参数：

```java
//如果 "/test/path/{id}" 中的 id 与参数名 id 不一致，则需要在 PathVariable 中指定参数名为 id。
@GetMapping("/test/path/{id}")
public String testPathParams(@PathVariable("id") Integer id) {
    System.out.println(id);
    return "你好，testPathParams";
}
```

使用 `@RequestParam` 接收查询参数：

```java
@PostMapping("/test/request")
public String testURLParams(@RequestParam Integer age) {
    System.out.println(age);
    return "你好，testURLParams";
}
```


使用 `@RequestBody` 接收 Json 参数，不推荐使用 Map 接受 json 参数，而是定义对应的 DTO（数据传输对象）来接收参数。

```java
@PostMapping("/test/body")
public String testBodyParams(@RequestBody PersonDTO person) {
    System.out.println(person);
    return "你好，testBodyParams";
}
```

### LomBok 项目

**引入项目**:

```groovy
compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'
```

>此外还需要安装 lombok插件。

**相关注解**：

- `@Data` 生成 `getter/setter/toString()` 等方法 。
- `@NonNull`： 要求非空字段。
- `@CleanUp`： 自动资源管理：不用再在 finally 中添加资源的 close 方法 。
- `@Setter/@Getter`： 自动生成 set 和 get 方法 。
- `@ToString`： 自动生成 toString 方法 。
- `@EqualsAndHashcode`： 从对象的字段中生成hashCode和equals的实现 。
- `@NoArgsConstructor/@RequiredArgsConstructor/@AllArgsConstructor`：自动生成构造方法 。
- `@Data`： 自动生成 set/get 方法，toString 方法，equals 方法，hashCode 方法，不带参数的构造方法 。
- `@Value`： 用于注解 final 类 。
- `@Builder`： 产生复杂的构建器 api 类，如果应用了 Builder 模式，又要自己定义无参构造函数，需要加构造函数上加上 `@Tolerate` 注解。
- `@SneakyThrows`： 异常处理（谨慎使用） 。
- `@Synchronized`：同步方法安全的转化 。
- `@Log`： 支持各种 logger 对象，使用时用对应的注解，如：`@Log4j`。

**JSP-269**：

- LomBok 是 JSP-269 规范的实现。

### JSR-303 与参数校验

引入校验组件，具体参考 [validation-starter-no-longer-included-in-web-starters](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.3-Release-Notes#validation-starter-no-longer-included-in-web-starters)：

```groovy
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

JSR 303 规范描述的是参数校验，HibernateValidator 是一种实现。

1. 类上加上 `@Validated` 注解才能开启该类的校验功能。
2. 在参数伤上加上对应的校验注解。

接收参数时，如果参数是类类型，也需要加上 `@Validated` 或 `@Valid` 注解。

```java
@PostMapping("/test/body")
public String testBodyParams(@RequestBody @Validated PersonDTO person) {
    System.out.println(person);
    return "你好，testBodyParams";
}
```

如果类型包含了嵌套类型，则需要在类型字段上加上 `@Valid` 对象：

```java
@Getter
@Setter
@ToString
public class PersonDTO {

    @Length(min = 2, max = 10)
    private String name;

    private Integer age;

    @Valid
    private SchoolDTO school;

}
```

1. `@Validated` 和 `@Valid` 注解具有一定相似性，`@Valid` 是 javax 中的注解，`@Validated` 是 Spring 中的扩展。
2. 自定义参数注解需要用到 ConstraintValidator。

## 9 什么是分层架构

分层架构的目的：

1. 微服务之前，大型项目，每一层都由不同的开发者或团队进行开发，分层可以让各层开发者各司其职，互不打搅。
2. 分层可以认为是水平分隔，而微服务可以认为是垂直分隔。
3. 归根到底，水平分隔和垂直分隔都是为了 OCP，把大型项目拆分各个小模块，以便于维护。

通常的分层：

1. Controller：接收前端调用，然后调用 Service 层，根据结果返回合适的响应。
2. Service：业务逻辑的实现。
3. 层与层之间一般使用接口衔接，引用接口而不是引用具体的实现，以防止因为具体的实现改变而导致调用方的改变。【按照规范是这样的，但是这样会显得比较繁琐，之所以会觉得繁琐，是因为代码粒度不够小，类承担的职责过多，替换难度大】
