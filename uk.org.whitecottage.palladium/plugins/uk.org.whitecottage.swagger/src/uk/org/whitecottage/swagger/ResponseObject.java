package uk.org.whitecottage.swagger;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseObject implements Response {
	private String description = "Description";
	private Map<String, Header> headers;
	private Map<String, MediaTypeObject> content;
	private Map<String, Link> links;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, Header> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, Header> headers) {
		this.headers = headers;
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

	public Map<String, MediaTypeObject> getContents() {
		return content;
	}

	public MediaTypeObject putContent(String key, MediaTypeObject value) {
		if (content == null) {
			content = new HashMap<>();
		}
		
		return content.put(key, value);
	}

	public void putAllContent(Map<? extends String, ? extends MediaTypeObject> m) {
		if (content == null) {
			content = new HashMap<>();
		}
		
		content.putAll(m);
	}

	public Map<String, Link> getLinks() {
		return links;
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
	
}
