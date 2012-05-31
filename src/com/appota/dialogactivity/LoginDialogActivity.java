package com.appota.dialogactivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.appota.model.AccessToken;
import com.appota.model.DefaultAppotaAuthorize;
import com.appota.payment.AppotaClient;

/**
 * Login Activity
 * <p>
 */

public class LoginDialogActivity extends Activity {


	private WebView webView;
	private ProgressDialog progressDialog;
	private String requestToken;
	private Intent intent;
	private AccessToken accessToken;
	private AppotaClient client;
	private String CLIENT_KEY;// = "247e8a768124f028af18270c026a483f04f7ec88d";
	private String CLIENT_SECRET;// = "0f529a2a4971c840b3c38a4c043197d604f7ec88d";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Display display = getWindowManager().getDefaultDisplay();
		LinearLayout layout = new LinearLayout(this);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(params);
		webView = new WebView(this);
		LayoutParams webParams = new LayoutParams(display.getWidth()-10, display.getHeight()-10);
		webView.setLayoutParams(webParams);
		layout.addView(webView);
		setContentView(layout);
		client = new AppotaClient();
		String requestTokenUrl = getIntent().getStringExtra("REQ_URL");
		CLIENT_KEY = getIntent().getStringExtra("CLIENT_KEY");
		CLIENT_SECRET = getIntent().getStringExtra("CLIENT_SECRET");
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setLightTouchEnabled(true);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		
		progressDialog = ProgressDialog.show(this, "Loading...", "Please wait...", true, true);
		
		webView.setWebChromeClient(new WebChromeClient());
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				System.out.println("Finished loading url " + url);
				if(progressDialog.isShowing()){
					progressDialog.dismiss();
				}
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				if(failingUrl.startsWith("http://localhost")){
					String[] str = failingUrl.split("\\?");
					String[] req = str[1].split("\\=");
					requestToken = req[1];
					accessToken = client.getAccessToken(requestToken, CLIENT_KEY, CLIENT_SECRET, "http://localhost", new DefaultAppotaAuthorize());
					intent = getIntent();
					Bundle b = new Bundle();
					b.putSerializable("ACCESS_TOKEN", accessToken);
					intent.putExtras(b);
					setResult(RESULT_OK, intent);
					finish();
				}
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				// TODO Auto-generated method stub
				handler.proceed();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return true;
			}
			
		});
		
		webView.loadUrl(requestTokenUrl);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	
}
