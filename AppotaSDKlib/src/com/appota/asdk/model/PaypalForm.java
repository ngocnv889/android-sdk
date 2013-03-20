package com.appota.asdk.model;

import java.io.Serializable;

public class PaypalForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6867409063070664713L;
	private String cmd;
	private int noShopping;
	private int noNote;
	private String currencyCode;
	private String bn;
	private double amount;
	private String itemName;
	private String notifyUrl;
	private String business;

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public int getNoShipping() {
		return noShopping;
	}

	public void setNoShopping(int noShopping) {
		this.noShopping = noShopping;
	}

	public int getNoNote() {
		return noNote;
	}

	public void setNoNote(int noNote) {
		this.noNote = noNote;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getBn() {
		return bn;
	}

	public void setBn(String bn) {
		this.bn = bn;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getBusiness() {
		return business;
	}

	public void setBusiness(String business) {
		this.business = business;
	}

}

/*
 * {"form":{"cmd":"_xclick","no_shipping":1,"no_note":1,
 * "currency_code":"USD","bn":"PP-BuyNowBF","amount":"100000",
 * "item_name":"N\u1ea1p TYM v\u00e0o Appota",
 * "notify_url":"https:\/\/pay.appota.com\/paypal\/514F682AD02E35F",
 * "business":"paypal@appota.com"}
 */
