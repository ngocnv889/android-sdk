package com.appota.asdk.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.appota.asdk.core.AppotaFactory;
import com.appota.asdk.core.DialogManager;
import com.appota.asdk.handler.BankPaymentHandler;
import com.appota.asdk.model.BankPayment;

public class InAppBankTask extends AsyncTask<Void, Void, BankPayment>{

	private String accessToken;
	private String amount;
	private String noticeURL;
	private String state;
	private String target;
	private AppotaFactory factory;
	private BankPaymentHandler handler;
	private Context context;
	private ProgressDialog pDialog;
	
	public InAppBankTask(Context context, String accessToken, String amount, String noticeURL, String state, String target){
		this.accessToken = accessToken;
		this.noticeURL = noticeURL;
		this.state = state;
		this.target = target;
		this.amount = amount;
		this.context = context;
	}
	
	@Override
	protected void onPostExecute(BankPayment bank) {
		// TODO Auto-generated method stub
		super.onPostExecute(bank);
		if(bank.getErrorCode() == 0){
			handler.onBankPaymentRequestSuccess(bank);
		} else {
			handler.onBankPaymentRequestError(bank.getErrorCode());
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
	
	public void setBankPaymentHandler(BankPaymentHandler handler){
		this.handler = handler;
	}

	@Override
	protected BankPayment doInBackground(Void... params) {
		// TODO Auto-generated method stub
		BankPayment bank = factory.bankInApp(accessToken, amount, noticeURL, state, target);
		return bank;
	}

}
