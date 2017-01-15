package com.subzero.userman;
import net.minidev.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.subzero.common.helpers.sharesdk.OAuthUserBean;
import com.subzero.common.helpers.sharesdk.ShareSDKHelper;
import com.subzero.common.helpers.sharesdk.ShareSDKHelper.OnListenerType;
import com.subzero.common.helpers.sharesdk.ShareSDKHelper.OnOAuthLoginListener;
import com.subzero.common.listener.CommonOnKeyListener;
import com.subzero.common.listener.CommonOnKeyListener.OnKeyType;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.KeyPadUtil;
import com.subzero.common.utils.StringUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.common.view.CheckImageView;
import com.subzero.common.view.CheckTextView;
import com.subzero.common.view.CheckTextView.OnCheckedClickListener;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.activity.IndexActivity;
import com.subzero.maternalshop.activity.MainActivity;
import com.subzero.maternalshop.activity.WelcomeActivity;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
/**
 * 登录页面
 * 启动项：
 * 	欢迎页面：WelcomeActivity
 * 	注册页面：RegisterActivity
 * 	忘记密码：ForgetPwdActivity
 * */
public class LoginActivity extends BaseActivity
{
	private EditText etPhoneNum;
	private EditText etPwd;
	private String phoneNum;
	private String pwd;
	/**模块名， 标记是从哪里进入 LoginActivity的*/
	private String moduleName;
	private OAuthUserBean oAuthUserBean;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		dialogLoading.setOnKeyListener(new CommonOnKeyListener(activity,OnKeyType.dismissNotKillActivity));
		initView();
		getIntentData();
	}
	@Override  
	protected void onNewIntent(Intent intent) {        
		super.onNewIntent(intent);  
		setIntent(intent); 
		//here we can use getIntent() to get the extra data.
		getIntentData();
	}
	private void getIntentData()
	{
		Intent intent = getIntent();
		moduleName = intent.getStringExtra(App.keyModuleName);
		phoneNum = intent.getStringExtra("phoneNum");
		pwd = intent.getStringExtra("pwd");
		if(TextUtils.isEmpty(phoneNum)){
			phoneNum = User.getPhoneNum(context);
		}
		if(TextUtils.isEmpty(pwd)){
			pwd = User.getPwd(context);
		}
		LogUtils.e("phoneNum = "+phoneNum);
		/*当前，有默认账号*/
		if(User.getIsOnline(context) || (!TextUtils.isEmpty(phoneNum))){
			String loginType = User.getLoginType(context);
			if(App.loginTypeOAuth.equalsIgnoreCase(loginType)){
				oAuthUserBean = new OAuthUserBean();
				oAuthUserBean.oAuthid = User.getOAuthid(context);
				oAuthUserBean.platformName = User.getPlatformName(context);
				oAuthUserBean.portraitUrl = User.getPortraitUrl(context);
				oAuthUserBean.nickName = User.getNickName(context);
				requestlogin(App.loginTypeOAuth);
			}else{
				requestlogin(App.loginTypeNormal);
			}
		}else{
			findViewById(R.id.iv_loading).setVisibility(View.GONE);
		}
	}
	@Override
	public void initView()
	{
		etPhoneNum = ((EditText)findViewById(R.id.et_phone_num));
		etPwd = ((EditText)findViewById(R.id.et_pwd));
		findViewById(R.id.bt_login).setOnClickListener(new MyOnClickListener());
		((CheckTextView)findViewById(R.id.ctv_forget_pwd)).setOnCheckedClickListener(new MyOnCheckedClickListener());
		((CheckTextView)findViewById(R.id.ctv_register)).setOnCheckedClickListener(new MyOnCheckedClickListener());
		((CheckImageView)findViewById(R.id.civ_login_weichat)).setOnCheckedClickListener(new ImageOnCheckedClickListener());
		((CheckImageView)findViewById(R.id.civ_login_qq)).setOnCheckedClickListener(new ImageOnCheckedClickListener());
		((CheckImageView)findViewById(R.id.civ_login_sina)).setOnCheckedClickListener(new ImageOnCheckedClickListener());
	}
	private final class ImageOnCheckedClickListener implements CheckImageView.OnCheckedClickListener
	{
		@Override
		public void onCheckClick(View v)
		{
			String platformName = null;
			if(R.id.civ_login_weichat == v.getId()){
				platformName =  Wechat.NAME;
			}else if(R.id.civ_login_qq == v.getId()){
				platformName = QQ.NAME;
			}else if(R.id.civ_login_sina == v.getId()){
				platformName = SinaWeibo.NAME;
			}
			new ShareSDKHelper(context, new MyOnOAuthLoginListener()).startOAuthLogin(platformName);
		}
	}
	private final class MyOnOAuthLoginListener implements OnOAuthLoginListener
	{
		@Override
		public void onStart(){
			dialogShow(dialogLoading, context);
		}
		@Override
		public void onFinish(OnListenerType onListenerType, String error, OAuthUserBean oAuthUserBean){
			if((OnListenerType.success == onListenerType) && (oAuthUserBean!=null)){
				LoginActivity.this.oAuthUserBean = oAuthUserBean;
				requestlogin(App.loginTypeOAuth);
			}
			dialogDismiss(dialogLoading);
		}
	}
	private final class MyOnCheckedClickListener implements OnCheckedClickListener
	{
		@Override
		public void onCheckClick(View v)
		{
			Intent intent = null;
			if(R.id.ctv_forget_pwd == v.getId()){
				intent = new Intent(context, ForgetPwdActivity.class);
				phoneNum = etPhoneNum.getText().toString();
				intent.putExtra("phoneNum", phoneNum);
			}
			else if(R.id.ctv_register == v.getId()){
				intent = new Intent(context, RegisterActivity.class);
			}
			startActivity(intent);
		}
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			phoneNum = ((EditText)findViewById(R.id.et_phone_num)).getText().toString();
			pwd = etPwd.getText().toString();
			if(R.id.bt_login == v.getId()){
				if(!StringUtil.isPhoneNum(phoneNum)){
					ToastUtil.shortAtCenter(context, "请正确输入手机号码！");
					return ;
				}
				if(TextUtils.isEmpty(pwd)){
					ToastUtil.shortAtCenter(context, "请正确输入密码！");
					return ;
				}
				KeyPadUtil.closeKeybord((EditText) findViewById(R.id.et_pwd), context);
				requestlogin(App.loginTypeNormal);
			}
		}
	}
	/**@param oAuthUserBean 
	 * @category 请求登录
	 * */
	private void requestlogin(String loginType)
	{
		//LogUtils.e("请求登录 phoneNum = "+phoneNum);
		findViewById(R.id.iv_loading).setVisibility(View.VISIBLE);
		String url = "";
		RequestParams params = new RequestParams(XUtil.charset);
		if(App.loginTypeNormal.equalsIgnoreCase(loginType)){
			params.addQueryStringParameter("tel", phoneNum);
			params.addQueryStringParameter("pwd", pwd);
			url = Url.loginApi;
		}else if(App.loginTypeOAuth.equalsIgnoreCase(loginType)){
			//  openid=&nice_name=&headimg=&type=）
			String type = "";
			params.addQueryStringParameter("openid", oAuthUserBean.oAuthid);
			User.setOAuthid(context, oAuthUserBean.oAuthid);
			params.addQueryStringParameter("nice_name", oAuthUserBean.nickName);
			User.setNickName(context, oAuthUserBean.nickName);
			if(oAuthUserBean.portraitUrl!=null){
				params.addQueryStringParameter("headimg", oAuthUserBean.portraitUrl);
				User.setPortraitUrl(context, oAuthUserBean.portraitUrl);
			}
			if(oAuthUserBean.platformName == Wechat.NAME){
				type = "weixin";
			}else if(oAuthUserBean.platformName == SinaWeibo.NAME){
				type = "weibo";
			}else if(oAuthUserBean.platformName == QQ.NAME){
				type = "qq";
			}
			User.setPlatformName(context, oAuthUserBean.platformName);
			User.setLoginType(context, loginType);
			params.addQueryStringParameter("type", type);
			url = Url.OAuthloginApi;
		}
		httpUtils.send(HttpMethod.GET, url, params , new LoginCallBack(loginType));
	}
	/**xUtils.HttpUtils的回调
	 * */
	private final class LoginCallBack extends RequestCallBack<String>
	{
		private String loginType;
		public LoginCallBack(String loginType) {
			this.loginType = loginType;
		}
		@Override
		public void onStart()
		{
			super.onStart();
			findViewById(R.id.iv_loading).setVisibility(View.VISIBLE);
		}
		@Override
		public void onFailure(HttpException error, String msg)
		{
			LogUtils.e("onFailure = "+XUtil.getErrorInfo(error));
			ToastUtil.longAtCenterInThread(context, XUtil.getErrorInfo(error));
			findViewById(R.id.iv_loading).setVisibility(View.GONE);
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			String result = info.result;
			JSONObject jsonObjectStatus = JsonSmartUtil.getJsonObject(result, "status");
			if("400".equalsIgnoreCase(JsonSmartUtil.getString(jsonObjectStatus, "code"))){
				ToastUtil.longAtCenterInThread(context, jsonObjectStatus.getAsString("error_desc"));
				User.logout(context);
				ViewUtil.setText2EditText(etPhoneNum, phoneNum);
				ViewUtil.setText2EditText(etPwd, pwd);
				findViewById(R.id.iv_loading).setVisibility(View.GONE);
				LogUtils.e("result = "+result);
				return;
			}
			LogUtils.e("result = "+result);
			if(App.loginTypeNormal.equalsIgnoreCase(loginType)){
				LogUtils.e("通过 手机号码  登录");
				User.setLoginType(context, loginType);
			}else if(App.loginTypeOAuth.equalsIgnoreCase(loginType)){
				LogUtils.e("通过 带三方  登录");
				User.setLoginType(context, loginType);
			}
			JSONObject jsonObjectData = JsonSmartUtil.getJsonObject(result, "data");
			JSONObject jsonObjectSession = (JSONObject) JsonSmartUtil.getJsonObject(jsonObjectData, ("session"));
			String uid = JsonSmartUtil.getString(jsonObjectSession,"uid");
			String sid = JsonSmartUtil.getString(jsonObjectSession,"sid");
			User.setIsOnline(context, true);
			User.setUserId(context, uid);
			User.setSID(context, sid);
			LogUtils.e("uid = "+uid+" sid = "+sid);
			User.setPhoneNum(context, phoneNum);
			User.setPwd(context, pwd);
			/*如果是从  引导页、注册页、Main页面、Index页面、忘记密码，登录成功之后，去IndexActivity
			 * 其他情况 从哪里来，再回到哪里去*/
			LogUtils.e("从某个模块过来的 = "+moduleName);
			if((moduleName!=null) 
					&& (!WelcomeActivity.class.getSimpleName().equalsIgnoreCase(moduleName)) 
					&& (!RegisterActivity.class.getSimpleName().equalsIgnoreCase(moduleName)) 
					&& (!ForgetPwdActivity.class.getSimpleName().equalsIgnoreCase(moduleName)) 
					&& (!IndexActivity.class.getSimpleName().equalsIgnoreCase(moduleName)) 
					&& (!MainActivity.class.getSimpleName().equalsIgnoreCase(moduleName))){
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				finish();
				return ;
			}
			startActivity(new Intent(context, IndexActivity.class));
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			finish();
		}
	}
}
