<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Insert title here</title>

    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery-1.9.1.min.js"></script>

    <script type="text/javascript">
        $(function () {
            $("#testJson").click(function () {
                var url = this.href;
                var args = {};
                $.post(url, args, function (data) {
                    for (var i = 0; i < data.length; i++) {
                        var id = data[i].id;
                        var lastName = data[i].lastName;
                        alert(id + ": " + lastName);
                    }
                });
                return false;
            });
        })
    </script>
</head>

<body>

<form action="testFileUpload" method="POST" enctype="multipart/form-data">
    File: <input type="file" name="file"/>
    Desc: <input type="text" name="desc"/>
    <input type="submit" value="Submit"/>
</form>

<br><br>

<a href="emps">List All Employees</a>
<br><br>

<a href="testJson" id="testJson">Test Json</a>
<br><br>

<form action="testHttpMessageConverter" method="POST" enctype="multipart/form-data">
    File: <input type="file" name="file"/>
    Desc: <input type="text" name="desc"/>
    <input type="submit" value="testHttpMessageConverter"/>
</form>

<br><br>

<a href="testResponseEntity">Test ResponseEntity</a>

<!--
    关于国际化:
    1. 在页面上能够根据浏览器语言设置的情况对文本(不是内容)、时间、数值进行本地化处理。
    2. 可以在 bean 中获取国际化资源文件与 Locale 匹配的消息。
    3. 可以通过超链接切换 Locale，而不再依赖于浏览器的语言设置情况

    解决:
    1. 使用 JSTL 的 fmt 标签
    2. 在 bean 中注入 ResourceBundleMessageSource 的示例, 使用其对应的 getMessage 方法即可
    3. 配置 LocalResolver 和 LocaleChangeInterceptor
-->
<br><br>
<a href="i18n">I18N PAGE</a>

<br><br>
<a href="testExceptionHandlerExceptionResolver?i=0">Test ExceptionHandlerExceptionResolver</a>

<br><br>
<a href="testResponseStatusExceptionResolver1?i=0">Test ResponseStatusExceptionResolver 1</a>
<br><br>
<a href="testResponseStatusExceptionResolver2?i=0">Test ResponseStatusExceptionResolver 2</a>

<br><br>
<a href="testDefaultHandlerExceptionResolver">Test DefaultHandlerExceptionResolver</a>

<br><br>
<a href="testSimpleMappingExceptionResolver?i=11">Test SimpleMappingExceptionResolver</a>
</body>
</html>