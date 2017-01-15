package com.subzero.maternalshop.activity;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.util.LogUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.subzero.common.listener.CommonOnKeyListener;
import com.subzero.common.listener.CommonOnKeyListener.OnKeyType;
import com.subzero.common.utils.XUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.config.App;
import com.subzero.userman.LoginActivity;
public abstract class BaseFragmentActivity extends FragmentActivity
{
	protected Context context;
	protected Activity activity;
	protected Dialog dialogLoading;
	protected HttpUtils httpUtils;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = this;
		activity = this;
		initSystemBar();
		initDialogLoading();
		initHttputils();
	}
	/**初始化视图*/
	public abstract void initView();
	/**初始化Loading对话框！*/
	private void initDialogLoading()
	{
		dialogLoading = new Dialog(context, R.style.dialog_loading);
		dialogLoading.setContentView(R.layout.loading_circle_orange);
		dialogLoading.setCancelable(true);
		dialogLoading.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialogLoading.setCanceledOnTouchOutside(false);
		dialogLoading.setOnKeyListener(new CommonOnKeyListener(activity,OnKeyType.dismissKillActivity));
	}
	/**初始化 xUtils.HttpUtils*/
	private void initHttputils()
	{
		httpUtils = XUtil.getHttpUtilInstance();
	}
	@TargetApi(19)
	private void initSystemBar()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintColor(getResources().getColor(R.color.subzero_title_color_status_bar));
			tintManager.setStatusBarTintEnabled(true);
		}
	}
	/**跳到 LoginActivity 不结束自己
	 * @param mustkillSelf 必须结束自己*/
	public void startLoginActvitiy(Activity activity, boolean mustkillSelf)
	{
		Intent intent = new Intent(activity, LoginActivity.class);
		intent.putExtra(App.keyModuleName, activity.getClass().getSimpleName());
		LogUtils.e("activity.getClass().getSimpleName() = "+activity.getClass().getSimpleName());
		startActivity(intent);
		if(mustkillSelf){
			activity.finish();
		}
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if(dialogLoading!=null){
			dialogLoading.dismiss();
		}
	}
}
