package com.appota.asdk.task;

import com.appota.asdk.core.AppotaFactory;
import com.appota.asdk.core.DialogManager;
import com.appota.asdk.exception.CardPaymentException;
import com.appota.asdk.handler.CardPaymentHandler;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class InAppCardTask extends AsyncTask<Void, Void, String>{
	
	private String accessToken;
	private String cardSerial;
	private String cardCode;
	private String vendor;
	private String noticeURL;
	private String state;
	private String target;
	private AppotaFactory factory;
	private CardPaymentHandler handler;
	private Context context;
	private ProgressDialog pDialog;
	
	public InAppCardTask(Context context, String accessToken, String cardSerial, String cardCode, String vendor, String noticeURL, String state, String target){
		this.accessToken = accessToken;
		this.cardCode = cardCode;
		this.cardSerial = cardSerial;
		this.noticeURL = noticeURL;
		this.state = state;
		this.target = target;
		this.vendor = vendor;
		this.context = context;
	}
	
	@Override
	protected void onPostExecute(String inappID) {
		// TODO Auto-generated method stub
		super.onPostExecute(inappID);
		if(inappID.contains("|")){
			handler.onCardPaymentRequestException(new CardPaymentException(inappID.substring(0, inappID.length()-2)));
		} else if(inappID.equalsIgnoreCase("-4")){
			handler.onCardPaymentRequestError(-4);
		} else if(inappID.equalsIgnoreCase("-3")){
			handler.onCardPaymentRequestError(-3);
		} else if(inappID.equalsIgnoreCase("-2")){
			handler.onCardPaymentRequestError(-2);
		} else if(inappID.equalsIgnoreCase("-1")){
			handler.onCardPaymentRequestError(-1);
		} else {
			handler.onCardPaymentRequestSuccess(inappID);
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
	
	public void setCardPaymenrHandler(CardPaymentHandler handler){
		this.handler = handler;
	}

	@Override
	protected String doInBackground(Void... params) {
		// TODO Auto-generated method stub
		String inappID = factory.cardInApp(accessToken, cardCode, cardSerial, vendor, noticeURL, state, target);
		return inappID;
	}

}
