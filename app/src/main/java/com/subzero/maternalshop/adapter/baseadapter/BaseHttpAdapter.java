package com.subzero.maternalshop.adapter.baseadapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.widget.BaseAdapter;

import com.lidroid.xutils.HttpUtils;
import com.subzero.common.listener.CommonOnKeyListener;
import com.subzero.common.listener.CommonOnKeyListener.OnKeyType;
import com.subzero.common.utils.XUtil;
import com.subzero.maternalshop.R;
public abstract class BaseHttpAdapter extends BaseAdapter
{
	protected HttpUtils httpUtils;
	protected Dialog dialogLoading;
	protected Context context;
	/**必须要金额*/
	protected boolean mustHasMoney;
	/**初始化 xUtils.HttpUtils*/
	protected void initHttputils(){
		httpUtils = XUtil.getHttpUtilInstance();
	}
	/**初始化Loading对话框！*/
	protected void initDialogLoading()
	{
		dialogLoading = new Dialog(context, R.style.dialog_loading);
		dialogLoading.setContentView(R.layout.loading_circle_orange);
		dialogLoading.setCancelable(true);
		dialogLoading.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialogLoading.setCanceledOnTouchOutside(false);
		dialogLoading.setOnKeyListener(new CommonOnKeyListener((Activity) context,OnKeyType.dismissKillActivity));
	}
}
