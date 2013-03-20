package com.appota.asdk.core;

import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.appota.asdk.R;
import com.appota.asdk.model.SMSOption;

public class DialogManager {

private Context context;
	
	public DialogManager(Context context){
		this.context = context;
	}
	
	public void showDialogWithItems(int messageId, List<SMSOption> smsOptions, DialogInterface.OnClickListener listener){
		String[] items = new String[smsOptions.size()];
		for(int i=0; i<smsOptions.size();i++){
			items[i] = smsOptions.get(i).getAmount() + " " + smsOptions.get(i).getCurrency();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(messageId);
		builder.setItems(items, listener);
		builder.show();
	}
	
	public void showBankValue(int messageId, int arrayId, DialogInterface.OnClickListener listener){
		String[] values = context.getResources().getStringArray(arrayId);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(messageId);
		builder.setItems(values, listener);
		builder.show();
	}
	
	public ProgressDialog showProgressDialog(){
		ProgressDialog pDialog = new ProgressDialog(context);
		pDialog.setMessage(context.getResources().getString(R.string.loading));
		pDialog.setCancelable(false);
		pDialog.show();
		return pDialog;
	}
}
