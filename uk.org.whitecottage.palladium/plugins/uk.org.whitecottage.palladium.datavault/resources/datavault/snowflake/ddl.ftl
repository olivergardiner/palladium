<#macro columnType propertyType><#if propertyType == "Identifier">STRING</#if><#if propertyType == "String">STRING</#if><#if propertyType == "Integer">INTEGER</#if><#if propertyType == "Real">REAL</#if><#if propertyType == "Numeric">REAL</#if><#if propertyType == "Boolean">BOOLEAN</#if><#if propertyType == "Date">DATE</#if><#if propertyType == "DateTime">DATETIME</#if><#if propertyType == "Time">TIME</#if><#if propertyType == "JSON">STRING</#if><#if propertyType == "OBJECT">OBJECT</#if><#if propertyType == "ARRAY">ARRAY</#if><#if propertyType == "TIMESTAMP">TIMESTAMP</#if><#if propertyType == "HASHKEY">BINARY</#if></#macro>
/* Datavault DDL for Snowflake generated from the PLDM */

/*
   The Type Hierarchy table simply records parent types and any subtypes used to construct Satellites
   This can be used to unpick which Satellite tables are relevant to a given row in a Hub table. Note
   that there will not be a Satellite table for the asserted (sub)type if the Satellite would have been
   empty but the Type Hierarchy can be traversed to identify all Satellites relevant to a given type.
*/

CREATE TABLE SYS_TypeHierarchy (
   hashKey BINARY PRIMARY KEY,
   name STRING,
   description STRING,
   parent BINARY,
   root BINARY<#if preferences.applyForeignKeys>,
   FOREIGN KEY parent REFERENCES SYS_TypeHierarchy (hashKey),
   FOREIGN KEY root REFERENCES SYS_TypeHierarchy (hashKey)
</#if>
);

/*
   Enumerations are handled in a very similar manner to Controlled Vocabularies and two tables are used:
   * The Enumerations table identifies the class of the Enumeration
   * The Enumeration Values table contain the possible values of the Enumeration literals
   
   The hash keys for Enumeration Values are created hashing both the Enumeration class and the Enumeration
   Literal to ensure uniqueness across all Enumeration classes
*/

/*
   Need to add template variable and template code to make the use of the Enumeration Tables optional.
   
   NB: this will also require additional code and template variables for explicit Enumeration tables.
*/

CREATE TABLE REF_Enumerations (
   hashKey BINARY PRIMARY KEY, /* hashKey is a hash of the Enumeration qualified name */
   name STRING,
   description STRING
);

CREATE TABLE REF_EnumerationValues (
   hashKey BINARY PRIMARY KEY, /* hashKey is a hash of the enumId AND the Enumeration literal name */
   enumId BINARY,
   name STRING,
   description STRING<#if preferences.applyForeignKeys>,
   FOREIGN KEY enumId REFERENCES SYS_Enumerations (hashKey),
</#if>
);

/*
   If used, the Controlled Vocabulary table allows all simple controlled vocabularies (i.e. simple lists
   of terms conforming to the structure (value, name, description) to be held in a single table to avoid
   table bloat.
   
   The Controlled Vocabulary table identifies the specific controlled vocabulary and, as with the Enumeration
   tables, the entries in the Controlled Vocabulary Values table define the individual values for a given
   controlled vocabulary.
   
   NB: As Enumerations are defined by the model, the Enumeration tables are populated directly. The
   Controlled Vocabulary tables, however, must be populated by business process and so carry the usual
   metadata properties of loadTimestamp and recordSource.
*/

<#if preferences.useCVTable>
CREATE TABLE REF_ControlledVocabularies (
   hashKey BINARY PRIMARY KEY, /* hashKey is a hash of the CV qualified name */
   name STRING,
   description STRING
);

CREATE TABLE REF_ControlledVocabularyValues (
   hashKey BINARY PRIMARY KEY /* hashKey should be a hash of the value AND the cvId */
   loadTimestamp TIMESTAMP,
   recordSource STRING,
   cvId BINARY,
   value STRING,
   name STRING,
   description STRING<#if preferences.applyForeignKeys>,
   FOREIGN KEY cvId REFERENCES SYS_ControlledVocabularies (hashKey),
</#if>
);

/*
   Reference Data that does not (or cannot) use the Controlled Vocabulary table is defined with individual
   tables that can reflect the particular struture required.

   Foreign key constraints are applied as ALTER TABLE statements after the tables have been created as it is
   not realistic to sequence the order in which tables are created.
*/

