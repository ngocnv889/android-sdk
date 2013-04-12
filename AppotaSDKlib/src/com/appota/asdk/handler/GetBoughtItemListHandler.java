package com.appota.asdk.handler;

import java.util.List;

import com.appota.asdk.model.BoughtItem;

public interface GetBoughtItemListHandler {

	public void onGetBoughtItemListSuccess(List<BoughtItem> listItem);
	public void onGetBoughtItemListError(int errorCode);
}
