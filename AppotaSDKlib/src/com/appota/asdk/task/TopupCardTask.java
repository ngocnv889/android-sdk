package com.appota.asdk.task;

import com.appota.asdk.core.AppotaFactory;
import com.appota.asdk.core.DialogManager;
import com.appota.asdk.exception.CardPaymentException;
import com.appota.asdk.handler.CardPaymentHandler;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class TopupCardTask extends AsyncTask<Void, Void, String>{
	
	private String accessToken;
	private String cardSerial;
	private String cardCode;
	private String vendor;
	private String noticeURL;
	private AppotaFactory factory;
	private CardPaymentHandler handler;
	private Context context;
	private ProgressDialog pDialog;
	
	public TopupCardTask(Context context, String accessToken, String cardSerial, String cardCode, String vendor, String noticeURL){
		this.accessToken = accessToken;
		this.cardCode = cardCode;
		this.cardSerial = cardSerial;
		this.noticeURL = noticeURL;
		this.vendor = vendor;
		this.context = context;
	}
	
	@Override
	protected void onPostExecute(String topupID) {
		// TODO Auto-generated method stub
		super.onPostExecute(topupID);
		if(topupID.contains("|")){
			handler.onCardPaymentRequestException(new CardPaymentException(topupID.substring(0, topupID.length()-2)));
		} else if(topupID.equalsIgnoreCase("-4")){
			handler.onCardPaymentRequestError(-4);
		} else if(topupID.equalsIgnoreCase("-3")){
			handler.onCardPaymentRequestError(-3);
		} else if(topupID.equalsIgnoreCase("-2")){
			handler.onCardPaymentRequestError(-2);
		} else if(topupID.equalsIgnoreCase("-1")){
			handler.onCardPaymentRequestError(-1);
		} else {
			handler.onCardPaymentRequestSuccess(topupID);
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
		String topupID = factory.cardTopup(accessToken, cardSerial, cardCode, vendor, noticeURL);
		return topupID;
	}

}
