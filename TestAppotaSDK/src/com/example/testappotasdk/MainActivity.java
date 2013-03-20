package com.example.testappotasdk;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.appota.asdk.core.AppMsg;
import com.appota.asdk.core.Appota;
import com.appota.asdk.core.SMSClickListener;
import com.appota.asdk.exception.ErrorHandler;
import com.appota.asdk.exception.TransactionException;
import com.appota.asdk.handler.GetAccessTokenHandler;
import com.appota.asdk.handler.TransactionStatusHandler;
import com.appota.asdk.model.AppotaAccessToken;
import com.appota.asdk.model.SMSPayment;
import com.appota.asdk.model.TransactionResult;
import com.appota.asdk.task.CheckInAppTransactionTask;
import com.appota.asdk.task.CheckTopupTask;
import com.appota.asdk.util.Constant;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements GetAccessTokenHandler, TransactionStatusHandler{
	
	private Appota appota;
	private String accessToken;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		appota = Appota.getInstance().setContext(this);
		accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("at", "");
		System.err.println(accessToken);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//receive request token from browser callback
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		if(intent.getData() != null){
			String requestToken = intent.getData().getQueryParameter("request_token");
			appota.getAccessToken(requestToken, "en", this);
		}
	}
	
	//get access token require user login 
	public void getAccessToken(View v){
		List<String> scopes = new ArrayList<String>();
		scopes.add(Constant.USER_PAYMENT_SCOPE);
		scopes.add(Constant.USER_INFO_SCOPE);
		scopes.add(Constant.USER_EMAIL_SCOPE);
		appota.getRequestToken(scopes, "test://myapphost", "en");
	}

	//get access token without user login 
	public void getNonUserAccessToken(View btn){
		List<String> scopes = new ArrayList<String>();
		scopes.add(Constant.INAPP_SCOPE);
		appota.getNonUserAccessToken(scopes, this);
	}
	
	//in-app purchase using sms
	public void inAppSMS(View btn){
		appota.inAppSMS(accessToken, "http://appota.com", "abc", "xyz", true, new SMSClickListener() {
			@Override
			public void onSMSClick(SMSPayment sms, int position) {
				// TODO Auto-generated method stub
				//handle onClick event when click on an sms option. for example, go to sms app with send number and syntax
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.putExtra("address", sms.getSmsOptions().get(position).getSendNumber());
				i.putExtra("sms_body", sms.getSmsOptions().get(position).getSyntax());
				i.setType("vnd.android-dir/mms-sms");
				startActivity(i);
			}
		});
	}
	
	//buy tym using sms
	public void topupSMS(View btn){
		appota.topupSMS(accessToken, false, new SMSClickListener() {
			
			@Override
			public void onSMSClick(SMSPayment sms, int position) {
				// TODO Auto-generated method stub
				//handle onClick event when click on an sms option.
				AppMsg.makeText(MainActivity.this, sms.getTransactionID(), AppMsg.STYLE_INFO, null).show();
			}
		});
	}
	
	//in-app purchase using mobile card
	public void inAppCard(View btn){
		appota.inAppCard(accessToken, "http://appota.com", "asd", "qwe");
	}
	
	//buy tym using mobile card
	public void topupCard(View v){
		appota.topupCard(accessToken, "http://appota.com");
	}
	
	//in-app purchase using internet banking
	public void inAppBank(View v){
		appota.inAppBank(accessToken, "http://appota.com", "asd", "qwe");
	}
	
	//buy tym using internet banking
	public void topupBank(View v){
		appota.topupBank(accessToken, "http://appota.com");
	}
	
	//in-app purchase using paypal
	public void inAppPaypal(View v){
		appota.inAppPaypal(accessToken, "http://appota.com", "asd", "qwe");
	}
	
	//buy tym using paypal
	public void topupPaypal(View v){
		appota.topupPaypal(accessToken, "");
	}

	@Override
	public void onGetAccessTokenSuccess(AppotaAccessToken accessToken) {
		// TODO Auto-generated method stub
		PreferenceManager.getDefaultSharedPreferences(this).edit().putString("at", accessToken.getAccessToken()).commit();
		AppMsg.makeText(this, accessToken.getAccessToken(), AppMsg.STYLE_CONFIRM, null).show();
	}

	@Override
	public void onGetAccessTokenError(int errorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetAccessTokenException(int exeptionCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTransactionSuccess(TransactionResult trans) {
		// TODO Auto-generated method stub
		Toast.makeText(this, trans.getMessage(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onTransactionError(int errorCode) {
		// TODO Auto-generated method stub
		ErrorHandler.getInstance().setContext(this).transactionErrorHandler(errorCode);
	}

	@Override
	public void onTransactionException(TransactionException ex) {
		// TODO Auto-generated method stub
		
	}
	
	
	//handle result
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		AppMsg msg; 
		if(requestCode == Constant.IN_APP_CARD_REQUEST_CODE && resultCode == RESULT_OK){
			msg = AppMsg.makeText(this, R.string.transaction_request_sent, AppMsg.STYLE_INFO, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(5 * 1000);
			msg.show();
			CheckInAppTransactionTask task = new CheckInAppTransactionTask(this, accessToken, data.getStringExtra(Constant.TRANSACTION_ID_KEY), false);
			task.setTransactionStatusHandler(this);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	        }
	        else {
	        	task.execute();
	        }
		} else if(requestCode == Constant.IN_APP_CARD_REQUEST_CODE && resultCode == RESULT_CANCELED){
			ErrorHandler.getInstance().setContext(this).cardErrorHandler(data.getIntExtra(Constant.TRANSACTION_ERROR_KEY, -3));
			//if card topup success
		} else if(requestCode == Constant.TOPUP_CARD_REQUEST_CODE && resultCode == RESULT_OK){
			//show success message for user
			msg = AppMsg.makeText(this, R.string.transaction_request_sent, AppMsg.STYLE_INFO, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(5 * 1000);
			msg.show();
			//check for transaction status
			appota.forceCheckTopupTransaction(accessToken, data.getStringExtra(Constant.TRANSACTION_ID_KEY), this);
			//else if card topup fail
		} else if(requestCode == Constant.TOPUP_CARD_REQUEST_CODE && resultCode == RESULT_CANCELED){
			//show error message
			ErrorHandler.getInstance().setContext(this).cardErrorHandler(data.getIntExtra(Constant.TRANSACTION_ERROR_KEY, -3));
		} else if(requestCode == Constant.IN_APP_BANK_REQUEST_CODE && resultCode == RESULT_OK){
			msg = AppMsg.makeText(this, R.string.transaction_request_sent, AppMsg.STYLE_INFO, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(5 * 1000);
			msg.show();
			CheckInAppTransactionTask task = new CheckInAppTransactionTask(this, accessToken, data.getStringExtra(Constant.TRANSACTION_ID_KEY), false);
			task.setTransactionStatusHandler(this);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	        }
	        else {
	        	task.execute();
	        }
		} else if(requestCode == Constant.TOPUP_BANK_REQUEST_CODE && resultCode == RESULT_OK){
			msg = AppMsg.makeText(this, R.string.transaction_request_sent, AppMsg.STYLE_INFO, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(5 * 1000);
			msg.show();
			CheckTopupTask task = new CheckTopupTask(this, accessToken, data.getStringExtra(Constant.TRANSACTION_ID_KEY), false);
			task.setTransactionStatusHandler(this);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	        }
	        else {
	        	task.execute();
	        }
		} else if(requestCode == Constant.TOPUP_PAYPAL_REQUEST_CODE && resultCode == RESULT_OK){
			msg = AppMsg.makeText(this, R.string.transaction_request_sent, AppMsg.STYLE_INFO, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(5 * 1000);
			msg.show();
			CheckTopupTask task = new CheckTopupTask(this, accessToken, data.getStringExtra(Constant.TRANSACTION_ID_KEY), false);
			task.setTransactionStatusHandler(this);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	        }
	        else {
	        	task.execute();
	        }
		} else if(requestCode == Constant.IN_APP_PAYPAL_REQUEST_CODE && resultCode == RESULT_OK){
			msg = AppMsg.makeText(this, R.string.transaction_request_sent, AppMsg.STYLE_INFO, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(5 * 1000);
			msg.show();
			CheckInAppTransactionTask task = new CheckInAppTransactionTask(this, accessToken, data.getStringExtra(Constant.TRANSACTION_ID_KEY), false);
			task.setTransactionStatusHandler(this);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	        }
	        else {
	        	task.execute();
	        }
		}
	}
}
