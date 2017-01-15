package com.subzero.userman.helper;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import android.os.Bundle;
import android.widget.ListView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.adapter.baseadapter.CompanyNoticeAdapter;
import com.subzero.maternalshop.bean.CompanyNoticeBean;
import com.subzero.maternalshop.config.Cache;
import com.subzero.maternalshop.config.Url;
/**使用介绍
 * 启动者：
 * 	默认首页：HeplerActivity
 * 适配器：
 * 	CompanyNoticeAdapter
 * */
public class CompanyNoticeActivity extends BaseActivity
{
	private CompanyNoticeAdapter adapter;
	private String catID;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_company_notice);
		catID = getIntent().getStringExtra("cat_id");
		initView();
		loadJsonData();
	}

	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(this, CommonClickType.back));
		adapter = new CompanyNoticeAdapter(context);
		((ListView)findViewById(R.id.lv)).setAdapter(adapter);
	}
	private void loadJsonData()
	{
		//http://muyin.cooltou.cn/zapi/?url=/help&format=json&action=article&cat_id=7&page1=&count=10
		LogUtils.e("catID = "+catID);
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("action", "article");
		params.addQueryStringParameter("cat_id", catID);
		params.addQueryStringParameter("page", pageindex+"");
		params.addQueryStringParameter("count", Url.rows+"");
		httpUtils.send(HttpMethod.GET, Url.helperListApi , params, new UserInfoCallBack());
	}
	/**请求广告的 回调
	 * */
	private final class UserInfoCallBack extends RequestCallBack<String>
	{
		@Override
		public void onStart()
		{
			super.onStart();
			dialogLoading.show();
		}
		@Override
		public void onFailure(HttpException error, String msg)
		{
			dialogLoading.dismiss();
			ToastUtil.longAtCenterInThread(context, XUtil.getErrorInfo(error));
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			dialogLoading.dismiss();
			String result = info.result;
			Cache.saveTmpFile(result);
			LogUtils.e("result = "+result);
			if("400".equalsIgnoreCase(JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "status"), "code"))){
				ToastUtil.longAtCenterInThread(context, JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "status"), "error_desc"));
				startLoginActvitiy(activity,false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "status"), "code"))){
				return ;
			}
			JSONArray jsonArray = JsonSmartUtil.getJsonArray(result, "data");
			if(JsonSmartUtil.isJsonArrayEmpty(jsonArray)){
				ToastUtil.shortAtCenterInThread(context, "暂无数据");
				return ;
			}
			List<CompanyNoticeBean> list = new ArrayList<CompanyNoticeBean>();
			for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray)) && (i<jsonArray.size()); i++)
			{
				JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonArray, i);
				CompanyNoticeBean bean = new CompanyNoticeBean();
				bean.id = JsonSmartUtil.getString(jsonObject, "article_id");
				bean.title = JsonSmartUtil.getString(jsonObject, "title");
				bean.content = JsonSmartUtil.getString(jsonObject, "content");
				bean.add_time = JsonSmartUtil.getString(jsonObject, "add_time");
				list.add(bean);
			}
			adapter.addItem(list);
		}
	}
}
