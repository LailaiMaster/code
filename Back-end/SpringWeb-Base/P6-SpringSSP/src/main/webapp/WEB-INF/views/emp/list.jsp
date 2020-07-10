<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Insert title here</title>

    <script type="text/javascript" src="${pageContext.request.contextPath }/scripts/jquery-1.9.1.min.js"></script>

    <script type="text/javascript">
        $(function () {
            $(".delete").click(function () {
                const label = $(this).next(":hidden").val();
                const flag = confirm("确定要删除" + label + "的信息吗?");
                if (flag) {
                    const url = $(this).attr("href");

                    $("#_form").attr("action", url);
                    $("#_method").val("DELETE");
                    $("#_form").submit();
                }
                return false;
            });
        })
    </script>
</head>
<body>

<form action="" method="POST" id="_form">
    <input type="hidden" id="_method" name="_method"/>
</form>

<c:if test="${page == null || page.numberOfElements == 0 }">
    没有任何记录.
</c:if>

<c:if test="${page != null && page.numberOfElements > 0 }">
    <table border="1" cellpadding="10" cellspacing="0">
        <tr>
            <th>ID</th>
            <th>LastName</th>
            <th>Email</th>
            <th>Birth</th>
            <th>CreateTime</th>
            <th>Department</th>
            <th>Edit</th>
            <th>Delete</th>
        </tr>

        <c:forEach items="${page.content }" var="emp">
            <tr>
                <td>${emp.id }</td>
                <td>${emp.lastName }</td>

                <td>${emp.email }</td>
                <td>
                    <fmt:formatDate value="${emp.birth }" pattern="yyyy-MM-dd"/>
                </td>

                <td>
                    <fmt:formatDate value="${emp.createTime }" pattern="yyyy-MM-dd hh:mm:ss"/>
                </td>
                <td>${emp.department.departmentName }</td>

                <td><a href="${pageContext.request.contextPath }/emp/${emp.id}">Edit</a></td>
                <td>
                    <a href="${pageContext.request.contextPath }/emp/${emp.id}" class="delete">Delete</a>
                    <input type="hidden" value="${emp.lastName }"/>
                </td>
            </tr>
        </c:forEach>

        <tr>
            <td colspan="8">
                共 ${page.totalElements } 条记录
                共 ${page.totalPages } 页
                当前 ${page.number + 1 } 页
                <a href="?pageNo=${page.number + 1 - 1 }">上一页</a>
                <a href="?pageNo=${page.number + 1 + 1 }">下一页</a>
            </td>
        </tr>

    </table>
</c:if>

</body>
</html>