package com.appota.asdk.core;

import com.appota.asdk.model.SMSPayment;


public interface SMSClickListener {

	public void onSMSClick(SMSPayment sms, int position);
}
