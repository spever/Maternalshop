package com.subzero.maternalshop.adapter.baseadapter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.subzero.common.utils.ViewUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.bean.HelperListBean;
import com.subzero.userman.helper.AboutUsActivity;
import com.subzero.userman.helper.CompanyNoticeActivity;
import com.subzero.userman.helper.OperationNoticeActivity;
/**使用介绍，适配器
 * 宿主：
 * 	收货地列表：AddrListActivity
 * 启动项：
 * 	收货地址：AddrDetailActivity
 * */
public class HelperListAdapter extends BaseHttpAdapter
{
	protected List<HelperListBean> list;
	public HelperListAdapter(Context context) {
		this.list = new ArrayList<HelperListBean>();
		this.context = context;
		initDialogLoading();
		initHttputils();
	}
	/**更新适配器*/
	public void updateItem(HelperListBean obj,int position){
		if((position<0) || (position > list.size()-1)){
			return;
		}
		list.set(position, obj);
		notifyDataSetChanged();
	}
	public void updateItem(List<HelperListBean> list){
		for (int i = 0; (list!=null)&&(i < list.size()); i++){
			HelperListBean bean = list.get(i);
			this.list.set(i, bean);
		}
		notifyDataSetChanged();
	}
	public void addItem(HelperListBean obj){
		list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<HelperListBean> list){
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void refreshItem(List<HelperListBean> list){
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
	public List<HelperListBean> getList(){
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_helper, parent, false);
			holder.tv =(TextView) convertView.findViewById(R.id.tv);
			holder.lineTop = convertView.findViewById(R.id.line_top);
			holder.lineLast = convertView.findViewById(R.id.line_last);
			holder.lineTopMargin0 = convertView.findViewById(R.id.line_top_margin0);
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
		}else{
			holder.lineLast.setVisibility(View.GONE);
		}
		ViewUtil.setText2TextView(holder.tv, list.get(position).cat_name);
		holder.tv.setOnClickListener(new MyOnClickListener(position));
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
			Intent intent = null;
			HelperListBean bean = getList().get(position);
			if("5".equalsIgnoreCase(bean.cat_id)){
				intent = new Intent(context, OperationNoticeActivity.class);
				intent.putExtra("cat_id", "5");
			}else if("7".equalsIgnoreCase(bean.cat_id)){
				intent = new Intent(context, CompanyNoticeActivity.class);
				intent.putExtra("cat_id", "7");
			}else if("8".equalsIgnoreCase(bean.cat_id)){
				intent = new Intent(context, AboutUsActivity.class);
				intent.putExtra("cat_id", "8");
			}
			context.startActivity(intent);
		}
	}
	protected final class ViewHolder{
		/**收货人*/
		public TextView tv;
		public View lineTop;
		public View lineTopMargin0;
		public View lineTopMargin16;
		public View lineLast;
	}
}
