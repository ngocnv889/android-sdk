package com.appota.asdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.appota.asdk.core.AlertDialogManager;
import com.appota.asdk.exception.CardPaymentException;
import com.appota.asdk.exception.ErrorHandler;
import com.appota.asdk.handler.CardPaymentHandler;
import com.appota.asdk.task.InAppCardTask;
import com.appota.asdk.task.TopupCardTask;
import com.appota.asdk.util.Constant;

@SuppressLint("NewApi")
public class CardPaymentActivity extends Activity{
	
	private Spinner vendor;
	private EditText editCardSerial;
	private EditText editCardCode;
	private Bundle bundle;
	private int errorCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_card);
		vendor = (Spinner) findViewById(R.id.spn_card_type);
		editCardSerial = (EditText) findViewById(R.id.edit_card_serial);
		editCardCode = (EditText) findViewById(R.id.edit_card_code);
		bundle = getIntent().getExtras();
	}
	
	public void confirmPayment(View v){
		if(TextUtils.isEmpty(editCardCode.getText().toString())){
			Toast.makeText(this, R.string.card_code_blank, Toast.LENGTH_SHORT).show();
			return;
		}
		if(TextUtils.isEmpty(editCardSerial.getText().toString())){
			Toast.makeText(this, R.string.card_serial_blank, Toast.LENGTH_SHORT).show();
			return;
		}
		AlertDialogManager am = new AlertDialogManager(this);
		am.showButtonsDialog(R.string.confirm_payment, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String vendorStr = "";
				switch (vendor.getSelectedItemPosition()) {
				case 0:
					vendorStr = "vinaphone";
					break;
				case 1:
					vendorStr = "mobifone";
					break;
				case 2:
					vendorStr = "viettel";
					break;
				case 3:
					vendorStr = "fpt";
					break;
				case 4:
					vendorStr = "vtc";
					break;
				case 5:
					vendorStr = "mega";
					break;
				}
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					if(bundle.getInt(Constant.PAYMENT_TYPE) == Constant.PAYMENT_TYPE_INAPP){
						InAppCardTask task = new InAppCardTask(CardPaymentActivity.this, bundle.getString(Constant.ACCESS_TOKEN_KEY), editCardSerial.getText().toString(), editCardCode.getText().toString(), vendorStr, bundle.getString(Constant.NOTICE_URL_KEY), bundle.getString(Constant.STATE_KEY), bundle.getString(Constant.TARGET_KEY));
						task.setCardPaymenrHandler(new CardPaymentHandler() {
							
							@Override
							public void onCardPaymentRequestSuccess(String transactionID) {
								// TODO Auto-generated method stub
								setResult(RESULT_OK, getIntent().putExtra(Constant.TRANSACTION_ID_KEY, transactionID));
								finish();
							}
							
							@Override
							public void onCardPaymentRequestException(CardPaymentException ex) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onCardPaymentRequestError(int errorCode) {
								// TODO Auto-generated method stub
								CardPaymentActivity.this.errorCode = errorCode;
								ErrorHandler.getInstance().setContext(CardPaymentActivity.this).cardErrorHandler(errorCode);
								//setResult(RESULT_CANCELED, getIntent().putExtra(Constant.TRANSACTION_ERROR_KEY, errorCode));
								//finish();
							}
						});
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				        }
				        else {
				        	task.execute();
				        }
					} else if(bundle.getInt(Constant.PAYMENT_TYPE) == Constant.PAYMENT_TYPE_TOPUP){
						TopupCardTask task = new TopupCardTask(CardPaymentActivity.this, bundle.getString(Constant.ACCESS_TOKEN_KEY), editCardSerial.getText().toString(), editCardCode.getText().toString(), vendorStr, bundle.getString(Constant.NOTICE_URL_KEY));
						task.setCardPaymenrHandler(new CardPaymentHandler() {
							
							@Override
							public void onCardPaymentRequestSuccess(String transactionID) {
								// TODO Auto-generated method stub
								setResult(RESULT_OK, getIntent().putExtra(Constant.TRANSACTION_ID_KEY, transactionID));
								finish();
							}
							
							@Override
							public void onCardPaymentRequestException(CardPaymentException ex) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onCardPaymentRequestError(int errorCode) {
								// TODO Auto-generated method stub
								CardPaymentActivity.this.errorCode = errorCode;
								ErrorHandler.getInstance().setContext(CardPaymentActivity.this).cardErrorHandler(errorCode);
								//setResult(RESULT_CANCELED, getIntent().putExtra(Constant.TRANSACTION_ERROR_KEY, errorCode));
								//finish();
							}
						});
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				        }
				        else {
				        	task.execute();
				        }
					}
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					dialog.dismiss();
					break;
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_CANCELED, getIntent().putExtra(Constant.TRANSACTION_ERROR_KEY, errorCode));
		super.onBackPressed();
	}
}
