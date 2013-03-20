package com.appota.asdk.model;

import java.io.Serializable;
import java.util.List;

public class SMSPayment implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3441013352409832323L;
	private String transactionID;
	private List<SMSOption> smsOptions;
	private int errorCode;
	
	public SMSPayment() {
	}
	
	public SMSPayment(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public String getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	public List<SMSOption> getSmsOptions() {
		return smsOptions;
	}
	public void setSmsOptions(List<SMSOption> smsOptions) {
		this.smsOptions = smsOptions;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	
}
