<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>ServletBase</title>
</head>

<body>

<ul>

    <%
        class Link {
            private final String name;
            private final String route;

            Link(String name, String route) {
                this.name = name;
                this.route = route;
            }

        }

        List<Link> linkList = new ArrayList<Link>();

        linkList.add(new Link("使用ServletConfig获取配置参数", "/servlet/ServletConfigSampleServlet"));
        linkList.add(new Link("通过ServletContext获取全局配置参数和设置参数", "/servlet/ServletContextSample1Servlet"));
        linkList.add(new Link("通过ServletContext转发请求到其他Servlet", "/servlet/ServletContextSample2Servlet"));
        linkList.add(new Link("演示 Servlet 是非线程安全的", "/servlet/UnsafeServlet"));
        linkList.add(new Link("打印 Servlet 生命周期", "/servlet/PrintLifecycleServlet"));
        linkList.add(new Link("借助BeanUtils框架，将参数填充到数据Bean种", "/servlet/BeanUtilsServlet"));
        linkList.add(new Link("演示请求转发", "/servlet/ForwardSourceServlet"));
        linkList.add(new Link("演示包含", "/servlet/IncludeSourceServlet"));
        linkList.add(new Link("获取请求参数时，解决中文请求参数的编码问题", "/servlet/RequestEncodeServlet"));
        linkList.add(new Link("获取请求属性", "/servlet/RequestPropertiesServlet"));
        linkList.add(new Link("以输入流的形式获取请求正文内容", "/servlet/RequestStreamServlet"));
        linkList.add(new Link("随机生成验证码", "/servlet/CodeImageServlet"));
        linkList.add(new Link("演示让浏览器下载中文名文件", "/servlet/DownloadCNImageServlet"));
        linkList.add(new Link("用字节流输出中文", "/servlet/ResponseEncode1Servlet"));
        linkList.add(new Link("使用字符流输出中文数据", "/servlet/ResponseEncode2Servlet"));
        linkList.add(new Link("演示让浏览器以下载方式读取数据", "/servlet/DownloadServlet"));
        linkList.add(new Link("演示Gzip压缩响应数据", "/servlet/GzipServlet"));
        linkList.add(new Link("演示302响应", "/servlet/Location302Servlet"));
        linkList.add(new Link("演示不让浏览器缓存", "/servlet/nocacheServlet"));

        for (Link link : linkList) {

    %>

    <li>
        <a href="<%out.write(getServletConfig().getServletContext().getContextPath());out.write(link.route);%>">
            <%out.write(link.name);%>
        </a>
    </li>

    <%
        }
    %>

    <li>
        <a href="register.html">处理注册的 Servlet，演示中文编码问题</a>
    </li>

</ul>

</body>

</html>