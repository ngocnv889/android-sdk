package com.appota.model;

import java.io.Serializable;

public class TopupChecker implements Serializable{

	private boolean isSuccess;
	private String message;
	private String tym;

	public TopupChecker(boolean isSuccess, String message, String tym) {
		this.isSuccess = isSuccess;
		this.message = message;
		this.tym = tym;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTym() {
		return tym;
	}

	public void setTym(String tym) {
		this.tym = tym;
	};

}
