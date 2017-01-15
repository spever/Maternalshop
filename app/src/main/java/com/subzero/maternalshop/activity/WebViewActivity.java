package com.subzero.maternalshop.activity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lidroid.xutils.util.LogUtils;
import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.maternalshop.R;
/**修改签名
 * 包含：
 * 	昵称、性别、年龄、手机号、签名、评价
 * 启动者：
 * 	个人中心：UserManActivity
 * */
public class WebViewActivity extends BaseActivity
{
	private String url;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		initView();
	}
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		LogUtils.e("url = "+url);
		WebView webView = (WebView) findViewById(R.id.wv);
		webView.loadUrl(url);
		WebSettings webSettings = webView.getSettings();
		/*支持缩放*/
		webSettings.setSupportZoom(true);          
		/*启用内置缩放装置*/
		webSettings.setBuiltInZoomControls(true);  
		webSettings.setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient() {
		    public boolean shouldOverrideUrlLoading(WebView view, String url)
		      { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
		            view.loadUrl(url);
		            return true;
		      }
		});
	}
}
