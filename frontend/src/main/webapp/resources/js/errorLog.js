$(function() {
	$(".filter-btn").on("click", function() {
		setTimeout(filterMarkers, 10);
	});
	
	$(".markerDescription").on("click", function() {
		$(this).parent().next(".markerSuggestion").toggleClass("hidden");
	});
});

function filterMarkers() {
	var showInfo = $("#toggle-info").hasClass("active");
	var showWarning = $("#toggle-warning").hasClass("active");
	var showError = $("#toggle-error").hasClass("active");
	var showCritical = $("#toggle-critical").hasClass("active");
	
	$("[data-severity=Informational]").toggleClass("hidden", !showInfo);
	$("[data-severity=Warning]").toggleClass("hidden", !showWarning);
	$("[data-severity=Error]").toggleClass("hidden", !showError);
	$("[data-severity=CriticalError]").toggleClass("hidden", !showCritical);
}