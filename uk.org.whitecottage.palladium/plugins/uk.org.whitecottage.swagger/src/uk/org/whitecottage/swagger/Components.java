package uk.org.whitecottage.swagger;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Components {
	private Map<String, Schema> schemas;
	private Map<String, Response> responses;
	private Map<String, Parameter> parameters;
	private Map<String, Example> examples;
	private Map<String, RequestBody> requestBodies;
	private Map<String, Header> headers;
	private Map<String, SecurityScheme> securitySchems;
	private Map<String, Link> links;
	private Map<String, Callback> callbacks;
	
	public Map<String, Schema> getSchemas() {
		return schemas;
	}
	
	public void setSchemas(Map<String, Schema> schemas) {
		this.schemas = schemas;
	}

	public Map<String, Response> getResponses() {
		return responses;
	}

	public void setResponses(Map<String, Response> responses) {
		this.responses = responses;
	}

	public Map<String, Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Parameter> parameters) {
		this.parameters = parameters;
	}

	public Map<String, Example> getExamples() {
		return examples;
	}

	public void setExamples(Map<String, Example> examples) {
		this.examples = examples;
	}

	public Map<String, RequestBody> getRequestBodies() {
		return requestBodies;
	}

	public void setRequestBodies(Map<String, RequestBody> requestBodies) {
		this.requestBodies = requestBodies;
	}

	public Map<String, Header> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, Header> headers) {
		this.headers = headers;
	}

	public Map<String, SecurityScheme> getSecuritySchems() {
		return securitySchems;
	}

	public void setSecuritySchems(Map<String, SecurityScheme> securitySchems) {
		this.securitySchems = securitySchems;
	}

	public Map<String, Link> getLinks() {
		return links;
	}

	public void setLinks(Map<String, Link> links) {
		this.links = links;
	}

	public Map<String, Callback> getCallbacks() {
		return callbacks;
	}

	public void setCallbacks(Map<String, Callback> callbacks) {
		this.callbacks = callbacks;
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

	public Example putExample(String key, Example value) {
		if (examples == null) {
			examples = new HashMap<>();
		}
		
		return examples.put(key, value);
	}

	public void putAllExamples(Map<? extends String, ? extends Example> m) {
		if (examples == null) {
			examples = new HashMap<>();
		}
		
		examples.putAll(m);
	}

	public Header putHeader(String key, Header value) {
		if (headers == null) {
			headers = new HashMap<>();
		}
		
		return headers.put(key, value);
	}

	public void putAllHeaders(Map<? extends String, ? extends Header> m) {
		if (headers == null) {
			headers = new HashMap<>();
		}
		
		headers.putAll(m);
	}

	public Link putLink(String key, Link value) {
		if (links == null) {
			links = new HashMap<>();
		}
		
		return links.put(key, value);
	}

	public void putAllLinks(Map<? extends String, ? extends Link> m) {
		if (links == null) {
			links = new HashMap<>();
		}
		
		links.putAll(m);
	}

	public Parameter putParameter(String key, Parameter value) {
		if (parameters == null) {
			parameters = new HashMap<>();
		}
		
		return parameters.put(key, value);
	}

	public void putAllParameters(Map<? extends String, ? extends Parameter> m) {
		if (parameters == null) {
			parameters = new HashMap<>();
		}
		
		parameters.putAll(m);
	}

	public RequestBody putRequestBody(String key, RequestBody value) {
		if (requestBodies == null) {
			requestBodies = new HashMap<>();
		}
		
		return requestBodies.put(key, value);
	}

	public void putAllRequestBodies(Map<? extends String, ? extends RequestBody> m) {
		if (requestBodies == null) {
			requestBodies = new HashMap<>();
		}
		
		requestBodies.putAll(m);
	}

	public Response putResponse(String key, Response value) {
		if (responses == null) {
			responses = new HashMap<>();
		}
		
		return responses.put(key, value);
	}

	public void putAllResponses(Map<? extends String, ? extends Response> m) {
		if (responses == null) {
			responses = new HashMap<>();
		}
		
		responses.putAll(m);
	}

	public Schema putSchema(String key, Schema value) {
		if (schemas == null) {
			schemas = new HashMap<>();
		}
		
		return schemas.put(key, value);
	}

	public void putAllSchemas(Map<? extends String, ? extends Schema> m) {
		if (schemas == null) {
			schemas = new HashMap<>();
		}
		
		schemas.putAll(m);
	}

	public SecurityScheme putSecurityScheme(String key, SecurityScheme value) {
		if (securitySchems == null) {
			securitySchems = new HashMap<>();
		}
		
		return securitySchems.put(key, value);
	}

	public void putAllSecuritySchemes(Map<? extends String, ? extends SecurityScheme> m) {
		if (securitySchems == null) {
			securitySchems = new HashMap<>();
		}
		
		securitySchems.putAll(m);
	}
	
}
