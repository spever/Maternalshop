package com.subzero.maternalshop.adapter.baseadapter.order;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.subzero.common.utils.ViewUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.order.DistributionActivity;
import com.subzero.maternalshop.activity.order.OrderSubmitActivity;
import com.subzero.maternalshop.bean.order.DistributionBean;
/**使用介绍，适配器
 * 宿主：
 * 	收货地列表：AddrListActivity
 * 启动项：
 * 	收货地址：AddrDetailActivity
 * */
public class DistributionListAdapter extends BaseAdapter
{
	protected List<DistributionBean> list;
	private Context context;
	public DistributionListAdapter(Context context) {
		this.list = new ArrayList<DistributionBean>();
		this.context = context;
	}
	/**更新适配器*/
	public void updateItem(DistributionBean obj,int position){
		if((position<0) || (position > list.size()-1)){
			return;
		}
		list.set(position, obj);
		notifyDataSetChanged();
	}
	public void updateItem(List<DistributionBean> list){
		for (int i = 0; (list!=null)&&(i < list.size()); i++){
			DistributionBean bean = list.get(i);
			this.list.set(i, bean);
		}
		notifyDataSetChanged();
	}
	public void addItem(DistributionBean obj){
		list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<DistributionBean> list){
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void refreshItem(List<DistributionBean> list){
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
	public List<DistributionBean> getList(){
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_distribution, parent, false);
			holder.tv =(TextView) convertView.findViewById(R.id.tv);
			holder.layoutBody = convertView.findViewById(R.id.layout_body);
			holder.ivEdit = (ImageView) convertView.findViewById(R.id.iv_edit);
			holder.lineTop = convertView.findViewById(R.id.line_top);
			holder.lineLast = convertView.findViewById(R.id.line_last);
			holder.lineTopMargin0 = convertView.findViewById(R.id.line_top_margin0);
			holder.lineBottom0 = convertView.findViewById(R.id.line_bottom_margin0);
			holder.lineTopMargin16 = convertView.findViewById(R.id.line_top_margin16);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		/*第一条线*/
		if(position == 0){
			holder.lineTop.setVisibility(View.VISIBLE);
			holder.lineTopMargin0.setVisibility(View.VISIBLE);
			holder.lineTopMargin16.setVisibility(View.GONE);
		}else{
			holder.lineTop.setVisibility(View.GONE);
			holder.lineTopMargin0.setVisibility(View.GONE);
			holder.lineTopMargin16.setVisibility(View.VISIBLE);
		}
		/*最后一条线*/
		if(position == (list.size()-1)){
			holder.lineLast.setVisibility(View.VISIBLE);
			holder.lineBottom0.setVisibility(View.VISIBLE);
		}else{
			holder.lineBottom0.setVisibility(View.GONE);
			holder.lineLast.setVisibility(View.GONE);
		}
		/*错位问题*/
		holder.ivEdit.setTag(list.get(position).shipping_id);
		String idTag = (String) holder.ivEdit.getTag();
		if(idTag.equalsIgnoreCase(list.get(position).shipping_id)){
			holder.ivEdit.setSelected(list.get(position).isSelected);
		}
		holder.ivEdit.setOnClickListener(new MyOnClickListener(position));
		holder.layoutBody.setOnClickListener(new MyOnClickListener(position));
		ViewUtil.setText2TextView(holder.tv, list.get(position).shipping_name);
		return convertView;
	}
	
	private final class MyOnClickListener implements OnClickListener
	{
		private int position;
		public MyOnClickListener(int position) {
			this.position = position;
		}
		@Override
		public void onClick(View v)
		{
			//TODO 如果是 从订单确认 进入 收货地址选择  点击确认默认地址后 finish
			DistributionBean bean = list.get(position);
			bean.isSelected = true;
			updateItem(bean, position);
			
			Intent data = new Intent(context, OrderSubmitActivity.class);
			data.putExtra("shipping_id", bean.shipping_id);
			data.putExtra("shipping_name", bean.shipping_name);
			data.putExtra("shipping_fee", bean.shipping_fee);
			((DistributionActivity)context).setResult(Activity.RESULT_OK, data);
			((DistributionActivity)context).finish();
		}
	}
	protected final class ViewHolder{
		/**收货人*/
		public TextView tv;
		public ImageView ivEdit;
		public View layoutBody;
		public View lineTop;
		public View lineTopMargin0;
		public View lineTopMargin16;
		public View lineBottom0;
		public View lineLast;
	}
}
