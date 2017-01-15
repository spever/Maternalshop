package com.subzero.maternalshop.activity.order;
import java.util.List;

import android.os.Bundle;
import android.widget.ListView;

import com.socks.library.KLog;
import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.adapter.baseadapter.order.SelectRedPacketAdapter;
import com.subzero.maternalshop.bean.RedPacketBean;
/**配送方式
 * 启动者：OrderSubmitActivity
 * 适配器：SelectRedPacketAdapter
 * */
public class SelectRedPacketActivity extends BaseActivity
{
	private  SelectRedPacketAdapter adapter;
	private List<RedPacketBean> list; 
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_red_packet);
		list = (List<RedPacketBean>) getIntent().getSerializableExtra("listRedPacketBean");
		initView();
		loadJsonData();
	}
	private void loadJsonData()
	{
		KLog.e("list = "+list);
		adapter.addItem(list);
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		ListView listView = (ListView) findViewById(R.id.lv);
		adapter = new SelectRedPacketAdapter(context);
		listView.setAdapter(adapter);
	}
}
