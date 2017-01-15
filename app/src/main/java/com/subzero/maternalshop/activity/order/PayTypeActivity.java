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
import com.subzero.maternalshop.adapter.baseadapter.order.PayTypeAdapter;
import com.subzero.maternalshop.bean.order.PayTypeBean;
/**配送方式
 * 启动者：OrderSubmitActivity
 * 适配器：PayTypeAdapter
 * */
public class PayTypeActivity extends BaseActivity
{
	/**支付方式 json数据*/
	public String paymentList;
	private PayTypeAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_type);
		paymentList = getIntent().getStringExtra("paymentList");
		initView();
		loadJsonData();
	}
	private void loadJsonData()
	{
		JSONArray jsonArray = JsonSmartUtil.getJsonArray(paymentList);
		LogUtils.e("paymentList = "+paymentList);
		List<PayTypeBean> list = new ArrayList<PayTypeBean>();
		for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray)) && (i<jsonArray.size()); i++)
		{
			JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonArray, i);
			PayTypeBean bean = new PayTypeBean();
			bean.pay_id = JsonSmartUtil.getString(jsonObject, "pay_id");
			bean.pay_name = JsonSmartUtil.getString(jsonObject, "pay_name");
			list.add(bean);
		}
		adapter.addItem(list);
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		ListView listView = (ListView) findViewById(R.id.lv);
		adapter = new PayTypeAdapter(context);
		listView.setAdapter(adapter);
	}
}
