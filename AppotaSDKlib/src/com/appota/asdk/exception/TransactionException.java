package com.appota.asdk.exception;

public class TransactionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4154552084612830622L;
	/**
	 * 
	 */
	/**
	 * 
	 */
	private String message;

	public TransactionException(String message) {
		super(message);
		this.message = message;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "TransactionException " + message;
	}
}
