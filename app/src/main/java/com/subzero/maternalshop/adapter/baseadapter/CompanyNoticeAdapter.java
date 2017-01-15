package com.subzero.maternalshop.adapter.baseadapter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.subzero.common.utils.ViewUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.bean.CompanyNoticeBean;
/**公司通知，适配器
 * 宿主：
 * 	使用介绍：CompanyNoticeActivity
 * */
public class CompanyNoticeAdapter extends BaseAdapter
{
	protected List<CompanyNoticeBean> list;
	protected Context context;
	public CompanyNoticeAdapter(Context context) {
		this.list = new ArrayList<CompanyNoticeBean>();
		this.context = context;
	}
	/**更新适配器*/
	public void updateItem(CompanyNoticeBean obj,int position){
		list.set(position, obj);
		notifyDataSetChanged();
	}
	public void updateItem(List<CompanyNoticeBean> list){
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void addItem(CompanyNoticeBean obj){
		list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<CompanyNoticeBean> list){
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_company_notice, parent, false);
			holder.tvTitle =(TextView) convertView.findViewById(R.id.tv_title);
			holder.tvContent =(TextView) convertView.findViewById(R.id.tv_content);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		ViewUtil.setText2TextView(holder.tvContent, list.get(position).content);
		ViewUtil.setText2TextView(holder.tvTitle, list.get(position).title);
		ViewUtil.setText2TextView(holder.tvTime, list.get(position).add_time);

		return convertView;
	}
	protected final class ViewHolder{
		public TextView tvTitle;
		public TextView tvContent;
		public TextView tvTime;
	}
}
