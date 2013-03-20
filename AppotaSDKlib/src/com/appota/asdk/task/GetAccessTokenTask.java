package com.appota.asdk.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.appota.asdk.core.AppotaFactory;
import com.appota.asdk.core.DialogManager;
import com.appota.asdk.handler.GetAccessTokenHandler;
import com.appota.asdk.model.AppotaAccessToken;

public class GetAccessTokenTask extends AsyncTask<Void, Void, AppotaAccessToken>{

	private AppotaFactory factory;
	private String clientKey;
	private String clientSecret;
	private String requestToken;
	private String lang;
	private Context context;
	private GetAccessTokenHandler handler;
	private ProgressDialog pDialog;
	
	public GetAccessTokenTask(Context context, String requestToken, String clientKey, String clientSecret, String lang){
		this.clientKey = clientKey;
		this.clientSecret = clientSecret;
		this.lang = lang;
		this.context = context;
		this.requestToken = requestToken;
	}
	
	public void setAccessTokenHandler(GetAccessTokenHandler handler){
		this.handler = handler;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		factory = AppotaFactory.getInstance().init(context);
		pDialog = new DialogManager(context).showProgressDialog();
	}
	
	@Override
	protected AppotaAccessToken doInBackground(Void... params) {
		// TODO Auto-generated method stub
		AppotaAccessToken accessToken = factory.getAccessToken(requestToken, clientKey, clientSecret, lang);
		return accessToken;
	}

	@Override
	protected void onPostExecute(AppotaAccessToken accessToken) {
		// TODO Auto-generated method stub
		super.onPostExecute(accessToken);
		if(accessToken.getErrorCode() == 0){
			handler.onGetAccessTokenSuccess(accessToken);
		} else {
			handler.onGetAccessTokenError(accessToken.getErrorCode());
		}
		pDialog.dismiss();
	}
}
