package com.appota.asdk.handler;

import com.appota.asdk.model.AppotaAccessToken;

public interface GetAccessTokenHandler {

	public abstract void onGetAccessTokenSuccess(AppotaAccessToken accessToken);
	public abstract void onGetAccessTokenError(int errorCode);
	public abstract void onGetAccessTokenException(int exeptionCode);
}
