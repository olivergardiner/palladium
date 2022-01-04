package uk.org.whitecottage.swagger;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ComplexParameterBase extends ParameterBase {
	private Map<String, MediaTypeObject> contents;
	
	public ComplexParameterBase() {
		contents = new HashMap<>();
	}

	public Map<String, MediaTypeObject> getContents() {
		return contents;
	}

	public MediaTypeObject putContents(String key, MediaTypeObject value) {
		contents.clear();
		
		return contents.put(key, value);
	}
}
