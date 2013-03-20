package com.appota.asdk.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.appota.asdk.core.AppotaFactory;
import com.appota.asdk.core.DialogManager;
import com.appota.asdk.handler.UserInfoHandler;
import com.appota.asdk.model.User;

public class GetUserInfoTask extends AsyncTask<Void, Void, User>{

	private ProgressDialog pDialog;
	private Context context;
	private String accessToken;
	private UserInfoHandler handler;
	private AppotaFactory factory;
	
	public GetUserInfoTask(Context context, String accessToken){
		this.context = context;
		this.accessToken = accessToken;
	}
	
	public void setUserInfoHandler(UserInfoHandler handler){
		this.handler = handler;
	}

	@Override
	protected void onPostExecute(User user) {
		super.onPostExecute(user);
		if(user.getErrorCode() == 0){
			handler.onGetUserInfoSuccess(user);
		} else {
			handler.onGetUserInfoError(user.getErrorCode());
		}
		pDialog.dismiss();
	}

	@Override
	protected void onPreExecute() {
		factory = AppotaFactory.getInstance().init(context);
		pDialog = new DialogManager(context).showProgressDialog();
	}

	@Override
	protected User doInBackground(Void... params) {
		User user = factory.getUserInfo(accessToken);
		return user;
	}
}
