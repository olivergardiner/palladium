<#list enumerations as enum>{ "id": "${enum.key}", "name": "${enum.name}", "values": [<#list enum.literals as literal> { "value": ${literal.value}, "name": "${literal.label}" }<#if literal?has_next>,</#if></#list> ] }
</#list>