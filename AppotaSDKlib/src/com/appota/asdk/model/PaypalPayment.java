package com.appota.asdk.model;

import java.io.Serializable;

public class PaypalPayment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6786292516945851507L;
	private String transactionId;
	private PaypalForm form;
	private int tym;
	private double amount;
	private String currency;
	private int errorCode;

	public PaypalPayment() {
	}

	public PaypalPayment(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTopupId(String topupId) {
		this.transactionId = topupId;
	}

	public PaypalForm getForm() {
		return form;
	}

	public void setForm(PaypalForm form) {
		this.form = form;
	}

	public int getTym() {
		return tym;
	}

	public void setTym(int tym) {
		this.tym = tym;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

}
