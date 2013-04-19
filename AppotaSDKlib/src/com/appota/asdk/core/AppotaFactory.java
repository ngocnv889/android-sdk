package com.appota.asdk.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.appota.asdk.handler.BankPaymentHandler;
import com.appota.asdk.handler.BuyItemHandler;
import com.appota.asdk.handler.CardPaymentHandler;
import com.appota.asdk.handler.CheckItemBoughtHandler;
import com.appota.asdk.handler.GetAccessTokenHandler;
import com.appota.asdk.handler.GetBoughtItemListHandler;
import com.appota.asdk.handler.GetItemListHandler;
import com.appota.asdk.handler.GetRequestTokenHandler;
import com.appota.asdk.handler.PaypalPaymentHandler;
import com.appota.asdk.handler.SMSPaymentHandler;
import com.appota.asdk.handler.UserInfoHandler;
import com.appota.asdk.http.MyHttpClient;
import com.appota.asdk.model.AppotaAccessToken;
import com.appota.asdk.model.AppotaItem;
import com.appota.asdk.model.BankOption;
import com.appota.asdk.model.BankPayment;
import com.appota.asdk.model.BoughtItem;
import com.appota.asdk.model.PaypalForm;
import com.appota.asdk.model.PaypalPayment;
import com.appota.asdk.model.SMSOption;
import com.appota.asdk.model.SMSPayment;
import com.appota.asdk.model.TransactionResult;
import com.appota.asdk.model.User;
import com.appota.asdk.util.Constant;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class AppotaFactory {

	private static volatile AppotaFactory defaultInstance;
	private Context context;
	private final String TAG = AppotaFactory.class.getSimpleName();
	private boolean enableLogging = false;
	private AQuery aq;
	private String clientKey;
	private String clientSecret;
	
	public static AppotaFactory getInstance() {
        if (defaultInstance == null) {
            synchronized (AppotaFactory.class) {
                if (defaultInstance == null) {
                    defaultInstance = new AppotaFactory();
                }
            }
        }
        return defaultInstance;
    }
	
	public AppotaFactory init(Context context){
		this.context = context;
		aq = new AQuery(context);
		ApplicationInfo ai;
		try {
			ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			enableLogging = bundle.getBoolean("enable_logging");
			clientKey = bundle.getString("client_key");
			clientSecret = bundle.getString("client_secret");
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
			enableLogging = false;
		} catch (NullPointerException e) {
			Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
			enableLogging = false;
		}
		return defaultInstance;
	}
	
	public String getRequestToken(String redirectURI, List<String> scopes, String lang) {
		String endpoint = "";
		String scope = "";
		for (String s : scopes) {
			scope += s + ",";
		}
			String params;
			try {
				params = URLEncoder.encode(Constant.RESPONSE_TYPE_KEY, Constant.ENCODE)
						+ "="
						+ URLEncoder.encode(Constant.RESPONSE_TYPE, Constant.ENCODE)
						+ "&"
						+ URLEncoder.encode(Constant.CLIENT_ID_KEY, Constant.ENCODE)
						+ "="
						+ URLEncoder.encode(clientKey, Constant.ENCODE)
						+ "&"
						+ URLEncoder.encode(Constant.CALLBACK_URL_KEY, Constant.ENCODE)
						+ "="
						+ URLEncoder.encode(redirectURI, Constant.ENCODE)
						+ "&"
						+ URLEncoder.encode(Constant.SCOPE_KEY, Constant.ENCODE)
						+ "="
						+ URLEncoder.encode(scope.substring(0, scope.length() - 1),
								Constant.ENCODE) + "&"
						+ URLEncoder.encode(Constant.STATE_KEY, Constant.ENCODE) + "="
						+ URLEncoder.encode(Constant.STATE, Constant.ENCODE) + "&"
						+ URLEncoder.encode(Constant.LANG_KEY, Constant.ENCODE) + "="
						+ URLEncoder.encode(lang, Constant.ENCODE);
				endpoint = Constant.REQUEST_TOKEN_ENDPOINT + params;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return endpoint;
	}
	
	public void getNonUserRequestToken(List<String> scopes, String lang, final GetRequestTokenHandler handler) {
		String scope = "";
		for (String s : scopes) {
			scope += s + ",";
		}
			String params;
			try {
				params = URLEncoder.encode(Constant.RESPONSE_TYPE_KEY, Constant.ENCODE)
						+ "="
						+ URLEncoder.encode(Constant.RESPONSE_TYPE, Constant.ENCODE)
						+ "&"
						+ URLEncoder.encode(Constant.CLIENT_ID_KEY, Constant.ENCODE)
						+ "="
						+ URLEncoder.encode(clientKey, Constant.ENCODE)
						+ "&"
						+ URLEncoder.encode(Constant.CALLBACK_URL_KEY, Constant.ENCODE)
						+ "="
						+ URLEncoder.encode(Constant.DEFAULT_REDIRECT_URI, Constant.ENCODE)
						+ "&"
						+ URLEncoder.encode(Constant.SCOPE_KEY, Constant.ENCODE)
						+ "="
						+ URLEncoder.encode(scope.substring(0, scope.length() - 1),
								Constant.ENCODE) + "&"
						+ URLEncoder.encode(Constant.STATE_KEY, Constant.ENCODE) + "="
						+ URLEncoder.encode(Constant.STATE, Constant.ENCODE) + "&"
						+ URLEncoder.encode(Constant.LANG_KEY, Constant.ENCODE) + "="
						+ URLEncoder.encode(lang, Constant.ENCODE);
				String endpoint = Constant.NON_USER_REQUEST_TOKEN_ENDPOINT + params;
				aq.ajax(endpoint, JSONObject.class, new AjaxCallback<JSONObject>(){
					@Override
					public void callback(String url, JSONObject json, AjaxStatus ajaxStatus) {
						if(json != null){
							if(enableLogging){
								Log.d(TAG, ajaxStatus.getCode() + ":" + json.toString());
							}
							try {
								String requestToken = json.getString("request_token");
								handler.onGetRequestTokenSuccess(requestToken);
							} catch (JSONException e) {
								e.printStackTrace();
								handler.onGetRequestTokenError(-2);
							}
						} else {
							if(enableLogging){
								Log.d(TAG, "Error: " + ajaxStatus.getCode());
							}
							handler.onGetRequestTokenError(ajaxStatus.getCode());
						}
					}
				});
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
	}
	
	public void getAccessToken(String requestToken, String lang, final GetAccessTokenHandler handler){
		if(TextUtils.isEmpty(requestToken)){
			handler.onGetAccessTokenError(-3);
		} else {
			String endpoint = Constant.ACCESS_TOKEN_ENDPOINT;
			Map<String, String> params = new HashMap<String, String>();
			params.put(Constant.REQUEST_TOKEN_KEY, requestToken);
			params.put(Constant.CLIENT_ID_KEY, clientKey);
			params.put(Constant.CLIENT_SECRET_KEY, clientSecret);
			params.put(Constant.CALLBACK_URL_KEY, Constant.DEFAULT_REDIRECT_URI);
			params.put(Constant.GRANT_TYPE_KEY, Constant.GRANT_TYPE);
			params.put(Constant.LANG_KEY, lang);
			aq.ajax(endpoint, params, JSONObject.class, new AjaxCallback<JSONObject>(){
				@Override
				public void callback(String url, JSONObject json, AjaxStatus ajaxStatus) {
					if(json != null){
						try {
	                		boolean status = json.getBoolean("status");
	                		if(status){
	                			Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	                			AppotaAccessToken accessToken = gson.fromJson(json.toString(), AppotaAccessToken.class);
	                			handler.onGetAccessTokenSuccess(accessToken);
	                		} else {
	                			handler.onGetAccessTokenError(json.getInt("error_code"));
	                		}
						} catch (JSONException e) {
							e.printStackTrace();
							handler.onGetAccessTokenError(-2);
						}
						if(enableLogging){
							Log.d(TAG, ajaxStatus.getCode() + ":" + json.toString());
						}
					} else {
						if(enableLogging){
							Log.d(TAG, "Error: " + ajaxStatus.getCode());
						}
						handler.onGetAccessTokenError(ajaxStatus.getCode());
					}
				}
			});
		}
	}
	
	public void getNonUserAccessToken(String requestToken, String lang, final GetAccessTokenHandler handler){
		if(TextUtils.isEmpty(requestToken)){
			handler.onGetAccessTokenError(-3);
		} else {
			String endpoint = Constant.NON_USER_ACCESS_TOKEN_ENDPOINT;
			Map<String, String> params = new HashMap<String, String>();
			params.put(Constant.REQUEST_TOKEN_KEY, requestToken);
			params.put(Constant.CLIENT_ID_KEY, clientKey);
			params.put(Constant.CLIENT_SECRET_KEY, clientSecret);
			params.put(Constant.CALLBACK_URL_KEY, Constant.DEFAULT_REDIRECT_URI);
			params.put(Constant.GRANT_TYPE_KEY, Constant.GRANT_TYPE);
			params.put(Constant.LANG_KEY, lang);
			aq.ajax(endpoint, params, JSONObject.class, new AjaxCallback<JSONObject>(){
				@Override
				public void callback(String url, JSONObject json, AjaxStatus ajaxStatus) {
					if(json != null){
						try {
	                		boolean status = json.getBoolean("status");
	                		if(status){
	                			Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	                			AppotaAccessToken accessToken = gson.fromJson(json.toString(), AppotaAccessToken.class);
	                			handler.onGetAccessTokenSuccess(accessToken);
	                		} else {
	                			handler.onGetAccessTokenError(json.getInt("error_code"));
	                		}
						} catch (JSONException e) {
							e.printStackTrace();
							handler.onGetAccessTokenError(-2);
						}
						if(enableLogging){
							Log.d(TAG, ajaxStatus.getCode() + ":" + json.toString());
						}
					} else {
						if(enableLogging){
							Log.d(TAG, "Error: " + ajaxStatus.getCode());
						}
						handler.onGetAccessTokenError(ajaxStatus.getCode());
					}
				}
			});
		}
	}
	
	public void getUserInfo(String accessToken, final UserInfoHandler handler){
		String endpoint = Constant.USER_INFO_ENDPOINT + accessToken;
		aq.ajax(endpoint, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus ajaxStatus) {
                if(json != null){
                	try {
                		boolean status = json.getBoolean("status");
                		if(status){
                			JSONObject obj = json.getJSONObject("data");
                			User user = new User();
                			user.setAvatarUr(obj.getString("avatar"));
                			user.setBirthday(obj.getString("birthday"));
                			user.setFullName(obj.getString("fullname"));
                			user.setUserName(obj.getString("username"));
                			user.setGender(obj.getString("gender"));
                			user.setGreenTym(obj.getInt("tym"));
                			handler.onGetUserInfoSuccess(user);
                		} else {
                			handler.onGetUserInfoError(json.getInt("error_code"));
                		}
					} catch (JSONException e) {
						e.printStackTrace();
						handler.onGetUserInfoError(-2);
					}
                	if(enableLogging){
                		Log.d(TAG, ajaxStatus.getCode() + ":" + json.toString());
                	}
                } else {
                	if(enableLogging){
                		Log.e(TAG, "Error: " + ajaxStatus.getCode());
                	}
                	handler.onGetUserInfoError(ajaxStatus.getCode());
                }
            }
		});
	}
	
	public void refreshToken(String token, String clientKey, String clientSecret, final GetAccessTokenHandler handler){
		String endpoint = Constant.REFRESH_TOKEN_ENDPOINT;
		Map<String, String> params = new HashMap<String, String>();
		params.put(Constant.REFRESH_TOKEN_KEY, token);
		params.put(Constant.CLIENT_ID_KEY, clientKey);
		params.put(Constant.CLIENT_SECRET_KEY, clientSecret);
		params.put(Constant.GRANT_TYPE_KEY, "refresh_token");
		aq.ajax(endpoint, params, JSONObject.class, new AjaxCallback<JSONObject>(){
			@Override
			public void callback(String url, JSONObject json, AjaxStatus ajaxStatus) {
				if(json != null){
					try {
                		boolean status = json.getBoolean("status");
                		if(status){
                			Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                			AppotaAccessToken accessToken = gson.fromJson(json.toString(), AppotaAccessToken.class);
                			handler.onGetAccessTokenSuccess(accessToken);
                		} else {
                			handler.onGetAccessTokenError(json.getInt("error_code"));
                		}
					} catch (JSONException e) {
						e.printStackTrace();
						handler.onGetAccessTokenError(-2);
					}
					if(enableLogging){
						Log.e(TAG, ajaxStatus.getCode() + " : " + json.toString());
					}
				} else {
					if(enableLogging){
						Log.e(TAG, "Error: " + ajaxStatus.getCode());
					}
					handler.onGetAccessTokenError(ajaxStatus.getCode());
				}
			}
		});
	}
	
	public void smsTopup(String accessToken, boolean shortSyntax, final SMSPaymentHandler handler){
		String endpoint = Constant.SMS_TOPUP_ENDPOINT + accessToken;
		Map<String, String> params = new HashMap<String, String>();
		if(shortSyntax){
			params.put("short", "1");
		} else {
			params.put("short", "0");
		}
		aq.ajax(endpoint, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus ajaxStatus) {
                if(json != null){
                	try {
                		boolean status = json.getBoolean("status");
                		if(status){
                			SMSPayment data = new SMSPayment();
                			List<SMSOption> smsOptions = new ArrayList<SMSOption>();
                			JSONObject dataObj = json.getJSONObject("data");
        					String topupId = dataObj.getString("topup_id");
        					JSONArray smsArray = dataObj.getJSONArray("options");
        					for(int j=0;j<smsArray.length();j++){
        						JSONObject smsObj = smsArray.getJSONObject(j);
        						String syntax = smsObj.getString("sms");
        						String sendNumber = smsObj.getString("send");
        						int tym = smsObj.getInt("tym");
        						double amount = smsObj.getDouble("amount");
        						String currency = smsObj.getString("currency");
        						SMSOption option = new SMSOption();
        						option.setAmount(amount);
        						option.setCurrency(currency);
        						option.setSendNumber(sendNumber);
        						option.setSyntax(syntax);
        						option.setTym(tym);
        						option.setTransactionId(topupId);
        						smsOptions.add(option);
        					}
        					data.setTransactionID(topupId);
        					data.setSmsOptions(smsOptions);
        					data.setErrorCode(0);
        					handler.onSMSPaymentRequestSuccess(data);
                		} else {
                			handler.onSMSPaymentRequestError(json.getInt("error_code"));
                		}
					} catch (JSONException e) {
						e.printStackTrace();
						handler.onSMSPaymentRequestError(-2);
					}
                	if(enableLogging){
                		Log.d(TAG, ajaxStatus.getCode() + ":" + json.toString());
                	}
                } else {
                	if(enableLogging){
                		Log.e(TAG, "Error: " + ajaxStatus.getCode());
                	}
                	handler.onSMSPaymentRequestError(ajaxStatus.getCode());
                }
            }
		});
	}
	
	public void cardTopup(String accessToken, String cardSerial, String cardCode, String vendor, String noticeURL, final CardPaymentHandler handler){
		String endpoint = Constant.CARD_TOPUP_ENDPOINT + accessToken;
		System.err.println(endpoint + " " + cardSerial + " " + cardCode + " " + vendor);
		Map<String, String> params = new HashMap<String, String>();
		params.put("vendor", vendor);
		params.put("card_serial", cardSerial);
		params.put("card_code", cardCode);
		if(!TextUtils.isEmpty(noticeURL)){
			params.put("notice_url", noticeURL);
		}
		aq.ajax(endpoint, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus ajaxStatus) {
                if(json != null){
                	if(json.has("data")){
                		String topupId;
    					JSONObject obj;
						try {
							obj = json.getJSONObject("data");
							for(int j=0; j< obj.length(); j++){
								topupId = obj.getString("topup_id");
		    					handler.onCardPaymentRequestSuccess(topupId);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							handler.onCardPaymentRequestError(-2);
						}
    				} else {
    					handler.onCardPaymentRequestError(-2);
    				}
                	if(enableLogging){
                		Log.d(TAG, ajaxStatus.getCode() + ":" + json.toString());
                	}
                } else {
                	if(enableLogging){
                		Log.e(TAG, "Error: " + ajaxStatus.getCode());
                	}
                	handler.onCardPaymentRequestError(ajaxStatus.getCode());
                }
            }
		});
	}
	
	public void bankTopup(String accessToken, String amount, String noticeURL, final BankPaymentHandler handler){
		String endpoint = Constant.BANK_TOPUP_ENDPOINT + accessToken;
		Map<String, String> params = new HashMap<String, String>();
		params.put("amount", amount);
		if(!TextUtils.isEmpty(noticeURL)){
			params.put("notice_url", noticeURL);
		}
		aq.ajax(endpoint, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus ajaxStatus) {
                if(json != null){
                	boolean status;
					try {
						status = json.getBoolean("status");
						if(status){
	                		BankPayment data = new BankPayment();
	                		BankOption option = new BankOption();
	                		JSONObject obj = json.getJSONObject("data");
	            			for(int j=0; j< obj.length(); j++){
	            				String topupId = obj.getString("topup_id");
	            				JSONArray smsArray = obj.getJSONArray("options");
	            				for(int k=0;k<smsArray.length();k++){
	            					JSONObject smsObj = smsArray.getJSONObject(k);
	            					
	            					String bankUrl = smsObj.getString("url");
	            					String bankName = smsObj.getString("bank");
	            					int tym = smsObj.getInt("tym");
	            					double payAmount = smsObj.getDouble("amount");
	            					String currency = smsObj.getString("currency");
	            					
	            					option = new BankOption();
	            					option.setAmount(payAmount);
	            					option.setCurrency(currency);
	            					option.setUrl(bankUrl);
	            					option.setBank(bankName);
	            					option.setTym(tym);
	            					
	            					data.setTopupId(topupId);
	            					data.setOption(option);
	            					data.setErrorCode(0);
	            				}
	            			}
	            			handler.onBankPaymentRequestSuccess(data);
	                	} else {
	                		handler.onBankPaymentRequestError(json.getInt("error_code"));
	                	}
					} catch (JSONException e) {
						e.printStackTrace();
						handler.onBankPaymentRequestError(-2);
					}
					if(enableLogging){
						Log.e(TAG, ajaxStatus.getCode() + " : " + json.toString());
					}
                } else {
                	if(enableLogging){
                		Log.e(TAG, "Error: " + ajaxStatus.getCode());
                	}
                	handler.onBankPaymentRequestError(ajaxStatus.getCode());
                }
            }
		});
	}
	
	public void paypalTopup(String accessToken, final String amount, String noticeURL, final PaypalPaymentHandler handler){
		String endpoint = Constant.PAYPAL_TOPUP_ENDPOINT + accessToken;
		Map<String, String> params = new HashMap<String, String>();
		params.put("amount", amount);
		if(!TextUtils.isEmpty(noticeURL)){
			params.put("notice_url", noticeURL);
		}
		aq.ajax(endpoint, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus ajaxStatus) {
                if(json != null){
                	boolean status;
					try {
						status = json.getBoolean("status");
						if(status){
							PaypalPayment data = new PaypalPayment();
							PaypalForm form = new PaypalForm();
							JSONObject obj = json.getJSONObject("data");
		        			for(int j=0; j< obj.length(); j++){
		        				String topupId = obj.getString("topup_id");
		        				JSONArray paypalArray = obj.getJSONArray("options");
		        				for(int k=0;k<paypalArray.length();k++){
		        					JSONObject paypalObj = paypalArray.getJSONObject(k);
		        					for(int n=0;n<paypalObj.length();n++){
		        						JSONObject paypalForm = paypalObj.getJSONObject("form");
		        						String cmd = paypalForm.getString("cmd");
		        						int noShipping = paypalForm.getInt("no_shipping");
		        						int noNote = paypalForm.getInt("no_note");
		        						String currencyCode = paypalForm.getString("currency_code");
		        						String bn = paypalForm.getString("bn");
		        						String itemName = paypalForm.getString("item_name");
		        						String notifyUrl = paypalForm.getString("notify_url");
		        						String business = paypalForm.getString("business");
		        						
		        						form.setAmount(Double.valueOf(amount));
		        						form.setBn(bn);
		        						form.setBusiness(business);
		        						form.setCmd(cmd);
		        						form.setCurrencyCode(currencyCode);
		        						form.setItemName(itemName);
		        						form.setNoNote(noNote);
		        						form.setNoShopping(noShipping);
		        						form.setNotifyUrl(notifyUrl);
		        					}
		        					int tym = paypalObj.getInt("tym");
		        					String currency = paypalObj.getString("currency");
		        					
		        					data.setAmount(Double.valueOf(amount));
		        					data.setCurrency(currency);
		        					data.setForm(form);
		        					data.setTopupId(topupId);
		        					data.setTym(tym);
		        					data.setErrorCode(0);
		        				}
		        			}
	            			handler.onPaypalPaymentRequestSuccess(data);
	                	} else {
	                		handler.onPaypalPaymentRequestError(json.getInt("error_code"));
	                	}
					} catch (JSONException e) {
						e.printStackTrace();
						handler.onPaypalPaymentRequestError(-2);
					}
					if(enableLogging){
						Log.e(TAG, ajaxStatus.getCode() + " : " + json.toString());
					}
                } else {
                	if(enableLogging){
                		Log.e(TAG, "Error: " + ajaxStatus.getCode());
                	}
                	handler.onPaypalPaymentRequestError(ajaxStatus.getCode());
                }
            }
		});
		
	}
	
	public TransactionResult checkTopup(String accessToken, String topupId){
		String endpoint = Constant.CHECK_TOPUP_ENDPOINT + accessToken;
		Map<String, String> params = new HashMap<String, String>();
		params.put("topup_id", topupId);
		long backoff = Constant.BACKOFF_MILLI_SECONDS + new Random().nextInt(1000);
		String value = "";
		TransactionResult result = new TransactionResult();
		result.setErrorCode(-3);
		result.setStatus(false);
		for (int i = 1; i <= 10; i++) {
            try {
            	value = post(endpoint, params);
            	if(enableLogging){
					Log.d(TAG, "CheckTopupTransaction. TopupID = [" + topupId + "] ------> " + value);
				}
            	JSONObject jsonObj = new JSONObject(value);
            	boolean isSuccess = jsonObj.getBoolean("status");
            	int errorCode = jsonObj.getInt("error_code");
            	if(errorCode == 0){
    				JSONObject dataObj = jsonObj.getJSONObject("data");
    				String amount = dataObj.getString("tym");
    				String msg = dataObj.getString("message");
    				String time = dataObj.getString("time");
    				String id = dataObj.getString("topup_id");
    				result.setStatus(isSuccess);
    				result.setMessage(msg);
    				result.setAmount(amount);
    				result.setErrorCode(errorCode);
    				result.setId(id);
    				result.setTime(time);
    				return result;
    			} else if(errorCode == 9){
    				Thread.sleep(backoff);
    				backoff *= 2;
    			} else {
    				result.setErrorCode(errorCode);
    				return result;
    			}
            } catch (IOException e) {
            	if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, "Failed to connect on attempt " + i + ":" + e);
				}
                if (i == Constant.MAX_ATTEMPTS) {
                	if(enableLogging){
                		Log.e(TAG, e.getMessage() + " error code: [-4]");
                	}
                	result.setErrorCode(-4);
                	return result;
                }
                try {
                     Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                     Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                	if(enableLogging){
                		Log.d(TAG, "Thread interrupted: abort remaining retries!");
                		Log.e(TAG, e1.getMessage() + " error code: [-1]");
                	}
                     Thread.currentThread().interrupt();
                     result.setErrorCode(-1);
	                return result;
                 }
                 backoff *= 2;
			} catch (JSONException e) {
				if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, e.getMessage() + " error code: [-2]");
				}
				result.setErrorCode(-2);
				return result;
			} catch (InterruptedException e) {
				if(enableLogging){
            		Log.d(TAG, "Thread interrupted: abort remaining retries!");
            		Log.e(TAG, e.getMessage() + " error code: [-1]");
            		e.printStackTrace();
            	}
                Thread.currentThread().interrupt();
                result.setErrorCode(-1);
                return result;
			}
		}
		return result;
	}
	
	
	public void smsInApp(String accessToken, String noticeURL, String state, String target, boolean shortSyntax, final SMSPaymentHandler handler){
		String endpoint = Constant.SMS_INAPP_ENDPOINT + accessToken;
		Map<String, String> params = new HashMap<String, String>();
		if(noticeURL != null){
			params.put("notice_url", noticeURL);
		}
		if(state != null){
			params.put("state", state);
		}
		if(target != null){
			params.put("target", target);
		}
		if(shortSyntax){
			params.put("short", "1");
		} else {
			params.put("short", "0");
		}
		aq.ajax(endpoint, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus ajaxStatus) {
                if(json != null){
                	try {
                		boolean status = json.getBoolean("status");
                		if(status){
                			SMSPayment data = new SMSPayment();
                			List<SMSOption> smsOptions = new ArrayList<SMSOption>();
                			JSONObject dataObj = json.getJSONObject("data");
        					String topupId = dataObj.getString("inapp_id");
        					JSONArray smsArray = dataObj.getJSONArray("options");
        					for(int j=0;j<smsArray.length();j++){
        						JSONObject smsObj = smsArray.getJSONObject(j);
        						String syntax = smsObj.getString("sms");
        						String sendNumber = smsObj.getString("send");
        						double amount = smsObj.getDouble("amount");
        						String currency = smsObj.getString("currency");
        						SMSOption option = new SMSOption();
        						option.setAmount(amount);
        						option.setCurrency(currency);
        						option.setSendNumber(sendNumber);
        						option.setSyntax(syntax);
        						smsOptions.add(option);
        					}
        					data.setTransactionID(topupId);
        					data.setSmsOptions(smsOptions);
        					data.setErrorCode(0);
        					handler.onSMSPaymentRequestSuccess(data);
                		} else {
                			handler.onSMSPaymentRequestError(json.getInt("error_code"));
                		}
					} catch (JSONException e) {
						e.printStackTrace();
						handler.onSMSPaymentRequestError(-2);
					}
                	if(enableLogging){
                		Log.d(TAG, ajaxStatus.getCode() + ":" + json.toString());
                	}
                } else {
                	if(enableLogging){
                		Log.e(TAG, "Error: " + ajaxStatus.getCode());
                	}
                	handler.onSMSPaymentRequestError(ajaxStatus.getCode());
                }
            }
		});
	}
	
	public void cardInApp(String accessToken, String cardCode, String cardSerial, String vendor, String noticeURL, String state, String target, final CardPaymentHandler handler){
		String endpoint = Constant.CARD_INAPP_ENDPOINT + accessToken;
		Map<String, String> params = new HashMap<String, String>();
		params.put("vendor", vendor);
		params.put("card_serial", cardSerial);
		params.put("card_code", cardCode);
		if(!TextUtils.isEmpty(noticeURL)){
			params.put("notice_url", noticeURL);
		}
		if(!TextUtils.isEmpty(state)){
			params.put("state", state);
		}
		if(!TextUtils.isEmpty(target)){
			params.put("target", target);
		}
		aq.ajax(endpoint, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus ajaxStatus) {
                if(json != null){
                	if(enableLogging){
                		Log.d(TAG, ajaxStatus.getCode() + ":" + json.toString());
                	}
                	if(json.has("data")){
                		String topupId;
    					JSONObject obj;
						try {
							obj = json.getJSONObject("data");
							for(int j=0; j< obj.length(); j++){
								topupId = obj.getString("inapp_id");
		    					handler.onCardPaymentRequestSuccess(topupId);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							handler.onCardPaymentRequestError(-2);
						}
    				} else {
    					handler.onCardPaymentRequestError(-2);
    				}
                } else {
                	if(enableLogging){
                		Log.e(TAG, "Error: " + ajaxStatus.getCode());
                	}
                	handler.onCardPaymentRequestError(ajaxStatus.getCode());
                }
            }
		});
	}
	
	public void bankInApp(String accessToken, String amount, String noticeURL, String state, String target, final BankPaymentHandler handler){
		String endpoint = Constant.BANK_INAPP_ENDPOINT + accessToken;
		Map<String, String> params = new HashMap<String, String>();
		params.put("amount", amount);
		if(!TextUtils.isEmpty(noticeURL)){
			params.put("notice_url", noticeURL);
		}
		if(!TextUtils.isEmpty(state)){
			params.put("state", state);
		}
		if(!TextUtils.isEmpty(target)){
			params.put("target", target);
		}
		aq.ajax(endpoint, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus ajaxStatus) {
                if(json != null){
                	boolean status;
					try {
						status = json.getBoolean("status");
						if(status){
	                		BankPayment data = new BankPayment();
	                		BankOption option = new BankOption();
	                		JSONObject obj = json.getJSONObject("data");
	            			for(int j=0; j< obj.length(); j++){
	            				String topupId = obj.getString("inapp_id");
	            				JSONArray smsArray = obj.getJSONArray("options");
	            				for(int k=0;k<smsArray.length();k++){
	            					JSONObject smsObj = smsArray.getJSONObject(k);
	            					
	            					String bankUrl = smsObj.getString("url");
	            					String bankName = smsObj.getString("bank");
	            					double payAmount = smsObj.getDouble("amount");
	            					String currency = smsObj.getString("currency");
	            					
	            					option = new BankOption();
	            					option.setAmount(payAmount);
	            					option.setCurrency(currency);
	            					option.setUrl(bankUrl);
	            					option.setBank(bankName);
	            					
	            					data.setTopupId(topupId);
	            					data.setOption(option);
	            					data.setErrorCode(0);
	            				}
	            			}
	            			handler.onBankPaymentRequestSuccess(data);
	                	} else {
	                		handler.onBankPaymentRequestError(json.getInt("error_code"));
	                	}
					} catch (JSONException e) {
						e.printStackTrace();
						handler.onBankPaymentRequestError(-2);
					}
					if(enableLogging){
						Log.e(TAG, ajaxStatus.getCode() + ":" + json.toString());
					}
                } else {
                	if(enableLogging){
                		Log.e(TAG, "Error: " + ajaxStatus.getCode());
                	}
                	handler.onBankPaymentRequestError(ajaxStatus.getCode());
                }
            }
		});
	}
	
	public void paypalInApp(String accessToken, final String amount, String noticeURL, String state, String target, final PaypalPaymentHandler handler){
		String endpoint = Constant.PAYPAL_INAPP_ENDPOINT + accessToken;
		Map<String, String> params = new HashMap<String, String>();
		params.put("amount", amount);
		if(!TextUtils.isEmpty(noticeURL)){
			params.put("notice_url", noticeURL);
		}
		if(!TextUtils.isEmpty(state)){
			params.put("state", state);
		}
		if(!TextUtils.isEmpty(target)){
			params.put("target", target);
		}
		aq.ajax(endpoint, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus ajaxStatus) {
                if(json != null){
                	boolean status;
					try {
						status = json.getBoolean("status");
						if(status){
							PaypalPayment data = new PaypalPayment();
							PaypalForm form = new PaypalForm();
							JSONObject obj = json.getJSONObject("data");
		        			for(int j=0; j< obj.length(); j++){
		        				String topupId = obj.getString("inapp_id");
		        				JSONArray paypalArray = obj.getJSONArray("options");
		        				for(int k=0;k<paypalArray.length();k++){
		        					JSONObject paypalObj = paypalArray.getJSONObject(k);
		        					for(int n=0;n<paypalObj.length();n++){
		        						JSONObject paypalForm = paypalObj.getJSONObject("form");
		        						String cmd = paypalForm.getString("cmd");
		        						int noShipping = paypalForm.getInt("no_shipping");
		        						int noNote = paypalForm.getInt("no_note");
		        						String currencyCode = paypalForm.getString("currency_code");
		        						String bn = paypalForm.getString("bn");
		        						String itemName = paypalForm.getString("item_name");
		        						String notifyUrl = paypalForm.getString("notify_url");
		        						String business = paypalForm.getString("business");
		        						
		        						form.setAmount(Double.valueOf(amount));
		        						form.setBn(bn);
		        						form.setBusiness(business);
		        						form.setCmd(cmd);
		        						form.setCurrencyCode(currencyCode);
		        						form.setItemName(itemName);
		        						form.setNoNote(noNote);
		        						form.setNoShopping(noShipping);
		        						form.setNotifyUrl(notifyUrl);
		        					}
		        					String currency = paypalObj.getString("currency");
		        					
		        					data.setAmount(Double.valueOf(amount));
		        					data.setCurrency(currency);
		        					data.setForm(form);
		        					data.setTopupId(topupId);
		        					data.setErrorCode(0);
		        				}
		        			}
	            			handler.onPaypalPaymentRequestSuccess(data);
	                	} else {
	                		handler.onPaypalPaymentRequestError(json.getInt("error_code"));
	                	}
					} catch (JSONException e) {
						e.printStackTrace();
						handler.onPaypalPaymentRequestError(-2);
					}
					if(enableLogging){
						Log.e(TAG, ajaxStatus.getCode() + ":" + json.toString());
					}
                } else {
                	if(enableLogging){
                		Log.e(TAG, "Error: " + ajaxStatus.getCode());
                	}
                	handler.onPaypalPaymentRequestError(ajaxStatus.getCode());
                }
            }
		});
	}
	
	public TransactionResult checkInApp(String accessToken, String inAppID){
		String endpoint = Constant.CHECK_INAPP_TRANSACTION_ENDPOINT + accessToken;
		Map<String, String> params = new HashMap<String, String>();
		params.put("inapp_id", inAppID);
		long backoff = Constant.BACKOFF_MILLI_SECONDS + new Random().nextInt(1000);
		String value = "";
		TransactionResult result = new TransactionResult();
		result.setErrorCode(9);
		for (int i = 1; i <= Constant.MAX_ATTEMPTS; i++) {
            try {
            	value = post(endpoint, params);
            	if(enableLogging){
					Log.d(TAG, "CheckInAppTransaction. ID = [" + inAppID + "] ------> " + value);
				}
            	JSONObject jsonObj = new JSONObject(value);
            	boolean isSuccess = jsonObj.getBoolean("status");
            	int errorCode = jsonObj.getInt("error_code");
    			if(errorCode == 0){
    				JSONObject dataObj = jsonObj.getJSONObject("data");
    				String amount = dataObj.getString("amount");
    				String msg = dataObj.getString("message");
    				String type = dataObj.getString("type");
    				String time = dataObj.getString("time");
    				String id = dataObj.getString("inapp_id");
    				String state = dataObj.getString("state");
    				String target = dataObj.getString("target");
    				String currency = dataObj.getString("currency");
    				result.setStatus(isSuccess);
    				result.setMessage(msg);
    				result.setAmount(amount);
    				result.setErrorCode(errorCode);
    				result.setCurrency(currency);
    				result.setId(id);
    				result.setState(state);
    				result.setTarget(target);
    				result.setType(type);
    				result.setTime(time);
    				return result;
    			} else if(errorCode == 9){
    				Thread.sleep(backoff);
    				backoff *= 2;
    			} else {
    				result.setErrorCode(errorCode);
    				return result;
    			}
            } catch (IOException e) {
            	if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, "Failed to connect on attempt " + i + ":" + e);
				}
                if (i == Constant.MAX_ATTEMPTS) {
                	if(enableLogging){
                		Log.e(TAG, e.getMessage() + " error code: [-4]");
                	}
                	result.setErrorCode(-4);
                	return result;
                }
                try {
                     Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                     Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                	if(enableLogging){
                		Log.d(TAG, "Thread interrupted: abort remaining retries!");
                		Log.e(TAG, e1.getMessage() + " error code: [-1]");
                	}
                     Thread.currentThread().interrupt();
                     result.setErrorCode(-1);
	                return result;
                 }
                 backoff *= 2;
			} catch (JSONException e) {
				if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, e.getMessage() + " error code: [-2]");
				}
				result.setErrorCode(-2);
				return result;
			} catch (InterruptedException e) {
				if(enableLogging){
            		Log.d(TAG, "Thread interrupted: abort remaining retries!");
            		Log.e(TAG, e.getMessage() + " error code: [-1]");
            		e.printStackTrace();
            	}
                Thread.currentThread().interrupt();
                result.setErrorCode(-1);
                return result;
			}
		}
		return result;
	}
	
	public void callRedirectURI(String redirectURI){
		Map<String, String> params = new HashMap<String, String>();
		try {
			post(redirectURI, params);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getItemList(String accessToken, int start, int level, String target, final GetItemListHandler handler){
		String url = Constant.INAPP_ITEM_LIST_ENDPOINT + accessToken;
		Map<String, String> params = new HashMap<String, String>();
		params.put("start", String.valueOf(start));
		params.put("level", String.valueOf(level));
		if(target != null){
			params.put("target", target);
		}
		aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus ajaxStatus) {
                if(json != null){
                	try {
                		boolean status = json.getBoolean("status");
                		if(status){
                			JSONArray array = json.getJSONArray("data");
    						if(array.length() > 0){
    							JSONObject obj;
    							Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    							AppotaItem item;
    							List<AppotaItem> itemList = new ArrayList<AppotaItem>(); 
    							for(int i=0; i<array.length(); i++){
    								obj = array.getJSONObject(i);
    								item = gson.fromJson(obj.toString(), AppotaItem.class);
    								itemList.add(item);
    							}
    							handler.onGetItemListSuccess(itemList);
    						}
                		} else {
                			handler.onGetItemListError(json.getInt("error_code"));
                		}
					} catch (JSONException e) {
						e.printStackTrace();
						handler.onGetItemListError(-2);
					}
                	if(enableLogging){
                		Log.d(TAG, ajaxStatus.getCode() + ":" + json.toString());
                	}
                } else {
                	if(enableLogging){
                		Log.e(TAG, "Error2: " + ajaxStatus.getCode());
                	}
                	handler.onGetItemListError(ajaxStatus.getCode());
                }
            }
		});
	}
	
	public void getBoughtItemList(String accessToken, int start, int level, String target, final GetBoughtItemListHandler handler){
		String url = Constant.USER_BOUGHT_LIST_ENDPOINT + accessToken;
		Map<String, String> params = new HashMap<String, String>();
		params.put("start", String.valueOf(start));
		params.put("level", String.valueOf(level));
		if(target != null){
			params.put("target", target);
		}
		aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus ajaxStatus) {
                if(json != null){
                	try {
                		boolean status = json.getBoolean("status");
                		if(status){
                			List<BoughtItem> itemList = new ArrayList<BoughtItem>();
                			BoughtItem item;
                			int totalBoughtItem = json.getInt("total");
                			JSONObject listObj = json.getJSONObject("data");
                			JSONArray namesArray = listObj.names();
                			if(namesArray.length() <= 0){
                				handler.onGetBoughtItemListError(-5);
                				return;
                			}
                			JSONArray array = listObj.toJSONArray(namesArray);
                			JSONObject obj;
                			for(int i=0;i<array.length();i++) {
                				int itemId = namesArray.getInt(i);
                				obj = array.getJSONObject(i);
                				int limitNumber = obj.getInt("limit_number");
                				String icon = obj.getString("icon");
                				int level = obj.getInt("level");
                				int price = obj.getInt("price");
                				int ownedQuantity = obj.getInt("total_item");
                				String description = obj.getString("description");
                				String name = obj.getString("name");
                				String target = obj.getString("target");
                				
                				item = new BoughtItem();
                				item.setDescription(description);
                				item.setIcon(icon);
                				item.setId(itemId);
                				item.setLevel(level);
                				item.setLimitNumber(limitNumber);
                				item.setName(name);
                				item.setOwnedQuantity(ownedQuantity);
                				item.setPrice(price);
                				item.setTarget(target);
                				item.setTotalBoughtItem(totalBoughtItem);
                				itemList.add(item);
                            }
                			handler.onGetBoughtItemListSuccess(itemList);
                		} else {
                			handler.onGetBoughtItemListError(json.getInt("error_code"));
                		}
					} catch (JSONException e) {
						e.printStackTrace();
						handler.onGetBoughtItemListError(-2);
					}
                	if(enableLogging){
                		Log.d(TAG, ajaxStatus.getCode() + ":" + json.toString());
                	}
                } else {
                	if(enableLogging){
                		Log.e(TAG, "Error2: " + ajaxStatus.getCode());
                	}
                	handler.onGetBoughtItemListError(ajaxStatus.getCode());
                }
            }
		});
	}
	
	public void isItemBought(String accessToken, String itemId, final CheckItemBoughtHandler handler){
		String url = Constant.CHECK_BOUGHT_ITEM_ENDPOINT + accessToken;
		Map<String, String> params = new HashMap<String, String>();
		params.put("item_id", itemId);
		aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus ajaxStatus) {
                if(json != null){
                	boolean status;
					try {
						status = json.getBoolean("status");
						if(status){
							JSONObject obj = json.getJSONObject("data");
							boolean isBought = obj.getBoolean("is_bought");
							handler.onCheckItemBoughtSuccess(isBought);
	                	} else {
	                		handler.onCheckItemBoughtError(json.getInt("error_code"));
	                	}
					} catch (JSONException e) {
						e.printStackTrace();
						handler.onCheckItemBoughtError(-2);
					}
					if(enableLogging){
						Log.d(TAG, ajaxStatus.getCode() + ":" + json.toString());
					}
                } else {
                	if(enableLogging){
                		Log.e(TAG, "Error2: " + ajaxStatus.getCode());
                	}
                	handler.onCheckItemBoughtError(ajaxStatus.getCode());
                }
            }
		});
	}
	
	public void buyItem(String accessToken, String itemId, int quantity, int level, final BuyItemHandler handler){
		String url = Constant.BUY_INAPP_ITEM_ENDPOINT + accessToken;
		Map<String, String> params = new HashMap<String, String>();
		params.put("item_id", itemId);
		params.put("total_item", String.valueOf(quantity));
		params.put("user_level", String.valueOf(level));
		aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus ajaxStatus) {
                if(json != null){
                	boolean status;
					try {
						status = json.getBoolean("status");
						if(status){
							String message = json.getString("message");
							handler.onBuyItemSuccess(message);
	                	} else {
	                		handler.onBuyItemFail(json.getString("message"));
	                	}
					} catch (JSONException e) {
						e.printStackTrace();
						handler.onBuyItemError(-2);
					}
					if(enableLogging){
						Log.d(TAG, ajaxStatus.getCode() + ":" + json.toString());
					}
                } else {
                	if(enableLogging){
                		Log.e(TAG, "Error2: " + ajaxStatus.getCode());
                	}
                	handler.onBuyItemError(ajaxStatus.getCode());
                }
            }
		});
	}
	
	private String post(String endpoint, Map<String, String> params) throws IOException {
		String returnValue = "";
		HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 10000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 10000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		DefaultHttpClient client = new MyHttpClient(httpParameters, context);
		HttpPost post = new HttpPost(endpoint);
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
		BasicNameValuePair pair = null;
		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			pair = new BasicNameValuePair(param.getKey(), param.getValue());
			paramList.add(pair);
		}
		try {
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(paramList);
			post.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse;
			httpResponse = client.execute(post);
			InputStream inputStream = httpResponse.getEntity().getContent();
	        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	        StringBuilder stringBuilder = new StringBuilder();
	        String bufferedStrChunk = null;
	        while((bufferedStrChunk = bufferedReader.readLine()) != null){
	            stringBuilder.append(bufferedStrChunk);
	        }
	        returnValue = stringBuilder.toString();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return returnValue;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return returnValue;
	}
	
	
}
