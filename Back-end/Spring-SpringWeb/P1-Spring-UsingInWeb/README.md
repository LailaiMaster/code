# 演示 Spring 应用于 Web 开发的原理

>这个项目并没有使用 SpringWeb，而是使用 Spring 基础库来模拟 SpringWeb 的原理。

如果让我们自己根据 Spring IOC 容器来实现一个 MVC 框架我们会怎么做呢？首先要看基于现有框架我们具有能力？

1. 一套 JavaEE API。
2. 一个 SpringIOC 容器框架。

JavaEE API 提供了 Servlet、Filter、Listenter 等功能，Filter 用于过滤请求，Filter 用于监听应用的生命周期，Servlet 用于处理请求，SpringIOC 则提供了一个 IOC 容器。因此我们可以：

1. 使用 SpringFramework 提供的 IOC 容器维护类与类之间的关系。
2. 通过 Filter 监听应用的启动，然后初始化 IOC 容器。
3. 在 Servlet 中处理请求，然后将请求交给 IOC 容器中的组件来处理。

那么具体做法如下：首先是在 web.xml 中配置 Servlet 和 Listener：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     xmlns="http://java.sun.com/xml/ns/javaee"
     xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
     id="WebApp_ID" version="2.5">

<context-param>
    <param-name>configLocation</param-name>
    <param-value>applicationContext.xml</param-value>
</context-param>

<listener>
    <listener-class>com.atguigu.spring.mock.listeners.SpringServletContextListener</listener-class>
</listener>

<servlet>
    <display-name>CoreServlet</display-name>
    <servlet-name>CoreServlet</servlet-name>
    <servlet-class>com.atguigu.spring.mock.servlets.CoreServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
    <servlet-name>CoreServlet</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>

</web-app>
```

在 Listener 中对 IOC 容器进行初始化：

```java
public class SpringServletContextListener implements ServletContextListener {

    public SpringServletContextListener() {
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        //1. 获取 Spring 配置文件的名称.
        ServletContext servletContext = arg0.getServletContext();
        String config = servletContext.getInitParameter("configLocation");
        //1. 创建 IOC 容器
        ApplicationContext ctx = new ClassPathXmlApplicationContext(config);
        //2. 把 IOC 容器放在 ServletContext 的一个属性中.
        servletContext.setAttribute("ApplicationContext", ctx);
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
    }

}
```

然后在 CoreServlet 中，通过 IOC 容器获取对应的 Controller 对请求进行处理：

```java
public class CoreServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1. 从 application 域对象中得到 IOC 容器的引用
        ServletContext servletContext = getServletContext();
        ApplicationContext ctx = (ApplicationContext) servletContext.getAttribute("ApplicationContext");
        //2. 从 IOC 容器中得到需要的 bean
        Map<String, Controller> beansOfType = ctx.getBeansOfType(Controller.class);
        String uri = request.getRequestURI().replace(request.getContextPath(),"");
        Controller target = null;
        for (Controller controller : beansOfType.values()) {
            if (uri.equals(controller.url())) {
                target = controller;
                break;
            }
        }
        //3. 调用组件，处理请求
        if (target != null) {
            target.handleRequest(request, response);
        }else {
            PrintWriter writer = response.getWriter();
            writer.write("not found");
        }
    }

}
```

applicationContext.xml 配置如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="aController" class="com.atguigu.spring.mock.controller.AController"/>

    <bean id="bController" class="com.atguigu.spring.mock.controller.BController"/>

</beans>
```

实际上，Spring MVC 的整体处理流程也是这样，具体参考下面【2.2 Spring MVC 处理流程】。
