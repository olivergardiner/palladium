package uk.org.whitecottage.swagger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "enum", "default", "description" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerVariable {
	@JsonIgnore
	private List<String> enumeration;
	@JsonIgnore
	private String defaultValue = "Default";
	private String description;
	
	public ServerVariable(String defaultValue, String description) {
		this.defaultValue = defaultValue;
		this.description = description;
	}

	public ServerVariable(String[] enumeration, String defaultValue, String description) {
		this.enumeration = new ArrayList<>();
		this.enumeration.addAll(Arrays.asList(enumeration));
		this.defaultValue = defaultValue;
		this.description = description;
	}

	public List<String> getEnum() {
		return enumeration;
	}
	
	public void setEnum(List<String> enumeration) {
		this.enumeration = enumeration;
	}
	
	public boolean addEnum(String e) {
		if (enumeration == null) {
			enumeration = new ArrayList<>();
		}
		
		return enumeration.add(e);
	}

	public boolean addAllEnums(Collection<? extends String> c) {
		if (enumeration == null) {
			enumeration = new ArrayList<>();
		}
		
		return enumeration.addAll(c);
	}

	public String getDefault() {
		return defaultValue;
	}
	
	public void setDefault(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
