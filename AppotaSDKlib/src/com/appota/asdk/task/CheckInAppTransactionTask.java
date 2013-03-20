package com.appota.asdk.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.appota.asdk.core.AppotaFactory;
import com.appota.asdk.core.DialogManager;
import com.appota.asdk.handler.TransactionStatusHandler;
import com.appota.asdk.model.TransactionResult;

public class CheckInAppTransactionTask extends AsyncTask<Void, Void, TransactionResult>{

	private String accessToken;
	private AppotaFactory factory;
	private TransactionStatusHandler handler;
	private Context context;
	private ProgressDialog pDialog;
	private String inappID;
	private boolean showProgress;
	
	public CheckInAppTransactionTask(Context context, String accessToken, String inappID, boolean showProgress){
		this.accessToken = accessToken;
		this.inappID = inappID;
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
		TransactionResult result = factory.checkInApp(accessToken, inappID);
		return result;
	}

}
