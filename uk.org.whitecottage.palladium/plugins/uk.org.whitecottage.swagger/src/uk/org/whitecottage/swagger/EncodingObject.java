package uk.org.whitecottage.swagger;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import uk.org.whitecottage.swagger.Parameter.Style;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EncodingObject {
	private String contentType;
	private Map<String, Header> headers;
	private String style;
	private boolean explode = false;
	private boolean allowReserved = false;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Map<String, Header> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, Header> headers) {
		this.headers = headers;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		switch (style) {
		case MATRIX:
			this.style = "matrix";
			break;
		case LABEL:
			this.style = "label";
			break;
		case FORM:
			this.style = "form";
			break;
		case SIMPLE:
			this.style = "simple";
			break;
		case SPACE_DELIMITED:
			this.style = "spaceDelimited";
			break;
		case PIPE_DELIMITED:
			this.style = "pipeDelimited";
			break;
		case DEEP_OBJECT:
			this.style = "deepObject";
			break;
		default:
			this.style = "matrix";
			break;
		}
	}

	public boolean isExplode() {
		return explode;
	}

	public void setExplode(boolean explode) {
		this.explode = explode;
	}

	public boolean isAllowReserved() {
		return allowReserved;
	}

	public void setAllowReserved(boolean allowReserved) {
		this.allowReserved = allowReserved;
	}

	public Header putHeader(String key, Header value) {
		return headers.put(key, value);
	}

	public void putAllHeaders(Map<? extends String, ? extends Header> m) {
		headers.putAll(m);
	}
}
