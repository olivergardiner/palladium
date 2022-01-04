<!doctype html>
<html lang="en">
  <head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <!-- <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"> -->
    <link rel="stylesheet" href="bootstrap.min.css">
	<!-- Font Awesome -->
	<!-- <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.8.2/css/all.css"> -->
	<link rel="stylesheet" href="fontawesome/css/all.min.css">
	<!-- Material Design Bootstrap -->
	<!-- <link href="https://cdnjs.cloudflare.com/ajax/libs/mdbootstrap/4.10.1/css/mdb.min.css" rel="stylesheet"> -->
	<link rel="stylesheet" href="mdb.min.css">
	<!-- Local CSS -->
    <link rel="stylesheet" href="style.css">

    <title>Logical Data Model Browser</title>
  </head>
  <body>
	<nav class="fixed-navbar navbar-dark fixed-top bg-dark flex-md-nowrap p-0 shadow">
		<a class="navbar-brand col-sm-3 col-md-2 mr-0" href="#"><h4>Logical Data Model</h4></a>
		<!--<input class="form-control form-control-dark w-100" type="text" placeholder="Search" aria-label="Search">
		<ul class="navbar-nav px-3">
			<li class="nav-item text-nowrap">
				<a class="nav-link" href="#">Sign out</a>
			</li>
		</ul>-->
	</nav>
  	<div class="container-fluid">
	  	<div class="row">
		    <nav class="col-3 d-none d-block bg-light sidebar">
		    	<div class="sidebar-sticky overflow-auto">
				
			 <div class="treeview border">
				<h5 id="model" class="pt-3 pl-3">Documentation</h5>
				<#if documentation?size gt 0>
					<ul class="mb-1 pl-3 pb-2">
					<#list documentation as package>
					<#if package.contents gt 0>
						<@folder package=package/>
					<#else>
						<@empty_folder package=package/>
					</#if>
					</#list>
					</ul>
				</#if>
			</div>
					<div class="treeview border">
						<h5 id="model" class="package-select pointer pt-3 pl-3" data-path=".">Data Catalogue</h5>
<#macro folder package>
	<li>
		<i class="fas fa-angle-right rotate"></i>
		<span class="package-select pointer" data-path="${package.path}"><i class="far fa-folder-open ic-w mx-1"></i>${package.name}</span>
		<ul class="nested">
		<#list package.packages as sub_package>
			<#if sub_package.contents gt 0>
				<@folder package=sub_package/>
			<#else>
				<@empty_folder package=sub_package/>
			</#if>
		</#list>
		<#if package.entities??>
			<#list package.entities as entity>
				<li><span class="entity-select pointer" data-path="${package.path}" data-name="${entity.name}"><i class="fas fa-asterisk ic-w mr-1"></i>${entity.name}</span></li>
			</#list>
		</#if>
		<#if package.interfaces??>
		<#list package.interfaces as interface>
			<li><span class="interface-select pointer" data-path="${package.path}" data-name="${interface.name}"><i class="far fa-circle ic-w mr-1"></i>${interface.name}</span></li>
		</#list>
		</#if>
		<#if package.data_types??>
		<#list package.data_types as data_type>
			<li><span class="data-type-select pointer" data-path="${package.path}" data-name="${data_type.name}"><i class="fas fa-circle ic-w mr-1"></i>${data_type.name}</span></li>
		</#list>
		</#if>
		<#if package.reference_entities??>
		<#list package.reference_entities as reference_entity>
			<li><span class="reference-entity-select pointer" data-path="${package.path}" data-name="${reference_entity.name}"><i class="far fa-dot-circle ic-w mr-1"></i>${reference_entity.name}</span></li>
		</#list>
		</#if>
		<#if package.enumerations??>
		<#list package.enumerations as enumeration>
			<li><span class="enumeration-select pointer" data-path="${package.path}" data-name="${enumeration.name}"><i class="fas fa-dot-circle ic-w mr-1"></i>${enumeration.name}</span></li>
		</#list>
		</#if>
		</ul>
	</li>
</#macro>
					
<#macro empty_folder package>
	<li>
		<i class="fas fa-angle-right invisible"></i>
		<span class="package-select pointer" data-path="${package.path}"><i class="far fa-folder-open ic-w mx-1"></i>${package.name}</span>
	</li>
</#macro>
											
<#if packages?size gt 0>
	<ul class="mb-1 pl-3 pb-2">
	<#list packages as package>
	<#if package.contents gt 0>
		<@folder package=package/>
	<#else>
		<@empty_folder package=package/>
	</#if>
	</#list>
	</ul>
</#if>
					</div>

					<div class="treeview border">
						<h5 id="model" class="pt-3 pl-3">Tutorials</h5>
<#if tutorials?size gt 0>
	<ul class="mb-1 pl-3 pb-2">
	<#list tutorials as package>
	<#if package.contents gt 0>
		<@folder package=package/>
	<#else>
		<@empty_folder package=package/>
	</#if>
	</#list>
	</ul>
</#if>
					</div>
		    	</div>
		    </nav>
			<main role="main" class="col-9 ml-sm-auto px-4">
				<div id="main">
				</div>
			</main>
	    </div>
    </div>

    <!-- Optional JavaScript -->
    <!-- jQuery first, then Popper.js, then Bootstrap JS -->
    <!-- <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script> -->
    <!-- <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"></script> -->
    <!-- <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script> -->
	<!-- <script src="https://cdnjs.cloudflare.com/ajax/libs/mdbootstrap/4.10.0/js/mdb.min.js"></script> -->
    <script src="jquery-3.4.1.min.js"></script>
    <script src="popper.min.js"></script>
    <script src="bootstrap.min.js"></script>
	<script src="mdb.min.js"></script>
	<script src="svg-pan-zoom.js"></script>
	<script src="pldm.js"></script>
  </body>
</html>