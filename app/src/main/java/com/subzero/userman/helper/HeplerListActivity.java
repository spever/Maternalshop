package com.subzero.userman.helper;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import android.os.Bundle;
import android.widget.ListView;

import com.lidroid.xutils.exception.HttpException;
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
import com.subzero.maternalshop.adapter.baseadapter.HelperListAdapter;
import com.subzero.maternalshop.bean.HelperListBean;
import com.subzero.maternalshop.config.Url;
/**帮助中心
 * 启动者：
 * 	默认首页：MineFragment
 * 启动项：
 * 	使用介绍：OperationNoticeActivity
 * 	公司通知：CompanyNoticeActivity
 * 	关于我们：AboutUsActivity
 * 适配器：HelperListAdapter
 * */
public class HeplerListActivity extends BaseActivity
{
	private HelperListAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_helper);
		initView();
		loadJsonData();
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(this, CommonClickType.back));
		adapter = new HelperListAdapter(context);
		ListView listView = ((ListView)findViewById(R.id.lv));//
		listView.setAdapter(adapter);
	}
	//TODO 加载Json数据
	private void loadJsonData()
	{
		//http://muyin.cooltou.cn/zapi/?url=/help&format=json
		httpUtils.send(HttpMethod.GET, Url.helperListApi , new UserInfoCallBack());
	}
	/**请求广告的 回调
	 * */
	private final class UserInfoCallBack extends RequestCallBack<String>
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
				ToastUtil.longAtCenterInThread(context, JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "status"), "error_desc"));
				startLoginActvitiy(activity,false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "status"), "code"))){
				return ;
			}
			JSONArray jsonArray = JsonSmartUtil.getJsonArray(result, "data");
			List<HelperListBean> list = new ArrayList<HelperListBean>();
			for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray)) && (i<jsonArray.size()); i++)
			{
				JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonArray, i);
				HelperListBean bean = new HelperListBean();
				bean.cat_id = JsonSmartUtil.getString(jsonObject, "cat_id");
				bean.cat_name = JsonSmartUtil.getString(jsonObject, "cat_name");
				list.add(bean);
			}
			adapter.addItem(list);
		}
	}
}
