{%- set source_model = "stg_${name}" -%}
{%- set src_pk = "HASHKEY" -%}
{%- set src_hashdiff = "HASHDIFF" -%}
{%- set src_payload = [<#list properties as property>"${property.name}"<#if property?has_next>, </#if></#list>] -%}
{%- set src_eff = "EFFECTIVE_FROM" -%}
{%- set src_ldts = "LOAD_DATE" -%}
{%- set src_source = "RECORD_SOURCE" -%}

{{ dbtvault.sat(src_pk=src_pk, src_hashdiff=src_hashdiff,
                src_payload=src_payload, src_eff=src_eff,
                src_ldts=src_ldts, src_source=src_source,
                source_model=source_model) }}