package com.subzero.maternalshop.activity.order;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import android.os.Bundle;
import android.widget.ListView;

import com.lidroid.xutils.util.LogUtils;
import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.adapter.baseadapter.order.DistributionListAdapter;
import com.subzero.maternalshop.bean.order.DistributionBean;
/**配送方式
 * 启动者：OrderSubmitActivity
 * 适配器：DistributionListAdapter
 * */
public class DistributionActivity extends BaseActivity
{
	/**配送方式的 Json数据*/
	private String shippingList;
	private DistributionListAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_distribution);
		shippingList = getIntent().getStringExtra("shippingList");
		initView();
		loadJsonData();
	}
	private void loadJsonData()
	{
		JSONArray jsonArray = JsonSmartUtil.getJsonArray(shippingList);
		LogUtils.e("shippingList = "+shippingList);
		List<DistributionBean> list = new ArrayList<DistributionBean>();
		for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray)) && (i<jsonArray.size()); i++)
		{
			JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonArray, i);
			DistributionBean bean = new DistributionBean();
			bean.shipping_id = JsonSmartUtil.getString(jsonObject, "shipping_id");
			bean.shipping_name = JsonSmartUtil.getString(jsonObject, "shipping_name");
			bean.shipping_fee = JsonSmartUtil.getString(jsonObject, "shipping_fee");
			list.add(bean);
		}
		adapter.addItem(list);
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		ListView listView = (ListView) findViewById(R.id.lv);
		adapter = new DistributionListAdapter(context);
		listView.setAdapter(adapter);
	}
}
