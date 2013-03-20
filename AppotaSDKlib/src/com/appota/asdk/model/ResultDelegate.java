package com.appota.asdk.model;

import java.io.Serializable;

import com.paypal.android.MEP.PayPalResultDelegate;

public class ResultDelegate implements PayPalResultDelegate, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1038547032211056365L;

	public void onPaymentCanceled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onPaymentFailed(String arg0, String arg1, String arg2,
			String arg3, String arg4) {
		// TODO Auto-generated method stub
		
	}

	public void onPaymentSucceeded(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

}
