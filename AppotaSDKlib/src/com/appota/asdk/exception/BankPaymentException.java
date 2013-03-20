package com.appota.asdk.exception;

public class BankPaymentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1921544916727887026L;
	/**
	 * 
	 */
	private String message;

	public BankPaymentException(String message) {
		super(message);
		this.message = message;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "BankPaymentException " + message;
	}
}
