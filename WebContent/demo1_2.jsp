<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>D3.js Drag and Drop, Zoomable, Panning, Collapsible Tree with auto-sizing.</title>
<style>
.node {
    cursor: pointer;
  }

  .overlay{
      background-color:#EEE;
  }
   
  .node circle {
    fill: #fff;
    stroke: steelblue;
    stroke-width: 1.5px;
  }
  
  .site {
    fill: #fff;
    stroke: #ff0;
    stroke-width: 1.5px;
  }
   
  .node text {
    font-size:10px; 
    font-family:sans-serif;
  }
   
  .link {
    fill: none;
    stroke: #ccc;
    stroke-width: 1.5px;
  }

  .templink {
    fill: none;
    stroke: red;
    stroke-width: 3px;
  }

  .ghostCircle.show{
      display:block;
  }

  .ghostCircle, .activeDrag .ghostCircle{
       display: none;
  }
  .link-div{
  	display:none;
  	position:absolute;
  	margin:20px;
  	padding:10px;
  	float: right;
  	height:50px;
  	weight:200px;
  	background-color: white;
  	border-radius:5px;
  }
  span{
  	font-weight: bold;
  }
  .line{
  	margin: 3px;
  	padding:2px;
  }
</style>
</head>
<body>
<script src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
<script src="http://d3js.org/d3.v3.min.js"></script>
<script src="dndTree.js"></script>
<body>
	<div class="link-div">
	<div class="line" id="line-title"><span>标题：</span><a class="title" target="_blank" href=""></a></div>
	<div class="line" id="line-source"><span>来源：</span><a class="source" href=""></a></div>
	</div>
    <div id="tree-container"></div>
</body>
</html>