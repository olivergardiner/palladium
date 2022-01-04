<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Package list</title>
		<link rel="stylesheet" type="text/css" href="stylesheet.css" title="Style">
	</head>
	<body>
		<div class="indexHeader"><a href="allentities-frame.html" target="packageFrame">All Entities</a></div>
		<div class="indexContainer">
			<h2 title="Packages">Packages</h2>
			<ul title="Packages">
			<#list packages?sort_by("qualifiedPackageName") as package>
				<li><a href="${package.packagePath}/package-frame.html" target="packageFrame">${package.qualifiedPackageName}</a></li>
			</#list>
			</ul>
		</div>
		<p>&nbsp;</p>
	</body>
</html>