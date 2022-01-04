{%- set source_model = "stg_${name}" -%}
{%- set src_pk = "HASHKEY" -%}
<#if identifiers?size == 1>
{%- set src_nk = "${identifiers[0]}" -%}
<#else>
{%- set src_nk = "[ <#list identifiers as identifier>"${identifier}"<#if identifier?has_next>, </#if></#list> ]}" -%}
</#if>
{%- set src_ldts = "LOAD_DATE" -%}
{%- set src_source = "RECORD_SOURCE" -%}

{{ dbtvault.hub(src_pk=src_pk, src_nk=src_nk, src_ldts=src_ldts,
                src_source=src_source, source_model=source_model) }}