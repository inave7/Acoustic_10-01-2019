package com.belaku.media;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by naveenprakash on 09/02/19.
 */

public class MyWebViewClient extends WebViewClient {

    public MyWebViewClient() {
        super();
        //start anything you need to
    }

    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        //Do something to the urls, views, etc.
    }
}
