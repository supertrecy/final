<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>用户操作界面</title>
	<style type="text/css">
		div{
		margin:0 100px 25px 100px;
		padding:100px;
		background: #d8d8d8;
		text-align: center;
		}
		h3{
		text-align: center;
		}
	</style>
</head>
<body>
	<h3>DEMO1 点击后就能得到数据库中所有新闻的溯源结果</h3>
	<div><a>点击click me！</a></div>
	<h3>DEMO2 输入关键词后能得到数据库中已存在新闻的溯源结果</h3>
	<div>
	<form action="">
	<label>请输入关键词：</label>
	<input type="text">
	<input type="submit">
	</form>
	</div>
</body>
</html>