var langData = $("#languageVis").data("langtype");

Highcharts.chart('languageVis', {
	chart : {
		type : 'bar'
	},
	title : {
		text : 'Languages Used'
	},
	exporting : {
		enabled : false
	},
	xAxis : {
		categories : [ 'Percentage' ]
	},
	yAxis : {
		min : 0,
		visible : false
	},
	legend : {
		reversed : true
	},
	plotOptions : {
		series : {
		
			stacking : 'normal',
			// Data Labels for Chart annotations
			dataLabels: {
				enabled: true,
                color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white'
			}	
		}
	},
	series : langData
})