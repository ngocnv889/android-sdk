package com.appota.model;

import java.io.Serializable;

public class InappChecker implements Serializable {

	private boolean isSuccess;
	private String message;

	public InappChecker(boolean isSuccess, String message) {
		this.isSuccess = isSuccess;
		this.message = message;
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

}
