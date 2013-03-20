package com.appota.asdk.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.appota.asdk.core.AppotaFactory;
import com.appota.asdk.core.DialogManager;
import com.appota.asdk.handler.PaypalPaymentHandler;
import com.appota.asdk.model.PaypalPayment;

public class TopupPaypalTask extends AsyncTask<Void, Void, PaypalPayment>{

	private String accessToken;
	private String amount;
	private String noticeURL;
	private AppotaFactory factory;
	private PaypalPaymentHandler handler;
	private Context context;
	private ProgressDialog pDialog;
	
	public TopupPaypalTask(Context context, String accessToken, String amount, String noticeURL){
		this.accessToken = accessToken;
		this.noticeURL = noticeURL;
		this.amount = amount;
		this.context = context;
	}
	
	@Override
	protected void onPostExecute(PaypalPayment paypal) {
		// TODO Auto-generated method stub
		super.onPostExecute(paypal);
		if(paypal.getErrorCode() == 0){
			handler.onPaypalPaymentRequestSuccess(paypal);
		} else {
			handler.onPaypalPaymentRequestError(paypal.getErrorCode());
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
	
	public void setPaypalPaymentHandler(PaypalPaymentHandler handler){
		this.handler = handler;
	}

	@Override
	protected PaypalPayment doInBackground(Void... params) {
		// TODO Auto-generated method stub
		PaypalPayment paypal = factory.paypalTopup(accessToken, amount, noticeURL);
		return paypal;
	}

}
