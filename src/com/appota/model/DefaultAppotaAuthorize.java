package com.appota.model;

public class DefaultAppotaAuthorize {

	private String redirectUri;
	private String grantType;
	private String lang;
	private String accessTokenUrl;
	private String refreshTokenEndpoint;

	public DefaultAppotaAuthorize() {
		redirectUri = "http://localhost";
		grantType = "authorization_code";
		lang = "en";
		accessTokenUrl = "https://id.appota.com/oauth/access_token";
		refreshTokenEndpoint = "https://id.appota.com/oauth/refresh_token";
	}

	public String getGrantType() {
		return grantType;
	}
	
	public void setGrantType(String type){
		this.grantType = type;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public String getAccessTokenUrl() {
		return accessTokenUrl;
	}
	
	public String getRefreshTokenEndpoint() {
		return refreshTokenEndpoint;
	}

}
