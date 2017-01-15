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
import com.subzero.maternalshop.bean.OperationNoticeBean;
/**使用介绍，适配器
 * 宿主：
 * 	使用介绍：OperationNoticeActivity
 * */
public class OperationNoticeAdapter extends BaseAdapter
{
	protected List<OperationNoticeBean> list;
	protected Context context;
	public OperationNoticeAdapter(Context context) {
		this.list = new ArrayList<OperationNoticeBean>();
		this.context = context;
	}
	/**更新适配器*/
	public void updateItem(OperationNoticeBean obj,int position){
		list.set(position, obj);
		notifyDataSetChanged();
	}
	public void updateItem(List<OperationNoticeBean> list){
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void addItem(OperationNoticeBean obj){
		list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<OperationNoticeBean> list){
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_operation_notice, parent, false);
			holder.tvTitle =(TextView) convertView.findViewById(R.id.tv_title);
			holder.tvContent =(TextView) convertView.findViewById(R.id.tv_content);
			holder.lineTop = convertView.findViewById(R.id.line_top);
			holder.lineTopMargin0 = convertView.findViewById(R.id.line_top_margin0);
			holder.lineTopMargin16 = convertView.findViewById(R.id.line_top_margin16);
			holder.lineLast = convertView.findViewById(R.id.line_last);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		ViewUtil.setText2TextView(holder.tvTitle, "Q. "+list.get(position).title);
		ViewUtil.setText2TextView(holder.tvContent, "A. "+list.get(position).content);
		/*解决 错位问题*/
		holder.lineTop.setTag(list.get(position).id);
		String idTag = (String) holder.lineTop.getTag();
		if(idTag.equalsIgnoreCase(list.get(position).id)){
			/*第一条线  的 上 间距；左间距*/
			if(position == 0){
				holder.lineTop.setVisibility(View.VISIBLE);
				holder.lineTopMargin0.setVisibility(View.VISIBLE);
				holder.lineTopMargin16.setVisibility(View.GONE);
			}else{
				holder.lineTop.setVisibility(View.GONE);
				holder.lineTopMargin0.setVisibility(View.GONE);
				holder.lineTopMargin16.setVisibility(View.VISIBLE);
			}
			
			/*最后一条线；展示 隐藏*/
			if(position == (list.size()-1)){
				holder.lineLast.setVisibility(View.VISIBLE);
			}else{
				holder.lineLast.setVisibility(View.GONE);
			}
		}
		return convertView;
	}
	protected final class ViewHolder{
		public TextView tvTitle;
		public TextView tvContent;
		/**做分隔线*/
		public View lineTop;
		public View lineTopMargin0;
		public View lineTopMargin16;
		public View lineLast;
	}
}
