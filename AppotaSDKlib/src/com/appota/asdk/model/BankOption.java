package com.appota.asdk.model;

public class BankOption {

	private String url;
	private String bank;
	private int tym;
	private double amount;
	private String currency;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
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
