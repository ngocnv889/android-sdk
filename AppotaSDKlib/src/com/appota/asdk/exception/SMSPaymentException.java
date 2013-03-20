package com.appota.asdk.exception;

public class SMSPaymentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3620907011428762427L;
	private String message;

	public SMSPaymentException(String message) {
		super(message);
		this.message = message;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "SMSPaymentException " + message;
	}
}
