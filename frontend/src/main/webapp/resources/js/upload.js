$(function() {
	$("#file").on("change", function(e) {
		var files = e.target.files;
		if (files.length > 0) {
			$("#selected-file").html(files[0].name);
		} else {
			$("#selected-file").html("");
		}
	});
});