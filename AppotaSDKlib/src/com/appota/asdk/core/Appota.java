package com.appota.asdk.core;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.appota.asdk.BankPaymentActivity;
import com.appota.asdk.CardPaymentActivity;
import com.appota.asdk.PaypalActivity;
import com.appota.asdk.R;
import com.appota.asdk.exception.BankPaymentException;
import com.appota.asdk.exception.ErrorHandler;
import com.appota.asdk.exception.PaypalPaymentException;
import com.appota.asdk.exception.SMSPaymentException;
import com.appota.asdk.handler.BankPaymentHandler;
import com.appota.asdk.handler.GetAccessTokenHandler;
import com.appota.asdk.handler.PaypalPaymentHandler;
import com.appota.asdk.handler.SMSPaymentHandler;
import com.appota.asdk.handler.TransactionStatusHandler;
import com.appota.asdk.model.BankPayment;
import com.appota.asdk.model.PaypalPayment;
import com.appota.asdk.model.SMSPayment;
import com.appota.asdk.task.CheckInAppTransactionTask;
import com.appota.asdk.task.CheckTopupTask;
import com.appota.asdk.task.GetAccessTokenTask;
import com.appota.asdk.task.GetNonUserAccessTokenTask;
import com.appota.asdk.task.InAppBankTask;
import com.appota.asdk.task.InAppPaypalTask;
import com.appota.asdk.task.InAppSMSTask;
import com.appota.asdk.task.TopupBankTask;
import com.appota.asdk.task.TopupPaypalTask;
import com.appota.asdk.task.TopupSMSTask;
import com.appota.asdk.util.Constant;

@SuppressLint("NewApi")
public class Appota {
	
	private static Appota defaultInstance;
	private Activity context;
	private String clientKey;
	private String clientSecret;
	private final String TAG = Appota.class.getSimpleName();

	public static Appota getInstance() {
        if (defaultInstance == null) {
            synchronized (Appota.class) {
                if (defaultInstance == null) {
                    defaultInstance = new Appota();
                }
            }
        }
        return defaultInstance;
    }
	
	public Appota setContext(Activity context){
		this.context = context;
		ApplicationInfo ai;
		try {
			ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
		    clientKey = bundle.getString("client_key");
		    clientSecret = bundle.getString("client_secret");
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
		} catch (NullPointerException e) {
			Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
		}
		return defaultInstance;
	}
	
