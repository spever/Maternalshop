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
import com.subzero.maternalshop.activity.order.OrderSubmitActivity;
import com.subzero.maternalshop.activity.order.PayTypeActivity;
import com.subzero.maternalshop.bean.order.PayTypeBean;
/**使用介绍，适配器
 * 宿主：
 * 	收货地列表：PayTypeActivity
 * 启动项：
 * 	收货地址：OrderSubmitActivity
 * */
public class PayTypeAdapter extends BaseAdapter
{
	protected List<PayTypeBean> list;
	private Context context;
	public PayTypeAdapter(Context context) {
		this.list = new ArrayList<PayTypeBean>();
		this.context = context;
	}
	/**更新适配器*/
	public void updateItem(PayTypeBean obj,int position){
		if((position<0) || (position > list.size()-1)){
			return;
		}
		list.set(position, obj);
		notifyDataSetChanged();
	}
	public void updateItem(List<PayTypeBean> list){
		for (int i = 0; (list!=null)&&(i < list.size()); i++){
			PayTypeBean bean = list.get(i);
			this.list.set(i, bean);
		}
		notifyDataSetChanged();
	}
	public void addItem(PayTypeBean obj){
		list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<PayTypeBean> list){
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void refreshItem(List<PayTypeBean> list){
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
	public List<PayTypeBean> getList(){
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_pay_type, parent, false);
			holder.tvPayName =(TextView) convertView.findViewById(R.id.tv_pay_name);
			holder.tvPayDescripe =(TextView) convertView.findViewById(R.id.tv_pay_descripe);
			holder.ivEdit = (ImageView) convertView.findViewById(R.id.iv_edit);
			holder.layoutPayType = convertView.findViewById(R.id.layout_pay_type);
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
		holder.ivEdit.setTag(list.get(position).pay_id);
		String idTag = (String) holder.ivEdit.getTag();
		if(idTag.equalsIgnoreCase(list.get(position).pay_id)){
			holder.ivEdit.setSelected(list.get(position).isSelected);
		}
		holder.ivEdit.setOnClickListener(new MyOnClickListener(position));
		holder.layoutPayType.setOnClickListener(new MyOnClickListener(position));
		 PayTypeBean bean = list.get(position);
		ViewUtil.setText2TextView(holder.tvPayName, list.get(position).pay_name);
		if("2".equals(bean.pay_id)){
			ViewUtil.setText2TextView(holder.tvPayDescripe, "有支付宝账号的用户使用");
		}else if("10".equals(bean.pay_id)){
			ViewUtil.setText2TextView(holder.tvPayDescripe, "有微信账号的用户使用");
		}else if("3".equals(bean.pay_id)){
			ViewUtil.setText2TextView(holder.tvPayDescripe, "有网银账号的用户使用");
		}
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
			PayTypeBean bean = list.get(position);
			bean.isSelected = true;
			updateItem(bean, position);
			Intent data = new Intent(context, OrderSubmitActivity.class);
			data.putExtra("pay_id", bean.pay_id);
			data.putExtra("pay_name", bean.pay_name);
			((PayTypeActivity)context).setResult(Activity.RESULT_OK, data);
			((PayTypeActivity)context).finish();
		}
	}
	protected final class ViewHolder{
		/**收货人*/
		public TextView tvPayName;
		public TextView tvPayDescripe;
		public ImageView ivEdit;
		public View layoutPayType;
		public View lineTop;
		public View lineTopMargin0;
		public View lineTopMargin16;
		public View lineBottom0;
		public View lineLast;
	}
}
