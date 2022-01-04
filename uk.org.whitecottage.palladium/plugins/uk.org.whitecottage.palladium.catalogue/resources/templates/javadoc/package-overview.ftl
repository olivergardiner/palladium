<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Overview</title>
		<link rel="stylesheet" type="text/css" href="<#if rootPath?length gt 1>${rootPath}</#if>stylesheet.css" title="Style">
		<script src="<#if rootPath?length gt 1>${rootPath}</#if>../js/svg-pan-zoom.js"></script>
		<script>
			function spz(selector) {
				svgPanZoom(selector, {
					zoomEnabled: false,
					controlIconsEnabled: true
				});
			}
		</script>
		<style>
			.diagram {
				width: 100%;
				border: 1px solid black;
				padding: 10px;
			}
			
			.diagram-div {
				width: 90%;
				margin: auto;
			}
		</style>
	</head>
	<body>
		<!-- <div class="topNav"></div> -->
		<div class="header">
			<h2 title="Class NodeModel" class="title">${qualifiedPackageName}</h2>
		</div>
		<div class="contentContainer">
			<div class="description">
				<div class="block">
					<#list comments as comment><p style="white-space: pre-line">${comment}</p></#list>
				</div>
			</div>
		</div>
		<!-- <div class="bottomNav"></div> -->
	</body>
</html>