package com.appota.asdk.exception;

public class CardPaymentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8579496371982645499L;
	private String message;

	public CardPaymentException(String message) {
		super(message);
		this.message = message;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "CardPaymentException " + message;
	}
}
