{%- set source_model = "stg_${name}" -%}
{%- set src_pk = "${column1Name}" -%}
{%- set src_fk = "${column2Name}" -%}
{%- set src_ldts = "LOAD_DATE" -%}
{%- set src_source = "RECORD_SOURCE" -%}

{{ dbtvault.link(src_pk=src_pk, src_fk=src_fk, src_ldts=src_ldts,
                 src_source=src_source, source_model=source_model) }}
