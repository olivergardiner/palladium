<#list types as type>{ "id": "${type.key}", "name": "${type.name}"<#if type.parent??>, "parent": "${type.parent}"</#if><#if type.root??>, "root": "${type.root}"</#if> }
</#list>