package com.subzero.maternalshop.activity.redpacket;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.subzero.common.utils.ViewUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.activity.IndexActivity;
import com.subzero.maternalshop.adapter.baseadapter.RedPacketListAdapter;
import com.subzero.maternalshop.bean.RedPacketBean;
import com.subzero.maternalshop.config.Cache;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.util.JsonCommonUtil;
/**红包列表
 * 启动者：MineFragment
 * 适配器：RedPacketListAdapter
 * 启动项：
 * 		商品详情：ShopDetailActivity
 * */
public class RedPacketListActivity extends BaseActivity
{
	private RedPacketListAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_red_packet_list);
		initView();
		loadJsonData();
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		ListView listView = (ListView) findViewById(R.id.lv);
		shellView = LayoutInflater.from(context).inflate(R.layout.layout_shell_data_empty, null);
		shellView.findViewById(R.id.shell_layou_empty).setOnClickListener(new MyOnClickListener());
		ViewUtil.addShell2View(findViewById(R.id.lv), context, shellView);
		adapter = new RedPacketListAdapter(context);
		listView.setAdapter(adapter);
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(R.id.shell_layou_empty == v.getId()){
				loadJsonData();
			}
		}
	}
	/**获取个红包详情*/
	private void loadJsonData()
	{
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("uid", User.getUserId(context));
		params.addQueryStringParameter("sid", User.getSID(context));
		httpHandler = httpUtils.send(HttpMethod.GET, Url.redPacketList, params , new RedPacketListCallBack());
	}
	/**@category 红包列表的回调
	 * */
	private final class RedPacketListCallBack extends RequestCallBack<String>
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
			ViewUtil.setText2TextView(shellView.findViewById(R.id.shell_tv_empty), XUtil.getErrorInfo(error));
			shellView.setVisibility(View.VISIBLE);
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			dialogDismiss(dialogLoading);
			String result = info.result;
			Cache.saveTmpFile(result);
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				((IndexActivity)activity).startLoginActvitiy(activity, false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ViewUtil.setText2TextView(shellView.findViewById(R.id.shell_tv_empty), JsonCommonUtil.getCommonErrorInfo(result));
				shellView.setVisibility(View.VISIBLE);
				return;
			}
			List<RedPacketBean> list = new ArrayList<RedPacketBean> (); 
			JSONArray jsonArray_lose = JsonSmartUtil.getJsonArray(JsonSmartUtil.getJsonObject(result, "data"), "lose");
			JSONArray jsonArray_success = JsonSmartUtil.getJsonArray(JsonSmartUtil.getJsonObject(result, "data"), "success");
			List<RedPacketBean> list_success = getRedPacketList(jsonArray_success,false);
			if((jsonArray_lose!=null) && (jsonArray_lose.size()>=1)){
				int successCanShowLoseLinePosition = list_success.size()-1;
				if((list_success!=null) && (successCanShowLoseLinePosition>=0)&& (list_success.size() > successCanShowLoseLinePosition) ){
					RedPacketBean bean = list_success.get(successCanShowLoseLinePosition);
					bean.canShowLine = true;
					list_success.set(successCanShowLoseLinePosition, bean);
				}
			}
			list.addAll(list_success);
			list.addAll(getRedPacketList(jsonArray_lose,true));
			adapter.addItem(list);
			shellView.setVisibility(View.GONE);
		}
	}
	private List<RedPacketBean> getRedPacketList( JSONArray jsonArray, boolean isLosed)
	{
		List<RedPacketBean> list = new ArrayList<RedPacketBean>();
		for (int i = 0; (jsonArray!=null) && (i<jsonArray.size()); i++)
		{
			JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonArray, i);
			RedPacketBean bean = new RedPacketBean();
			bean.min_goods_amount = JsonSmartUtil.getString(jsonObject, "min_goods_amount");
			bean.status = JsonSmartUtil.getString(jsonObject, "status");
			bean.supplier = JsonSmartUtil.getString(jsonObject, "supplier");
			bean.supplier_id = JsonSmartUtil.getString(jsonObject, "supplier_id");
			bean.type_money = JsonSmartUtil.getString(jsonObject, "type_money");
			bean.type_name = JsonSmartUtil.getString(jsonObject, "type_name");
			bean.use_enddate = JsonSmartUtil.getString(jsonObject, "use_enddate");
			bean.use_startdate = JsonSmartUtil.getString(jsonObject, "use_startdate");
			bean.canShowTopLine  = true;	
			bean.isLosed = isLosed;
			if(isLosed && (i==0)){
				bean.canShowTopLine  = false;	
			}	
			bean.canShowLine = false;
			bean.min_goods_amount = JsonSmartUtil.getString(jsonObject, "min_goods_amount");
			list.add(bean);
		}
		return list;
	}
}
