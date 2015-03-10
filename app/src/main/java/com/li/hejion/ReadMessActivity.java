package com.li.hejion;




import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import java.io.File;

public class ReadMessActivity extends Activity {
    /** Called when the activity is first created. */

    public static final String ua = "Android";
	private WebView webView;
	Dialog progressDialog;
    ImageButton backBtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_readmess);
        
        //Mozilla/5.0 (Linux; Android 4.4.4; LG-LU6200 Build/KTU84P) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 Mobile Safari/537.36
//        webView =new WebView(this);
        webView=(WebView)findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString(ua);
        webView.getSettings().setLoadWithOverviewMode(true);

        Log.e("ua",webView.getSettings().getUserAgentString());
        backBtn=(ImageButton)findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
        Intent intent=this.getIntent();
//        Bundle bundle=intent.getExtras();
//        String url=bundle.getString("url");
        String url=intent.getExtras().getString("url");
        webView.loadUrl(url);



        //SmartConfig.getWebUserAgent(ReadMessActivity.this


        progressDialog = ProgressDialog.show(ReadMessActivity.this, null,"正在努力加载数据,请稍候...");

        progressDialog.setCancelable(true);

        webView.setWebViewClient(new WebViewClient()
        {



			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				progressDialog.cancel();
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
				progressDialog.show();
			}



        });
    }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK&&webView.canGoBack())
		{
			webView.goBack();
			return true;
		}
		else {
			return super.onKeyDown(keyCode, event);
			}
		}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (webView != null) {
			webView.clearHistory();
			webView.removeAllViewsInLayout();
			webView.clearDisappearingChildren();
			webView.clearFocus();
			webView.clearView();
			webView.destroy();

            // 在退出应用的时候加上如下代码清除浏览网页的缓存
            ReadMessActivity.this.getApplicationContext().deleteDatabase(
                    "webview.db");
            ReadMessActivity.this.getApplicationContext().deleteDatabase(
                    "webviewCache.db");
		}
	}
	
		
	}