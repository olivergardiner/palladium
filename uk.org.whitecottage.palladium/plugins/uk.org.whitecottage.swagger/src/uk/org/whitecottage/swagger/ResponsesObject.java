package uk.org.whitecottage.swagger;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponsesObject  extends HashMap<String, Response> {
	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	public Response getDefaultResponse() {
		return get("default");
	}
	
	@JsonIgnore
	public Response setDefaultResponse(Response response) {
		return put("default", response);
	}
}
