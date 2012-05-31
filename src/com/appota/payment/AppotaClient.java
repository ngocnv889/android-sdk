package com.appota.payment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appota.model.AccessToken;
import com.appota.model.BankOption;
import com.appota.model.BankTopup;
import com.appota.model.DefaultAppotaAuthorize;
import com.appota.model.DefaultAppotaOauth;
import com.appota.model.HostVerifier;
import com.appota.model.InappChecker;
import com.appota.model.PaypalForm;
import com.appota.model.PaypalTopup;
import com.appota.model.SMSOption;
import com.appota.model.SMSTopup;
import com.appota.model.TopupChecker;

public class AppotaClient {
	
	private final String CARD_CODE_KEY = "card_code";
	private final String CARD_SERIAL_KEY = "card_serial";
	private final String VENDOR_KEY = "vendor";
	private final String AMOUNT_KEY = "amount";
	private final String TOPUP_KEY = "topup_id";
	private final String RESPONSE_TYPE_KEY = "response_type";
	private final String CLIENT_ID_KEY = "client_id";
	private final String CLIENT_SECRET_KEY = "client_secret";
	private final String SCOPE_KEY = "scope";
	private final String STATE_KEY = "state";
	private final String LANG_KEY = "lang";
	private final String CALLBACK_URL_KEY = "redirect_uri";
	private final String ENCODE = "UTF-8";
	private final String REQUEST_TOKEN_KEY = "request_token";
	private final String GRANT_TYPE_KEY = "grant_type";
	private final String REFRESH_TOKEN_KEY = "refresh_token";
	private static AppotaClient client;
	
