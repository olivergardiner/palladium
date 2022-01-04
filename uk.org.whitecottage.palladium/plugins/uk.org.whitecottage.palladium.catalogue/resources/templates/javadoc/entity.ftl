<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Overview</title>
		<link rel="stylesheet" type="text/css" href="<#if rootPath?length gt 1>${rootPath}</#if>stylesheet.css" title="Style">
	</head>
	<body>
		<!-- <div class="topNav"></div> -->
		<div class="header">
			<div class="subTitle">${qualifiedPackageName}</div>
			<h2 title="Class NodeModel" class="title">${entityName}</h2>
		</div>
		<div class="contentContainer">
			<ul class="inheritance">
				<li>
					<#list entityHierarchy as ancestor>
					<ul class="inheritance">
						<li><#if ancestor.entityPath != ""><a href="${rootPath}${ancestor.entityPath}.html">${ancestor.qualifiedEntityName}</a><#else>${ancestor.qualifiedEntityName}</#if></li>
						<li>
					</#list>
					<#list entityHierarchy as ancestor>
						</li>
					</ul>
					</#list>
				</li>
			</ul>
			<div class="description">
				<ul class="blockList">
					<li class="blockList">
						<hr>
						<br>
						<pre><#if isAbstract>abstract </#if><span class="strong">${entityName}</span><#if parent != ""> extends ${parent}</#if></pre>
						
						<#if interfaces?size gt 0>
						<br>
						<#assign separator="">
						<pre>implements <#list interfaces as interface><#if interface.interfacePath != "">${separator}<a href="${rootPath}${interface.interfacePath}.html">${interface.qualifiedInterfaceName}</a><#else>${interface.qualifiedInterfaceName}</#if><#assign separator=", "></#list></pre>
						</#if>

						<div class="block">
							<#list comments as comment><p style="white-space: pre-line">${comment}</p></#list>
						</div>
					</li>
				</ul>
			</div>
			<div class="summary">
				<ul class="blockList">
					<li class="blockList">
<!-- ========== ATTRIBUTE SUMMARY =========== -->
						<#assign total=attributes?size + inheritedAttributes?size + implementedAttributes?size>
						<#if total gt 0>
							<ul class="blockList">
								<li class="blockList">
									<h3>Attribute Summary</h3>
									<#if attributes?size gt 0>
									<table class="overviewSummary" border="0" cellpadding="3" cellspacing="0" summary="Attribute Summary table, listing attributes and an explanation">
										<caption><span>Attributes</span><span class="tabEnd">&nbsp;</span></caption>
										<tbody>
											<tr>
												<th class="colFirst" scope="col">Type and Multiplicity</th>
												<th class="colLast" scope="col">Attribute</th>
											</tr>
											<#assign odd=true>
											<#list attributes as attribute>
												<#assign odd=!odd>
												<tr class="<#if odd>altColor<#else>rowColor</#if>">
													<td class="colFirst"><code>${attribute.stereotypes}&nbsp;<#if attribute.typePath?length gt 0><a href="${rootPath}${attribute.typePath}.html"></#if>${attribute.type}<#if attribute.typePath?length gt 0></a></#if>[${attribute.cardinality}]</code></td>
													<td class="colLast"><code><strong>${attribute.name}</strong></code></td>
												</tr>
											</#list>
										</tbody>
									</table>
									</#if>

									<#list inheritedAttributes as superClass>
										<#if superClass.attributes?size gt 0>
											<ul class="blockList">
												<li class="blockList">
													<h3>Attributes inherited from <a href="${rootPath}${superClass.entityPath}.html" title="${superClass.entityName}">${superClass.entityName}</a></h3>
													<#assign separator="">
													<#list superClass.attributes as attribute><code>${separator}${attribute}</code><#assign separator=", "></#list>
												</li>
											</ul>
										</#if>
									</#list>

									<#list implementedAttributes as interface>
										<#if interface.attributes?size gt 0>
											<ul class="blockList">
												<li class="blockList">
													<h3>Attributes directly implemented from <a href="${rootPath}${interface.entityPath}.html" title="${interface.entityName}">${interface.entityName}</a></h3>
													<#assign separator="">
													<#list interface.attributes as attribute><code>${separator}${attribute}</code><#assign separator=", "></#list>
												</li>
											</ul>
										</#if>
									</#list>
								
								</li>
							</ul>
						</#if>
<!-- ========== ATTRIBUTE SUMMARY =========== -->
<!-- ========== ENUMERATION SUMMARY =========== -->
						<#if enumerationValues?size gt 0>
							<ul class="blockList">
								<li class="blockList">
									<h3>Enumeration Values</h3>
									<table class="overviewSummary" border="0" cellpadding="3" cellspacing="0" summary="Attribute Summary table, listing attributes and an explanation">
										<caption><span>Values</span><span class="tabEnd">&nbsp;</span></caption>
										<tbody>
											<tr>
												<th class="colFirst" scope="col">Term Value</th>
												<th class="colLast" scope="col">Description</th>
											</tr>
											<#assign odd=true>
											<#list enumerationValues as value>
												<#assign odd=!odd>
												<tr class="<#if odd>altColor<#else>rowColor</#if>">
													<td class="colFirst"><code>${value.name}</code></td>
													<td class="colLast" style="white-space: pre-line"><#list value.comments as comment>${comment}</#list></td>
												</tr>
											</#list>
										</tbody>
									</table>
								</li>
							</ul>
						</#if>
<!-- ========== ENUMERATION SUMMARY =========== -->
<!-- ========== ASSOCIATION SUMMARY =========== -->
						<#if associations?size gt 0>
							<ul class="blockList">
								<li class="blockList">
									<h3>Association Summary</h3>
									<table class="overviewSummary" border="0" cellpadding="3" cellspacing="0" summary="Association Summary table, listing associations and an explanation">
										<caption><span>Associations</span><span class="tabEnd">&nbsp;</span></caption>
										<tbody>
											<tr>
												<th class="colFirst" scope="col">Entity and Cardinality</th>
												<th class="colLast" scope="col">Description</th>
											</tr>
											<#assign odd=true>
											<#list associations?sort_by("name") as association>
												<#assign odd=!odd>
												<tr class="<#if odd>altColor<#else>rowColor</#if>">
													<td class="colFirst"><code><#if association.typePath?length gt 0><a href="${rootPath}${association.typePath}.html"></#if>${association.type}<#if association.typePath?length gt 0></a></#if> ${association.cardinality}</code></td>
													<td class="colLast" style="white-space: pre-line"><#list association.comments as comment>${comment}</#list></td>
												</tr>
											</#list>
										</tbody>
									</table>
								</li>
							</ul>
						</#if>
<!-- ========== ASSOCIATION SUMMARY =========== -->
<!-- ========== ATTRIBUTE DETAIL =========== -->
						<#if attributes?size gt 0>
							<ul class="blockList">
								<li class="blockList">
									<h3>Attribute Detail</h3>
									<#list attributes as attribute>
										<ul class="blockList">
											<li class="blockList">
												<h3>${attribute.stereotypes}${attribute.type}[${attribute.cardinality}] ${attribute.name}</h3>
												<div class="block" style="white-space: pre-line"><#list attribute.comments as comment>${comment}</#list></div>
											</li>
										</ul>
									</#list>
								</li>
							</ul>
						</#if>
<!-- ========== ATTRIBUTE DETAIL =========== -->
					</li>
				</ul>
			</div>
		</div>
		<!-- <div class="bottomNav"></div> -->
	</body>
</html>