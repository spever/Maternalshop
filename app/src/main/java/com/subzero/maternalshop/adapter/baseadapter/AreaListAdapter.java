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
import com.subzero.maternalshop.bean.AreaBean;
/**使用介绍，适配器
 * 宿主：
 * 	区域列表：AreaListActivity
 * */
public class AreaListAdapter extends BaseAdapter
{
	protected List<AreaBean> list;
	protected Context context;
	public AreaListAdapter(Context context) {
		this.list = new ArrayList<AreaBean>();
		this.context = context;
	}
	/**更新适配器*/
	public void updateItem(AreaBean obj,int position){
		list.set(position, obj);
		notifyDataSetChanged();
	}
	public void updateItem(List<AreaBean> list){
		for (int i = 0; (list!=null)&&(i < list.size()); i++){
			AreaBean bean = list.get(i);
			this.list.set(i, bean);
		}
		notifyDataSetChanged();
	}
	public void addItem(AreaBean obj){
		list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<AreaBean> list){
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void refreshItem(List<AreaBean> list){
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
	public List<AreaBean> getList(){
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_area_list, parent, false);
			holder.tvName =(TextView) convertView.findViewById(R.id.tv_name);
			holder.lineLast = convertView.findViewById(R.id.line_last);
			holder.lineTop = convertView.findViewById(R.id.line_top);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		/*第一条线*/
		if(position == 0){
			holder.lineTop.setVisibility(View.VISIBLE);
		}else{
			holder.lineTop.setVisibility(View.GONE);
		}
		/*最后一条线*/
		if(position == (list.size()-1)){
			holder.lineLast.setVisibility(View.VISIBLE);
		}else{
			holder.lineLast.setVisibility(View.GONE);
		}
		holder.tvName.setText(list.get(position).region_name);
		return convertView;
	}
	protected final class ViewHolder{
		/**产品描述*/
		public TextView tvName;
		/**做分隔线*/
		public View lineLast;
		public View lineTop;
	}
}
