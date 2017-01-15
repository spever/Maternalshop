package com.subzero.maternalshop.fragment;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.HttpHandler;
import com.subzero.common.listener.CommonOnKeyListener;
import com.subzero.common.listener.CommonOnKeyListener.OnKeyType;
import com.subzero.common.utils.ActivityUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.config.App;
public abstract class BaseFragment extends Fragment
{
	protected HttpHandler<String> httpHandler = null;
	protected HttpUtils httpUtils;
	protected Dialog dialogLoading;
	/**当前页码，从 1 开始*/
	protected int pageindex = 1;
	/**加载类型：  首次加载  上拉加载  下拉刷新*/
	protected String loadType;
	/**已经加载数据 完成*/
	protected boolean hasLoaded;
	/**当前Fragment可以加载数据*/
	protected boolean canLoading;
	protected long tableCountOld = 0;
	protected View rootView;
	/**给View 加的外壳*/
	protected View shellView;
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		initHttputils();
		initDialogLoading();
		pageindex = 1;
		hasLoaded = false;
		canLoading = true;
		loadType = App.loadFirst;
	}
	/**初始化视图*/
	public abstract void initView();
	/**初始化Loading对话框！*/
	private void initDialogLoading()
	{
		dialogLoading = new Dialog(getActivity(), R.style.dialog_loading);
		dialogLoading.setContentView(R.layout.loading_circle_orange);
		dialogLoading.setCancelable(true);
		dialogLoading.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialogLoading.setCanceledOnTouchOutside(false);
		dialogLoading.setOnKeyListener(new CommonOnKeyListener(getActivity(),OnKeyType.dismissKillActivity));
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
	public void onDestroy()
	{
		super.onDestroy();
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
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		if((rootView!=null) && (rootView.getParent()!=null)){
			((ViewGroup)rootView.getParent()).removeView(rootView);
		}
	}
	/**初始化 xUtils.HttpUtils*/
	private void initHttputils()
	{
		httpUtils = XUtil.getHttpUtilInstance();
	}
}
