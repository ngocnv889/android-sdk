package com.appota.asdk.handler;

import com.appota.asdk.exception.BankPaymentException;
import com.appota.asdk.model.BankPayment;

public interface BankPaymentHandler {

	public void onBankPaymentRequestSuccess(BankPayment bank);
	public void onBankPaymentRequestError(int errorCode);
	public void onBankPaymentRequestException(BankPaymentException ex);
}
