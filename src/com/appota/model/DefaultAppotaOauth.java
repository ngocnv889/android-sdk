package com.appota.model;

public class DefaultAppotaOauth {

	private String requestTokenUrl;
	private String responseType;
	private String redirectUri;
	private String state;
	private String lang;

	public DefaultAppotaOauth() {
		requestTokenUrl = "https://id.appota.com/oauth/request_token";
		responseType = "code";
		redirectUri = "http://localhost";
		state = "null";
		lang = "en";
	}

	public String getRequestTokenUrl() {
		return requestTokenUrl;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public void setRequestTokenUrl(String requestTokenUrl) {
		this.requestTokenUrl = requestTokenUrl;
	}

}
