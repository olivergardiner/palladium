$(function() {
	$('.treeview').mdbTreeview();
	
	$(".package-select").click(function() {
		var path = $(this).attr("data-path");
		$("#main").load(path + "/package.html");
	});
	
	$(".entity-select").click(function() {
		var type = $(this).attr("data-name");
		var path = $(this).attr("data-path");
		$("#main").load(path + "/E_" + type + ".html");
	});
	
	$(".interface-select").click(function() {
		var type = $(this).attr("data-name");
		var path = $(this).attr("data-path");
		$("#main").load(path + "/I_" + type + ".html");
	});
	
	$(".data-type-select").click(function() {
		var type = $(this).attr("data-name");
		var path = $(this).attr("data-path");
		$("#main").load(path + "/D_" + type + ".html");
	});
	
	$(".reference-entity-select").click(function() {
		var type = $(this).attr("data-name");
		var path = $(this).attr("data-path");
		$("#main").load(path + "/R_" + type + ".html");
	});
	
	$(".enumeration-select").click(function() {
		var type = $(this).attr("data-name");
		var path = $(this).attr("data-path");
		$("#main").load(path + "/L_" + type + ".html");
	});

});

$("#main").load("Documentation/SubjectAreaModel/package.html");

function spz(selector) {
	svgPanZoom(selector, {
		zoomEnabled: false,
		controlIconsEnabled: true
	});
}
