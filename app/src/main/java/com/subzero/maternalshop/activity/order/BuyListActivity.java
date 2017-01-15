package com.subzero.maternalshop.activity.order;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import android.os.Bundle;
import android.widget.ListView;

import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.adapter.baseadapter.BuyListAdapter;
import com.subzero.maternalshop.bean.ProductBean;
/**已经选购，待支付的清单
 * 启动者：OrderSubmitActivity
 * 适配器：BuyListAdapter
 * */
public class BuyListActivity extends BaseActivity
{
	/**所购买的 商品的 列表*/
	private String goodsList;
	/**购物中的 商品在 总 数量*/
	private int numberInCart;
	private BuyListAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy_list);
		goodsList = getIntent().getStringExtra("goodsList");
		numberInCart = getIntent().getIntExtra("numberInCart", 0);
		initView();
		setupAdapter();
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		ListView listView = (ListView) findViewById(R.id.lv);
		ViewUtil.setText2TextView(findViewById(R.id.tv_count), numberInCart+"");
		adapter = new BuyListAdapter(context);
		listView.setAdapter(adapter);
		
	}
	private void setupAdapter()
	{
		JSONArray jsonArray = JsonSmartUtil.getJsonArray(goodsList);
		List<ProductBean> list = new ArrayList<ProductBean>();
		for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray)) && (i<jsonArray.size()); i++)
		{
			JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonArray, i);
			ProductBean bean = new ProductBean();
			bean.goods_id = JsonSmartUtil.getString(jsonObject, "goods_id");
			bean.goods_name = JsonSmartUtil.getString(jsonObject, "goods_name");
			bean.shop_price = JsonSmartUtil.getString(jsonObject, "shop_price");
			bean.market_price = JsonSmartUtil.getString(jsonObject, "market_price");
			bean.goods_number = JsonSmartUtil.getString(jsonObject, "goods_number");
			bean.thumb = JsonSmartUtil.getString(jsonObject, "img");
			list.add(bean);
		}
		adapter.addItem(list);
	}
}
