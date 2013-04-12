package com.appota.asdk.core;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.appota.asdk.CardPaymentActivity;
import com.appota.asdk.LoginActivity;
import com.appota.asdk.PaypalActivity;
import com.appota.asdk.ProgressDialogManager;
import com.appota.asdk.R;
import com.appota.asdk.exception.ErrorHandler;
import com.appota.asdk.handler.BankPaymentHandler;
import com.appota.asdk.handler.BuyItemHandler;
import com.appota.asdk.handler.CheckItemBoughtHandler;
import com.appota.asdk.handler.GetAccessTokenHandler;
import com.appota.asdk.handler.GetBoughtItemListHandler;
import com.appota.asdk.handler.GetItemListHandler;
import com.appota.asdk.handler.GetRequestTokenHandler;
import com.appota.asdk.handler.PaypalPaymentHandler;
import com.appota.asdk.handler.SMSPaymentHandler;
import com.appota.asdk.handler.TransactionStatusHandler;
import com.appota.asdk.model.BankOption;
import com.appota.asdk.model.BankPayment;
import com.appota.asdk.model.PaypalPayment;
import com.appota.asdk.model.SMSPayment;
import com.appota.asdk.task.CheckInAppTransactionTask;
import com.appota.asdk.task.CheckTopupTask;
import com.appota.asdk.util.Constant;


@SuppressLint("NewApi")
public class AppotaSDK {
	
	private static volatile AppotaSDK defaultInstance;
	private Activity context;
	private AppotaFactory appotaFactory;

	public static AppotaSDK getInstance() {
        if (defaultInstance == null) {
            synchronized (AppotaSDK.class) {
                if (defaultInstance == null) {
                    defaultInstance = new AppotaSDK();
                }
            }
        }
        return defaultInstance;
    }
	
	public AppotaSDK init(Activity context){
		this.context = context;
		appotaFactory = AppotaFactory.getInstance().init(context);
		return this;
	}
	
	public void getTopupRequestToken(String redirectURI, List<String> scopes, String lang){
		String endpoint = appotaFactory.getRequestToken(redirectURI, scopes, lang);
		Intent i = new Intent(context, LoginActivity.class);
		i.putExtra(Constant.REQUEST_TOKEN_URL_KEY, endpoint);
		i.putExtra(Constant.REDIRECT_URI_KEY, redirectURI);
		//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivityForResult(i, Constant.REQUEST_TOKEN_REQUEST_CODE);
	}
	
	public void getInAppRequestToken(List<String> scopes, String lang, GetRequestTokenHandler handler){
		appotaFactory.getNonUserRequestToken(scopes, lang, handler);
	}
	
	public void getTopupAccessToken(String requestToken, String lang, GetAccessTokenHandler handler){
		appotaFactory.getAccessToken(requestToken, lang, handler);
	}
	
	public void getInAppAccessToken(String requestToken, String lang, GetAccessTokenHandler handler){
		appotaFactory.getNonUserAccessToken(requestToken, lang, handler);
	}
	
	public void smsTopup(final Activity activity, String accessToken, boolean shortSyntax, final OnItemClickListener onItemClick){
		final ProgressDialog pDialog = ProgressDialogManager.showProgressDialog(context, R.string.loading);
		appotaFactory.smsTopup(accessToken, shortSyntax, new SMSPaymentHandler() {
			@Override
			public void onSMSPaymentRequestSuccess(SMSPayment sms) {
				// TODO Auto-generated method stub
				if(pDialog.isShowing()){
					pDialog.dismiss();
				}
				final Dialog dialog = new Dialog(activity);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		        dialog.setContentView(R.layout.layout_tym_sms);
		        dialog.setCancelable(true);
		        ((TextView)dialog.findViewById(R.id.dialog_title)).setText(R.string.buy_tym_sms);
		        SMSTYMAdapter adapter = new SMSTYMAdapter(activity.getApplicationContext(), R.layout.sms_item, sms.getSmsOptions());
		        HorizontalListView listSMS = (HorizontalListView)dialog.findViewById(R.id.sms_list);
		        listSMS.setAdapter(adapter);
		        listSMS.setOnItemClickListener(onItemClick);
		        ((Button)dialog.findViewById(R.id.btn_close_dialog)).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
		        dialog.show();
			}
			
			@Override
			public void onSMSPaymentRequestError(int errorCode) {
				// TODO Auto-generated method stub
				pDialog.dismiss();
				ErrorHandler.getInstance().setContext(activity).smsErrorHandler(errorCode);
			}
		});
	}
	
