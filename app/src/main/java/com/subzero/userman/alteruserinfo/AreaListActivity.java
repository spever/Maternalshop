package com.subzero.userman.alteruserinfo;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.adapter.baseadapter.AreaListAdapter;
import com.subzero.maternalshop.bean.AreaBean;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.Url;
/**订单列表
 * 启动者：
 * 	地址详情：AddrDetailActivity
 * 适配器：AreaListAdapter*/
public class AreaListActivity extends BaseActivity
{
	private AreaListAdapter adapter;
	private String areaType;
	private String proviceID;
	private String cityID;
	private String countyID;
	private String proviceName;
	private String cityName;
	private String countyName;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_area_list);
		areaType = App.areaProvince;
		pageindex = 1;
		initView();
		loadJsonData();
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		ListView listView = (ListView) findViewById(R.id.lv);
		adapter = new AreaListAdapter(context);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new MyOnItemClickListener());
	}
	private final class MyOnItemClickListener implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			if(App.areaProvince.equalsIgnoreCase(areaType)){
				areaType = App.areaProvince;
				areaType = App.areaCity;
				proviceID = adapter.getList().get(position).region_id;
				proviceName = adapter.getList().get(position).region_name;
			}else if(App.areaCity.equalsIgnoreCase(areaType)){
				areaType = App.areaCounty;
				cityID = adapter.getList().get(position).region_id;
				cityName = adapter.getList().get(position).region_name;
			}else if(App.areaCounty.equalsIgnoreCase(areaType)){
				areaType = App.areaCounty;
				countyID = adapter.getList().get(position).region_id;
				countyName = adapter.getList().get(position).region_name;
				Intent data = new Intent(context, AddrDetailActivity.class);
				data.putExtra("proviceID", proviceID);
				data.putExtra("proviceName", proviceName);
				data.putExtra("cityID", cityID);
				data.putExtra("cityName", cityName);
				data.putExtra("countyID", countyID);
				data.putExtra("countyName", countyName);
				setResult(RESULT_OK, data);
				finish();
			}
			loadJsonData();
		}
	}
	private void loadJsonData()
	{
		RequestParams params = new RequestParams(XUtil.charset);
		if(App.areaProvince.equalsIgnoreCase(areaType)){
			params.addQueryStringParameter("parent_id", "1");
		}else if(App.areaCity.equalsIgnoreCase(areaType)){
			params.addQueryStringParameter("parent_id", proviceID);
		}else if(App.areaCounty.equalsIgnoreCase(areaType)){
			params.addQueryStringParameter("parent_id", cityID);
		}
		httpUtils.send(HttpMethod.GET, Url.areaListApi, params , new AreaListCallBack());
	}
	/**区域列表的 回调
	 * */
	private final class AreaListCallBack extends RequestCallBack<String>
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
			dialogDismiss(dialogLoading);
			String result = info.result;
			//LogUtils.e("result = "+result);
			if("400".equalsIgnoreCase(JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "status"), "code"))){
				ToastUtil.shortAtCenterInThread(context, JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "status"), "error_desc"));
				startLoginActvitiy(activity, false);
				return;
			}
			JSONArray jsonArray = JsonSmartUtil.getJsonArray(result, "data");
			if(JsonSmartUtil.isJsonArrayEmpty(jsonArray)){
				ToastUtil.longAtCenterInThread(context, "暂无数据");
			}
			List<AreaBean> listAreaBean = new ArrayList<AreaBean>();
			for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray)) && (i<jsonArray.size()); i++)
			{
				JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonArray, i);
				AreaBean bean = new AreaBean();
				bean.parent_id = JsonSmartUtil.getString(jsonObject, "parent_id");
				bean.region_id = JsonSmartUtil.getString(jsonObject, "region_id");
				bean.parent_id = JsonSmartUtil.getString(jsonObject, "parent_id");
				bean.region_name = JsonSmartUtil.getString(jsonObject, "region_name");
				bean.isDefault = false;
				listAreaBean.add(bean);
			}
			adapter.refreshItem(listAreaBean);
		}
	}
}
