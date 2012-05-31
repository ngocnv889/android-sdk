package com.appota.model;

import java.io.Serializable;

public class AccessToken implements Serializable{

	private String token;
	private String expired;
	private String refreshToken;
	private String tokenType;

	public AccessToken(String token, String expired, String refreshToken) {
		this.token = token;
		this.expired = expired;
		this.refreshToken = refreshToken;
		tokenType = "Bearer";
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getExpired() {
		return expired;
	}

	public void setExpired(String expired) {
		this.expired = expired;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getTokenType() {
		return tokenType;
	}

}
