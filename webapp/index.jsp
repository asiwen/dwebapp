<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
    <title>HelloWorldJSP~</title>
</head>
<body>
    <%
    Date d = new Date();
    		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		String now = df.format(d);

    out.println("HelloJsp: " + now);
    %>
</body>
</html>