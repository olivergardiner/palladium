<div class="justify-content-start align-items-left pt-2 pb-2 mb-2 border-bottom">
	<h5>${type} ${qualifiedPackageName}::${entityName}</h5>
	
	<h6 class="mt-4">Inheritance</h6>
	<ul class="inheritance">
		<li>
			<ul class="inheritance">
				<li>${type}</li>
				<li>
					<#list entityHierarchy as ancestor>
					<ul class="inheritance">
						<li>${ancestor.qualifiedEntityName}</li>
						<li>
					</#list>
					<#list entityHierarchy as ancestor>
						</li>
					</ul>
					</#list>
				</li>
			</ul>
		</li>
	</ul>
</div>

<div class="justify-content-start align-items-left pt-2 pb-2 mb-2">
	<h6 class="pb-2">
		<pre><#if isAbstract>abstract </#if><span class="strong">${entityName}</span><#if parent != ""> extends ${parent}</#if></pre>
		<#if interfaces?size gt 0>
		<#assign separator="">
		<pre> implements <#list interfaces as interface>${interface.qualifiedInterfaceName}<#assign separator=", "></#list></pre>
		</#if>
	</h6>

	<#list comments as comment><p style="white-space: pre-line">${comment}</p></#list>
	</div>
</div>

<#assign total=attributes?size + inheritedAttributes?size + implementedAttributes?size>
<#if total gt 0>
<div class="justify-content-start align-items-left px-2 py-3 mb-2">
	<div class="my-1 mx-2 p-1 bg-light border">
	<#if attributes?size gt 0>
		<h6 class="p-2">Attributes</h6>
		
		<table class="table table-sm table-striped border ml-3 mr-3">
			<colgroup>
				<col style="width: 20%">
				<col style="width: 15%">
				<col style="width: 65%">
			</colgroup>
			<thead class="table-dark">
				<tr class="mr-3">
					<th scope="col">Type and Multiplicity</th>
					<th scope="col">Name</th>
					<th scope="col">Description</th>
				</tr>
			</thead>
			<tbody>
			<#list attributes as attribute>
				<tr id="attr_${attribute.name}">
					<td><code>${attribute.type}[${attribute.cardinality}]</code></td>
					<td>${attribute.name}</td>
					<td><#list attribute.comments as comment>${comment}</#list></td>
				</tr>
			</#list>
			</tbody>
		</table>
	</#if>
		
	<#list inheritedAttributes as superClass>
		<#if superClass.attributes?size gt 0>
		<h6 class="p-2">Attributes inherited from ${superClass.entityName}</h6>

		<table class="table table-sm table-striped border ml-3 mr-5">
			<col style="width: 20%">
			<col style="width: 15%">
			<col style="width: *">
			<thead class="table-dark">
				<tr>
					<th scope="col">Type and Multiplicity</th>
					<th scope="col">Name</th>
					<th scope="col">Description</th>
				</tr>
			</thead>
			<tbody>
			<#list superClass.attributes as attribute>
				<tr>
					<td><code>${attribute.type}[${attribute.cardinality}]</code></td>
					<td>${attribute.name}</td>
					<td><#list attribute.comments as comment><p style="white-space: pre-line">${comment}</p></#list></td>
				</tr>
			</#list>
			</tbody>
		</table>
		</#if>
	</#list>

	<#list implementedAttributes as interface>
		<#if interface.attributes?size gt 0>
		<h6 class="p-2">Attributes directly implemented from ${interface.entityName} (does not include implied sub-interfaces)</h6>

		<table class="table table-sm table-striped border ml-3 mr-5">
			<col style="width: 20%">
			<col style="width: 15%">
			<col style="width: *">
			<thead class="table-dark">
				<tr>
					<th scope="col">Type and Multiplicity</th>
					<th scope="col">Name</th>
					<th scope="col">Description</th>
				</tr>
			</thead>
			<tbody>
			<#list interface.attributes as attribute>
				<tr>
					<td><code>${attribute.type}[${attribute.cardinality}]</code></td>
					<td>${attribute.name}</td>
					<td><#list attribute.comments as comment><p style="white-space: pre-line">${comment}</p></#list></td>
				</tr>
			</#list>
			</tbody>
		</table>
		</#if>
	</#list>

	</div>
