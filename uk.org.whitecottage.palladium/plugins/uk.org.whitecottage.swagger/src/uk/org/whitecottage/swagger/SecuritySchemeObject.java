package uk.org.whitecottage.swagger;

import com.fasterxml.jackson.annotation.JsonInclude;

import uk.org.whitecottage.swagger.Parameter.In;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecuritySchemeObject implements SecurityScheme {
	private String type;
	private String description;
	private String name;
	private String in;
	private String scheme;
	private String bearerFormat;
	private OAuthFlows flows;
	private String openIdConnectUrl;
	
	public enum SecurityType {
		API_KEY,
		HTTP,
		OAUTH2,
		OPEN_ID_CONNECT
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(SecurityType type) {
		switch (type) {
		case API_KEY:
			this.type = "apiKey";
			break;
		case HTTP:
			this.type = "http";
			break;
		case OAUTH2:
			this.type = "oauth2";
			break;
		case OPEN_ID_CONNECT:
			this.type = "openIdConnect";
			break;
		default:
			this.type = "apiKey";
			break;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		type = "apiKey";
		scheme = null;
		bearerFormat = null;
		flows = null;
		openIdConnectUrl = null;
	}

	public String getIn() {
		return in;
	}

	public void setIn(In in) {
		switch (in) {
		case QUERY:
			this.in = "query";
			break;
		case HEADER:
			this.in = "header";
			break;
		case COOKIE:
			this.in = "cookie";
			break;
		default:
			this.in = "query";
			break;
		}
		type = "apiKey";
		scheme = null;
		bearerFormat = null;
		flows = null;
		openIdConnectUrl = null;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
		type = "http";
		name = null;
		in = null;
		flows = null;
		openIdConnectUrl = null;
	}

	public String getBearerFormat() {
		return bearerFormat;
	}

	public void setBearerFormat(String bearerFormat) {
		this.bearerFormat = bearerFormat;
		type = "http";
		name = null;
		in = null;
		flows = null;
		openIdConnectUrl = null;
	}

	public String getOpenIdConnectUrl() {
		return openIdConnectUrl;
	}

	public void setOpenIdConnectUrl(String openIdConnectUrl) {
		this.openIdConnectUrl = openIdConnectUrl;
		type = "openIdConnect";
		name = null;
		in = null;
		scheme = null;
		bearerFormat = null;
		flows = null;
	}

	public OAuthFlows getFlows() {
		return flows;
	}

	public void setFlows(OAuthFlows flows) {
		this.flows = flows;
		type = "oauth2";
		name = null;
		in = null;
		scheme = null;
		bearerFormat = null;
		openIdConnectUrl = null;
	}
}
