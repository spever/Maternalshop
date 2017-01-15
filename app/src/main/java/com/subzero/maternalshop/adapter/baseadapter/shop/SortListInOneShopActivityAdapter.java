package com.subzero.maternalshop.adapter.baseadapter.shop;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.shop.SortListInOneShopActivity;
import com.subzero.maternalshop.activity.sort.SortDetailListActivity;
import com.subzero.maternalshop.bean.shop.ProductClassBean;
import com.subzero.maternalshop.config.App;
/**使用介绍，适配器
 * 宿主：
 * 	店铺详情 宝贝分类 一级列表：SortListInOneShopActivity
 * 启动项：
 * 	店铺详情 宝贝分类 二级列表：SortDetailListActivity
 * */
public class SortListInOneShopActivityAdapter extends BaseAdapter
{
	protected List<ProductClassBean> list;
	protected Context context;
	public SortListInOneShopActivityAdapter(Context context) {
		this.list = new ArrayList<ProductClassBean>();
		this.context = context;
	}
	/**更新适配器*/
	public void updateItem(ProductClassBean obj,int position){
		list.set(position, obj);
		notifyDataSetChanged();
	}
	public void updateItem(List<ProductClassBean> list){
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void addItem(ProductClassBean obj){
		list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<ProductClassBean> list){
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_sort_list, parent, false);
			holder.tvTitle =(TextView) convertView.findViewById(R.id.tv_title);
			holder.lineTop = convertView.findViewById(R.id.line_top);
			holder.lineBottomMargin0 = convertView.findViewById(R.id.line_bottom_margin0);
			holder.lineBottomMargin16 = convertView.findViewById(R.id.line_bottom_margin16);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.tvTitle.setText(list.get(position).name);


		/*解决 错位问题*/
		holder.lineTop.setTag(list.get(position).id);
		String idTag = (String) holder.lineTop.getTag();
		if(idTag.equalsIgnoreCase(list.get(position).id)){
			/*第一条线  的 上 间距；左间距*/
			if(position == 0){
				holder.lineTop.setVisibility(View.VISIBLE);
			}else{
				holder.lineTop.setVisibility(View.GONE);
			}
			
			/*最后一条线；展示 隐藏*/
			if(position == (list.size()-1)){
				holder.lineBottomMargin0.setVisibility(View.VISIBLE);
				holder.lineBottomMargin16.setVisibility(View.GONE);
			}else{
				holder.lineBottomMargin0.setVisibility(View.GONE);
				holder.lineBottomMargin16.setVisibility(View.VISIBLE);
			}
		}
		holder.tvTitle.setOnClickListener(new MyOnClickListener(position));
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
			Intent intent = new Intent(context, SortDetailListActivity.class);
			ProductClassBean bean = list.get(position);
			intent.putExtra("catID", bean.id);
			intent.putExtra("catName", bean.name);
			intent.putExtra("suppID", bean.suppId);
			intent.putExtra(App.keyModuleName, SortListInOneShopActivity.class.getSimpleName());
			intent.putExtra("functionType", App.functionTypeSortInOneShop);
			context.startActivity(intent);
		}
	}
	protected final class ViewHolder{
		public TextView tvTitle;
		/**做分隔线*/
		public View lineTop;
		public View lineBottomMargin0;
		public View lineBottomMargin16;
	}
}
