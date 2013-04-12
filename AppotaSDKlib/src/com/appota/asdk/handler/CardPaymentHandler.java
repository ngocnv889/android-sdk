package com.appota.asdk.handler;


public interface CardPaymentHandler {

	public void onCardPaymentRequestSuccess(String transactionID);
	public void onCardPaymentRequestError(int errorCode);
}
