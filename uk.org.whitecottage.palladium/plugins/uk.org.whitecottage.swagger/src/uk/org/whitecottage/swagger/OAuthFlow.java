package uk.org.whitecottage.swagger;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuthFlow {
	private String authorizationUrl;
	private String tokenUrl;
	private String refreshUrl;
	private Map<String, String> scopes;
	
	public String getAuthorizationUrl() {
		return authorizationUrl;
	}
	
	public void setAuthorizationUrl(String authorizationUrl) {
		this.authorizationUrl = authorizationUrl;
	}
	
	public String getTokenUrl() {
		return tokenUrl;
	}
	
	public void setTokenUrl(String tokenUrl) {
		this.tokenUrl = tokenUrl;
	}
	
	public String getRefreshUrl() {
		return refreshUrl;
	}
	
	public void setRefreshUrl(String refreshUrl) {
		this.refreshUrl = refreshUrl;
	}
	
	public Map<String, String> getScopes() {
		return scopes;
	}
	
	public void setScopes(Map<String, String> scopes) {
		this.scopes = scopes;
	}

	public String putScope(String key, String value) {
		return scopes.put(key, value);
	}

	public void putAllScopes(Map<? extends String, ? extends String> m) {
		scopes.putAll(m);
	}
}
