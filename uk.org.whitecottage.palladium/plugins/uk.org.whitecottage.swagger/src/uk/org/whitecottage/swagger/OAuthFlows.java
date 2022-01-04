package uk.org.whitecottage.swagger;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuthFlows {
	private OAuthFlow implicit;
	private OAuthFlow password;
	private OAuthFlow clientCredentials;
	private OAuthFlow authorizationCode;
	
	public OAuthFlow getImplicit() {
		return implicit;
	}
	
	public void setImplicit(OAuthFlow implicit) {
		this.implicit = implicit;
	}
	
	public OAuthFlow getPassword() {
		return password;
	}
	
	public void setPassword(OAuthFlow password) {
		this.password = password;
	}
	
	public OAuthFlow getClientCredentials() {
		return clientCredentials;
	}
	
	public void setClientCredentials(OAuthFlow clientCredentials) {
		this.clientCredentials = clientCredentials;
	}
	
	public OAuthFlow getAuthorizationCode() {
		return authorizationCode;
	}
	
	public void setAuthorizationCode(OAuthFlow authorizationCode) {
		this.authorizationCode = authorizationCode;
	}
}
