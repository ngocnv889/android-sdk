package com.appota.asdk;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogManager {
	
	public static ProgressDialog showProgressDialog(Context context, int message){
		ProgressDialog pDialog = new ProgressDialog(context);
		pDialog.setMessage(context.getResources().getString(message));
		pDialog.setCancelable(false);
		pDialog.show();
		return pDialog;
	}
}
