package com.client.tok.ui.web;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.client.tok.BuildConfig;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.pagejump.IntentConstants;

public class WebViewActivity extends BaseCommonTitleActivity {
    private WebView mWebView;
    private String mUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mWebView = $(R.id.id_web_view);
        initSetting();
        readData();
        mWebView.loadUrl(mUrl);
    }

    private void readData() {
        mUrl = getIntent().getStringExtra(IntentConstants.WEB_PATH);
    }

    @Override
    public int getBackIcon() {
        return R.drawable.ic_action_close;
    }

    private void initSetting() {
        WebSettings settings = mWebView.getSettings();
        settings.setAppCacheEnabled(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        String userAgent = settings.getUserAgentString();
        settings.setUserAgentString(
            userAgent + " TokAndroid/" + String.valueOf(BuildConfig.VERSION_NAME));
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDatabaseEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
    }
}