	public void getRequestToken(List<String> scopes, String redirectURI, String lang){
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(AppotaFactory.getInstance().init(context).getRequestToken(clientKey, redirectURI, scopes, lang)));
		context.startActivity(i);
	}
	
	public void getAccessToken(String requestToken, String lang, GetAccessTokenHandler handler){
		GetAccessTokenTask task = new GetAccessTokenTask(context, requestToken, clientKey, clientSecret, lang);
		task.setAccessTokenHandler(handler);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else {
        	task.execute();
        }
	}
	
	public void getNonUserAccessToken(List<String> scopes, GetAccessTokenHandler handler){
		GetNonUserAccessTokenTask task = new GetNonUserAccessTokenTask(context, clientKey, clientSecret, scopes, "en");
		task.setRequestTokenHandler(handler);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else {
        	task.execute();
        }
	}
	
	public void inAppSMS(final String accessToken, String noticeURL, String state, String target, boolean shortSyntax, final SMSClickListener listener){
		InAppSMSTask task = new InAppSMSTask(context, accessToken, noticeURL, state, target, shortSyntax);
		task.setSMSPaymentHandler(new SMSPaymentHandler() {
			
			@Override
			public void onSMSPaymentRequestSuccess(final SMSPayment sms) {
				// TODO Auto-generated method stub
				//showSMSOptionsDialog(sms, onClick);
				showSMSOptionsDialog(sms, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.onSMSClick(sms, which);
//						CheckInAppTransactionTask task = new CheckInAppTransactionTask(context, accessToken, sms.getTransactionID(), false);
//						task.setTransactionStatusHandler(handler);
//						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//				        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				        }
//				        else {
//				        	task.execute();
//				        }
//						
//						Intent i = new Intent(Intent.ACTION_VIEW);
//						i.putExtra("address", sms.getSmsOptions().get(which).getSendNumber());
//						i.putExtra("sms_body", sms.getSmsOptions().get(which).getSyntax());
//						i.setType("vnd.android-dir/mms-sms");
//						context.startActivity(i);
					}
				});
			}
			
			@Override
			public void onSMSPaymentRequestException(SMSPaymentException ex) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSMSPaymentRequestError(int errorCode) {
				// TODO Auto-generated method stub
				ErrorHandler.getInstance().setContext(context).smsErrorHandler(errorCode);
			}
		});
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else {
        	task.execute();
        }
	}
	
	public void topupSMS(final String accessToken, boolean shortSyntax, final SMSClickListener listener){
		TopupSMSTask task = new TopupSMSTask(context, accessToken, shortSyntax);
		task.setSMSPaymentHandler(new SMSPaymentHandler() {
			
			@Override
			public void onSMSPaymentRequestSuccess(final SMSPayment sms) {
				// TODO Auto-generated method stub
				showSMSOptionsDialog(sms, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int which) {
						// TODO Auto-generated method stub
						listener.onSMSClick(sms, which);
					}
				});
//				showSMSOptionsDialog(sms, new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						
//						CheckTopupTask task = new CheckTopupTask(context, accessToken, sms.getTransactionID(), false);
//						task.setTransactionStatusHandler(handler);
//						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//				        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				        }
//				        else {
//				        	task.execute();
//				        }
//						
//						Intent i = new Intent(Intent.ACTION_VIEW);
//						i.putExtra("address", sms.getSmsOptions().get(which).getSendNumber());
//						i.putExtra("sms_body", sms.getSmsOptions().get(which).getSyntax());
//						i.setType("vnd.android-dir/mms-sms");
//						context.startActivity(i);
//					}
//				});
			}
			
			@Override
			public void onSMSPaymentRequestException(SMSPaymentException ex) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSMSPaymentRequestError(int errorCode) {
				// TODO Auto-generated method stub
				ErrorHandler.getInstance().setContext(context).smsErrorHandler(errorCode);
			}
		});
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else {
        	task.execute();
        }
	}
	
	public void inAppCard(final String accessToken, String noticeURL, String state, String target){
		Intent cardIntent = new Intent(context, CardPaymentActivity.class);
		Bundle b = new Bundle();
		b.putInt(Constant.PAYMENT_TYPE, Constant.PAYMENT_TYPE_INAPP);
		b.putString(Constant.ACCESS_TOKEN_KEY, accessToken);
		b.putString(Constant.NOTICE_URL_KEY, noticeURL);
		b.putString(Constant.STATE_KEY, state);
		b.putString(Constant.TARGET_KEY, target);
		cardIntent.putExtras(b);
		//cardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivityForResult(cardIntent, Constant.IN_APP_CARD_REQUEST_CODE);
	}
	
	public void topupCard(final String accessToken, String noticeURL){
		Intent cardIntent = new Intent(context, CardPaymentActivity.class);
		Bundle b = new Bundle();
		b.putInt(Constant.PAYMENT_TYPE, Constant.PAYMENT_TYPE_TOPUP);
		b.putString(Constant.ACCESS_TOKEN_KEY, accessToken);
		b.putString(Constant.NOTICE_URL_KEY, noticeURL);
		cardIntent.putExtras(b);
		//cardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivityForResult(cardIntent, Constant.TOPUP_CARD_REQUEST_CODE);
	}
	
	public void inAppBank(final String accessToken, final String noticeURL, final String state, final String target){
		showOptionValue(R.array.bank_value, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String amount = "50000";
				switch (which) {
				case 0:
					amount = "50000";
					break;
				case 1:
					amount = "100000";
					break;
				case 2:
					amount = "200000";
					break;
				case 3:
					amount = "500000";
					break;
				case 4:
					amount = "1000000";
					break;
				case 5:
					amount = "2000000";
					break;
				}
				InAppBankTask task = new InAppBankTask(context, accessToken, amount, noticeURL, state, target);
				task.setBankPaymentHandler(new BankPaymentHandler() {
					
					@Override
					public void onBankPaymentRequestSuccess(BankPayment bank) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(context, BankPaymentActivity.class);
						intent.putExtra(Constant.SMART_LINK_URL_KEY, bank.getOption().getUrl());
						intent.putExtra(Constant.TRANSACTION_ID_KEY, bank.getTransactionId());
						context.startActivityForResult(intent, Constant.IN_APP_BANK_REQUEST_CODE);
					}
					
					@Override
					public void onBankPaymentRequestException(BankPaymentException ex) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onBankPaymentRequestError(int errorCode) {
						// TODO Auto-generated method stub
						ErrorHandler.getInstance().setContext(context).bankErrorHandler(errorCode);
					}
				});
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		        }
		        else {
		        	task.execute();
		        }
			}
		});
	}
	
	public void topupBank(final String accessToken, final String noticeURL){
		showOptionValue(R.array.bank_topup_value, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String amount = "50000";
				switch (which) {
				case 0:
					amount = "50000";
					break;
				case 1:
					amount = "100000";
					break;
				case 2:
					amount = "200000";
					break;
				case 3:
					amount = "500000";
					break;
				case 4:
					amount = "1000000";
					break;
				case 5:
					amount = "2000000";
					break;
				}
				TopupBankTask task = new TopupBankTask(context, accessToken, amount, noticeURL);
				task.setBankPaymentHandler(new BankPaymentHandler() {
					
					@Override
					public void onBankPaymentRequestSuccess(BankPayment bank) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(context, BankPaymentActivity.class);
						intent.putExtra(Constant.SMART_LINK_URL_KEY, bank.getOption().getUrl());
						intent.putExtra(Constant.TRANSACTION_ID_KEY, bank.getTransactionId());
						context.startActivityForResult(intent, Constant.TOPUP_BANK_REQUEST_CODE);
					}
					
					@Override
					public void onBankPaymentRequestException(BankPaymentException ex) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onBankPaymentRequestError(int errorCode) {
						// TODO Auto-generated method stub
						ErrorHandler.getInstance().setContext(context).bankErrorHandler(errorCode);
					}
				});
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		        }
		        else {
		        	task.execute();
		        }
			}
		});
	}
	
	public void inAppPaypal(final String accessToken, final String noticeURL, final String state, final String target){
		showOptionValue(R.array.paypal_value, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String amount = "5";
				switch (which) {
				case 0:
					amount = "5";
					break;
				case 1:
					amount = "10";
					break;
				case 2:
					amount = "50";
					break;
				}
				InAppPaypalTask task = new InAppPaypalTask(context, accessToken, amount, noticeURL, state, target);
				task.setPaypalPaymentHandler(new PaypalPaymentHandler() {
					
					@Override
					public void onPaypalPaymentRequestSuccess(PaypalPayment paypal) {
						// TODO Auto-generated method stub
						Intent paypalIntent = new Intent(context, PaypalActivity.class);
						Bundle b = new Bundle();
						b.putSerializable(Constant.PAYPAL_DATA, paypal);
						paypalIntent.putExtras(b);
						context.startActivityForResult(paypalIntent, Constant.IN_APP_PAYPAL_REQUEST_CODE);
					}
					
					@Override
					public void onPaypalPaymentRequestException(PaypalPaymentException ex) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onPaypalPaymentRequestError(int errorCode) {
						// TODO Auto-generated method stub
						ErrorHandler.getInstance().setContext(context).paypalErrorHandler(errorCode);
					}
				});
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		        }
		        else {
		        	task.execute();
		        }
			}
		});
	}
	
	public void topupPaypal(final String accessToken, final String noticeURL){
		showOptionValue(R.array.paypal_topup_value, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String amount = "5";
				switch (which) {
				case 0:
					amount = "5";
					break;
				case 1:
					amount = "10";
					break;
				case 2:
					amount = "50";
					break;
				}
				TopupPaypalTask task = new TopupPaypalTask(context, accessToken, amount, noticeURL);
				task.setPaypalPaymentHandler(new PaypalPaymentHandler() {
					
					@Override
					public void onPaypalPaymentRequestSuccess(PaypalPayment paypal) {
						// TODO Auto-generated method stub
						Intent paypalIntent = new Intent(context, PaypalActivity.class);
						Bundle b = new Bundle();
						b.putSerializable(Constant.PAYPAL_DATA, paypal);
						paypalIntent.putExtras(b);
						context.startActivityForResult(paypalIntent, Constant.TOPUP_PAYPAL_REQUEST_CODE);
					}
					
					@Override
					public void onPaypalPaymentRequestException(PaypalPaymentException ex) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onPaypalPaymentRequestError(int errorCode) {
						// TODO Auto-generated method stub
						ErrorHandler.getInstance().setContext(context).paypalErrorHandler(errorCode);
					}
				});
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		        }
		        else {
		        	task.execute();
		        }
			}
		});
	}
	
	public void forceCheckInAppTransaction(String accessToken, String inAppID, TransactionStatusHandler handler){
		CheckInAppTransactionTask task = new CheckInAppTransactionTask(context, accessToken, inAppID, true);
		task.setTransactionStatusHandler(handler);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else {
        	task.execute();
        }
	}
	
	public void forceCheckTopupTransaction(String accessToken, String topupID, TransactionStatusHandler handler){
		CheckTopupTask task = new CheckTopupTask(context, accessToken, topupID, true);
		task.setTransactionStatusHandler(handler);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else {
        	task.execute();
        }
	}
	
	public void showSMSOptionsDialog(SMSPayment sms, DialogInterface.OnClickListener onSMSClick){
		DialogManager dm = new DialogManager(context);
		dm.showDialogWithItems(R.string.app_name, sms.getSmsOptions(), onSMSClick);
	}
	
	public void showOptionValue(int arrayId, DialogInterface.OnClickListener onClick){
		DialogManager dm = new DialogManager(context);
		dm.showBankValue(R.string.app_name, arrayId, onClick);
	}
}
