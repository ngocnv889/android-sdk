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
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.appota.asdk.http.MyHttpClient;
import com.appota.asdk.model.AppotaAccessToken;
import com.appota.asdk.model.BankOption;
import com.appota.asdk.model.BankPayment;
import com.appota.asdk.model.PaypalForm;
import com.appota.asdk.model.PaypalPayment;
import com.appota.asdk.model.SMSOption;
import com.appota.asdk.model.SMSPayment;
import com.appota.asdk.model.TransactionResult;
import com.appota.asdk.model.User;
import com.appota.asdk.util.Constant;
import com.appota.asdk.util.Util;


public class AppotaFactory {

	private static volatile AppotaFactory defaultInstance;
	private Context context;
	private final String TAG = AppotaFactory.class.getSimpleName();
	private boolean enableLogging = false;
	
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
		ApplicationInfo ai;
		try {
			ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			enableLogging = bundle.getBoolean("enable_logging");
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
			enableLogging = false;
		} catch (NullPointerException e) {
			Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
			enableLogging = false;
		}
		return defaultInstance;
	}
	
	public String getRequestToken(String clientKey, String redirectURI, List<String> scopes, String lang) {
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
	
	public String getNonUserRequestToken(String clientKey, List<String> scopes, String lang) {
		String requestToken = "";
		String scope = "";
		for (String s : scopes) {
			scope += s + ",";
		}
		try {
			String params = URLEncoder.encode(Constant.RESPONSE_TYPE_KEY, Constant.ENCODE)
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
			System.err.println(endpoint);
			Map<String, String> map = new HashMap<String, String>();
			long backoff = Constant.BACKOFF_MILLI_SECONDS + new Random().nextInt(1000);
			for (int i = 1; i <= Constant.MAX_ATTEMPTS; i++) {
				String value;
				try {
					value = post(endpoint, map);
					if(enableLogging){
						Log.d(TAG, "GetNonUserRequestToken ------> " + value);
					}
					JSONObject obj = new JSONObject(value);
					requestToken = obj.getString("request_token");
					return requestToken;
				} catch (IOException e) {
					if(enableLogging){
						e.printStackTrace();
						Log.e(TAG, "Failed to connect on attempt " + i + ":" + e);
					}
	                if (i == Constant.MAX_ATTEMPTS) {
	                	if(enableLogging){
	                		Log.e(TAG, e.getMessage() + " error code: [-4]");
	                	}
	                	return "-4";
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
	                     return "-1";
	                 }
	                 backoff *= 2;
				} catch (JSONException e) {
					if(enableLogging){
						e.printStackTrace();
						Log.e(TAG, e.getMessage() + " error code: [-2]");
					}
					return "-2";
				}
			}
		} catch (UnsupportedEncodingException e) {
			if(enableLogging){
				Log.e(TAG, e.getMessage() + " error code: [-3]");
				e.printStackTrace();
			}
			return "-3";
		}
		return requestToken;
	}
	
	public AppotaAccessToken getAccessToken(String requestToken, String clientKey, String clientSecret, String lang){
		AppotaAccessToken accessToken = new AppotaAccessToken();
		accessToken.setErrorCode(-3);
		if(requestToken.equals("")){
			accessToken.setErrorCode(-3);
		} else {
			String endpoint = Constant.ACCESS_TOKEN_ENDPOINT;
			Map<String, String> params = new HashMap<String, String>();
			params.put(Constant.REQUEST_TOKEN_KEY, requestToken);
			params.put(Constant.CLIENT_ID_KEY, clientKey);
			params.put(Constant.CLIENT_SECRET_KEY, clientSecret);
			params.put(Constant.CALLBACK_URL_KEY, Constant.DEFAULT_REDIRECT_URI);
			params.put(Constant.GRANT_TYPE_KEY, Constant.GRANT_TYPE);
			params.put(Constant.LANG_KEY, lang);
			long backoff = Constant.BACKOFF_MILLI_SECONDS + new Random().nextInt(1000);
			for (int i = 1; i <= Constant.MAX_ATTEMPTS; i++) {
				try {
					String value = post(endpoint, params);
					if(enableLogging){
						Log.d(TAG, "GetAccessTokenToken ------> " + value);
					}
					JSONObject jsonObj = new JSONObject(value);
					String status = jsonObj.getString("status");
					if (status.equals("true")) {
						String accessTokenStr = jsonObj.getString("access_token");
						String refreshToken = jsonObj.getString("refresh_token");
						String expireDate = jsonObj.getString("expires_in");
						accessToken = new AppotaAccessToken();
						accessToken.setAccessToken(accessTokenStr);
						accessToken.setExpireDate(expireDate);
						accessToken.setRefreshToken(refreshToken);
						accessToken.setErrorCode(0);
						return accessToken;
					} else {
						String message = jsonObj.getString("message");
						accessToken.setExpireDate(message);
						accessToken.setErrorCode(-5);
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
	                	accessToken.setErrorCode(-4);
	                	return accessToken;
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
	                     accessToken.setErrorCode(-1);
		                return accessToken;
	                 }
	                 backoff *= 2;
				} catch (JSONException e) {
					if(enableLogging){
						e.printStackTrace();
						Log.e(TAG, e.getMessage() + " error code: [-2]");
					}
					accessToken.setErrorCode(-2);
					return accessToken;
				}
			}
		}
		return accessToken;
	}
	
	public AppotaAccessToken getNonUserAccessToken(String requestToken, String clientKey, String clientSecret, String lang){
		AppotaAccessToken accessToken = new AppotaAccessToken();
		if(TextUtils.equals(requestToken, "-4")){
			accessToken.setErrorCode(-4);
			return accessToken;
		} else if(TextUtils.equals(requestToken, "-3")){
			accessToken.setErrorCode(-3);
			return accessToken;
		}  else if(TextUtils.equals(requestToken, "-2")){
			accessToken.setErrorCode(-2);
			return accessToken;
		} else {
			String endpoint = Constant.NON_USER_ACCESS_TOKEN_ENDPOINT;
			Map<String, String> params = new HashMap<String, String>();
			params.put(Constant.REQUEST_TOKEN_KEY, requestToken);
			params.put(Constant.CLIENT_ID_KEY, clientKey);
			params.put(Constant.CLIENT_SECRET_KEY, clientSecret);
			params.put(Constant.CALLBACK_URL_KEY, Constant.DEFAULT_REDIRECT_URI);
			params.put(Constant.GRANT_TYPE_KEY, Constant.GRANT_TYPE);
			params.put(Constant.LANG_KEY, lang);
			long backoff = Constant.BACKOFF_MILLI_SECONDS + new Random().nextInt(1000);
			for (int i = 1; i <= Constant.MAX_ATTEMPTS; i++) {
				try {
					String value = post(endpoint, params);
					if(enableLogging){
						Log.d(TAG, "GetNonUserAccessTokenToken ------> " + value);
					}
					JSONObject jsonObj = new JSONObject(value);
					String status = jsonObj.getString("status");
					if (status.equals("true")) {
						String accessTokenStr = jsonObj.getString("access_token");
						String refreshToken = jsonObj.getString("refresh_token");
						String expireDate = jsonObj.getString("expires_in");
						accessToken = new AppotaAccessToken();
						accessToken.setAccessToken(accessTokenStr);
						accessToken.setExpireDate(expireDate);
						accessToken.setRefreshToken(refreshToken);
						accessToken.setErrorCode(0);
						return accessToken;
					} else {
						String message = jsonObj.getString("message");
						accessToken.setExpireDate(message);
						accessToken.setErrorCode(-5);
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
	                	accessToken.setErrorCode(-4);
	                	return accessToken;
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
	                     accessToken.setErrorCode(-1);
		                return accessToken;
	                 }
	                 backoff *= 2;
				} catch (JSONException e) {
					if(enableLogging){
						e.printStackTrace();
						Log.e(TAG, e.getMessage() + " error code: [-2]");
					}
					accessToken.setErrorCode(-2);
					return accessToken;
				}
			}
		}
		return accessToken;
	}
	
	public User getUserInfo(String accessToken){
		User user = new User();
		user.setErrorCode(-3);
		String endpoint = Constant.USER_INFO_ENDPOINT;
		Map<String, String> params = new HashMap<String, String>();
		long backoff = Constant.BACKOFF_MILLI_SECONDS + new Random().nextInt(1000);
		for (int i = 1; i <= Constant.MAX_ATTEMPTS; i++) {
			try {
				String value = post(endpoint, params);
				if(enableLogging){
					Log.d(TAG, "GetUserInfo ------> " + value);
				}
				JSONObject jsonObj = new JSONObject(value);
				String status = jsonObj.getString("status");
				if (status.equals("true")) {
					JSONObject obj = jsonObj.getJSONObject("data");
					String userName = obj.getString("username");
					String fullName = obj.getString("fullname");
					String birthday = obj.getString("birthday");
					String gender = obj.getString("gender");
					int greenTym = obj.getInt("blue_tym");
					int purpleTym = obj.getInt("purple_tym");
					String avatarUrl = obj.getString("avatar");
					user.setUserName(userName);
					user.setFullName(fullName);
					user.setBirthday(birthday);
					user.setGender(gender);
					user.setGreenTym(greenTym);
					user.setPurpleTym(purpleTym);
					user.setAvatarUr(avatarUrl);
				} else {
					user.setErrorCode(jsonObj.getInt("error_code"));
				}
				return user;
			} catch (IOException e) {
				if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, "Failed to connect on attempt " + i + ":" + e);
				}
                if (i == Constant.MAX_ATTEMPTS) {
                	if(enableLogging){
                		Log.e(TAG, e.getMessage() + " error code: [-4]");
                	}
                	user.setErrorCode(-4);
                	return user;
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
                     user.setErrorCode(-1);
	                return user;
                 }
                 backoff *= 2;
			} catch (JSONException e) {
				if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, e.getMessage() + " error code: [-2]");
				}
				user.setErrorCode(-2);
				return user;
			}
		}
		return user;
	}
	
	public AppotaAccessToken refreshToken(String token, String clientKey, String clientSecret){
		AppotaAccessToken accessToken = new AppotaAccessToken();
		accessToken.setErrorCode(-3);
		String endpoint = Constant.REFRESH_TOKEN_ENDPOINT;
		Map<String, String> params = new HashMap<String, String>();
		params.put(Constant.REFRESH_TOKEN_KEY, token);
		params.put(Constant.CLIENT_ID_KEY, clientKey);
		params.put(Constant.CLIENT_SECRET_KEY, clientSecret);
		params.put(Constant.GRANT_TYPE_KEY, "refresh_token");
		long backoff = Constant.BACKOFF_MILLI_SECONDS + new Random().nextInt(1000);
		for (int i = 1; i <= Constant.MAX_ATTEMPTS; i++) {
			try {
				String value = post(endpoint, params);
				if(enableLogging){
					Log.d(TAG, "RefreshToken ------> " + value);
				}
				JSONObject jsonObj = new JSONObject(value);
				String status = jsonObj.getString("status");
				if (status.equals("true")) {
					String accessTokenStr = jsonObj.getString("access_token");
					String refreshToken = jsonObj.getString("refresh_token");
					String expireDate = jsonObj.getString("expires_in");
					accessToken = new AppotaAccessToken();
					accessToken.setAccessToken(accessTokenStr);
					accessToken.setErrorCode(0);
					accessToken.setExpireDate(expireDate);
					accessToken.setRefreshToken(refreshToken);
					return accessToken;
				} else {
					String message = jsonObj.getString("message");
					accessToken.setExpireDate(message);
					accessToken.setErrorCode(-5);
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
                	accessToken.setErrorCode(-4);
                	return accessToken;
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
                     accessToken.setErrorCode(-1);
	                return accessToken;
                 }
                 backoff *= 2;
			} catch (JSONException e) {
				if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, e.getMessage() + " error code: [-2]");
				}
				accessToken.setErrorCode(-2);
				return accessToken;
			}
		}
		return accessToken;
	}
	
	public AppotaAccessToken login(Context context, String oldAccessToken, String username, String password){
		AppotaAccessToken accessToken = new AppotaAccessToken();
		accessToken.setErrorCode(-3);
		String deviceId = Util.getDeviceId(context);
		String carrier = Util.getCarrier(context);
		String deviceOs = "android";
		String osVersion = Build.MANUFACTURER + " " + Build.PRODUCT + " " + Build.VERSION.RELEASE;
		String endpoint = Constant.LOGIN_ENDPOINT + oldAccessToken;
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("password", password);
		params.put("device_id", deviceId);
		params.put("device_os", deviceOs);
		params.put("vendor", carrier);
		params.put("device_os_version", osVersion);
		long backoff = Constant.BACKOFF_MILLI_SECONDS + new Random().nextInt(1000);
		for (int i = 1; i <= Constant.MAX_ATTEMPTS; i++) {
            try {
                String value = post(endpoint, params);
                if(enableLogging){
					Log.d(TAG, "Login ------> " + value);
				}
                JSONObject obj = new JSONObject(value);
                boolean status = obj.getBoolean("status");
                if(!status){
                	accessToken.setExpireDate(obj.getString("message"));
                	accessToken.setErrorCode(-5);
                	return accessToken;
                }
                String accessTokenStr = obj.getString("access_token");
				String refreshTokenStr = obj.getString("refresh_token");
				String expireDate = obj.getString("expires_in");
				accessToken.setExpireDate(expireDate);
				accessToken.setRefreshToken(refreshTokenStr);
				accessToken.setAccessToken(accessTokenStr);
				accessToken.setErrorCode(0);
                return accessToken;
            } catch (IOException e) {
            	if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, "Failed to connect on attempt " + i + ":" + e);
				}
                if (i == Constant.MAX_ATTEMPTS) {
                	if(enableLogging){
                		Log.e(TAG, e.getMessage() + " error code: [-4]");
                	}
                	accessToken.setErrorCode(-4);
                	return accessToken;
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
                     accessToken.setErrorCode(-1);
	                return accessToken;
                 }
                 backoff *= 2;
			} catch (JSONException e) {
				if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, e.getMessage() + " error code: [-2]");
				}
				accessToken.setErrorCode(-2);
				return accessToken;
			}
		}
		return accessToken;
	}
	
	public SMSPayment smsTopup(String accessToken, boolean shortSyntax){
		String endpoint = Constant.SMS_TOPUP_ENDPOINT + accessToken;
		Map<String, String> params = new HashMap<String, String>();
		if(shortSyntax){
			params.put("short", "1");
		} else {
			params.put("short", "0");
		}
		SMSPayment data = new SMSPayment();
		data.setErrorCode(-3);
		List<SMSOption> smsOptions = new ArrayList<SMSOption>();
		long backoff = Constant.BACKOFF_MILLI_SECONDS + new Random().nextInt(1000);
		String value = "";
		for (int i = 1; i <= Constant.MAX_ATTEMPTS; i++) {
            try {
            	value = post(endpoint, params);
            	if(enableLogging){
					Log.d(TAG, "TopupSMS ------> " + value);
				}
				JSONObject obj = new JSONObject(value);
				boolean status = obj.getBoolean("status");
				if(status){
					JSONObject dataObj = obj.getJSONObject("data");
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
						smsOptions.add(option);
						
					}
					data.setTransactionID(topupId);
					data.setSmsOptions(smsOptions);
					data.setErrorCode(0);
				} else {
					data.setErrorCode(obj.getInt("error_code"));
				}
				return data;
            } catch (IOException e) {
            	if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, "Failed to connect on attempt " + i + ":" + e);
				}
                if (i == Constant.MAX_ATTEMPTS) {
                	if(enableLogging){
                		Log.e(TAG, e.getMessage() + " error code: [-4]");
                	}
                	data.setErrorCode(-4);
                	return data;
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
                     data.setErrorCode(-1);
	                return data;
                 }
                 backoff *= 2;
			} catch (JSONException e) {
				if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, e.getMessage() + " error code: [-2]");
				}
				data.setErrorCode(-2);
				return data;
			}
		}
		return data;
	}
	
	public String cardTopup(String accessToken, String cardSerial, String cardCode, String vendor, String noticeURL){
		String endpoint = Constant.CARD_TOPUP_ENDPOINT + accessToken;
		Map<String, String> params = new HashMap<String, String>();
		params.put("vendor", vendor);
		params.put("card_serial", cardSerial);
		params.put("card_code", cardCode);
		if(!TextUtils.isEmpty(noticeURL)){
			params.put("notice_url", noticeURL);
		}
		String topupId = "-3";
		long backoff = Constant.BACKOFF_MILLI_SECONDS + new Random().nextInt(1000);
		String value = "";
		for (int i = 1; i <= Constant.MAX_ATTEMPTS; i++) {
            try {
            	value = post(endpoint, params);
            	if(enableLogging){
					Log.d(TAG, "TopupCardWithNoticeURL ------> " + value);
				}
				JSONObject jsonObj = new JSONObject(value);
				if(jsonObj.has("data")){
					JSONObject obj = jsonObj.getJSONObject("data");
					for(int j=0; j< obj.length(); j++){
						topupId = obj.getString("topup_id");
					}
				} else {
					topupId = jsonObj.getString("message") + "|";
				}
				return topupId;
            } catch (IOException e) {
            	Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                if (i == Constant.MAX_ATTEMPTS) {
                	topupId = "-4";
                    break;
                }
                try {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    topupId = "-1";
                    return topupId;
                }
                backoff *= 2;
            } catch (JSONException e) {
				e.printStackTrace();
				topupId = "-2";
			}
		}
		return topupId;
	}
	
	public BankPayment bankTopup(String accessToken, String amount, String noticeURL){
		String endpoint = Constant.BANK_TOPUP_ENDPOINT + accessToken;
		Map<String, String> params = new HashMap<String, String>();
		params.put("amount", amount);
		if(!TextUtils.isEmpty(noticeURL)){
			params.put("notice_url", noticeURL);
		}
		BankPayment data = new BankPayment();
		data.setErrorCode(-3);
		BankOption option = new BankOption();
		long backoff = Constant.BACKOFF_MILLI_SECONDS + new Random().nextInt(1000);
		String value = "";
		for (int i = 1; i <= Constant.MAX_ATTEMPTS; i++) {
            try {
            	value = post(endpoint, params);
            	if(enableLogging){
					Log.d(TAG, "TopupBank ------> " + value);
				}
            	JSONObject jsonObj = new JSONObject(value);
    			JSONObject obj = jsonObj.getJSONObject("data");
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
    			return data;
            } catch (IOException e) {
            	if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, "Failed to connect on attempt " + i + ":" + e);
				}
                if (i == Constant.MAX_ATTEMPTS) {
                	if(enableLogging){
                		Log.e(TAG, e.getMessage() + " error code: [-4]");
                	}
                	data.setErrorCode(-4);
                	return data;
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
                     data.setErrorCode(-1);
	                return data;
                 }
                 backoff *= 2;
			} catch (JSONException e) {
				if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, e.getMessage() + " error code: [-2]");
				}
				data.setErrorCode(-2);
				return data;
			}
		}
		return data;
	}
	
	public PaypalPayment paypalTopup(String accessToken, String amount, String noticeURL){
		String endpoint = Constant.PAYPAL_TOPUP_ENDPOINT + accessToken;
		Map<String, String> params = new HashMap<String, String>();
		params.put("amount", amount);
		if(!TextUtils.isEmpty(noticeURL)){
			params.put("notice_url", noticeURL);
		}
		PaypalPayment data = new PaypalPayment();
		data.setErrorCode(-3);
		PaypalForm form = new PaypalForm();
		long backoff = Constant.BACKOFF_MILLI_SECONDS + new Random().nextInt(1000);
		String value = "";
		for (int i = 1; i <= Constant.MAX_ATTEMPTS; i++) {
            try {
            	value = post(endpoint, params);
            	if(enableLogging){
					Log.d(TAG, "TopupPaypalWithNoticeURL ------> " + value);
				}
            	JSONObject jsonObj = new JSONObject(value);
            	boolean status = jsonObj.getBoolean("status");
            	if(status){
            		JSONObject obj = jsonObj.getJSONObject("data");
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
            	} else {
            		data.setErrorCode(jsonObj.getInt("error_code"));
            	}
    			return data;
            } catch (IOException e) {
            	if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, "Failed to connect on attempt " + i + ":" + e);
				}
                if (i == Constant.MAX_ATTEMPTS) {
                	if(enableLogging){
                		Log.e(TAG, e.getMessage() + " error code: [-4]");
                	}
                	data.setErrorCode(-4);
                	return data;
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
                     data.setErrorCode(-1);
	                return data;
                 }
                 backoff *= 2;
			} catch (JSONException e) {
				if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, e.getMessage() + " error code: [-2]");
				}
				data.setErrorCode(-2);
				return data;
			}
		}
		return data;
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
	
	
	public SMSPayment smsInApp(String accessToken, String noticeURL, String state, String target, boolean shortSyntax){
		String endpoint = Constant.SMS_INAPP_ENDPOINT + accessToken;
		Map<String, String> params = new HashMap<String, String>();
		if(!TextUtils.isEmpty(noticeURL)){
			params.put("notice_url", noticeURL);
		}
		if(!TextUtils.isEmpty(state)){
			params.put("state", state);
		}
		if(!TextUtils.isEmpty(target)){
			params.put("target", target);
		}
		if(shortSyntax){
			params.put("short", "1");
		} else {
			params.put("short", "0");
		}
		SMSPayment data = new SMSPayment();
		data.setErrorCode(-3);
		List<SMSOption> smsOptions = new ArrayList<SMSOption>();
		long backoff = Constant.BACKOFF_MILLI_SECONDS + new Random().nextInt(1000);
		String value = "";
		for (int i = 1; i <= Constant.MAX_ATTEMPTS; i++) {
            try {
            	value = post(endpoint, params);
            	if(enableLogging){
					Log.d(TAG, "InAppSMS ------> " + value);
				}
				JSONObject obj = new JSONObject(value);
				boolean status = obj.getBoolean("status");
				if(status){
					JSONObject dataObj = obj.getJSONObject("data");
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
				} else {
					data.setErrorCode(obj.getInt("error_code"));
				}
				return data;
            } catch (IOException e) {
            	if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, "Failed to connect on attempt " + i + ":" + e);
				}
                if (i == Constant.MAX_ATTEMPTS) {
                	if(enableLogging){
                		Log.e(TAG, e.getMessage() + " error code: [-4]");
                	}
                	data.setErrorCode(-4);
                	return data;
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
                    data.setErrorCode(-1);
	                return data;
                 }
                 backoff *= 2;
			} catch (JSONException e) {
				if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, e.getMessage() + " error code: [-2]");
				}
				data.setErrorCode(-2);
				return data;
			}
		}
		return data;
	}
	
	public String cardInApp(String accessToken, String cardCode, String cardSerial, String vendor, String noticeURL, String state, String target){
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
		String topupId = "-3";
		long backoff = Constant.BACKOFF_MILLI_SECONDS + new Random().nextInt(1000);
		String value = "";
		for (int i = 1; i <= Constant.MAX_ATTEMPTS; i++) {
            try {
            	value = post(endpoint, params);
            	if(enableLogging){
					Log.d(TAG, "InAppCard ------> " + value);
				}
				JSONObject jsonObj = new JSONObject(value);
				if(jsonObj.has("data")){
					JSONObject obj = jsonObj.getJSONObject("data");
					for(int j=0; j< obj.length(); j++){
						topupId = obj.getString("inapp_id");
					}
				} else {
					topupId = jsonObj.getString("message") + "|";
				}
				return topupId;
            } catch (IOException e) {
            	if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, "Failed to connect on attempt " + i + ":" + e);
				}
                if (i == Constant.MAX_ATTEMPTS) {
                	if(enableLogging){
                		Log.e(TAG, e.getMessage() + " error code: [-4]");
                	}
                	topupId = "-4";
                	return topupId;
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
                     topupId = "-1";
	                return topupId;
                 }
                 backoff *= 2;
			} catch (JSONException e) {
				if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, e.getMessage() + " error code: [-2]");
				}
				topupId = "-2";
				return topupId;
			}
		}
		return topupId;
	}
	
	public BankPayment bankInApp(String accessToken, String amount, String noticeURL, String state, String target){
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
		BankPayment data = new BankPayment();
		data.setErrorCode(-3);
		BankOption option = new BankOption();
		long backoff = Constant.BACKOFF_MILLI_SECONDS + new Random().nextInt(1000);
		String value = "";
		for (int i = 1; i <= Constant.MAX_ATTEMPTS; i++) {
            try {
            	value = post(endpoint, params);
            	if(enableLogging){
					Log.d(TAG, "InAppBank ------> " + value);
				}
            	JSONObject jsonObj = new JSONObject(value);
            	boolean status = jsonObj.getBoolean("status");
            	if(status){
            		JSONObject obj = jsonObj.getJSONObject("data");
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
            	} else {
            		data.setErrorCode(jsonObj.getInt("error_code"));
            	}
    			return data;
            } catch (IOException e) {
            	if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, "Failed to connect on attempt " + i + ":" + e);
				}
                if (i == Constant.MAX_ATTEMPTS) {
                	if(enableLogging){
                		Log.e(TAG, e.getMessage() + " error code: [-4]");
                	}
                	data.setErrorCode(-4);
                	return data;
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
                     data.setErrorCode(-1);
	                return data;
                 }
                 backoff *= 2;
			} catch (JSONException e) {
				if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, e.getMessage() + " error code: [-2]");
				}
				data.setErrorCode(-2);
				return data;
			}
		}
		return data;
	}
	
	public PaypalPayment paypalInApp(String accessToken, String amount, String noticeURL, String state, String target){
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
		PaypalPayment data = new PaypalPayment();
		data.setErrorCode(-3);
		PaypalForm form = new PaypalForm();
		long backoff = Constant.BACKOFF_MILLI_SECONDS + new Random().nextInt(1000);
		String value = "";
		for (int i = 1; i <= Constant.MAX_ATTEMPTS; i++) {
            try {
            	value = post(endpoint, params);
            	if(enableLogging){
					Log.d(TAG, "InAppPaypal ------> " + value);
				}
            	JSONObject jsonObj = new JSONObject(value);
            	boolean status = jsonObj.getBoolean("status");
            	if(status){
            		JSONObject obj = jsonObj.getJSONObject("data");
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
            	} else {
            		data.setErrorCode(jsonObj.getInt("error_code"));
            	}
    			return data;
            } catch (IOException e) {
            	if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, "Failed to connect on attempt " + i + ":" + e);
				}
                if (i == Constant.MAX_ATTEMPTS) {
                	if(enableLogging){
                		Log.e(TAG, e.getMessage() + " error code: [-4]");
                	}
                	data.setErrorCode(-4);
                	return data;
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
                     data.setErrorCode(-1);
	                return data;
                 }
                 backoff *= 2;
			} catch (JSONException e) {
				if(enableLogging){
					e.printStackTrace();
					Log.e(TAG, e.getMessage() + " error code: [-2]");
				}
				data.setErrorCode(-2);
				return data;
			}
		}
		return data;
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
