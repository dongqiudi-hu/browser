package com.dqd.video;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BrowserActivity extends AppCompatActivity {
    private WebView mWebContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broswer);
        mWebContent = (WebView) findViewById(R.id.webview);
        mWebContent.setHorizontalScrollBarEnabled(true);
        mWebContent.setVerticalScrollBarEnabled(true);
        mWebContent.getSettings().setDomStorageEnabled(true);
        // mWebContent.getSettings().setDefaultTextEncodingName("UTF-8");
        final String USER_AGENT_STRING = "news/" + "1" + " "
                + mWebContent.getSettings().getUserAgentString() + " NewsApp/"
                + "1" + " Rong/2.0" + " NetType/";
        mWebContent.getSettings().setJavaScriptEnabled(true);
        mWebContent.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebContent.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebContent.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebContent.getSettings().setUserAgentString(USER_AGENT_STRING);
        mWebContent.getSettings().setSupportZoom(true);
        mWebContent.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebContent.getSettings().setUseWideViewPort(true);
        mWebContent.getSettings().setLoadWithOverviewMode(true);
        mWebContent.getSettings().setDatabaseEnabled(true);
        mWebContent.getSettings().setAllowFileAccess(true);
        mWebContent.getSettings().setGeolocationEnabled(true);
        mWebContent.setWebChromeClient(new WebChromeClient());
        mWebContent.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        mWebContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if ((url.startsWith("https://") || url.startsWith("http://"))) {
                    view.loadUrl(url);
                    return true;
                } else return true;
            }
        });
        if (Build.VERSION.SDK_INT >= 21) {
            mWebContent.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebContent.removeJavascriptInterface("accessibility");
        mWebContent.removeJavascriptInterface("accessibilityTraversal");
        mWebContent.removeJavascriptInterface("searchBoxJavaBridge_");
        mWebContent.loadUrl(getIntent().getStringExtra("url"));
    }

    public static Intent instance(Context context, String url) {
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra("url", url);
        return intent;
    }

}