</#if>
<#if referenceEntities?size gt 0>
<#list referenceEntities as referenceEntity>
CREATE TABLE REF_${referenceEntity.name} (
   hashKey value PRIMARY KEY, /* hashKey should be created as a hash of the designated value */
   loadTimestamp TIMESTAMP,
   recordSource STRING,
<#list referenceEntity.properties as property>
   ${property.name} <@columnType property.column.columnType/><#if property.isMandatory> NOT NULL</#if><#if property?has_next>,</#if>
</#list>
);

</#list>
</#if>
<#if referenceEntities?size gt 0 && preferences.applyForeignKeys>
<#list referenceEntities as referenceEntity>
<#list referenceEntity.properties as property>
<#if property.column.isReferenceData>
ALTER TABLE REF_${referenceEntity.name} ADD FOREIGN KEY (${property.name}) REFERENCES REF_${property.column.reference} (hashKey);

</#if>
</#list>
<#if referenceEntity.isChild>
ALTER TABLE REF_${referenceEntity.name} ADD FOREIGN KEY (hashKey) REFERENCES REF_${referenceEntity.root} (hashKey);

</#if>
</#list>
</#if>
<#list hubs as hub>
CREATE TABLE HUB_${hub.name} (
   hashkey BINARY PRIMARY KEY,
   loadTimestamp TIMESTAMP,
   recordSource STRING,
   type BINARY,
<#list hub.identifiers as identifier>
   ${identifier} STRING<#if identifier?has_next || preferences.applyForeignKeys>,</#if>
</#list>
<#if preferences.applyForeignKeys>
   FOREIGN KEY (type) REFERENCES SYS_TypeHierarchy (hashKey)
</#if>
);

</#list>
<#list links as link>
CREATE TABLE LNK_${link.name} (
   hashkey BINARY PRIMARY KEY,
   loadTimestamp TIMESTAMP,
   recordSource STRING,
   ${link.column1Name} HASHKEY,
   ${link.column2Name} HASHKEY<#if preferences.applyForeignKeys>,
   FOREIGN KEY (${link.column1Name}) REFERENCES HUB_${link.hub1Name} (hashKey),
   FOREIGN KEY (${link.column2Name}) REFERENCES HUB_${link.hub2Name} (hashKey),
</#if>
);

</#list>
<#list satellites as satellite>
CREATE TABLE SAT_${satellite.name} (
   hashkey BINARY PRIMARY KEY,
   loadTimestamp TIMESTAMP,
   recordSource STRING,
<#list satellite.properties as property>
   ${property.name} <@columnType property.column.columnType/><#if property.isMandatory> NOT NULL</#if><#if preferences.applyForeignKeys && property.column.isReferenceData> FOREIGN KEY REFERENCES REF_${property.column.reference} (hashKey)</#if><#if property?has_next || preferences.applyForeignKeys>,</#if>
</#list>
<#if preferences.applyForeignKeys>
   FOREIGN KEY (hashKey) REFERENCES <#if satellite.isForHub>HUB_<#else>LNK_</#if>${satellite.satelliteOf} (hashKey)
</#if>
);

</#list>
<#if types?size gt 0>
INSERT INTO SYS_TypeHierarchy (hashKey, name, description, parent, root) VALUES
<#list types as type>
   (
      ${type.key},
      '${type.name}',
      '${type.description}',
      ${type.parent},
      ${type.root}
   )<#if type?has_next>,<#else>;</#if>
</#list>
</#if>

<#if enumerations?size gt 0>
INSERT INTO REF_Enumerations (hashKey, name, description) VALUES
<#list enumerations as enum>
   (
      ${enum.key},
      '${enum.name}',
      '${enum.description}'
   )<#if enum?has_next>,<#else>;</#if>
</#list>
</#if>

<#if enumerations?size gt 0>
INSERT INTO REF_EnumerationValues (hashKey, enumId, name, description) VALUES
<#list enumerations as enum>
<#list enum.values as value>
   (
      ${value.value},
      ${enum.key},
      '${value.label}',
      '${value.description}'
   )<#if enum?has_next || value?has_next>,<#else>;</#if>
</#list>
</#list>
</#if>

<#if controlledVocabularies?size gt 0>
INSERT INTO REF_ControlledVocabularies (hashKey, name, description) VALUES
<#list controlledVocabularies as cv>
   (
      ${cv.key},
      '${cv.name}',
      '${cv.description}'
   )<#if cv?has_next>,<#else>;</#if>
</#list>
</#if>