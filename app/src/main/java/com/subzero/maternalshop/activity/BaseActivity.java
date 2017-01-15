package com.subzero.maternalshop.activity;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.util.LogUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.subzero.common.listener.CommonOnKeyListener;
import com.subzero.common.listener.CommonOnKeyListener.OnKeyType;
import com.subzero.common.utils.ActivityUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.config.App;
import com.subzero.userman.LoginActivity;
public abstract class BaseActivity extends Activity
{
	protected Context context;
	protected Activity activity;
	/**给View 加的外壳*/
	protected View shellView;
	protected Dialog dialogLoading;
	protected HttpHandler<String> httpHandler = null;
	protected HttpUtils httpUtils;
	/**当前页码，从 1 开始*/
	protected int pageindex = 1;
	/**加载类型：  首次加载  上拉加载  下拉刷新*/
	protected String loadType;
	/**后台 有数据
	 * 在刷新的时候 重置一下值
	 * hasMore = true;*/
	protected boolean hasMore;
	/**从 哪个模块 进到 这个 模块的*/
	protected String moduleName;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = this;
		activity = this;
		hasMore = true;
		pageindex = 1;
		loadType = App.loadFirst;
		initSystemBar();
		initUtils();
		initDialogLoading(); 
	}
	/**初始化视图*/
	public abstract void initView();
	/**初始化 xUtils.HttpUtils*/
	private void initUtils()
	{
		httpUtils = XUtil.getHttpUtilInstance();
	}
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
		LogUtils.e("开始跳转 Activity = "+activity.getClass().getSimpleName());
		intent.putExtra(App.keyModuleName, activity.getClass().getSimpleName());
		startActivity(intent);
		if(mustkillSelf){
			activity.finish();
		}
	}
	public void dialogShow(Dialog dialog,Context context){
		if((context!=null) && ActivityUtil.isActivityOnForeground(context) && (dialog!=null)){
			dialogLoading.show();
		}
	}
	public void dialogDismiss(Dialog dialog){
		if(dialog!=null){
			dialogLoading.dismiss();
		}
	}
	@Override
	protected void onResume()
	{
		super.onResume();
	}
	@Override
	protected void onPause()
	{
		super.onPause();
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if(dialogLoading!=null){
			dialogLoading.dismiss();
			dialogLoading = null;
		}
		if(httpHandler!=null){
			httpHandler.cancel();
			httpHandler = null;
		}
	}
}
