package com.appota.asdk.task;

import com.appota.asdk.core.AppotaFactory;
import com.appota.asdk.core.DialogManager;
import com.appota.asdk.handler.TransactionStatusHandler;
import com.appota.asdk.model.TransactionResult;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class CheckTopupTask extends AsyncTask<Void, Void, TransactionResult>{

	private String accessToken;
	private AppotaFactory factory;
	private TransactionStatusHandler handler;
	private Context context;
	private ProgressDialog pDialog;
	private String topupID;
	private boolean showProgress;
	
	public CheckTopupTask(Context context, String accessToken, String topupID, boolean showProgress){
		this.accessToken = accessToken;
		this.topupID = topupID;
		this.context = context;
		this.showProgress = showProgress;
	}
	
	@Override
	protected void onPostExecute(TransactionResult result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(result.getErrorCode() == 0){
			handler.onTransactionSuccess(result);
		} else {
			handler.onTransactionError(result.getErrorCode());
		}
		if(showProgress){
			pDialog.dismiss();
		}
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		factory = AppotaFactory.getInstance().init(context);
		if(showProgress){
			pDialog = new DialogManager(context).showProgressDialog();
		}
	}
	
	public void setTransactionStatusHandler(TransactionStatusHandler handler){
		this.handler = handler;
	}

	@Override
	protected TransactionResult doInBackground(Void... params) {
		// TODO Auto-generated method stub
		TransactionResult result = factory.checkTopup(accessToken, topupID);
		return result;
	}
}
