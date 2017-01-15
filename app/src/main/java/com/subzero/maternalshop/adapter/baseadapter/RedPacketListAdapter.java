package com.subzero.maternalshop.adapter.baseadapter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.shop.ShopDetailActivity;
import com.subzero.maternalshop.bean.RedPacketBean;
import com.subzero.maternalshop.config.App;
/**使用介绍，适配器
 * 宿主：
 * 	收货地列表：AddrListActivity
 * 启动项：
 * 	收货地址：AddrDetailActivity
 * 	商品详情：ShopDetailActivity
 * */
public class RedPacketListAdapter extends BaseHttpAdapter
{
	protected List<RedPacketBean> list;
	private String moduleName;
	public RedPacketListAdapter(Context context) {
		this.list = new ArrayList<RedPacketBean>();
		this.context = context;
		initDialogLoading();
		initHttputils();
	}
	public String getModuleName(){
		return moduleName;
	}
	public void setModuleName(String moduleName)	{
		this.moduleName = moduleName;
	}
	/**更新适配器*/
	public void updateItem(RedPacketBean obj,int position){
		if((position<0) || (position > list.size()-1)){
			return;
		}
		list.set(position, obj);
		notifyDataSetChanged();
	}
	public void updateItem(List<RedPacketBean> list){
		for (int i = 0; (list!=null)&&(i < list.size()); i++){
			RedPacketBean bean = list.get(i);
			this.list.set(i, bean);
		}
		notifyDataSetChanged();
	}
	public void addItem(RedPacketBean obj){
		list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<RedPacketBean> list){
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void refreshItem(List<RedPacketBean> list){
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void removeItem(int position){
		if(list == null || list.size()<=0 || position >= list.size() || position<0){
			return;
		}
		this.list.remove(position);
		notifyDataSetChanged();
	}
	public List<RedPacketBean> getList(){
		return list;
	}
	public void clearAll(){
		this.list.clear();
		notifyDataSetChanged();
	}
	@Override
	public int getCount(){
		return (list==null) ? 0:list.size();
	}
	@Override
	public Object getItem(int position){
		return (list==null) ? null:list.get(position);
	}
	@Override
	public long getItemId(int position){
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(list == null || list.size()<=0){
			return convertView;
		}
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_red_packet_list, parent, false);
			holder.tvShopName =(TextView) convertView.findViewById(R.id.tv_shop_name);
			holder.tvTypeName =(TextView) convertView.findViewById(R.id.tv_type);
			holder.tvTime =(TextView) convertView.findViewById(R.id.tv_time);
			holder.tvAmmount =(TextView) convertView.findViewById(R.id.tv_ammount);
			holder.tvRuler =(TextView) convertView.findViewById(R.id.tv_ruler);
			holder.lineLast = convertView.findViewById(R.id.line_last);
			holder.lineLosed = convertView.findViewById(R.id.line_losed);
			holder.lineTop = convertView.findViewById(R.id.line_top);
			holder.layoutRoot = convertView.findViewById(R.id.layout_root);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		/*最后一条线*/
		if(position == (list.size()-1)){
			holder.lineLast.setVisibility(View.VISIBLE);
		}else{
			holder.lineLast.setVisibility(View.GONE);
		}
		RedPacketBean bean = list.get(position);
		if(bean.canShowLine){
			holder.lineLosed.setVisibility(View.VISIBLE);
		}else{
			holder.lineLosed.setVisibility(View.GONE);
		}
		if(bean.canShowTopLine){
			holder.lineTop.setVisibility(View.VISIBLE);
		}else{
			holder.lineTop.setVisibility(View.GONE);
		}
		if(TextUtils.isEmpty(bean.supplier)){
			ViewUtil.setText2TextView(holder.tvShopName, "附近逛");
		}else{
			ViewUtil.setText2TextView(holder.tvShopName, bean.supplier);
		}
		ViewUtil.setText2TextView(holder.tvAmmount, "￥"+bean.type_money);
		ViewUtil.setText2TextView(holder.tvTypeName, bean.type_name+"  消费满￥"+bean.min_goods_amount+"可用");
		ViewUtil.setText2TextView(holder.tvTime, "有效期: "+bean.use_startdate+" - "+bean.use_enddate);
		holder.layoutRoot.setOnClickListener(new MyOnClickListener(position, App.clickBody));
		return convertView;
	}
	private final class MyOnClickListener implements OnClickListener
	{
		private int position;
		private String clickType;
		public MyOnClickListener(int position,String clickType) {
			this.position = position;
			this.clickType = clickType;
		}
		@Override
		public void onClick(View v)
		{
			
			if(App.clickBody.equalsIgnoreCase(clickType)){
				RedPacketBean bean = list.get(position);
				if(TextUtils.isEmpty(bean.supplier)){
					ToastUtil.shortAtCenter(context, "网站自营店");
					return ;
				}
				Intent intent = new Intent(context, ShopDetailActivity.class);
				intent.putExtra("suppId", bean.supplier_id);
				context.startActivity(intent);
			}
		}
	}
	protected final class ViewHolder{
		/**收货人*/
		public TextView tvShopName;
		/**做分隔线*/
		public View lineLast;	
		public View lineTop;	
		/**做分隔线*/
		public View lineLosed;
		/**具体收货地址*/
		public TextView tvTypeName;
		/**有效期*/
		public TextView tvTime;
		/**有金额*/
		public TextView tvAmmount;
		/**使用规则*/
		public TextView tvRuler;
		public View layoutRoot;
	}
}
