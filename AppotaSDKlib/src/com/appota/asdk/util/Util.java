package com.appota.asdk.util;

import android.content.Context;
import android.telephony.TelephonyManager;

public class Util {

	public static String getDeviceId(Context context){
		return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	}
	
	public static String getCarrier(Context context) {
		TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String carrier = tMgr.getNetworkOperatorName();
		return carrier;
	}
}
