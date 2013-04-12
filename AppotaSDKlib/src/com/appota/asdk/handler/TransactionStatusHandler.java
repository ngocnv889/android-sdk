package com.appota.asdk.handler;

import com.appota.asdk.model.TransactionResult;

public interface TransactionStatusHandler {

	public void onTransactionSuccess(TransactionResult trans);
	public void onTransactionError(int errorCode);
}
