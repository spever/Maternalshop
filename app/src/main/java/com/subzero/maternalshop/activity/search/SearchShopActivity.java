package com.subzero.maternalshop.activity.search;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import subzero.maxwin.XListView;
import subzero.maxwin.XListView.IXListViewListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.KeyPadUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.common.view.CheckTextView;
import com.subzero.common.view.CheckTextView.OnCheckedClickListener;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.activity.shop.ShopDetailActivity;
import com.subzero.maternalshop.adapter.baseadapter.SearchShopAdapter;
import com.subzero.maternalshop.bean.shop.ShopBean;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.Cache;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.util.JsonCommonUtil;
/**搜索店铺页面
 * 启动者：
 * 	主页：HomepageFragment
 * 启动项：
 * 	店铺详情：ShopDetailActivity
 * 适配器：
 * 	SearchShopAdapter
 * */
public class SearchShopActivity extends BaseActivity
{
	private SearchShopAdapter adapter;
	private String keyWords;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_shop);
		initView();
	}
	@Override
	public void initView(){
		findViewById(R.id.iv_back).setOnClickListener(new MyOnClickListener());
		((CheckTextView)findViewById(R.id.ctv_edit)).setOnCheckedClickListener(new MyOnCheckedClickListener());
		XListView xlistView = (XListView) findViewById(R.id.xlv);
		xlistView.setPullLoadEnable(true);
		xlistView.setXListViewListener(new MyIXListViewListener(xlistView));
		xlistView.setOnItemClickListener(new MyOnItemClickListener());
		adapter = new SearchShopAdapter(context);
		xlistView.setAdapter(adapter);
	}
	private final class MyOnItemClickListener implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			Intent intent = new Intent(context, ShopDetailActivity.class);
			intent.putExtra("suppId", adapter.getList().get(position-1).supplier_id);
			startActivity(intent);
		}
	}
	private final class MyOnCheckedClickListener implements OnCheckedClickListener
	{
		@Override
		public void onCheckClick(View v)
		{
			keyWords = ((EditText)findViewById(R.id.et_search)).getText().toString();
			if(TextUtils.isEmpty(keyWords)){
				ToastUtil.shortAtCenter(context, "请输入内容");
				return ;
			}
			findViewById(R.id.xlv).setVisibility(View.GONE);
			hasMore = true;
			loadType = App.loadFirst;
			loadJsonData();
		}
	}
	/**TODO: 下拉刷新
	 * */
	protected final class MyIXListViewListener implements IXListViewListener
	{
		private XListView xlistView;
		public MyIXListViewListener(XListView xlistView) {
			this.xlistView = xlistView;
		}
		@Override
		public void onRefresh()
		{
			String label = DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
			xlistView.setRefreshTime(label);
			loadType = App.loadRefresh;
			pageindex = 1;
			hasMore = true;
			KeyPadUtil.closeKeybord((EditText) findViewById(R.id.et_search), context);
			loadJsonData();
		}
		@Override
		public void onLoadMore()
		{
			loadType = App.loadMore;
			pageindex++;
			KeyPadUtil.closeKeybord((EditText) findViewById(R.id.et_search), context);
			loadJsonData();
		}
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(R.id.iv_back == v.getId()){
				KeyPadUtil.closeKeybord((EditText) findViewById(R.id.et_search), context);
				finish();
			}
		}
	}
	public void loadJsonData()
	{
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("province", User.getCityId(context));
		//params.addQueryStringParameter("keywords", keyWords);
		params.addQueryStringParameter("keywords", Url.encode(keyWords));
		params.addQueryStringParameter("count", pageindex+"");
		params.addQueryStringParameter("page", Url.rows+"");
		LogUtils.e(XUtil.params2String(params));
		httpHandler = httpUtils.send(HttpMethod.GET, Url.searchShopApi, params , new SearchShopCallBack());
	}
	/**@category 订单列表的 回调
	 * */
	private final class SearchShopCallBack extends RequestCallBack<String>
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
			((XListView) findViewById(R.id.xlv)).stopXListView();
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			dialogDismiss(dialogLoading);
			String result = info.result;
			Cache.saveTmpFile(result);
			/*Session 过期的异常*/
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			if(sessionErrorInfo!=null){
				((XListView) findViewById(R.id.xlv)).stopXListView();
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				startLoginActvitiy(activity, false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				((XListView) findViewById(R.id.xlv)).stopXListView();
				ToastUtil.shortAtCenterInThread(context, JsonCommonUtil.getCommonErrorInfo(result));
				return;
			}
			List<ShopBean> list = new ArrayList<ShopBean>();
			JSONArray jsonArray = JsonSmartUtil.getJsonArray(result, "data");
			for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray)) && (i<jsonArray.size()); i++)
			{
				JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonArray, i);
				ShopBean bean = new ShopBean();
				bean.comment_count = JsonSmartUtil.getString(jsonObject, "comment_count");
				bean.company_intro = JsonSmartUtil.getString(jsonObject, "company_intro");
				bean.lat = JsonSmartUtil.getString(jsonObject, "company_latitude");
				bean.lng = JsonSmartUtil.getString(jsonObject, "company_longitude");
				bean.order_count = JsonSmartUtil.getString(jsonObject, "order_count");
				bean.shop_logo = JsonSmartUtil.getString(jsonObject, "shop_logo");
				bean.supplier_id = JsonSmartUtil.getString(jsonObject, "supplier_id");
				bean.supplier_name = JsonSmartUtil.getString(jsonObject, "supplier_name");
				list.add(bean);
			}
			boolean isEmpty = JsonSmartUtil.isJsonArrayEmpty(jsonArray);
			if(!hasMore){
				ToastUtil.shortAtCenterInThread(context, "没有更多数据");
				((XListView)findViewById(R.id.xlv)).stopXListView();
				return ;
			}
			findViewById(R.id.xlv).setVisibility(View.VISIBLE);
			if(App.loadFirst.equalsIgnoreCase(loadType)){
				if(isEmpty){
					findViewById(R.id.xlv).setVisibility(View.GONE);
					findViewById(R.id.iv_data_empty).setVisibility(View.VISIBLE);
				}else{
					findViewById(R.id.xlv).setVisibility(View.VISIBLE);
					findViewById(R.id.iv_data_empty).setVisibility(View.GONE);
				}
				adapter.refreshItem(list);
				((ListView)findViewById(R.id.xlv)).setSelection(0);
			}else if(App.loadMore.equalsIgnoreCase(loadType)){
				adapter.addItem(list);
			}else if(App.loadRefresh.equalsIgnoreCase(loadType)){
				adapter.refreshItem(list);
			}
			JSONObject jsonObject_paginated = JsonSmartUtil.getJsonObject(result, "paginated");
			String more = JsonSmartUtil.getString(jsonObject_paginated, "more");
			hasMore = "1".equalsIgnoreCase(more);
			((XListView) findViewById(R.id.xlv)).stopXListView();
		}
	}
}
