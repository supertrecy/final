<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户操作界面</title>
<style type="text/css">
div {
	margin: 0 100px 25px 100px;
	padding: 100px;
	background: #d8d8d8;
	text-align: center;
}

h3 {
	text-align: center;
}
</style>
<body>
	<h3>DEMO1 点击后就能得到数据库中所有新闻的溯源结果</h3>
	<div>
		<ul style="list-style: none;">
		<li><a href="demo1_0.jsp">demo1.0</a></li>
		<li><a href="demo1_1.jsp">demo1.1</a></li>
		</ul>
	</div>
	<h3>DEMO2 输入关键词后能得到数据库中已存在新闻的溯源结果</h3>
	<div>
		<form action="TraceToSourceServlet">
			<label>请输入关键词：</label> <input type="text" name="keyword"> <input
				type="submit">
		</form>
	</div>
	<h3>DEMO3 输入关键词后实时抓取新闻的溯源结果</h3>
	<div>
		<form action="TraceToSourceRealTimeServlet">
			<label>请输入关键词：</label> <input type="text" name="keyword"> <input
				type="submit">
		</form>
	</div>
</body>
</html>