	public static AppotaClient getInstance(){
		if(client == null){
			client = new AppotaClient();
		}
		return client;
	}
	
	
	/**
	 * Build the request token endpoint 
	 * <p>
	 * 
	 *
	 * @param  clientID : you received after register your application
	 * @param  scopes : user permission list. (see developer.appota.com)
	 * @param  DefaultAppotaOauth : contains default oauth parameters 
	 * @return  request token endpoint: https://id.appota.com/oauth/request_token/......
	 */
	public String requestToken(String clientId, List<String> scopes, DefaultAppotaOauth defOauth){
		
		String scope = "";
		String requestTokenUrl = null;
		
		for(String s : scopes){
			scope += s + ",";
		}
		
		try {
			String params = URLEncoder.encode(RESPONSE_TYPE_KEY, ENCODE) + "=" + URLEncoder.encode(defOauth.getResponseType(), ENCODE)
					+ "&" + URLEncoder.encode(CLIENT_ID_KEY, ENCODE) + "=" + URLEncoder.encode(clientId, ENCODE)
					+ "&" + URLEncoder.encode(CALLBACK_URL_KEY, ENCODE) + "=" + URLEncoder.encode(defOauth.getRedirectUri(), ENCODE)
					+ "&" + URLEncoder.encode(SCOPE_KEY, ENCODE) + "=" + URLEncoder.encode(scope.substring(0, scope.length()-1), ENCODE)
					+ "&" + URLEncoder.encode(STATE_KEY, ENCODE) + "=" + URLEncoder.encode(defOauth.getState(), ENCODE)
					+ "&" + URLEncoder.encode(LANG_KEY, ENCODE) + "=" + URLEncoder.encode(defOauth.getLang(), ENCODE);
			requestTokenUrl = defOauth.getRequestTokenUrl() + "?" + params;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return requestTokenUrl;
	}
	
	
	
	public String accessToken(String requestToken, String clientId, String clientSecret, DefaultAppotaAuthorize defAuthorize){
		String accessTokenUrl = null;
		try {
			String params = URLEncoder.encode(REQUEST_TOKEN_KEY, ENCODE) + "=" + URLEncoder.encode(requestToken, ENCODE)
					+ "&" + URLEncoder.encode(CLIENT_ID_KEY, ENCODE) + "=" + URLEncoder.encode(clientId, ENCODE)
					+ "&" + URLEncoder.encode(CLIENT_SECRET_KEY, ENCODE) + "=" + URLEncoder.encode(clientSecret, ENCODE)
					+ "&" + URLEncoder.encode(CALLBACK_URL_KEY, ENCODE) + "=" + URLEncoder.encode(defAuthorize.getRedirectUri(), ENCODE)
					+ "&" + URLEncoder.encode(GRANT_TYPE_KEY, ENCODE) + "=" + URLEncoder.encode(defAuthorize.getGrantType(), ENCODE)
					+ "&" + URLEncoder.encode(LANG_KEY, ENCODE) + "=" + URLEncoder.encode(defAuthorize.getLang(), ENCODE);
			accessTokenUrl = defAuthorize.getAccessTokenUrl() + "?" + params;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return accessTokenUrl;
	}
	
	/*public DefaultHttpClient getTolerantClient() {
		DefaultHttpClient client = new DefaultHttpClient();
		SSLSocketFactory sslSocketFactory = (SSLSocketFactory) client.getConnectionManager().getSchemeRegistry().getScheme("https").getSocketFactory();
		final X509HostnameVerifier delegate = sslSocketFactory.getHostnameVerifier();
		if (!(delegate instanceof HostVerifier)) {
			sslSocketFactory.setHostnameVerifier(new HostVerifier(delegate));
		}
		return client;
	}*/
	
	
	/**
	 * get access token
	 * <p>
	 * 
	 *
	 * @param  requestToken : request token you received before
	 * @param  clientId
	 * @param  clientSecret
	 * @param  DefaultAppotaAuthorize : contains default authorize parameters 
	 * @return  Access Token: contains accessToken, refreshToken, exprieDate
	 */
	
	public AccessToken getAccessToken(String requestToken, String clientId, String clientSecret, String redirectUri,
			DefaultAppotaAuthorize def) {
		HttpsURLConnection conn = null;
		InputStream in;
		URL url = null;
		String returnValue = "";
		String topupId = null;
		String errorMsg = "";
		AccessToken accessToken = null;
		try {
			String params = URLEncoder.encode(REQUEST_TOKEN_KEY, "UTF-8") + "=" + URLEncoder.encode(requestToken, "UTF-8") + "&" + URLEncoder.encode(CLIENT_ID_KEY, "UTF-8") + "=" + URLEncoder.encode(clientId, "UTF-8") + "&" + URLEncoder.encode(CLIENT_SECRET_KEY, "UTF-8") + "=" + URLEncoder.encode(clientSecret, "UTF-8")
					+ "&" + URLEncoder.encode(CALLBACK_URL_KEY, "UTF-8") + "=" + URLEncoder.encode(redirectUri) + "&" + URLEncoder.encode(GRANT_TYPE_KEY, "UTF-8") + "=" + URLEncoder.encode(def.getGrantType(), "UTF-8")
					+ "&" + URLEncoder.encode(LANG_KEY, "UTF-8") + "=" + URLEncoder.encode(def.getLang(), "UTF-8");
			System.out.println(params);
			url = new URL("https://id.appota.com/oauth/access_token");
			trustAllHosts();
			conn = (HttpsURLConnection) url.openConnection();
			conn.setHostnameVerifier(notVerify);
			conn.setDoOutput(true);
			OutputStreamWriter ow = new OutputStreamWriter(conn.getOutputStream());
			ow.write(params);
			ow.flush();
			in = new BufferedInputStream(conn.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
			returnValue = sb.toString();
			System.out.println(returnValue);
			JSONObject jsonObj = new JSONObject(returnValue);
			String token = jsonObj.getString("access_token");
			String expired = jsonObj.getString("expires_in");
			String refreshToken = jsonObj.getString("refresh_token");
			accessToken = new AccessToken(token, expired, refreshToken);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return accessToken;
		//return topupId;
	}
	
	
	/*public void getCategories(String endpoint, String accessToken, String storeId){
		BufferedReader in;
		String returnValue = "";
		
		DefaultHttpClient client = new DefaultHttpClient();
		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

        SchemeRegistry registry = new SchemeRegistry();
        SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
        socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
        registry.register(new Scheme("https", socketFactory, 443));

        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        HttpPost request = new HttpPost();
        
        try {
			request.setURI(new URI(endpoint));
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("access_token", accessToken));
			params.add(new BasicNameValuePair("store", storeId));
			
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
    		request.setEntity(entity);
			HttpResponse response = client.execute(request);
	        in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	        StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = in.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
			returnValue = sb.toString();
			System.out.println(returnValue);
			JSONObject jsonObj = new JSONObject(returnValue);
			String token = jsonObj.getString("access_token");
			String expired = jsonObj.getString("expires_in");
			String refreshToken = jsonObj.getString("refresh_token");
			accessToken = new AccessToken(token, expired, refreshToken);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}*/
	
	
	/**
	 * Pay by phone card 
	 * <p>
	 * 
	 *
	 * @param  code : phone card's code
	 * @param  serial : phone card's serial
	 * @param  vendor : the vendor name (vinaphone, mobifone, vietel,...)
	 * @param  endPoint : the Appota API endPoint URL
	 * @param  accessToken : the API's accessToken you get when authorized app
	 * @return  topupId : to check if your transaction is success or not
	 */

	public String cardPayment(String code, String serial, String vendor,
			String endPoint, String accessToken) {
		HttpsURLConnection conn = null;
		InputStream in;
		URL url = null;
		String returnValue = "";
		String topupId = null;
		String errorMsg = "";
		try {
			String params = URLEncoder.encode(CARD_CODE_KEY, "UTF-8") + "=" + URLEncoder.encode(String.valueOf(code), "UTF-8") + "&" + URLEncoder.encode(CARD_SERIAL_KEY, "UTF-8") + "=" + URLEncoder.encode(serial, "UTF-8") + "&" + URLEncoder.encode(VENDOR_KEY, "UTF-8") + "=" + URLEncoder.encode(vendor, "UTF-8");
			System.out.println(params);
			url = new URL(endPoint + "?access_token=" + accessToken);
			trustAllHosts();
			conn = (HttpsURLConnection) url.openConnection();
			conn.setHostnameVerifier(notVerify);
			conn.setDoOutput(true);
			OutputStreamWriter ow = new OutputStreamWriter(conn.getOutputStream());
			ow.write(params);
			ow.flush();
			in = new BufferedInputStream(conn.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
			returnValue = sb.toString();
			System.out.println(returnValue);
			JSONObject jsonObj = new JSONObject(returnValue);
			if(jsonObj.has("data")){
				JSONObject obj = jsonObj.getJSONObject("data");
				for(int i=0; i< obj.length(); i++){
					topupId = obj.getString("topup_id");
				}
				return topupId;
			} else {
				errorMsg = jsonObj.getString("message");
				return errorMsg;
			}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return topupId;
	}
	
	/**
	 * Pay by SMS
	 * <p>
	 * 
	 *
	 * @param  endPoint : the Appota API endPoint URL
	 * @param  accessToken : the API's accessToken you get when authorized app
	 * @return  SMSTopup : SMS options for user to pay. ex: (1000 VND -> 5 tym send to 8x01)
	 * and topupId   to check if user's transaction is success or not.
	 */

	public SMSTopup smsPayment(String endPoint, String accessToken) {
		HttpsURLConnection conn = null;
		InputStream in;
		URL url = null;
		String returnValue = "";
		List<SMSOption> smsOptions = new ArrayList<SMSOption>();
		SMSTopup sms = null;
		try {
			url = new URL(endPoint + "?access_token=" + accessToken);
			trustAllHosts();
			conn = (HttpsURLConnection) url.openConnection();
			conn.setHostnameVerifier(notVerify);
			in = new BufferedInputStream(conn.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
			returnValue = sb.toString();
			JSONObject jsonObj = new JSONObject(returnValue);
			System.out.println(returnValue);
			JSONObject obj = jsonObj.getJSONObject("data");
				
				String topupId = obj.getString("topup_id");
				JSONArray smsArray = obj.getJSONArray("options");
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
				System.out.println("SMS size= " +smsOptions.size());
				sms = new SMSTopup();
				sms.setTopupId(topupId);
				sms.setSmsOptions(smsOptions);
			return sms;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sms;
	}
	
	/**
	 * Pay by internet banking
	 * <p>
	 * 
	 *
	 * @param  amount : amount of money you need to pay
	 * @param  endPoint : the Appota API endPoint URL
	 * @param  accessToken : the API's accessToken you get when authorized app
	 * @return  BankTopup : redirect user to banking paygate using browser and TopupId to check if user's transaction is success or not
	 */

	public BankTopup bankPayment(double amount, String endPoint,
			String accessToken) {
		HttpsURLConnection conn = null;
		InputStream in;
		URL url = null;
		String returnValue = "";
		BankTopup bank = null;
		BankOption option = null;
		try {
			String params = URLEncoder.encode(AMOUNT_KEY, "UTF-8") + "=" + URLEncoder.encode(String.valueOf(amount), "UTF-8");
			url = new URL(endPoint + "?access_token=" + accessToken);
			trustAllHosts();
			conn = (HttpsURLConnection) url.openConnection();
			conn.setHostnameVerifier(notVerify);
			conn.setDoOutput(true);
			OutputStreamWriter ow = new OutputStreamWriter(conn.getOutputStream());
			ow.write(params);
			ow.flush();
			in = new BufferedInputStream(conn.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
			returnValue = sb.toString();
			JSONObject jsonObj = new JSONObject(returnValue);
			JSONObject obj = jsonObj.getJSONObject("data");
			for(int i=0; i< obj.length(); i++){
				
				String topupId = obj.getString("topup_id");
				
				JSONArray smsArray = obj.getJSONArray("options");
				for(int j=0;j<smsArray.length();j++){
					JSONObject smsObj = smsArray.getJSONObject(j);
					
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
					
					bank = new BankTopup();
					bank.setTopupId(topupId);
					bank.setOption(option);
				}
			}
			return bank;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bank;
	}
	
	/**
	 * Pay via paypal
	 * <p>
	 * 
	 *
	 * @param  amount : amount of money you need to pay
	 * @param  endPoint : the Appota API endPoint URL
	 * @param  accessToken : the API's accessToken you get when authorized app
	 * @return  PaypalTopup : contains paypal information to pay, and TopupId to check if user's transaction is success or not.
	 */

	public PaypalTopup paypalPayment(double amount, String endPoint, String accessToken) {
		HttpsURLConnection conn = null;
		InputStream in;
		URL url = null;
		String returnValue = "";
		PaypalTopup paypal = null;
		PaypalForm form = null;
		try {
			String params = URLEncoder.encode(AMOUNT_KEY, "UTF-8") + "=" + URLEncoder.encode(String.valueOf(amount), "UTF-8");
			url = new URL(endPoint + "?access_token=" + accessToken);
			trustAllHosts();
			conn = (HttpsURLConnection) url.openConnection();
			conn.setHostnameVerifier(notVerify);
			conn.setDoOutput(true);
			OutputStreamWriter ow = new OutputStreamWriter(conn.getOutputStream());
			ow.write(params);
			ow.flush();
			in = new BufferedInputStream(conn.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
			returnValue = sb.toString();
			JSONObject jsonObj = new JSONObject(returnValue);
			JSONObject obj = jsonObj.getJSONObject("data");
			for(int i=0; i< obj.length(); i++){
				
				String topupId = obj.getString("topup_id");
				
				JSONArray paypalArray = obj.getJSONArray("options");
				for(int j=0;j<paypalArray.length();j++){
					
					JSONObject paypalObj = paypalArray.getJSONObject(j);
					
					for(int k=0;k<paypalObj.length();k++){
						JSONObject paypalForm = paypalObj.getJSONObject("form");
						String cmd = paypalForm.getString("cmd");
						int noShipping = paypalForm.getInt("no_shipping");
						int noNote = paypalForm.getInt("no_note");
						String currencyCode = paypalForm.getString("currency_code");
						String bn = paypalForm.getString("bn");
						String itemName = paypalForm.getString("item_name");
						String notifyUrl = paypalForm.getString("notify_url");
						String business = paypalForm.getString("business");
						
						form = new PaypalForm();
						form.setAmount(amount);
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
					paypal = new PaypalTopup();
					paypal.setAmount(amount);
					paypal.setCurrency(currency);
					paypal.setForm(form);
					paypal.setTopupId(topupId);
					paypal.setTym(tym);
				}
				
			}
			return paypal;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return paypal;
	}
	
	/**
	 * Check if user's transaction is success or not
	 * <p>
	 * 
	 *
	 * @param  topupId : topupId you get after doing transction
	 * @param  endPoint : the Appota API endPoint URL
	 * @param  accessToken : the API's accessToken you get when authorized app
	 * @return  isSuccess : true if transaction succeeded otherwise false if transaction failed.
	 */

	public TopupChecker checkTopup(String topupId, String endPoint, String accessToken) {
		HttpsURLConnection conn = null;
		InputStream in;
		URL url = null;
		String returnValue = "";
		boolean isSuccess = false;
		TopupChecker topup = null;
		try {
			String params = URLEncoder.encode(TOPUP_KEY, "UTF-8") + "=" + URLEncoder.encode(String.valueOf(topupId), "UTF-8");
			url = new URL(endPoint + "?access_token=" + accessToken);
			trustAllHosts();
			conn = (HttpsURLConnection) url.openConnection();
			conn.setHostnameVerifier(notVerify);
			conn.setDoOutput(true);
			OutputStreamWriter ow = new OutputStreamWriter(conn.getOutputStream());
			ow.write(params);
			ow.flush();
			in = new BufferedInputStream(conn.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
			returnValue = sb.toString();
			JSONObject jsonObj = new JSONObject(returnValue);
			System.out.println(returnValue);
			isSuccess = jsonObj.getBoolean("status");
			if(isSuccess){
				JSONObject dataObj = jsonObj.getJSONObject("data");
				//for(int i=0;i<dataArr.length();i++){
					//JSONObject dataObj = dataArr.getJSONObject(i);
					String tym = dataObj.getString("tym");
					String msg = dataObj.getString("message");
					topup = new TopupChecker(isSuccess, msg, tym);
				//}
				return topup;
			} else {
				String msg = jsonObj.getString("message");
				topup = new TopupChecker(isSuccess, msg, "0");
				return topup;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return topup;
	}
	
	
	/**
	 * refresh access token
	 * <p>
	 * 
	 *
	 * @param  refreshToken : refresh token that you received before
	 * @param  clientId
	 * @param  clientSecret
	 * @param  DefaultAppotaAuthorize : contains default authorize parameters 
	 * @return  Access Token: contains accessToken, refreshToken, exprieDate
	 */
	
	
	public AccessToken refreshAccessToken(String clientId, String clientSecret, String refreshToken, DefaultAppotaAuthorize def){
		BufferedReader in;
		String returnValue = "";
		AccessToken accessToken = null;
		
		DefaultHttpClient client = new DefaultHttpClient();
		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

        SchemeRegistry registry = new SchemeRegistry();
        SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
        socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
        registry.register(new Scheme("https", socketFactory, 443));

        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        HttpPost request = new HttpPost();
        def.setGrantType("refresh_token");
        try {
			request.setURI(new URI(def.getRefreshTokenEndpoint()));
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(REFRESH_TOKEN_KEY, refreshToken));
			params.add(new BasicNameValuePair(CLIENT_ID_KEY, clientId));
			params.add(new BasicNameValuePair(CLIENT_SECRET_KEY, clientSecret));
			params.add(new BasicNameValuePair(CALLBACK_URL_KEY, def.getRedirectUri()));
			params.add(new BasicNameValuePair(GRANT_TYPE_KEY, def.getGrantType()));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
    		request.setEntity(entity);
			HttpResponse response = client.execute(request);
	        in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	        StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = in.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
			returnValue = sb.toString();
			System.out.println(returnValue);
			JSONObject jsonObj = new JSONObject(returnValue);
			String token = jsonObj.getString("access_token");
			String expired = jsonObj.getString("expires_in");
			String refToken = jsonObj.getString("refresh_token");
			accessToken = new AccessToken(token, expired, refToken);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return accessToken;
	}
	
	/**
	 * In-app purchase by phone card 
	 * <p>
	 * 
	 *
	 * @param  code : phone card's code
	 * @param  serial : phone card's serial
	 * @param  vendor : the vendor name (vinaphone, mobifone, vietel,...)
	 * @param  endPoint : the Appota API endPoint URL
	 * @param  accessToken : the API's accessToken you get when authorized app
	 * @return  inappId : use to check if your transaction is success or not
	 */

	public String inappCard(String code, String serial, String vendor, String endPoint, String accessToken) {
		HttpsURLConnection conn = null;
		InputStream in;
		URL url = null;
		String returnValue = "";
		String topupId = null;
		String errorMsg = "";
		try {
			String params = URLEncoder.encode(CARD_CODE_KEY, "UTF-8") + "=" + URLEncoder.encode(String.valueOf(code), "UTF-8") + "&" + URLEncoder.encode(CARD_SERIAL_KEY, "UTF-8") + "=" + URLEncoder.encode(serial, "UTF-8") + "&" + URLEncoder.encode(VENDOR_KEY, "UTF-8") + "=" + URLEncoder.encode(vendor, "UTF-8");
			System.out.println(params);
			url = new URL(endPoint + "?access_token=" + accessToken);
			trustAllHosts();
			conn = (HttpsURLConnection) url.openConnection();
			conn.setHostnameVerifier(notVerify);
			conn.setDoOutput(true);
			OutputStreamWriter ow = new OutputStreamWriter(conn.getOutputStream());
			ow.write(params);
			ow.flush();
			in = new BufferedInputStream(conn.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
			returnValue = sb.toString();
			System.out.println(returnValue);
			JSONObject jsonObj = new JSONObject(returnValue);
			if(jsonObj.has("data")){
				JSONObject obj = jsonObj.getJSONObject("data");
				for(int i=0; i< obj.length(); i++){
					topupId = obj.getString("inapp_id");
				}
				return topupId;
			} else {
				errorMsg = jsonObj.getString("message");
				return errorMsg;
			}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return topupId;
	}
	
	/**
	 * In-app purchase by SMS
	 * <p>
	 * 
	 *
	 * @param  endPoint : the Appota API endPoint URL
	 * @param  accessToken : the API's accessToken you get when authorized app
	 * @return  SMSTopup : SMS options for user to pay. ex: (1000 VND -> 5 tym send to 8x01)
	 * and inappId   to check if user's transaction is success or not.
	 */

	public SMSTopup inappSMS(String endPoint, String accessToken) {
		HttpsURLConnection conn = null;
		InputStream in;
		URL url = null;
		String returnValue = "";
		List<SMSOption> smsOptions = new ArrayList<SMSOption>();
		SMSTopup sms = null;
		try {
			url = new URL(endPoint + "?access_token=" + accessToken);
			trustAllHosts();
			conn = (HttpsURLConnection) url.openConnection();
			conn.setHostnameVerifier(notVerify);
			in = new BufferedInputStream(conn.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
			returnValue = sb.toString();
			JSONObject jsonObj = new JSONObject(returnValue);
			System.out.println(returnValue);
			JSONObject obj = jsonObj.getJSONObject("data");
				
				String topupId = obj.getString("inapp_id");
				JSONArray smsArray = obj.getJSONArray("options");
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
				System.out.println("SMS size= " +smsOptions.size());
				sms = new SMSTopup();
				sms.setTopupId(topupId);
				sms.setSmsOptions(smsOptions);
			return sms;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sms;
	}
	
	/**
	 * In-app purchase by internet banking
	 * <p>
	 * 
	 *
	 * @param  amount : amount of money you need to pay
	 * @param  endPoint : the Appota API endPoint URL
	 * @param  accessToken : the API's accessToken you get when authorized app
	 * @return  BankTopup : redirect user to banking paygate using browser and inappId to check if user's transaction is success or not
	 */

	public BankTopup inappBank(double amount, String endPoint,
			String accessToken) {
		HttpsURLConnection conn = null;
		InputStream in;
		URL url = null;
		String returnValue = "";
		BankTopup bank = null;
		BankOption option = null;
		try {
			String params = URLEncoder.encode(AMOUNT_KEY, "UTF-8") + "=" + URLEncoder.encode(String.valueOf(amount), "UTF-8");
			url = new URL(endPoint + "?access_token=" + accessToken);
			trustAllHosts();
			conn = (HttpsURLConnection) url.openConnection();
			conn.setHostnameVerifier(notVerify);
			conn.setDoOutput(true);
			OutputStreamWriter ow = new OutputStreamWriter(conn.getOutputStream());
			ow.write(params);
			ow.flush();
			in = new BufferedInputStream(conn.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
			returnValue = sb.toString();
			JSONObject jsonObj = new JSONObject(returnValue);
			JSONObject obj = jsonObj.getJSONObject("data");
			for(int i=0; i< obj.length(); i++){
				
				String topupId = obj.getString("inapp_id");
				
				JSONArray smsArray = obj.getJSONArray("options");
				for(int j=0;j<smsArray.length();j++){
					JSONObject smsObj = smsArray.getJSONObject(j);
					
					String bankUrl = smsObj.getString("url");
					String bankName = smsObj.getString("bank");
					double payAmount = smsObj.getDouble("amount");
					String currency = smsObj.getString("currency");
					
					option = new BankOption();
					option.setAmount(payAmount);
					option.setCurrency(currency);
					option.setUrl(bankUrl);
					option.setBank(bankName);
					
					bank = new BankTopup();
					bank.setTopupId(topupId);
					bank.setOption(option);
				}
			}
			return bank;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bank;
	}
	
	/**
	 * In-app purchase by paypal
	 * <p>
	 * 
	 *
	 * @param  amount : amount of money you need to pay
	 * @param  endPoint : the Appota API endPoint URL
	 * @param  accessToken : the API's accessToken you get when authorized app
	 * @return  PaypalTopup : contains paypal information to pay, and inappId to check if user's transaction is success or not.
	 */

	public PaypalTopup inappPaypal(double amount, String endPoint, String accessToken) {
		HttpsURLConnection conn = null;
		InputStream in;
		URL url = null;
		String returnValue = "";
		PaypalTopup paypal = null;
		PaypalForm form = null;
		try {
			String params = URLEncoder.encode(AMOUNT_KEY, "UTF-8") + "=" + URLEncoder.encode(String.valueOf(amount), "UTF-8");
			url = new URL(endPoint + "?access_token=" + accessToken);
			trustAllHosts();
			conn = (HttpsURLConnection) url.openConnection();
			conn.setHostnameVerifier(notVerify);
			conn.setDoOutput(true);
			OutputStreamWriter ow = new OutputStreamWriter(conn.getOutputStream());
			ow.write(params);
			ow.flush();
			in = new BufferedInputStream(conn.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
			returnValue = sb.toString();
			JSONObject jsonObj = new JSONObject(returnValue);
			JSONObject obj = jsonObj.getJSONObject("data");
			for(int i=0; i< obj.length(); i++){
				
				String topupId = obj.getString("inapp_id");
				
				JSONArray paypalArray = obj.getJSONArray("options");
				for(int j=0;j<paypalArray.length();j++){
					
					JSONObject paypalObj = paypalArray.getJSONObject(j);
					
					for(int k=0;k<paypalObj.length();k++){
						JSONObject paypalForm = paypalObj.getJSONObject("form");
						String cmd = paypalForm.getString("cmd");
						int noShipping = paypalForm.getInt("no_shipping");
						int noNote = paypalForm.getInt("no_note");
						String currencyCode = paypalForm.getString("currency_code");
						String bn = paypalForm.getString("bn");
						String itemName = paypalForm.getString("item_name");
						String notifyUrl = paypalForm.getString("notify_url");
						String business = paypalForm.getString("business");
						
						form = new PaypalForm();
						form.setAmount(amount);
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
					paypal = new PaypalTopup();
					paypal.setAmount(amount);
					paypal.setCurrency(currency);
					paypal.setForm(form);
					paypal.setTopupId(topupId);
					paypal.setTym(tym);
				}
				
			}
			return paypal;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return paypal;
	}
	
	
	/**
	 * Check if user's transaction is success or not
	 * <p>
	 * 
	 *
	 * @param  topupId : topupId you get after doing transction
	 * @param  endPoint : the Appota API endPoint URL
	 * @param  accessToken : the API's accessToken you get when authorized app
	 * @return  isSuccess : true if transaction succeeded otherwise false if transaction failed.
	 */

	public InappChecker checkInapp(String inappId, String endPoint, String accessToken) {
		HttpsURLConnection conn = null;
		InputStream in;
		URL url = null;
		String returnValue = "";
		boolean isSuccess = false;
		InappChecker checker = null;
		try {
			String params = URLEncoder.encode(TOPUP_KEY, "UTF-8") + "=" + URLEncoder.encode(String.valueOf(inappId), "UTF-8");
			url = new URL(endPoint + "?access_token=" + accessToken);
			trustAllHosts();
			conn = (HttpsURLConnection) url.openConnection();
			conn.setHostnameVerifier(notVerify);
			conn.setDoOutput(true);
			OutputStreamWriter ow = new OutputStreamWriter(conn.getOutputStream());
			ow.write(params);
			ow.flush();
			in = new BufferedInputStream(conn.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
			returnValue = sb.toString();
			JSONObject jsonObj = new JSONObject(returnValue);
			System.out.println(returnValue);
			isSuccess = jsonObj.getBoolean("status");
			if(isSuccess){
				JSONObject dataObj = jsonObj.getJSONObject("data");
					String msg = dataObj.getString("message");
					checker = new InappChecker(isSuccess, msg);
				return checker;
			} else {
				String msg = jsonObj.getString("message");
				checker = new InappChecker(isSuccess, msg);
				return checker;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return checker;
	}
	
	// always verify the host - dont check for certificate
		final static HostnameVerifier notVerify = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		/**
		 * Trust every server - dont check for any certificate
		 */
		private static void trustAllHosts() {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
					// TODO Auto-generated method stub

				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
					// TODO Auto-generated method stub

				}

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return null;
				}

			} };

			// Install the all-trusting trust manager
			try {
				SSLContext sc = SSLContext.getInstance("TLS");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection
						.setDefaultSSLSocketFactory(sc.getSocketFactory());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}
