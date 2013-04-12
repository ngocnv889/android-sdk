package com.appota.asdk.model;

import java.io.Serializable;

public class AppotaAccessToken implements Serializable{

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getGoogleEmail() {
		return googleEmail;
	}

	public void setGoogleEmail(String googleEmail) {
		this.googleEmail = googleEmail;
	}

	public String getGoogleToken() {
		return googleToken;
	}

	public void setGoogleToken(String googleToken) {
		this.googleToken = googleToken;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String accessToken;
	private String refreshToken;
	private String expiresIn;
	private String countryName;
	private String countryCode;
	private int errorCode;
	private String googleEmail;
	private String googleToken;

	public AppotaAccessToken(String accessToken, String refreshToken,
			String expiresIn, String countryName, String countryCode,
			String googleEmail, String googleToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiresIn = expiresIn;
		this.countryName = countryName;
		this.countryCode = countryCode;
		this.googleEmail = googleEmail;
		this.googleToken = googleToken;
	}

	public AppotaAccessToken() {
	}

}
