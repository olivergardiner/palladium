package uk.org.whitecottage.swagger;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestBodyObject implements RequestBody {
	private String description;
	private Map<String, MediaTypeObject> content;
	private boolean required;
	
	public RequestBodyObject() {
		content = new HashMap<>();
	}

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

	public Map<String, MediaTypeObject> getContent() {
		return content;
	}

	public MediaTypeObject putContent(String key, MediaTypeObject value) {
		return content.put(key, value);
	}

	public void putAllContent(Map<? extends String, ? extends MediaTypeObject> m) {
		content.putAll(m);
	}
	
}
