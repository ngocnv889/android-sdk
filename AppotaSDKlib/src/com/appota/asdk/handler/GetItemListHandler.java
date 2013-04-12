package com.appota.asdk.handler;

import com.appota.asdk.model.AppotaItem;

import java.util.List;

public interface GetItemListHandler {

	public void onGetItemListSuccess(List<AppotaItem> listItem);
	public void onGetItemListError(int errorCode);
}
