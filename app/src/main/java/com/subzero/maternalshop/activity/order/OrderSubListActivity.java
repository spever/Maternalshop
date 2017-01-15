package com.subzero.maternalshop.activity.order;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.common.utils.ViewUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.adapter.baseadapter.order.OrderSubListAdapter;
import com.subzero.maternalshop.bean.ProductBean;
import com.subzero.maternalshop.config.App;
/**订单列表
 * 启动者：
 * 	订单列表 ：OrderListActivity
 * 适配器：OrderSubListAdapter*/
public class OrderSubListActivity extends BaseActivity
{
	private OrderSubListAdapter adapter;
	private  List<ProductBean> list;
	private String actionType;
	/**共 n 件 商品*/
	private String productCount;
	/**订单id*/
	private String orderID;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_sub_list);
		Intent intent = getIntent();
		list = (List<ProductBean>) intent.getSerializableExtra("listProduct");
		actionType = intent.getStringExtra("actionType");
		productCount = intent.getStringExtra("productCount");
		orderID = intent.getStringExtra("orderID");
		initView();
		loadJsonData();
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		if(App.orderListMustRefresh){
			finish();
		}
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		ViewUtil.setText2TextView(findViewById(R.id.tv_product_count), "共"+productCount+"件");
		ListView listView = (ListView) findViewById(R.id.lv);
		adapter = new OrderSubListAdapter(context);
		adapter.setActionType(actionType);
		adapter.setOrderID(orderID);
		listView.setAdapter(adapter);
	}
	private void loadJsonData(){
		adapter.addItem(list);
	}
}
