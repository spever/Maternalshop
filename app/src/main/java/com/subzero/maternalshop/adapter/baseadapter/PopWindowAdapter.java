package com.subzero.maternalshop.adapter.baseadapter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.subzero.maternalshop.R;
import com.subzero.maternalshop.bean.LocationBean;
/**公司通知，适配器
 * 宿主：
 * 	使用介绍：CompanyNoticeActivity
 * */
public class PopWindowAdapter extends BaseAdapter
{
	protected List<LocationBean> list;
	protected Context context;
	public PopWindowAdapter(Context context) {
		this.list = new ArrayList<LocationBean>();
		this.context = context;
	}
	/**更新适配器*/
	public void updateItem(LocationBean obj,int position){
		list.set(position, obj);
		notifyDataSetChanged();
	}
	public void updateItem(List<LocationBean> list){
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void addItem(LocationBean obj){
		list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<LocationBean> list){
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void refreshItem(List<LocationBean> list){
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
	public List<LocationBean> getList(){
		return list;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_popwindow_location, parent, false);
			holder.tvLocation =(TextView) convertView.findViewById(R.id.tv);
			holder.lineTop = convertView.findViewById(R.id.line_top);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		if(position == 0){
			holder.lineTop.setVisibility(View.VISIBLE);
		}else{
			holder.lineTop.setVisibility(View.GONE);
		}
		holder.tvLocation.setText(list.get(position).name);
		return convertView;
	}
	protected final class ViewHolder{
		public TextView tvLocation;
		public View lineTop;
	}
}
