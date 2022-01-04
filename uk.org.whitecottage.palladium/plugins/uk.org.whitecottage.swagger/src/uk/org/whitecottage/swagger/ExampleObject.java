package uk.org.whitecottage.swagger;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExampleObject implements Example {
	private String summary;
	private String description;
	private Object value;
	private String externalValue;
	
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
		this.externalValue = null;
	}
	public String getExternalValue() {
		return externalValue;
	}
	public void setExternalValue(String externalValue) {
		this.externalValue = externalValue;
		this.externalValue = null;
	}
}
