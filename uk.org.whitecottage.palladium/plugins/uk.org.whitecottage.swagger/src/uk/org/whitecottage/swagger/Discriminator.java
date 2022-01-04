package uk.org.whitecottage.swagger;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Discriminator {
	private String propertyName = "PropertyName";
	private Map<String, String> mapping;
	
	public String getPropertyName() {
		return propertyName;
	}
	
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	
	public Map<String, String> getMapping() {
		return mapping;
	}
	
	public void setMapping(Map<String, String> mapping) {
		this.mapping = mapping;
	}

	public String putMapping(String key, String value) {
		if (mapping == null) {
			mapping = new HashMap<>();
		}
		
		return mapping.put(key, value);
	}

	public void putAllMappings(Map<? extends String, ? extends String> m) {
		if (mapping == null) {
			mapping = new HashMap<>();
		}
		
		mapping.putAll(m);
	}
}
