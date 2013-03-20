package com.appota.asdk.handler;

import com.appota.asdk.model.User;

public interface UserInfoHandler {

	public void onGetUserInfoSuccess(User user);
	public void onGetUserInfoError(int errorCode);
}
