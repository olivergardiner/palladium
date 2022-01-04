package uk.org.whitecottage.swagger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperationObject {
	private List<String> tags;
	private String summary;
	private String description;
	private ExternalDocumentation externalDocs;
	private String operationId ;
	private List<Parameter> parameters;
	private RequestBody requestBody;
	private ResponsesObject responses;
	private Map<String, Callback> callbacks;
	private boolean deprecated = false;
	private SecurityRequirement security;
	private List<Server> servers;	

	public OperationObject() {
		responses = new ResponsesObject();
	}

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

	public String getOperationId() {
		return operationId;
	}

	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	public List<String> getTags() {
		return tags;
	}

	public ExternalDocumentation getExternalDocs() {
		return externalDocs;
	}

	public void setExternalDocs(ExternalDocumentation externalDocs) {
		this.externalDocs = externalDocs;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public RequestBody getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(RequestBody requestBody) {
		this.requestBody = requestBody;
	}

	public ResponsesObject getResponses() {
		return responses;
	}

	public Map<String, Callback> getCallbacks() {
		return callbacks;
	}

	public SecurityRequirement getSecurity() {
		return security;
	}

	public void setSecurity(SecurityRequirement security) {
		this.security = security;
	}

	public List<Server> getServers() {
		return servers;
	}

	public Callback putCallback(String key, Callback value) {
		if (callbacks == null) {
			callbacks = new HashMap<>();
		}
		
		return callbacks.put(key, value);
	}

	public void putAllCallbacks(Map<? extends String, ? extends Callback> m) {
		if (callbacks == null) {
			callbacks = new HashMap<>();
		}
		
		callbacks.putAll(m);
	}

	public boolean addServer(Server e) {
		if (servers == null) {
			servers = new ArrayList<>();
		}
		
		return servers.add(e);
	}

	public boolean addAllServers(Collection<? extends Server> c) {
		if (servers == null) {
			servers = new ArrayList<>();
		}
		
		return servers.addAll(c);
	}

	public boolean addParameter(Parameter e) {
		if (parameters == null) {
			parameters = new ArrayList<>();
		}
		
		return parameters.add(e);
	}

	public boolean addAllParameters(Collection<? extends Parameter> c) {
		if (parameters == null) {
			parameters = new ArrayList<>();
		}
		
		return parameters.addAll(c);
	}

	public boolean addTag(String e) {
		if (tags == null) {
			tags = new ArrayList<>();
		}
		
		return tags.add(e);
	}

	public boolean addAllTags(Collection<? extends String> c) {
		if (tags == null) {
			tags = new ArrayList<>();
		}
		
		return tags.addAll(c);
	}
}
