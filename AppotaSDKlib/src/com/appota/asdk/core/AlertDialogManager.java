package com.appota.asdk.core;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertDialogManager {
	
	private Context context;
	
	public AlertDialogManager(Context context){
		this.context = context;
	}
	
	public void showButtonsDialog(int messageId, DialogInterface.OnClickListener listener){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(messageId);
		builder.setPositiveButton(android.R.string.ok, listener);
		builder.setNegativeButton(android.R.string.cancel, listener);
		builder.show();
	}
	
	public void showPositiveDialog(int messageId, DialogInterface.OnClickListener listener){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(messageId);
		builder.setPositiveButton(android.R.string.ok, listener);
		builder.setCancelable(false);
		builder.show();
	}

	public void showDialog(int messageId, DialogInterface.OnClickListener listener){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(messageId);
		builder.setPositiveButton(android.R.string.ok, listener);
		builder.show();
	}
	
	public void showDialog(String message, DialogInterface.OnClickListener listener){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setPositiveButton(android.R.string.ok, listener);
		builder.show();
	}
	
	public void showDialogWithItems(int messageId, String items[], DialogInterface.OnClickListener listener){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(messageId);
		builder.setItems(items, listener);
		builder.show();
	}

}
