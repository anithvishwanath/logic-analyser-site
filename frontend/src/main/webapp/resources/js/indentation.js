$(function() {
	var indentationArray = $("#indentationContainer").data("indentation");
	// alert(indentationArray);
	console.log(indentationArray);
	Highcharts.chart('indentationContainer', {

		chart : {
			type : 'columnrange',
			inverted : true
		},
		title : {
			text : 'Indentation Summary'
		},
		xAxis : {
			visible : false
		},
		yAxis : {

			visible : false

		},
		tooltip : {
			enabled : false
		},
		legend:{
			enabled: false
		},
		
		exporting : {
			enabled : false
		},
		series : [ {
			name : 'Indentation',
			data : indentationArray

		} ]

	});
});