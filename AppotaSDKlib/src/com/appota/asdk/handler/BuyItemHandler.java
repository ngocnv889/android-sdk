package com.appota.asdk.handler;

public interface BuyItemHandler {

	public void onBuyItemSuccess(String message);
	public void onBuyItemError(int errorCode);
	public void onBuyItemFail(String message);
}
