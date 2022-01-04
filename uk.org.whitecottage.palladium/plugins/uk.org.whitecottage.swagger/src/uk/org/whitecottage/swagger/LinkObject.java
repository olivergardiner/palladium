package uk.org.whitecottage.swagger;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LinkObject implements Link {
	private String operationRef;
	private String operationId;
	private Map<String, Object> parameters;
	private Object requestBody;
	private String description;
	private Server server;
	
	public String getOperationRef() {
		return operationRef;
	}
	
	public void setOperationRef(String operationRef) {
		this.operationRef = operationRef;
		this.operationId = null;
	}
	
	public String getOperationId() {
		return operationId;
	}
	
	public void setOperationId(String operationId) {
		this.operationId = operationId;
		this.operationRef = null;
	}
	
	public Map<String, Object> getParameters() {
		return parameters;
	}
	
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	public Object getRequestBody() {
		return requestBody;
	}
	
	public void setRequestBody(Object requestBody) {
		this.requestBody = requestBody;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Server getServer() {
		return server;
	}
	
	public void setServer(Server server) {
		this.server = server;
	}

	public Object putParameter(String key, Object value) {
		if (parameters == null) {
			parameters = new HashMap<>();
		}
		
		return parameters.put(key, value);
	}

	public void putAllParameters(Map<? extends String, ? extends Object> m) {
		if (parameters == null) {
			parameters = new HashMap<>();
		}
		
		parameters.putAll(m);
	}

}
