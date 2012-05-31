package com.appota.model;

import java.io.Serializable;

public class PaypalTopup implements Serializable{

	private String topupId;
	private PaypalForm form;
	private int tym;
	private double amount;
	private String currency;

	public String getTopupId() {
		return topupId;
	}

	public void setTopupId(String topupId) {
		this.topupId = topupId;
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

}
