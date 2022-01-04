package uk.org.whitecottage.swagger;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Server {
	private String url = "/";
	private String description;
	private Map<String, ServerVariable> variables;
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, ServerVariable> getVariables() {
		return variables;
	}

	public ServerVariable putVariable(String key, ServerVariable value) {
		if (variables == null) {
			variables = new HashMap<>();
		}
		
		return variables.put(key, value);
	}

	public void putAllVariables(Map<? extends String, ? extends ServerVariable> m) {
		if (variables == null) {
			variables = new HashMap<>();
		}
		
		variables.putAll(m);
	}
}
