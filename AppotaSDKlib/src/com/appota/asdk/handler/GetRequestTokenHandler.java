package com.appota.asdk.handler;


public interface GetRequestTokenHandler {

	public abstract void onGetRequestTokenSuccess(String requestToken);
	public abstract void onGetRequestTokenError(int errorCode);
}
