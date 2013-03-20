package com.appota.asdk.exception;

import android.app.Activity;
import android.view.Gravity;

import com.appota.asdk.R;
import com.appota.asdk.core.AppMsg;



public class ErrorHandler {
	
	private static ErrorHandler defaultInstance;
	private static transient Activity context;
	private static final int ALERT_DURATION = 5 * 1000;

	
	public static ErrorHandler getInstance() {
        if (defaultInstance == null) {
            synchronized (ErrorHandler.class) {
                if (defaultInstance == null) {
                    defaultInstance = new ErrorHandler();
                }
            }
        }
        return defaultInstance;
    }
	
	public ErrorHandler setContext(Activity context){
		ErrorHandler.context = context;
		return defaultInstance;
	}

	public void smsErrorHandler(int errorCode){
		AppMsg msg;
		switch (errorCode) {
		case -1:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega1), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case -2:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega2), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case -3:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega3), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case -4:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega4), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 1:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code1), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 3:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code3), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 10:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code10), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 91:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code91), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		default:
			break;
		}
	}
	
	public void cardErrorHandler(int errorCode){
		AppMsg msg;
		switch (errorCode) {
		case -1:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega1), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case -2:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega2), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case -3:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega3), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case -4:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega4), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 1:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code1), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 3:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code3), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 10:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code10), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 91:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code91), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		default:
			break;
		}
	}
	
	public void bankErrorHandler(int errorCode){
		AppMsg msg;
		switch (errorCode) {
		case -1:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega1), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case -2:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega2), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case -3:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega3), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case -4:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega4), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 1:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code1), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 11:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code11), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 10:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code10), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 91:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code91), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		default:
			break;
		}
	}
	
	public void paypalErrorHandler(int errorCode){
		AppMsg msg;
		switch (errorCode) {
		case -1:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega1), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case -2:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega2), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case -3:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega3), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case -4:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega4), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 1:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code1), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 11:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code11), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 91:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code91), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		default:
			break;
		}
	}
	
	public void transactionErrorHandler(int errorCode){
		AppMsg msg;
		switch (errorCode) {
		case -1:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega1), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case -2:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega2), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case -3:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega3), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case -4:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_nega4), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 1:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code1), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 7:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code7), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 8:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code8), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 9:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code9), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 19:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code19), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		case 91:
			msg = AppMsg.makeText(context, String.format(context.getResources().getString(R.string.error_code91), errorCode), AppMsg.STYLE_ALERT, null);
			msg.setLayoutGravity(Gravity.TOP);
			msg.setDuration(ALERT_DURATION);
			msg.show();
			break;
		default:
			break;
		}
	}
}
