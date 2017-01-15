package com.subzero.maternalshop.activity.shop;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import android.os.Bundle;
import android.widget.ListView;

import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.common.utils.ActivityUtil;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.adapter.baseadapter.shop.SortListInOneShopActivityAdapter;
import com.subzero.maternalshop.bean.shop.ProductClassBean;
/**店铺详情 宝贝分类  一级列表
 * 启动者：
 * 	默认首页：ShopDetailActivity
 * 启动项：
 * 	店铺详情 宝贝分类 二级列表：SortDetailListActivity
 * 适配器：
 * 	SortListAdapter
 * */
public class SortListInOneShopActivity extends BaseActivity
{
	private SortListInOneShopActivityAdapter adapter;
	/**店铺 宝贝分类 的json串*/
	private String categories;
	/**店铺的 id*/
	private String suppId;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sort_list_in_one_shop);
		ActivityUtil.pushActivity(activity);
		categories = getIntent().getStringExtra("categories");
		suppId = getIntent().getStringExtra("suppId");
		initView();
		loadJsonData();
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context,CommonClickType.back));
		adapter = new SortListInOneShopActivityAdapter(context);
		ListView listView = ((ListView)findViewById(R.id.lv));
		listView.setAdapter(adapter);
	}
	private void loadJsonData()
	{
		//LogUtils.e("categories = "+categories);
		JSONArray jsonArray = JsonSmartUtil.getJsonArray(categories);
		List<ProductClassBean> list = new ArrayList<ProductClassBean>();
		for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray)) &&  (i < jsonArray.size()); i++)
		{
			JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonArray, i);
			ProductClassBean bean = new ProductClassBean();
			bean.suppId = suppId;
			bean.id = JsonSmartUtil.getString(jsonObject, "id");
			bean.name = JsonSmartUtil.getString(jsonObject, "name");
			bean.url = JsonSmartUtil.getString(jsonObject, "url");
			list.add(bean);
		}
		adapter.addItem(list);
	}
}
