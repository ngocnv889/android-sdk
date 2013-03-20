package com.appota.asdk.task;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.appota.asdk.core.AppotaFactory;
import com.appota.asdk.core.DialogManager;
import com.appota.asdk.handler.GetAccessTokenHandler;
import com.appota.asdk.model.AppotaAccessToken;
import com.appota.asdk.util.Constant;

public class GetNonUserAccessTokenTask extends AsyncTask<Void, Void, AppotaAccessToken>{
	
	private AppotaFactory factory;
	private String clientKey;
	private String clientSecret;
	private List<String> scopes;
	private String lang;
	private Context context;
	private GetAccessTokenHandler handler;
	private ProgressDialog pDialog;
	
	public GetNonUserAccessTokenTask(Context context, String clientKey, String clientSecret, List<String> scopes, String lang){
		this.clientKey = clientKey;
		this.clientSecret = clientSecret;
		this.scopes = scopes;
		this.lang = lang;
		this.context = context;
	}
	
	public void setRequestTokenHandler(GetAccessTokenHandler handler){
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
		String requestToken = factory.getNonUserRequestToken(clientKey, scopes, lang);
		AppotaAccessToken accessToken = factory.getNonUserAccessToken(requestToken, clientKey, clientSecret, Constant.LANG_EN);
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
