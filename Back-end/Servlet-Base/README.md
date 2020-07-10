# JavaEE基础：Servlet 基础

base：基础部分

- ServletConfigSampleServlet：使用ServletConfig获取配置参数。
- ServletContextSample1Servlet：通过ServletContext获取全局配置参数和设置参数，并读取配置文件属性。
- ServletContextSample2Servlet：通过ServletContext转发请求到其他Servlet。
- UnsafeServlet：演示 Servlet 是非线程安全的。

generic：部分

- PrintLifecycleServlet：打印 Servlet 生命周期。

request 部分

- BeanUtilsServlet：借助BeanUtils框架，将参数填充到数据Bean种。
- ForwardSourceServlet、ForwardTargetServlet：演示请求转发。
- IncludeSourceServlet、IncludeTargetServlet：演示包含。
- RequestEncodeServlet：获取请求参数时，解决中文请求参数的编码问题。
- RequestPropertiesServlet：获取请求属性。
- RequestStreamServlet：以输入流的形式获取请求正文内容。

response 部分

- CodeImageServlet：随机生成验证码。
- DownloadCNImageServlet：演示让浏览器下载中文名文件。
- ResponseEncode1Servlet：用字节流输出中文。
- ResponseEncode2Servlet：使用字符流输出中文数据。
- RegisterServlet：处理注册的 Servlet，演示中文编码问题。

http 部分

- DownloadServlet：演示让浏览器以下载方式读取数据。
- GzipServlet：演示Gzip压缩响应数据。
- Location302Servlet：演示302响应。
- NoCacheServlet：演示不让浏览器缓存。
