package com.appota.asdk.handler;

import com.appota.asdk.exception.SMSPaymentException;
import com.appota.asdk.model.SMSPayment;

public interface SMSPaymentHandler {

	public void onSMSPaymentRequestSuccess(SMSPayment sms);
	public void onSMSPaymentRequestError(int errorCode);
	public void onSMSPaymentRequestException(SMSPaymentException ex);
}
