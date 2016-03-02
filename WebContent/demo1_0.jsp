<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>demo1.0 圆形</title>
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

.node circle {
	fill: #fff;
	stroke: steelblue;
	stroke-width: 1.5px;
}

.node {
	font: 10px sans-serif;
}

.link {
	fill: none;
	stroke: #ccc;
	stroke-width: 1.5px;
}
</style>
<body>
	<script src="//d3js.org/d3.v3.min.js"></script>
	<script>
		var diameter = 960;

		var tree = d3.layout.tree().size([ 360, diameter / 2 - 120 ])
				.separation(function(a, b) {
					return (a.parent == b.parent ? 1 : 2) / a.depth;
				});

		var diagonal = d3.svg.diagonal.radial().projection(function(d) {
			return [ d.y, d.x / 180 * Math.PI ];
		});

		var svg = d3.select("body").append("svg").attr("width", diameter).attr(
				"height", diameter - 150).append("g").attr("transform",
				"translate(" + diameter / 2 + "," + diameter / 2 + ")");

		d3.json("flare.json", function(error, root) {
			if (error)
				throw error;

			var nodes = tree.nodes(root), links = tree.links(nodes);

			var link = svg.selectAll(".link").data(links).enter()
					.append("path").attr("class", "link").attr("d", diagonal);

			var node = svg.selectAll(".node").data(nodes).enter().append("g")
					.attr("class", "node").attr(
							"transform",
							function(d) {
								return "rotate(" + (d.x - 90) + ")translate("
										+ d.y + ")";
							})

			node.append("circle").attr("r", 4.5);

			node.append("text").attr("dy", ".31em").attr("text-anchor",
					function(d) {
						return d.x < 180 ? "start" : "end";
					}).attr("transform", function(d) {
				return d.x < 180 ? "translate(8)" : "rotate(180)translate(-8)";
			}).text(function(d) {
				return d.name;
			});
		});

		d3.select(self.frameElement).style("height", diameter - 150 + "px");
	</script>
</body>
</html>