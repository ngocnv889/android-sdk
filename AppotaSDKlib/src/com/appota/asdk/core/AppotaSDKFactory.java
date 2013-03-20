package com.appota.asdk.core;

import java.util.List;

import com.appota.asdk.handler.BankPaymentHandler;
import com.appota.asdk.handler.CardPaymentHandler;
import com.appota.asdk.handler.PaypalPaymentHandler;
import com.appota.asdk.handler.SMSPaymentHandler;
import com.appota.asdk.handler.TransactionStatusHandler;
import com.appota.asdk.model.AppotaAccessToken;
import com.appota.asdk.model.BankPayment;
import com.appota.asdk.model.PaypalPayment;
import com.appota.asdk.model.SMSPayment;
import com.appota.asdk.model.TransactionResult;

public abstract class AppotaSDKFactory {

	public abstract String getRequestToken(String clientID, String redirectURI, List<String> scopes, String lang);
	public abstract AppotaAccessToken getAccessToken(String requestToken, String clientID, String clientSecret, String redirectURI, String lang);
	public abstract SMSPayment smsPaymentWithNoticeURL(String accessToken, String noticeURL);
	public abstract SMSPayment smsPaymentWithApplicationHandler(String accessToken, SMSPaymentHandler handler);
	public abstract String cardPaymentWithNoticeURL(String accessToken, String cardSerial, String cardCode, String vendor, String noticeURL);
	public abstract String cardPaymentWithApplicationHandler(String accessToken, String cardSerial, String cardCode, String vendor, CardPaymentHandler handler);
	public abstract BankPayment bankPaymentWithNoticeURL(String accessToken, String amount, String noticeURL);
	public abstract BankPayment bankPaymentWithApplicationHandler(String accessToken, String amount, BankPaymentHandler handler);
	public abstract PaypalPayment paypalPaymentWithNoticeURL(String accessToken, String amount, String noticeURL);
	public abstract PaypalPayment paypalPaymentWithApplicationHandler(String accessToken, String amount, PaypalPaymentHandler handler);
	public abstract TransactionResult checkTransactionStatus(String accessToken, String transactionID, TransactionStatusHandler handler);
}
