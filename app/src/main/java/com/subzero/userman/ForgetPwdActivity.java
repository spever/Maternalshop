package com.subzero.userman;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.common.utils.StringUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.common.view.CheckTextView;
import com.subzero.common.view.CheckTextView.OnCheckedClickListener;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.util.JsonCommonUtil;
/**
 * 找回密码
 * 启动者：
 * 	注册登录：LoginActivity
 * */
public class ForgetPwdActivity extends BaseActivity
{
	private EditText etPhoneNum;
	private EditText etPwd;
	private EditText etCode;
	private CheckTextView ctvGetCode;
	private String phoneNum;
	private String pwd;
	private String code;
	/**已经 请求得到 验证码*/
	private boolean hasCode;
	/**正在获取短信*/
	protected boolean isLoadCode;
	private boolean canLoop;
	private int count = 60;
	private MyEventHandler eventHandler;
	private SmsHandler smsHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_pwd);
		phoneNum = getIntent().getStringExtra("phoneNum");
		hasCode = false;
		isLoadCode = false;
		canLoop = true;
		initSMS();
		initView();
	}
	@Override
	public void initView()
	{
		etPhoneNum = (EditText) findViewById(R.id.et_phone_num);
		etCode = (EditText) findViewById(R.id.et_code);
		etPwd = (EditText) findViewById(R.id.et_pwd);
		ViewUtil.setText2EditText(etPhoneNum, phoneNum);
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		findViewById(R.id.bt_submit).setOnClickListener(new MyOnClickListener());
		ctvGetCode = (CheckTextView) findViewById(R.id.ctv_get_code);
		ctvGetCode.setOnCheckedClickListener(new MyOnCheckedClickListener());
	}
	private final class MyOnCheckedClickListener implements OnCheckedClickListener
	{
		@Override
		public void onCheckClick(View v)
		{
			phoneNum = etPhoneNum.getText().toString();
			if(R.id.ctv_get_code == v.getId()){
				if(!StringUtil.isPhoneNum(phoneNum)){
					ToastUtil.shortAtTop(context, "手机号码，非法！");
					return;
				}
				hasCode = true;
				if(isLoadCode){
					ToastUtil.shortAtCenter(context, count+"秒之后再试");
					return;
				}
				/*向ShareSDK服务器，申请获取验证码*/
				SMSSDK.getVerificationCode("+86",phoneNum);
			}
		}
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(R.id.bt_submit == v.getId()){
				code = etCode.getText().toString(); 
				phoneNum = etPhoneNum.getText().toString();
				pwd = etPwd.getText().toString();
				ToastUtil.shortAtCenter(context, "验证码通过");
				if(!hasCode) {
					ToastUtil.shortAtCenter(context, "先获取验证码！");
					return ;
				}
				if(TextUtils.isEmpty(phoneNum)){
					ToastUtil.shortAtCenter(context, "手机号码为空");
					return;
				}
				if(TextUtils.isEmpty(code)){
					ToastUtil.shortAtCenter(context, "验证码为空");
					return;
				}
				if(TextUtils.isEmpty(pwd)){
					ToastUtil.shortAtCenter(context, "密码为空");
					return;
				}
				dialogLoading.show();
				SMSSDK.submitVerificationCode("86", phoneNum,code);	
			}
		}
	}

	/**ShareSDK的回调*/
	private final class MyEventHandler extends EventHandler 
	{
		@Override
		public void beforeEvent(int event, Object data)
		{
			super.beforeEvent(event, data);
		}
		@Override
		public void afterEvent(int event, int result, Object data)
		{
			super.afterEvent(event, result, data);
			Message msg = new Message();
			msg.arg1 = event;
			msg.arg2 = result;
			msg.obj = data;
			smsHandler.sendMessage(msg);
		}
	}
	@SuppressLint("HandlerLeak")
	private final class SmsHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg) 
		{
			super.handleMessage(msg);
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			LogUtils.e("result = "+result);
			if (result == SMSSDK.RESULT_COMPLETE) 
			{
				/*验证通过*/
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
					//TODO 向自己的服务器 申请更新密码
					ToastUtil.shortAtCenter(context, "验证码通过");
					RequestParams params = new RequestParams(XUtil.charset);
					params.addBodyParameter("mobile", phoneNum);
					params.addBodyParameter("pwd", pwd);
					LogUtils.e("mobile = "+phoneNum+" pwd = "+pwd);
					//?url=/user/forget_password&format=json
					httpUtils.send(HttpMethod.POST, Url.forgetPwdApi, params , new ForgetPwdCallBack());
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					ToastUtil.shortAtCenter(context, "验证码已经发送");
					new TimeThread().start();
				}else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表
					//Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
				}
			} else {
				ToastUtil.shortAtCenter(context, "验证码错误");
				dialogLoading.dismiss();
				((Throwable) data).printStackTrace();
				LogUtils.e("有异常："+data);
			}
		}
	}
	/**xUtils.HttpUtils的回调
	 * */
	private final class ForgetPwdCallBack extends RequestCallBack<String>
	{
		@Override
		public void onStart()
		{
			super.onStart();
			dialogShow(dialogLoading, context);
		}
		@Override
		public void onFailure(HttpException error, String msg)
		{
			dialogDismiss(dialogLoading);
			LogUtils.e("onFailure = "+XUtil.getErrorInfo(error));
			ToastUtil.longAtCenterInThread(context, XUtil.getErrorInfo(error));
			dialogLoading.dismiss();
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			dialogDismiss(dialogLoading);
			String result = info.result;
			LogUtils.e("result = "+result);
			if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ToastUtil.shortAtCenterInThread(context, JsonCommonUtil.getCommonErrorInfo(result));
				return ;
			}
			ToastUtil.shortAtCenterInThread(context, "修改成功");
			Intent intent = new Intent(context, LoginActivity.class);
			intent.putExtra("phoneNum", phoneNum);
			intent.putExtra("pwd", pwd);
			intent.putExtra(App.keyModuleName, ForgetPwdActivity.class.getSimpleName());
			startActivity(intent);
			finish();
		}
	}
	private final class TimeThread extends Thread
	{
		@Override
		public void run()
		{
			super.run();
			count = 60;
			while(count>0 && canLoop)
			{
				runOnUiThread(new Runnable() {
					@Override
					public void run()
					{
						isLoadCode = true;
						ctvGetCode.setText("剩余"+count+"秒");
					}
				});
				count -- ;
				SystemClock.sleep(1000);
			}
			isLoadCode = false;
			runOnUiThread(new Runnable() {
				@Override
				public void run(){
					ctvGetCode.setText("获取验证码");
				}
			});
		}
	}
	private void initSMS()
	{
		eventHandler = new MyEventHandler();
		smsHandler = new SmsHandler();
		SMSSDK.registerEventHandler(eventHandler);
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		SMSSDK.unregisterEventHandler(eventHandler);
		canLoop = false;
	}
}