</div>
</#if>

<#assign total=associations?size + inheritedAssociations?size + implementedAssociations?size>
<#if total gt 0>
<div class="justify-content-start align-items-left px-2 py-3 mb-2">
	<div class="my-1 mx-2 p-1 bg-light border">
	<#if associations?size gt 0>
		<h6 class="p-2">Associations</h6>
		
		<table class="table table-sm table-striped border ml-3 mr-5">
			<col style="width: 35%">
			<col style="width: *">
			<thead class="table-dark">
				<tr>
					<th scope="col">Type and Cardinality</th>
					<th scope="col">Description</th>
				</tr>
			</thead>
			<tbody>
		<#list associations as association>
				<tr>
					<td>${association.type} ${association.cardinality}</td>
					<td><#list association.comments as comment><p style="white-space: pre-line">${comment}</p></#list></td>
				</tr>
		</#list>
			</tbody>
		</table>
	</#if>
	
	<#list inheritedAssociations as superClass>
		<#if superClass.associations?size gt 0>
		<h6 class="p-2">Associations inherited from ${superClass.entityName}</h6>
		
		<table class="table table-sm table-striped border ml-3 mr-5">
			<col style="width: 35%">
			<col style="width: *">
			<thead class="table-dark">
				<tr>
					<th scope="col">Type and Cardinality</th>
					<th scope="col">Description</th>
				</tr>
			</thead>
			<tbody>
			<#list superClass.associations as association>
				<tr>
					<td>${association.type} ${association.cardinality}</td>
					<td><#list association.comments as comment><p style="white-space: pre-line">${comment}</p></#list></td>
				</tr>
			</#list>
			</tbody>
		</table>
		</#if>
	</#list>
	
	<#list implementedAssociations as interface>
		<#if interface.associations?size gt 0>
		<h6 class="p-2">Associations inherited from ${interface.entityName}</h6>
		
		<table class="table table-sm table-striped border ml-3 mr-5">
			<col style="width: 35%">
			<col style="width: *">
			<thead class="table-dark">
				<tr>
					<th scope="col">Type and Cardinality</th>
					<th scope="col">Description</th>
				</tr>
			</thead>
			<tbody>
			<#list interface.associations as association>
				<tr>
					<td>${association.type} ${association.cardinality}</td>
					<td><#list association.comments as comment><p style="white-space: pre-line">${comment}</p></#list></td>
				</tr>
			</#list>
			</tbody>
		</table>
		</#if>
	</#list>
		
	</div>
</div>
</#if>

<#assign total=enumerationValues?size + inheritedEnumerationValues?size>
<#if total gt 0>
<div class="justify-content-start align-items-left px-2 py-3 mb-2">
	<div class="my-1 mx-2 p-1 bg-light border">
	<#if enumerationValues?size gt 0>
		<h6 class="p-2">Enumeration Literals</h6>
		
		<table class="table table-sm table-striped border ml-3 mr-5">
			<col style="width: 35%">
			<col style="width: *">
			<thead class="table-dark">
				<tr>
					<th scope="col">Value</th>
					<th scope="col">Definition</th>
				</tr>
			</thead>
			<tbody>
		<#list enumerationValues as enumeration>
				<tr>
					<td>${enumeration.name}</td>
					<td><#list enumeration.comments as comment><p style="white-space: pre-line">${comment}</p></#list></td>
				</tr>
		</#list>
			</tbody>
		</table>
	</#if>

	<#list inheritedEnumerationValues as superClass>
		<#if superClass.enumerationValues?size gt 0>
		<h6 class="p-2">Enumeration literals inherited from ${superClass.entityName}</h6>

		<table class="table table-sm table-striped border ml-3 mr-5">
			<col style="width: 35%">
			<col style="width: *">
			<thead class="table-dark">
				<tr>
					<th scope="col">Value</th>
					<th scope="col">Definition</th>
				</tr>
			</thead>
			<tbody>
			<#list superClass.enumerationValues as enumeration>
				<tr>
					<td>${enumeration.name}</td>
					<td><#list enumeration.comments as comment><p style="white-space: pre-line">${comment}</p></#list></td>
				</tr>
			</#list>
			</tbody>
		</table>

		</#if>
	</#list>
	
	</div>
</div>
</#if>
