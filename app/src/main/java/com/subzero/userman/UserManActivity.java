package com.subzero.userman;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import subzero.nereo.MultiImageSelectorActivity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.squareup.picasso.Picasso;
import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.common.utils.ActivityUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.common.view.CircleImageView;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.activity.TextActivity;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.CallBackType;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.util.JsonCommonUtil;
import com.subzero.userman.alteruserinfo.AlterUserInfoActivity;
import com.subzero.userman.alteruserinfo.PhoneNumActivity;
/**个人中心
 * 启动者：
 * 	我的： MineFragment
 * 启动项：
 * 	修改手机号码：PhoneNumActivity
 * 	修改签名：SignActivity
 * 	年龄 昵称：AlterUserInfoActivity
 * 	选择头像：MultiImageSelectorActivity
 * 	重新登录：LoginActivity
 * 	*/
public class UserManActivity extends BaseActivity
{
	private boolean mustKillAllActivity;
	private CircleImageView circleImageView;
	/**修改 头像*/
	public static final int requestCodePhoto = 100;
	/**修改昵称*/
	public static final int requestCodeNickName = 101;
	/**修该年龄*/
	public static final int requestCodeAge = 102;
	/**修改签名*/
	public static final int requestCodeSign = 103;
	/**修改手机号码*/
	public static final int requestCodePhoneNum = 104;
	/**对话框：选择性别*/
	private Dialog dialogGender;
	/**头像 的地址*/
	private String userLogoUrl;
	/**昵称*/
	private String nickName;
	/**年龄*/
	private String age;
	/**签名*/
	private String sign;
	/**手机号*/
	private String phoneNum;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_man);
		mustKillAllActivity = false;
		userLogoUrl = getIntent().getStringExtra("userLogoUrl");
		initView();
		initDialogGender();
		diaplayUserInfo();
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		findViewById(R.id.bt_logout).setOnClickListener(new MyOnClickListener());
		circleImageView = (CircleImageView) findViewById(R.id.iv_user_logo);
		findViewById(R.id.layout_user_logo).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.layout_user_man_nickname).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_nickname_value).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.layout_user_man_age).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_age_value).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.layout_user_man_sign).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_sign_value).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.layout_user_man_gender).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_gender_value).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.layout_user_man_phone_num).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_phone_num_value).setOnClickListener(new MyOnClickListener());
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = null;
			if(R.id.bt_logout == v.getId()){
				/*退出登录*/
				RequestParams params = new RequestParams(XUtil.charset);
				httpUtils.send(HttpMethod.GET, Url.logoutApi, params , new UserInfoAlterCallBack(CallBackType.logout));
			}else if(R.id.layout_user_logo == v.getId()){
				/*选择头像*/
				intent = new Intent(context, MultiImageSelectorActivity.class);
				intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
				intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
				intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
				startActivityForResult(intent , requestCodePhoto);
			}else if((R.id.layout_user_man_nickname == v.getId())  || R.id.tv_nickname_value == v.getId()){
				/*修改昵称*/
				intent = new Intent(context, AlterUserInfoActivity.class);
				intent.putExtra("alterType", App.alterNickName);
				startActivityForResult(intent, requestCodeNickName);
			}else if((R.id.layout_user_man_age == v.getId())  || R.id.tv_age_value == v.getId()){
				/*修改年龄*/
				intent = new Intent(context, AlterUserInfoActivity.class);
				intent.putExtra("alterType", App.alterAge);
				startActivityForResult(intent, requestCodeAge);
			}else if((R.id.layout_user_man_sign == v.getId()) ||R.id.tv_sign_value == v.getId()){
				/*修改签名*/
				intent = new Intent(context, TextActivity.class);
				startActivityForResult(intent, requestCodeSign);
			}else if((R.id.layout_user_man_gender == v.getId()) || (R.id.tv_gender_value == v.getId())){
				/*修改性别*/
				dialogGender.show();
			}else if((R.id.layout_user_man_phone_num==v.getId()) || (R.id.tv_phone_num_value==v.getId())){
				/*修改手机号*/
				intent = new Intent(context, PhoneNumActivity.class);
				startActivityForResult(intent,requestCodePhoneNum);
			}
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(data == null || data.getExtras() == null){
			return ;
		}
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("format", "json");
		params.addQueryStringParameter("uid", User.getUserId(context));
		params.addQueryStringParameter("sid", User.getSID(context));
		if(requestCode == requestCodePhoto){
			ArrayList<String> listUri = data.getExtras().getStringArrayList(MultiImageSelectorActivity.EXTRA_RESULT);
			if(listUri==null || listUri.size()<=0){
				return ;
			}
			File file = new File(listUri.get(0));
			Options opts = new Options();
			opts.inSampleSize = 4;
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
			circleImageView.setImageBitmap(bitmap);
			params.addBodyParameter("user_headimg", file);
		}else if(requestCode == requestCodeNickName){
			nickName = data.getStringExtra("nickName");
			LogUtils.e("修改 user_nice_name = "+nickName);
			params.addBodyParameter("user_nice_name", nickName);
		}else if(requestCode == requestCodeAge){
			age = data.getStringExtra("age");
			LogUtils.e("修改 user_age = "+age);
			params.addBodyParameter("user_age", age);
		}else if(requestCode == requestCodeSign){
			sign = data.getStringExtra("sign");
			LogUtils.e("修改 user_autograph = "+sign);
			params.addBodyParameter("user_autograph", sign);
		}else if(requestCode == requestCodePhoneNum){
			phoneNum = data.getStringExtra("phoneNum");
			LogUtils.e("修改 mobile_phone = "+phoneNum);
			params.addBodyParameter("mobile_phone", phoneNum);
		}
		requestAlterUserInfo(params);
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**请求修改 个人资料*/
	private void requestAlterUserInfo(RequestParams params){
		httpUtils.send(HttpMethod.POST, Url.userInfoAlterApi, params, new UserInfoAlterCallBack(CallBackType.alterUserInfo));
	}
	/**请求广告的 回调
	 * */
	private final class UserInfoAlterCallBack extends RequestCallBack<String>
	{
		private CallBackType callBackType;
		public UserInfoAlterCallBack(CallBackType callBackType) {
			this.callBackType = callBackType;
		}
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
			ToastUtil.longAtCenterInThread(context, XUtil.getErrorInfo(error));
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			dialogDismiss(dialogLoading);
			String result = info.result;
			LogUtils.e("result = "+result);
			if(JsonCommonUtil.getSessionErrorInfo(result) !=null ){
				startLoginActvitiy(activity, false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ToastUtil.shortAtCenterInThread(context, JsonCommonUtil.getCommonErrorInfo(result));
				return ;
			}	
			if(CallBackType.alterUserInfo == callBackType){
				ToastUtil.shortAtCenterInThread(context, "修改成功");
				App.mustrefreshUserInfo = true;
				//diaplayUserInfo();
			}else if(CallBackType.logout == callBackType){
				User.logout(context);
				Intent intent = new Intent(context, LoginActivity.class);
				startActivity(intent);
				mustKillAllActivity = true;
				ActivityUtil.pushActivity(activity);
			}
		}
	}
	private void diaplayUserInfo()
	{
		ViewUtil.setText2TextView(findViewById(R.id.tv_nickname_value), User.getNickName(context));
		ViewUtil.setText2TextView(findViewById(R.id.tv_gender_value), User.getGender(context));
		ViewUtil.setText2TextView(findViewById(R.id.tv_age_value), User.getAge(context));
		ViewUtil.setText2TextView(findViewById(R.id.tv_phone_num_value), User.getPhoneNum(context));
		ViewUtil.setText2TextView(findViewById(R.id.tv_sign_value), User.getSign(context));
		LogUtils.e("userLogoUrl = "+userLogoUrl);
		if(!TextUtils.isEmpty(userLogoUrl)){
			Picasso.with(context).load(userLogoUrl+"?"+new Random().nextInt(1000)).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(circleImageView);			
		}
	}
	@Override
	protected void onStop()
	{
		super.onStop();
		if(mustKillAllActivity){
			ActivityUtil.killAllActivity();
		}else{
			ActivityUtil.clearAllActivity(false);
		}
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		dialogGender.dismiss();
	}
	/**初始化对话框：修改性别*/
	private void initDialogGender()
	{
		dialogGender = new Dialog(context, R.style.dialog_quit);
		dialogGender.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_user_info_gender, null);
		TextView tvFemale = (TextView) view.findViewById(R.id.tv_female);
		TextView tvMale = (TextView) view.findViewById(R.id.tv_male);
		tvFemale.setOnClickListener(new DialogOnClickListener());
		tvMale.setOnClickListener(new DialogOnClickListener());
		dialogGender.setContentView(view);
	}
	private final class  DialogOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			RequestParams params = new RequestParams(XUtil.charset);
			params.addQueryStringParameter("format", "json");
			params.addQueryStringParameter("uid", User.getUserId(context));
			params.addQueryStringParameter("sid", User.getSID(context));
			String gender = "";
			if(R.id.tv_male == v.getId()){
				User.setGender(context, "男");
				gender = "男";
				params.addBodyParameter("user_sex", "1");
			}else if(R.id.tv_female == v.getId()){
				User.setGender(context, "女");
				gender = "女";
				params.addBodyParameter("user_sex", "2");
			}
			((TextView) findViewById(R.id.tv_gender_value)).setText(gender);
			dialogGender.dismiss();
			requestAlterUserInfo(params);
		}
	}
}
