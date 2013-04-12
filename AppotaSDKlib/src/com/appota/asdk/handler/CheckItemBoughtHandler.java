package com.appota.asdk.handler;

public interface CheckItemBoughtHandler {

	public void onCheckItemBoughtSuccess(boolean isBought);
	public void onCheckItemBoughtError(int errorCode);
}
