package com.appota.asdk;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.appota.asdk.core.AppMsg;
import com.appota.asdk.util.Constant;

import android.app.Activity;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;

public class LoginActivity extends Activity {

	private FrameLayout webHolder;
	private WebView webView;
	private ProgressBar progressBar;
	private String url;
	private String redirectURI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createLayout();
		drawUI();
	}
	
	private void createLayout(){
		url = getIntent().getStringExtra(Constant.REQUEST_TOKEN_URL_KEY);
		redirectURI = getIntent().getStringExtra(Constant.REDIRECT_URI_KEY);
		webHolder = new FrameLayout(this);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		webHolder.setLayoutParams(params);
		webHolder.setBackgroundColor(Color.WHITE);
		
		progressBar = new ProgressBar(this);
		FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		progressParams.gravity = Gravity.CENTER;
		progressBar.setLayoutParams(progressParams);
		
		webView = new WebView(this);
		FrameLayout.LayoutParams webParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		webView.setLayoutParams(webParams);
		
		webHolder.addView(progressBar);
		webHolder.addView(webView);
		setContentView(webHolder);
		String pattern = "^[a-z]+:{1}/{2}[a-z]+$";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(redirectURI);
		if(!matcher.matches()){
			AppMsg.makeText(LoginActivity.this, R.string.redirect_uri_not_valid, AppMsg.STYLE_ALERT, null).show();
			return;
		}
	}

	@SuppressWarnings("deprecation")
	private void drawUI(){
		if (webView != null) {
			WebSettings settings = webView.getSettings();
			webView.setVisibility(View.GONE);
			webView.getSettings().setSupportZoom(true);
			webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
			webView.setScrollbarFadingEnabled(true);
			webView.getSettings().setLoadsImagesAutomatically(true);
			webView.requestFocus(View.FOCUS_DOWN);
			webView.setOnTouchListener(new View.OnTouchListener() {
		        @Override
		        public boolean onTouch(View v, MotionEvent event) {
		            switch (event.getAction()) {
		                case MotionEvent.ACTION_DOWN:
		                case MotionEvent.ACTION_UP:
		                    if (!v.hasFocus()) {
		                        v.requestFocus();
		                    }
		                    break;
		            }
		            return false;
		        }
		    });
			settings.setJavaScriptEnabled(true);
			settings.setPluginsEnabled(true);
	        settings.setAllowFileAccess(true);
			settings.setJavaScriptCanOpenWindowsAutomatically(true);
			settings.setSupportMultipleWindows(true);
			
			webView.setWebViewClient(new AppStoreVnViewClient());
			webView.loadUrl(url);
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		webView.restoreState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		webView.saveState(outState);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		webView.clearCache(true);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	private class AppStoreVnViewClient extends WebViewClient {
		
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			String host = redirectURI.substring(0, redirectURI.indexOf(":"));
			if(url.startsWith(host)){
				String requestToken = url.substring(url.lastIndexOf("=") +1, url.length());
				setResult(RESULT_OK, getIntent().putExtra(Constant.REQUEST_TOKEN_KEY, requestToken));
				finish();
				return true;
			} else {
				webView.loadUrl(url);
				return super.shouldOverrideUrlLoading(view, url);
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			progressBar.setVisibility(View.GONE);
			webView.setVisibility(View.VISIBLE);
		}
	}

}
