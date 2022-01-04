package uk.org.whitecottage.swagger;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonSchema {
	private String title;
	private String description;
	private Number multipleOf;
	private Number maximum;
	private Boolean exclusiveMaximum;
	private Number minimum;
	private Boolean exclusiveMinimum;
	private Integer maxLength;
	private Integer minLength;
	private String pattern;
	private Integer maxItems;
	private Integer minItems;
	private Boolean uniqueItems;
	private Integer maxProperties;
	private Integer minProperties;
	private Boolean required;
	@JsonIgnore
	private List<Object> enumeration;
	private String type;
	private List<Schema> allOf;
	private List<Schema> anyOf;
	private List<Schema> oneOf;
	private List<Schema> not;
	private Schema items;
	private Map<String, Schema> properties;
	private Schema additionalProperties;
	@JsonIgnore
	private Object defaultValue;
	private String format;
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Number getMultipleOf() {
		return multipleOf;
	}

	public void setMultipleOf(Number multipleOf) {
		this.multipleOf = multipleOf;
	}

	public Number getMaximum() {
		return maximum;
	}

	public void setMaximum(Number maximum) {
		this.maximum = maximum;
	}

	public Boolean getExclusiveMaximum() {
		return exclusiveMaximum;
	}

	public void setExclusiveMaximum(Boolean exclusiveMaximum) {
		this.exclusiveMaximum = exclusiveMaximum;
	}

	public Number getMinimum() {
		return minimum;
	}

	public void setMinimum(Number minimum) {
		this.minimum = minimum;
	}

	public Boolean getExclusiveMinimum() {
		return exclusiveMinimum;
	}

	public void setExclusiveMinimum(Boolean exclusiveMinimum) {
		this.exclusiveMinimum = exclusiveMinimum;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public Integer getMinLength() {
		return minLength;
	}

	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public Integer getMaxItems() {
		return maxItems;
	}

	public void setMaxItems(Integer maxItems) {
		this.maxItems = maxItems;
	}

	public Integer getMinItems() {
		return minItems;
	}

	public void setMinItems(Integer minItems) {
		this.minItems = minItems;
	}

	public Boolean getUniqueItems() {
		return uniqueItems;
	}

	public void setUniqueItems(Boolean uniqueItems) {
		this.uniqueItems = uniqueItems;
	}

	public Integer getMaxProperties() {
		return maxProperties;
	}

	public void setMaxProperties(Integer maxProperties) {
		this.maxProperties = maxProperties;
	}

	public Integer getMinProperties() {
		return minProperties;
	}

	public void setMinProperties(Integer minProperties) {
		this.minProperties = minProperties;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	@JsonInclude
	public List<Object> getEnum() {
		return enumeration;
	}

	@JsonInclude
	public void setEnum(List<Object> enumeration) {
		this.enumeration = enumeration;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Schema> getAllOf() {
		return allOf;
	}

	public void setAllOf(List<Schema> allOf) {
		this.allOf = allOf;
	}

	public List<Schema> getAnyOf() {
		return anyOf;
	}

	public void setAnyOf(List<Schema> anyOf) {
		this.anyOf = anyOf;
	}

	public List<Schema> getOneOf() {
		return oneOf;
	}

	public void setOneOf(List<Schema> oneOf) {
		this.oneOf = oneOf;
	}

	public List<Schema> getNot() {
		return not;
	}

	public void setNot(List<Schema> not) {
		this.not = not;
	}

	public Schema getItems() {
		return items;
	}

	public void setItems(Schema items) {
		this.items = items;
	}

	public Map<String, Schema> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Schema> properties) {
		this.properties = properties;
	}

	public Schema getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(Schema additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	@JsonInclude
	public Object getDefault() {
		return defaultValue;
	}

	@JsonInclude
	public void setDefault(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public boolean addEnum(Object e) {
		return enumeration.add(e);
	}

	public boolean addAllEnums(Collection<? extends Object> c) {
		return enumeration.addAll(c);
	}
}
