package com.appota.asdk.exception;

public class PaypalPaymentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4630878957112898169L;
	/**
	 * 
	 */
	/**
	 * 
	 */
	private String message;

	public PaypalPaymentException(String message) {
		super(message);
		this.message = message;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "PaypalPaymentException " + message;
	}
}
