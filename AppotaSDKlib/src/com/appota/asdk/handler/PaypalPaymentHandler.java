package com.appota.asdk.handler;

import com.appota.asdk.model.PaypalPayment;

public interface PaypalPaymentHandler {

	public void onPaypalPaymentRequestSuccess(PaypalPayment paypal);
	public void onPaypalPaymentRequestError(int errorCode);
}
