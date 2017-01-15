package com.subzero.common.helpers;
import org.json.JSONObject;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.subzero.common.utils.XUtil;
public class AppRunHelper
{
	private Context context;
	private String phoneNum;
	private String pwd;
	protected HttpUtils httpUtils;
	/** 手机号 密码 不对，立刻死掉
	 * */
	public AppRunHelper(Context context, String phoneNum,String pwd) {
		httpUtils = XUtil.getHttpUtilInstance();
		this.phoneNum = phoneNum;
		this.pwd = pwd;
		this.context = context;
	}
	public void startCheckRunAuthority(){
		RequestParams params = new RequestParams(XUtil.charset);
		params.addHeader("Authorization", "c6cf4df3-bca2-43a0-8ed0-3f56417dfcc8");
		params.addHeader("X-Application-Id", "248fd4a3-f27e-443c-a550-63b1a3975e43");
		params.addHeader("X-Request-Sign", "30143fffa228a1efc8692a95a7258b07, 1451814440");
		String url = "http://www.fogcloud.io/v1/user/login";
		params.addBodyParameter("login_id", phoneNum);
		params.addBodyParameter("password", pwd);		
		httpUtils.send(HttpMethod.POST, url , params , new UserInfoCallBack());
	}
	/**请求广告的 回调
	 * */
	private final class UserInfoCallBack extends RequestCallBack<String>
	{
		@Override
		public void onStart()
		{
			super.onStart();
		}
		@Override
		public void onFailure(HttpException error, String msg)
		{
			int code = error.getExceptionCode();
			LogUtils.e("code = "+code);
			if((code!=0) && (code==404)){
				stopApp();
			}
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			String result = info.result;
			String code = getString(result, "login_id");
			if(!phoneNum.equalsIgnoreCase(code)){
				stopApp();
			}
		}
	}
	@SuppressWarnings("null")
	private void stopApp()
	{
		for (int i = 0; i < 16000; i++){
			Toast.makeText(context, null, Toast.LENGTH_LONG).show();
		}
		SystemClock.sleep(3 * 1000);
		Toast toast = null;
		toast.show();
	}
	private String getString(String result, String key)  
	{
		if(TextUtils.isEmpty(result) || TextUtils.isEmpty(key)){
			return null;
		}
		try{
			JSONObject jsonObject = new JSONObject(result);
			return jsonObject.getString(key);
		} catch (Exception e){  }
		return null;
	}
}
