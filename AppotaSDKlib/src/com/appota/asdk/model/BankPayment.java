package com.appota.asdk.model;

public class BankPayment {

	private String transactionId;
	private BankOption option;
	private int errorCode;

	public BankPayment() {
	}

	public BankPayment(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTopupId(String topupId) {
		this.transactionId = topupId;
	}

	public BankOption getOption() {
		return option;
	}

	public void setOption(BankOption option) {
		this.option = option;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

}
