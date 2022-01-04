package uk.org.whitecottage.swagger;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ParameterBase {
	private String description;
	private boolean required = false;
	private boolean deprecated = false;
	private boolean allowEmptyValue = false;
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isRequired() {
		return required;
	}
	
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	public boolean isDeprecated() {
		return deprecated;
	}
	
	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}
	
	public boolean isAllowEmptyValue() {
		return allowEmptyValue;
	}
	
	public void setAllowEmptyValue(boolean allowEmptyValue) {
		this.allowEmptyValue = allowEmptyValue;
	}
}
