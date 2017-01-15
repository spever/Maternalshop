package com.subzero.userman.alteruserinfo;
import java.util.List;

import net.minidev.json.JSONArray;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.common.view.CheckTextView;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.adapter.baseadapter.AddrListAdapter;
import com.subzero.maternalshop.bean.AddrBean;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.util.JsonAddrList;
import com.subzero.maternalshop.util.JsonCommonUtil;
/**订单列表
 * 启动者：
 * 	我的：MineFragment
 * 启动项：
 * 	地址详情：AddrDetailActivity
 * 适配器：AddrListAdapter*/
public class AddrListActivity extends BaseActivity
{
	private AddrListAdapter adapter;
	/**收货地址列表为空*/
	private boolean isAddrListEmpty;
	private String moduleName;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addr_list);
		moduleName = getIntent().getStringExtra("moduleName");
		pageindex = 1;
		isAddrListEmpty = false;
		initView();
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		((CheckTextView)findViewById(R.id.ctv_right)).setOnClickListener(new MyOnClickListener());
		ListView listView = (ListView) findViewById(R.id.lv);
		adapter = new AddrListAdapter(context);
		listView.setAdapter(adapter);
		adapter.setModuleName(moduleName);
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(R.id.ctv_right == v.getId()){
				Intent intent = new Intent(context, AddrDetailActivity.class);
				intent.putExtra("isAddrListEmpty", isAddrListEmpty);
				startActivity(intent);
			}
		}
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		loadJsonData();
	}
	private void loadJsonData()
	{
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("uid", User.getUserId(context));
		params.addQueryStringParameter("sid", User.getSID(context));
		//LogUtils.e(XUtil.params2String(params));
		httpUtils.send(HttpMethod.GET, Url.addrListApi, params , new AddrListCallBack());
	}
	/**常用地址列表的 回调
	 * */
	private final class AddrListCallBack extends RequestCallBack<String>
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
			ToastUtil.longAtCenterInThread(context, XUtil.getErrorInfo(error));
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			ListView listView = (ListView) findViewById(R.id.lv);
			dialogDismiss(dialogLoading);
			String result = info.result;
			//LogUtils.e("result = "+result);
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				startLoginActvitiy(activity, false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ToastUtil.shortAtCenterInThread(context, JsonCommonUtil.getCommonErrorInfo(result));
				listView.setVisibility(View.GONE);
				findViewById(R.id.iv_data_empty).setVisibility(View.VISIBLE);
				return;
			}
			JSONArray jsonArray = JsonSmartUtil.getJsonArray(result, "data");
			if(JsonSmartUtil.isJsonArrayEmpty(jsonArray)){
				isAddrListEmpty = true;
				ToastUtil.shortAtCenterInThread(context, "暂无数据，请新增");
				listView.setVisibility(View.GONE);
				findViewById(R.id.iv_data_empty).setVisibility(View.VISIBLE);
				return ;
			}
			List<AddrBean> list = JsonAddrList.getAddrList(jsonArray);
			adapter.refreshItem(list);
			listView.setVisibility(View.VISIBLE);
		}
	}
}
