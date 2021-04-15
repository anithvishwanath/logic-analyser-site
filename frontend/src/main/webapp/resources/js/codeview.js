var editor;
var Range = ace.require('ace/range').Range;
var markers;
var aceMarkerMap = {};

$(function() {
	editor = ace.edit("codeView");
	
	var language = $('#codeView').data('lang');
	if (language) {
		editor.session.setMode("ace/mode/" + language);
	} else {
		editor.session.setMode("ace/mode/html");
	}
	
	editor.session.setUseWorker(false);
	editor.session.setTabSize(4);
	editor.setReadOnly(true);
	editor.setShowFoldWidgets(false);
	
	editor.session.on("changeScrollTop", function(e) {
		moveMarkers();
		setTimeout(moveMarkers, 100);
	});

	var selectedLine = $('#codeView').data('line');
	if (selectedLine) {
		selectedLine = parseInt(selectedLine);
		
		editor.session.addMarker(new Range(selectedLine-1, 0, selectedLine-1, 1), "selected-line", "fullLine");
		editor.scrollToLine(selectedLine, true, true);
	}
	
	createMarkers();
	setTimeout(moveMarkers, 100);
});

var highlightLineMarker;
function highlightLine(line, show) {
	console.log(line);
	if (highlightLineMarker) {
		editor.session.removeMarker(highlightLineMarker);
	}
	if (show) {
		highlightLineMarker = editor.session.addMarker(new Range(line, 0, line, 1), "highlighted-line", "fullLine");
	}
}

function createMarkers() {
	markers = $("#codeView").data("markers");
	var editSession = editor.getSession();
	
	$(markers).each(function (key, marker) {
		var startLocation = editSession.getDocument().indexToPosition(marker.start, 0);
		var endLocation = editSession.getDocument().indexToPosition(marker.end, 0);
		
		var styleClass;
		switch (marker.type) {
		case 0: // Information
			styleClass = "marker information";
			break;
		case 1: // Warning
			styleClass = "marker warning";
			break;
		default:
		case 2: // Error
			styleClass = "marker error";
			break;
		case 3: // Critical Error
			styleClass = "marker critical";
			break;
		}
		
		styleClass += " marker-id-" + marker.id;
		
		var id = editSession.addMarker(
			new Range(
				startLocation.row, 
				startLocation.column, 
				endLocation.row, 
				endLocation.column
			),
			styleClass
		);
		
		$("#marker-" + marker.id).hide();
		aceMarkerMap[marker.id] = id;
	});
}

function moveMarkers() {
	var config = editor.renderer.layerConfig;
	var padding = 4;
	var buffer = 3;
	
	var last = -10000;
	$(markers).each(function (key, marker) {
		var line = editor.session.getDocument().indexToPosition(marker.start, 0).row;
		
		var errorNode = $("#marker-" + marker.id);
		if (line < config.firstRow - buffer || line > config.lastRow + buffer) {
			errorNode.hide();
		} else {
			errorNode.show();
			
			var top = (line - config.firstRow) * config.lineHeight - config.offset;
			
			if (top < last) {
				top = last;
			}
			
			last = top + errorNode[0].offsetHeight + padding;
			
			errorNode.css("position", "absolute");
			errorNode.css("top", top);
		}
	});
}

$(function() {
	$(".marker-view").hover(
		function() { // In
			var id = $(this).data("mid");
			var selected;
			$(markers).each(function (key, marker) {
				if (marker.id == id) {
					selected = marker;
				}
			});
			
			var line = editor.session.getDocument().indexToPosition(selected.start, 0).row;
			highlightLine(line, true);
		},
		function() { // Out
			highlightLine();
		}
	);
});