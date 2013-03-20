package com.appota.asdk.util;

public class Constant {

	public static final String RESPONSE_TYPE_KEY = "response_type";
	public static final String CLIENT_ID_KEY = "client_id";
	public static final String CLIENT_SECRET_KEY = "client_secret";
	public static final String SCOPE_KEY = "scope";
	public static final String STATE_KEY = "state";
	public static final String LANG_KEY = "lang";
	public static final String LANG_EN = "en";
	public static final String LANG_VI = "vi";
	public static final String CALLBACK_URL_KEY = "redirect_uri";
	public static final String ENCODE = "UTF-8";
	public static final String REQUEST_TOKEN_KEY = "request_token";
	public static final String GRANT_TYPE_KEY = "grant_type";
	public static final String REFRESH_TOKEN_KEY = "refresh_token";
	public static final String TRANSACTION_ID_KEY = "trans_success";
	public static final String TRANSACTION_ERROR_KEY = "trans_error";
	
	public static final int BACKOFF_MILLI_SECONDS = 2000;
	public static final int MAX_ATTEMPTS = 5;
	
	public static final int IN_APP_CARD_REQUEST_CODE = 100;
	public static final int TOPUP_CARD_REQUEST_CODE = 101;
	public static final int IN_APP_BANK_REQUEST_CODE = 102;
	public static final int TOPUP_BANK_REQUEST_CODE = 103;
	public static final int IN_APP_PAYPAL_REQUEST_CODE = 104;
	public static final int TOPUP_PAYPAL_REQUEST_CODE = 105;
	public static final int PAYMENT_TYPE_INAPP = 1;
	public static final int PAYMENT_TYPE_TOPUP = 0;
	public static final String PAYMENT_TYPE = "payment_type";
	public static final String NOTICE_URL_KEY = "notice_url";
	public static final String ACCESS_TOKEN_KEY = "access_token_";
	public static final String TARGET_KEY = "target_";
	public static final String SMART_LINK_URL_KEY = "smartlink_url";
	public static final String PAYPAL_DATA = "paypal_data";
	
	public static final String DEFAULT_REDIRECT_URI = "http://localhost";
	public static final String GRANT_TYPE = "authorization_code";
	public static final String STATE = "appota";
	public static final String RESPONSE_TYPE = "code";
	public static final String DEALER_SCOPE = "dealer";
	public static final String USER_INFO_SCOPE = "user.info";
	public static final String USER_EMAIL_SCOPE = "user.email";
	public static final String CONTENT_INFO_SCOPE = "content.info";
	public static final String CONTENT_COMMENT_SCOPE = "content.comment";
	public static final String USER_COMMENT_SCOPE = "user.comment";
	public static final String CONTENT_DOWNLOAD_SCOPE = "content.download";
	public static final String CONTENT_BUY_SCOPE = "content.buy";
	public static final String USER_CHARGE_SCOPE = "user.charge";
	public static final String USER_PAYMENT_SCOPE = "user.payment";
	public static final String INAPP_SCOPE = "inapp";
	
	public static final String USER_INFO_ENDPOINT = "https://api.appota.com/user/detail?access_token=";
	public static final String NON_USER_REQUEST_TOKEN_ENDPOINT = "https://id.appota.com/app/request_token?";
	public static final String NON_USER_ACCESS_TOKEN_ENDPOINT = "https://id.appota.com/app/access_token";
	
	public static final String REQUEST_TOKEN_ENDPOINT = "https://id.appota.com/oauth/request_token?";
	public static final String ACCESS_TOKEN_ENDPOINT = "https://id.appota.com/oauth/access_token";
	public static final String REFRESH_TOKEN_ENDPOINT = "https://id.appota.com/app/refresh_token";
	
	public static final String LOGIN_ENDPOINT = "https://api.appota.com/user/login?access_token=";
	public static final String CARD_TOPUP_ENDPOINT = "https://api.appota.com/payment/topup_card?access_token=";
	public static final String SMS_TOPUP_ENDPOINT = "https://api.appota.com/payment/topup_sms?access_token=";
	public static final String BANK_TOPUP_ENDPOINT = "https://api.appota.com/payment/topup_bank?access_token=";
	public static final String PAYPAL_TOPUP_ENDPOINT = "https://api.appota.com/payment/topup_paypal?access_token=";
	public static final String CHECK_TOPUP_ENDPOINT = "https://api.appota.com/payment/topup?access_token=";
	
	public static final String CARD_INAPP_ENDPOINT = "https://api.appota.com/payment/inapp_card?access_token=";
	public static final String SMS_INAPP_ENDPOINT = "https://api.appota.com/payment/inapp_sms?access_token=";
	public static final String BANK_INAPP_ENDPOINT = "https://api.appota.com/payment/inapp_bank?access_token=";
	public static final String PAYPAL_INAPP_ENDPOINT = "https://api.appota.com/payment/inapp_paypal?access_token=";
	public static final String CHECK_INAPP_TRANSACTION_ENDPOINT = "https://api.appota.com/payment/inapp?access_token=";
	
	public static final String INAPP_ITEM_LIST_ENDPOINT = "https://api.appota.com/item/get_list?access_token=";
	public static final String CHECK_BOUGHT_ITEM_ENDPOINT = "https://api.appota.com/item/is_bought?access_token=";
	public static final String BUY_INAPP_ITEM_ENDPOINT = "https://api.appota.com/item/buy?access_token=";
	public static final String USER_BOUGHT_LIST_ENDPOINT = "https://api.appota.com/item/bought?access_token=";
}
