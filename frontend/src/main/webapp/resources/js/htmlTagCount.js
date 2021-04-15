var output = d3.select("#htmlTagCountVis");
var width = output.attr("width");
var height = output.attr("height");

var data = {
	children : JSON.parse(output.attr("data-tagcount"))
};

var root = d3.hierarchy(data).sum(function(d) {
	return d.count;
});

var maxErrors = d3.max(
	data.children, 
	function(d) {
		return d.errors;
	})
	;

var bubble = d3.pack()
	.size([width, height])
	.padding(4)
	;

var node = output.selectAll("g").data(bubble(root).leaves()).enter()
		.append("g").attr("transform", function(d) {
			return "translate(" + d.x + "," + d.y + ")";
		});

node.append("circle")
	.attr("r", function(d) {
		return d.r;
	})
	.style("fill", function(d) {
		var weight;
		if (maxErrors == 0) {
			weight = 0;
		} else {
			weight = d.data.errors / maxErrors;
		}
		
		// From: 36, 85, 122
		// To: 198, 21, 36
		var r = (1-weight)*66 + weight*204;
		var g = (1-weight)*133 + weight*59;
		var b = (1-weight)*244 + weight*46;
		
		r = Math.round(r);
		g = Math.round(g);
		b = Math.round(b);
		
		return "rgb(" + r + "," + g + "," + b + ")";
	})
	;

// TODO: This can clip out if the circle is too small
// I can add a clipPath but that doesnt solve the
// Readability issue
node.append("text")
	.style("text-anchor", "middle")
	.style("fill", "#fff")
//	.style("font-size", function(d) {
//		var size = d.r;
//		if (d.data.name.length > 5) {
//			size -= (d.data.name.length - 5) * 10;
//			
//		}
//		console.log(size);
//		return size;
//	})
	.style("dominant-baseline", "central")
	.text(function(d) {
		return d.data.name; 
	})
	;

node.append("title")
	.text(function(d) {
		return "<" + d.data.name + ">\n" + 
			"Occured " + d.data.count + " time(s)\n" +
			d.data.warnings + " warnings\n" + 
			d.data.errors + " errors";
	})
	;