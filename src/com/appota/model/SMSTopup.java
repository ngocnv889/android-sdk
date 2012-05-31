package com.appota.model;

import java.io.Serializable;
import java.util.List;

public class SMSTopup implements Serializable{

	private String topupId;
	private List<SMSOption> smsOptions;
	
	public String getTopupId() {
		return topupId;
	}
	public void setTopupId(String topupId) {
		this.topupId = topupId;
	}
	public List<SMSOption> getSmsOptions() {
		return smsOptions;
	}
	public void setSmsOptions(List<SMSOption> smsOptions) {
		this.smsOptions = smsOptions;
	}
	
	
}
