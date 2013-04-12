package com.example.testappotasdk;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.appota.asdk.core.AppMsg;
import com.appota.asdk.core.AppotaSDK;
import com.appota.asdk.core.SMSClickListener;
import com.appota.asdk.exception.ErrorHandler;
import com.appota.asdk.handler.BuyItemHandler;
import com.appota.asdk.handler.CheckItemBoughtHandler;
import com.appota.asdk.handler.GetAccessTokenHandler;
import com.appota.asdk.handler.GetBoughtItemListHandler;
import com.appota.asdk.handler.GetItemListHandler;
import com.appota.asdk.handler.GetRequestTokenHandler;
import com.appota.asdk.handler.TransactionStatusHandler;
import com.appota.asdk.model.AppotaAccessToken;
import com.appota.asdk.model.AppotaItem;
import com.appota.asdk.model.BoughtItem;
import com.appota.asdk.model.SMSOption;
import com.appota.asdk.model.SMSPayment;
import com.appota.asdk.model.TransactionResult;
import com.appota.asdk.util.Constant;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	
	private AppotaSDK appota;
	private String accessToken;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		appota = AppotaSDK.getInstance().init(this);
		accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("at", "");
		System.err.println(accessToken);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//get access token require user login 
	public void getAccessToken(View v){
		List<String> scopes = new ArrayList<String>();
		scopes.add(Constant.USER_PAYMENT_SCOPE);
		scopes.add(Constant.USER_INFO_SCOPE);
		scopes.add(Constant.USER_EMAIL_SCOPE);
		scopes.add(Constant.USER_CHARGE_SCOPE);
		appota.getTopupRequestToken("test://myapphost", scopes, "en");
	}

	//get access token without user login 
	public void getNonUserAccessToken(View btn){
		List<String> scopes = new ArrayList<String>();
		scopes.add(Constant.INAPP_SCOPE);
//		scopes.add(Constant.USER_EMAIL_SCOPE);
//		scopes.add(Constant.USER_INFO_SCOPE);
//		scopes.add(Constant.USER_PAYMENT_SCOPE);
//		scopes.add(Constant.USER_CHARGE_SCOPE);
		appota.getInAppRequestToken(scopes, "en", new GetRequestTokenHandler() {
			
			@Override
			public void onGetRequestTokenSuccess(String requestToken) {
				// TODO Auto-generated method stub
				appota.getInAppAccessToken(requestToken, "en", new GetAccessTokenHandler() {
					
					@Override
					public void onGetAccessTokenSuccess(AppotaAccessToken accessToken) {
						// TODO Auto-generated method stub
						PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putString("at", accessToken.getAccessToken()).commit();
						AppMsg.makeText(MainActivity.this, accessToken.getAccessToken(), AppMsg.STYLE_CONFIRM, null).show();
					}
					
					@Override
					public void onGetAccessTokenError(int errorCode) {
						// TODO Auto-generated method stub
						
					}
				});
			}
			
			@Override
			public void onGetRequestTokenError(int errorCode) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	//in-app purchase using sms
	public void inAppSMS(View btn){
		accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("at", "");
		appota.smsInApp(this, accessToken, "http://appota.com", "", "", false, new SMSClickListener() {
			@Override
			public void onSMSClick(SMSPayment sms, int position) {
				// TODO Auto-generated method stub
				//do whatever you want. For example, go to the sms app
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.putExtra("address", sms.getSmsOptions().get(position).getSendNumber());
				i.putExtra("sms_body", sms.getSmsOptions().get(position).getSyntax());
				i.setType("vnd.android-dir/mms-sms");
				startActivity(i);
				
				//get Transaction Id and check for transaction status
				String transactionId = sms.getTransactionID();
				AppMsg.makeText(MainActivity.this, transactionId, AppMsg.STYLE_INFO, null).show();
			}
		});
	}
	
	//buy tym using sms
	public void topupSMS(View btn){
		accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("at", "");
		appota.smsTopup(this, accessToken, true, new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long arg3) {
				// TODO Auto-generated method stub
				//get clicked sms option data
				SMSOption sms = (SMSOption) parent.getItemAtPosition(position);
				
				//do whatever you want. For example, go to the sms app
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.putExtra("address", sms.getSendNumber());
				i.putExtra("sms_body", sms.getSyntax());
				i.setType("vnd.android-dir/mms-sms");
				startActivity(i);
				
				//get Transaction Id and check for transaction status
				String transactionId = sms.getTransactionId();
				AppMsg.makeText(MainActivity.this, transactionId, AppMsg.STYLE_INFO, null).show();
//				appota.checkTopup(accessToken, transactionId);
			}
		});
	}
	
	//in-app purchase using mobile card
	public void inAppCard(View btn){
		accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("at", "");
		appota.cardInApp(this, accessToken, "http://appota.com", "asd", "qwe");
	}
	
	//buy tym using mobile card
	public void topupCard(View v){
		accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("at", "");
		appota.cardTopup(this, accessToken, "http://appota.com");
	}
	
	//in-app purchase using internet banking
	public void inAppBank(View v){
		accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("at", "");
		appota.bankInApp(this, accessToken, null, null, "http://appota.com");
	}
	
	//buy tym using internet banking
	public void topupBank(View v){
		accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("at", "");
		appota.bankTopup(this, accessToken, "http://appota.com");
	}
	
	//in-app purchase using paypal
	public void inAppPaypal(View v){
		accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("at", "");
		appota.paypalInApp(this, accessToken, "http://appota.com", "asd", "qwe");
	}
	
	//buy tym using paypal
	public void topupPaypal(View v){
		accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("at", "");
		appota.paypalTopup(this, accessToken, "http://appota.com");
	}

	
	public void getItemList(View v){
		accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("at", "");
		appota.getItemList(accessToken, 0, 0, null, new GetItemListHandler() {
			
			@Override
			public void onGetItemListSuccess(List<AppotaItem> listItem) {
				// TODO Auto-generated method stub
				AppMsg.makeText(MainActivity.this, listItem.get(0).getName(), AppMsg.STYLE_INFO, null).show();
			}
			
			@Override
			public void onGetItemListError(int errorCode) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void getUserBoughtItem(View v){
		accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("at", "");
		appota.getBoughtItemList(accessToken, 0, 1, "", new GetBoughtItemListHandler() {
			
			@Override
			public void onGetBoughtItemListSuccess(List<BoughtItem> listItem) {
				// TODO Auto-generated method stub
				if(listItem.size() > 0){
					AppMsg.makeText(MainActivity.this, listItem.get(0).getName(), AppMsg.STYLE_INFO, null).show();
				}
			}
			
			@Override
			public void onGetBoughtItemListError(int errorCode) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void checkItemBought(View v){
		accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("at", "");
		appota.isItemBought(accessToken, "64", new CheckItemBoughtHandler() {
			
			@Override
			public void onCheckItemBoughtSuccess(boolean isBought) {
				// TODO Auto-generated method stub
				AppMsg.makeText(MainActivity.this, isBought + "", AppMsg.STYLE_INFO, null).show();
			}
			
			@Override
			public void onCheckItemBoughtError(int errorCode) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void buyItem(View v){
		accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("at", "");
		appota.buyItem(accessToken, "65", 1, 1, new BuyItemHandler() {
			
			@Override
			public void onBuyItemSuccess(String message) {
				// TODO Auto-generated method stub
				AppMsg.makeText(MainActivity.this, message, AppMsg.STYLE_INFO, null).show();
			}
			
			@Override
			public void onBuyItemFail(String message) {
				// TODO Auto-generated method stub
				AppMsg.makeText(MainActivity.this, message, AppMsg.STYLE_INFO, null).show();
			}
			
			@Override
			public void onBuyItemError(int errorCode) {
				// TODO Auto-generated method stub
				AppMsg.makeText(MainActivity.this, errorCode + "", AppMsg.STYLE_INFO, null).show();
			}
		});
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
			String inappID = data.getStringExtra(Constant.TRANSACTION_ID_KEY);
			appota.checkInAppTransaction(accessToken, inappID, false, new TransactionStatusHandler() {
				
				@Override
				public void onTransactionSuccess(TransactionResult trans) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onTransactionError(int errorCode) {
					// TODO Auto-generated method stub
					
				}
			});
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
			appota.checkTopupTransaction(accessToken, data.getStringExtra(Constant.TRANSACTION_ID_KEY), false, new TransactionStatusHandler() {
				
				@Override
				public void onTransactionSuccess(TransactionResult trans) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onTransactionError(int errorCode) {
					// TODO Auto-generated method stub
					
				}
			});
			//else if card topup fail
		} else if(requestCode == Constant.TOPUP_CARD_REQUEST_CODE && resultCode == RESULT_CANCELED){
			//show error message
			if(data != null){
				ErrorHandler.getInstance().setContext(this).cardErrorHandler(data.getIntExtra(Constant.TRANSACTION_ERROR_KEY, -3));
			}
		} else if(requestCode == Constant.TOPUP_PAYPAL_REQUEST_CODE && resultCode == RESULT_OK){
			msg = AppMsg.makeText(this, R.string.transaction_request_sent, AppMsg.STYLE_INFO, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(5 * 1000);
			msg.show();
			//check transaction status
		} else if(requestCode == Constant.IN_APP_PAYPAL_REQUEST_CODE && resultCode == RESULT_OK){
			msg = AppMsg.makeText(this, R.string.transaction_request_sent, AppMsg.STYLE_INFO, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(5 * 1000);
			msg.show();
			//check transaction status
		} else if(requestCode == Constant.REQUEST_TOKEN_REQUEST_CODE && resultCode == RESULT_OK){
			String requestToken = data.getStringExtra(Constant.REQUEST_TOKEN_KEY);
			AppMsg.makeText(MainActivity.this, requestToken, AppMsg.STYLE_CONFIRM, null).show();
			appota.getTopupAccessToken(requestToken, "en", new GetAccessTokenHandler() {
				
				@Override
				public void onGetAccessTokenSuccess(AppotaAccessToken accessToken) {
					// TODO Auto-generated method stub
					PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putString("at", accessToken.getAccessToken()).commit();
					AppMsg.makeText(MainActivity.this, accessToken.getAccessToken(), AppMsg.STYLE_CONFIRM, null).show();
				}
				
				@Override
				public void onGetAccessTokenError(int errorCode) {
					// TODO Auto-generated method stub
					
				}
			});
		}
	}
}