	public void smsInApp(final Activity activity, String accessToken, String noticeURL, String state, String target, boolean shortSyntax, final SMSClickListener onSMSClick){
		final ProgressDialog pDialog = ProgressDialogManager.showProgressDialog(context, R.string.loading);
		appotaFactory.smsInApp(accessToken, noticeURL, state, target, shortSyntax, new SMSPaymentHandler() {
			@Override
			public void onSMSPaymentRequestSuccess(final SMSPayment sms) {
				// TODO Auto-generated method stub
				if(pDialog.isShowing()){
					pDialog.dismiss();
				}
				showSMSOptionsDialog(sms, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						onSMSClick.onSMSClick(sms, arg1);
					}
				});
			}
			
			@Override
			public void onSMSPaymentRequestError(int errorCode) {
				// TODO Auto-generated method stub
				pDialog.dismiss();
				ErrorHandler.getInstance().setContext(activity).smsErrorHandler(errorCode);
			}
		});
	}
	
	public void bankTopup(final Activity activity, final String accessToken, final String noticeURL){
		final ProgressDialog pDialog = ProgressDialogManager.showProgressDialog(activity, R.string.loading);
		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_tym_sms);
        dialog.setCancelable(true);
        final List<BankOption> listBankOption = new ArrayList<BankOption>();
        listBankOption.add(new BankOption(479, 50000));
        listBankOption.add(new BankOption(974, 100000));
        listBankOption.add(new BankOption(1964, 200000));
        listBankOption.add(new BankOption(4934, 500000));
        listBankOption.add(new BankOption(9884, 1000000));
        listBankOption.add(new BankOption(19784, 2000000));
        ((TextView)dialog.findViewById(R.id.dialog_title)).setText(R.string.buy_tym_bank);
        BankTYMAdapter adapter = new BankTYMAdapter(activity.getApplicationContext(), R.layout.sms_item, listBankOption);
        HorizontalListView list = (HorizontalListView)dialog.findViewById(R.id.sms_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos, long arg3) {
				BankOption bank = (BankOption) parent.getItemAtPosition(pos);
				appotaFactory.bankTopup(accessToken, String.valueOf(bank.getAmount()), noticeURL, new BankPaymentHandler() {
					@Override
					public void onBankPaymentRequestSuccess(BankPayment bank) {
						if(pDialog.isShowing()){
							pDialog.dismiss();
						}
//						Intent intent = new Intent(activity, BankPaymentActivity.class);
//						intent.putExtra(Constant.SMART_LINK_URL_KEY, bank.getOption().getUrl());
//						intent.putExtra(Constant.TRANSACTION_ID_KEY, bank.getTransactionId());
//						activity.startActivityForResult(intent, Constant.TOPUP_BANK_REQUEST_CODE);
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(bank.getOption().getUrl()));
						activity.startActivity(intent);
					}
					
					@Override
					public void onBankPaymentRequestError(int errorCode) {
						if(pDialog.isShowing()){
							pDialog.dismiss();
						}
						ErrorHandler.getInstance().setContext(activity).bankErrorHandler(errorCode);
					}
				});
			}
		});
        ((Button)dialog.findViewById(R.id.btn_close_dialog)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
        dialog.show();
	}
	
	public void bankInApp(final Activity activity, final String accessToken, final String state, final String target, final String noticeURL){
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
				final ProgressDialog pDialog = ProgressDialogManager.showProgressDialog(activity, R.string.loading);
				appotaFactory.bankInApp(accessToken, amount, noticeURL, state, target, new BankPaymentHandler() {
					
					@Override
					public void onBankPaymentRequestSuccess(BankPayment bank) {
						// TODO Auto-generated method stub
						if(pDialog.isShowing()){
							pDialog.dismiss();
						}
//						Intent intent = new Intent(activity, BankPaymentActivity.class);
//						intent.putExtra(Constant.SMART_LINK_URL_KEY, bank.getOption().getUrl());
//						intent.putExtra(Constant.TRANSACTION_ID_KEY, bank.getTransactionId());
//						activity.startActivityForResult(intent, Constant.IN_APP_BANK_REQUEST_CODE);
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(bank.getOption().getUrl()));
						activity.startActivity(intent);
					}
					
					@Override
					public void onBankPaymentRequestError(int errorCode) {
						// TODO Auto-generated method stub
						if(pDialog.isShowing()){
							pDialog.dismiss();
						}
						ErrorHandler.getInstance().setContext(activity).bankErrorHandler(errorCode);
					}
				});
			}
		});
	}
	
	public void paypalTopup(final Activity activity, final String accessToken, final String noticeURL){
		final ProgressDialog pDialog = ProgressDialogManager.showProgressDialog(activity, R.string.loading);
		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_tym_sms);
        dialog.setCancelable(true);
        final List<PaypalPayment> listPaypal = new ArrayList<PaypalPayment>();
        listPaypal.add(new PaypalPayment(5, 850));
        listPaypal.add(new PaypalPayment(10, 1700));
        listPaypal.add(new PaypalPayment(50, 8500));
        ((TextView)dialog.findViewById(R.id.dialog_title)).setText(R.string.buy_tym_paypal);
        PaypalTYMAdapter ppAdapter = new PaypalTYMAdapter(activity.getApplicationContext(), R.layout.sms_item, listPaypal);
        HorizontalListView listPP = (HorizontalListView) dialog.findViewById(R.id.sms_list);
        listPP.setAdapter(ppAdapter);
        listPP.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos, long arg3) {
				PaypalPayment pp = (PaypalPayment) parent.getItemAtPosition(pos);
				appotaFactory.paypalTopup(accessToken, String.valueOf(pp.getAmount()), noticeURL, new PaypalPaymentHandler() {
					@Override
					public void onPaypalPaymentRequestSuccess(PaypalPayment paypal) {
						// TODO Auto-generated method stub
						if(pDialog != null){
							pDialog.dismiss();
						}
						Intent paypalIntent = new Intent(activity, PaypalActivity.class);
						Bundle b = new Bundle();
						b.putSerializable(Constant.PAYPAL_DATA, paypal);
						paypalIntent.putExtras(b);
						activity.startActivityForResult(paypalIntent, Constant.TOPUP_PAYPAL_REQUEST_CODE);
					}
					
					@Override
					public void onPaypalPaymentRequestError(int errorCode) {
						// TODO Auto-generated method stub
						if(pDialog.isShowing()){
							pDialog.dismiss();
						}
						ErrorHandler.getInstance().setContext(activity).paypalErrorHandler(errorCode);
					}
				});
			}
		});
        ((Button)dialog.findViewById(R.id.btn_close_dialog)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
        dialog.show();
	}
	
	public void paypalInApp(final Activity activity, final String accessToken, final String noticeURL, final String state, final String target){
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
				final ProgressDialog pDialog = ProgressDialogManager.showProgressDialog(activity, R.string.loading);
				appotaFactory.paypalInApp(accessToken, amount, noticeURL, state, target, new PaypalPaymentHandler() {
					@Override
					public void onPaypalPaymentRequestSuccess(PaypalPayment paypal) {
						// TODO Auto-generated method stub
						if(pDialog.isShowing()){
							pDialog.dismiss();
						}
						Intent paypalIntent = new Intent(activity, PaypalActivity.class);
						Bundle b = new Bundle();
						b.putSerializable(Constant.PAYPAL_DATA, paypal);
						paypalIntent.putExtras(b);
						activity.startActivityForResult(paypalIntent, Constant.IN_APP_PAYPAL_REQUEST_CODE);
					}
					
					@Override
					public void onPaypalPaymentRequestError(int errorCode) {
						// TODO Auto-generated method stub
						if(pDialog.isShowing()){
							pDialog.dismiss();
						}
						ErrorHandler.getInstance().setContext(activity).paypalErrorHandler(errorCode);
					}
				});
			}
		});
	}
	
	public void cardInApp(Activity activity, String accessToken, String noticeURL, String state, String target){
		Intent cardIntent = new Intent(activity, CardPaymentActivity.class);
		Bundle b = new Bundle();
		b.putInt(Constant.PAYMENT_TYPE, Constant.PAYMENT_TYPE_INAPP);
		b.putString(Constant.ACCESS_TOKEN_KEY, accessToken);
		b.putString(Constant.NOTICE_URL_KEY, noticeURL);
		b.putString(Constant.STATE_KEY, state);
		b.putString(Constant.TARGET_KEY, target);
		cardIntent.putExtras(b);
		//cardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivityForResult(cardIntent, Constant.IN_APP_CARD_REQUEST_CODE);
	}
	
	public void cardTopup(Activity activity, String accessToken, String noticeURL){
		Intent cardIntent = new Intent(activity, CardPaymentActivity.class);
		Bundle b = new Bundle();
		b.putInt(Constant.PAYMENT_TYPE, Constant.PAYMENT_TYPE_TOPUP);
		b.putString(Constant.ACCESS_TOKEN_KEY, accessToken);
		b.putString(Constant.NOTICE_URL_KEY, noticeURL);
		cardIntent.putExtras(b);
		//cardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivityForResult(cardIntent, Constant.TOPUP_CARD_REQUEST_CODE);
	}
	
	public void getItemList(String accessToken, int start, int level, String target, GetItemListHandler handler){
		appotaFactory.getItemList(accessToken, start, level, target, handler);
	}
	
	public void getBoughtItemList(String accessToken, int start, int level, String target, GetBoughtItemListHandler handler){
		appotaFactory.getBoughtItemList(accessToken, start, level, target, handler);
	}
	
	public void isItemBought(String accessToken, String itemId, CheckItemBoughtHandler handler){
		appotaFactory.isItemBought(accessToken, itemId, handler);
	}
	
	public void buyItem(String accessToken, String itemId, int quantity, int level, BuyItemHandler handler){
		appotaFactory.buyItem(accessToken, itemId, quantity, level, handler);
	}
	
	public void checkTopupTransaction(String accessToken, String topupId, boolean isShowProgressDialog, TransactionStatusHandler handler){
		CheckTopupTask task = new CheckTopupTask(context, accessToken, topupId, isShowProgressDialog);
		task.setTransactionStatusHandler(handler);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else {
        	task.execute();
        }
	}
	
	public void checkInAppTransaction(String accessToken, String inappID, boolean isShowProgressDialog, TransactionStatusHandler handler){
		CheckInAppTransactionTask task = new CheckInAppTransactionTask(context, accessToken, inappID, isShowProgressDialog);
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
