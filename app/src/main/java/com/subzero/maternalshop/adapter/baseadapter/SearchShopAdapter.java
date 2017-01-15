package com.subzero.maternalshop.adapter.baseadapter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.subzero.common.utils.ViewUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.bean.shop.ShopBean;
import com.subzero.maternalshop.config.App;
/**使用介绍，适配器
 * 宿主：
 * 	收货地列表：AddrListActivity
 * 启动项：
 * 	收货地址：AddrDetailActivity
 * */
public class SearchShopAdapter extends BaseHttpAdapter
{
	protected List<ShopBean> list;
	public SearchShopAdapter(Context context) {
		this.list = new ArrayList<ShopBean>();
		this.context = context;
		initDialogLoading();
		initHttputils();
	}
	/**更新适配器*/
	public void updateItem(ShopBean obj,int position){
		if((position<0) || (position > list.size()-1)){
			return;
		}
		list.set(position, obj);
		notifyDataSetChanged();
	}
	public void updateItem(List<ShopBean> list){
		for (int i = 0; (list!=null)&&(i < list.size()); i++){
			ShopBean bean = list.get(i);
			this.list.set(i, bean);
		}
		notifyDataSetChanged();
	}
	public void addItem(ShopBean obj){
		list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<ShopBean> list){
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void refreshItem(List<ShopBean> list){
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
	public List<ShopBean> getList(){
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_search_shop, parent, false);
			holder.tvShopName =(TextView) convertView.findViewById(R.id.tv_shop_name);
			holder.ivShopLogo = (ImageView) convertView.findViewById(R.id.iv_shop_logo);
			holder.tvSalesVolume = (TextView) convertView.findViewById(R.id.tv_product_sales_volume);
			holder.tvSalesType = (TextView) convertView.findViewById(R.id.tv_sales_type);
			holder.tvComment = (TextView) convertView.findViewById(R.id.tv_comment);
			holder.lineLast = convertView.findViewById(R.id.line_last);
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
		String logoUrl = list.get(position).shop_logo;
		/*解决 错位问题*/
		holder.ivShopLogo.setTag(list.get(position).supplier_id);
		String idTag = (String) holder.ivShopLogo.getTag();
		if(idTag.equalsIgnoreCase(list.get(position).supplier_id)){
			if(!TextUtils.isEmpty(logoUrl)){
				Picasso.with(context).load(logoUrl).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(holder.ivShopLogo);
			}
		}
		ViewUtil.setText2TextView(holder.tvShopName, list.get(position).supplier_name);
		ViewUtil.setText2TextView(holder.tvComment, App.getCommentLable(list.get(position).comment_count));
		ViewUtil.setText2TextView(holder.tvSalesType, App.getSalseTypeLable(list.get(position).company_intro));
		ViewUtil.setText2TextView(holder.tvSalesVolume, App.getSalesVolumeLable(list.get(position).order_count));
		return convertView;
	}
	protected final class ViewHolder{
		/**收货人*/
		public TextView tvShopName;
		public ImageView ivShopLogo;
		/**主营*/
		public TextView tvSalesType;
		/**月销量*/
		public TextView tvSalesVolume;
		/**评论数量*/
		public TextView tvComment;
		public View lineLast;
	}
}
