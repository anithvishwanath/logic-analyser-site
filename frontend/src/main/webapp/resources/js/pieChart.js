$(function(){
	var warningCount= $("#pieChartContainer").data("warningcount");
	var errors = $("#pieChartContainer").data("errorcount");
	var criticalErrorCount=$("#pieChartContainer").data("criticalerrorcount");
	
	Highcharts.chart('pieChartContainer', {
	    chart: {
	        plotBackgroundColor: null,
	        plotBorderWidth: null,
	        plotShadow: false,
	        type: 'pie'
	        	
	    },
	    title: {
	        text: 'Severity Rating'
	    },
	    tooltip: {
	        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
	    },
	    plotOptions: {
	        pie: {
	        	 colors: [
	        	     '#CC3B2E', 
	        	     //CE
	        	     '#730500',
	        	     //E
	        	     '#F89406'
	        	     //W
	        	     
	        	   ],
	            allowPointSelect: true,
	            cursor: 'pointer',
	            dataLabels: {
	                enabled: true,
	                format: '<b>{point.name}</b>: {point.percentage:.1f} %',
	                style: {
	                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
	                }
	            }
	        }
	    },
	    exporting : {
			enabled : false
		},
	    series: [{
	        name: '',
	        colorByPoint: true,
	        data: [{
	            name: 'Errors',
	            y: errors,
	            sliced: true,
	            selected: true
	        }, {
	            name: 'Critical Errors',
	            y: criticalErrorCount
	        }, {
	            name: 'Warnings',
	            y: warningCount
	        }]
	    }]
	});


	
})

