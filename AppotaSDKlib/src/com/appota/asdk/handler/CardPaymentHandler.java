package com.appota.asdk.handler;

import com.appota.asdk.exception.CardPaymentException;

public interface CardPaymentHandler {

	public void onCardPaymentRequestSuccess(String transactionID);
	public void onCardPaymentRequestError(int errorCode);
	public void onCardPaymentRequestException(CardPaymentException ex);
}
