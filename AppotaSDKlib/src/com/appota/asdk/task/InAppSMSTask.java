package com.appota.asdk.task;

import com.appota.asdk.core.AppotaFactory;
import com.appota.asdk.core.DialogManager;
import com.appota.asdk.handler.SMSPaymentHandler;
import com.appota.asdk.model.SMSPayment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class InAppSMSTask extends AsyncTask<Void, Void, SMSPayment>{
	
	private AppotaFactory factory;
	private String accessToken;
	private String noticeURL;
	private String state;
	private String target;
	private boolean shortSyntax;
	private SMSPaymentHandler handler;
	private Context context;
	private ProgressDialog pDialog;
	
	public InAppSMSTask(Context context, String accessToken, String noticeURL, String state, String target, boolean shortSyntax){
		this.context = context;
		this.accessToken = accessToken;
		this.noticeURL = noticeURL;
		this.shortSyntax = shortSyntax;
		this.state = state;
		this.target = target;
	}
	
	public void setSMSPaymentHandler(SMSPaymentHandler handler){
		this.handler = handler;
	}
	
	@Override
	protected void onPostExecute(SMSPayment sms) {
		// TODO Auto-generated method stub
		super.onPostExecute(sms);
		if(sms.getErrorCode() == 0){
			handler.onSMSPaymentRequestSuccess(sms);
		} else {
			handler.onSMSPaymentRequestError(sms.getErrorCode());
		}
		pDialog.dismiss();
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		factory = AppotaFactory.getInstance().init(context);
		pDialog = new DialogManager(context).showProgressDialog();
	}

	@Override
	protected SMSPayment doInBackground(Void... params) {
		// TODO Auto-generated method stub
		SMSPayment sms = factory.smsInApp(accessToken, noticeURL, state, target, shortSyntax);
		return sms;
	}

}
