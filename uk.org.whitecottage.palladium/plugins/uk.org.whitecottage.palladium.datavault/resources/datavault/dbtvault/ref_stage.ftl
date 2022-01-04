{%- set yaml_metadata -%}
source_model: 'ctx_variant_value'
derived_columns:
  RECORD_SOURCE: '!variant_value.ndjson'
hashed_columns:
  VARIANT_VALUE_HASHDIFF:
    is_hashdiff: true
    columns:
      - 'DEFINITION'
{%- endset -%}

{% set metadata_dict = fromyaml(yaml_metadata) %}

{% set source_model = metadata_dict['source_model'] %}

{% set derived_columns = metadata_dict['derived_columns'] %}

{% set hashed_columns = metadata_dict['hashed_columns'] %}

WITH staging AS (
{{ dbtvault.stage(include_source_columns=true,
                  source_model=source_model,
                  derived_columns=derived_columns,
                  hashed_columns=hashed_columns,
                  ranked_columns=none) }}
)

SELECT *,
{# We can opt to use natural keys for reference data but we need to copy into a separate column (or the hub will complain) thus: #}
       code AS CODE_PK,
       TO_DATE('{{ var('load_date') }}') AS LOAD_DATE,
       TO_DATE('{{ var('load_date') }}') AS EFFECTIVE_FROM
FROM staging