package com.subzero.common.utils;

import android.webkit.WebSettings;
import android.webkit.WebView;

public class WvUtil
{
	public static String encoding = "UTF-8";
	public static String mimeType = "text/html";
	public static String baseUrl = " ";
	public static String historyUrl = " ";
	/**WebView设置单行显示，属性*/
	public static void setLayoutAlgorithm(WebView webView)
	{
		/* 设置是否显示水平滚动条*/
		webView.setHorizontalScrollBarEnabled(false);
		/*设置是否显示垂直滚动条*/
		webView.setVerticalScrollBarEnabled(false); 
		WebSettings webSettings = webView.getSettings();
		/*解决：文字过多，超出手机屏（必须有标签的）*/
		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		/*解决：图片过大，超出手机屏*/
		webSettings.setUseWideViewPort(true); 
	}
}
