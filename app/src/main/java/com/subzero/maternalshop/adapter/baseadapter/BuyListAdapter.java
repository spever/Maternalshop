package com.subzero.maternalshop.adapter.baseadapter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.subzero.common.utils.ViewUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.bean.ProductBean;
/**使用介绍，适配器
 * 宿主：
 * 	使用介绍：OperationNoticeActivity
 * 启动项：
 * 	商品详情：ProductDetailActivity
 * */
public class BuyListAdapter extends BaseAdapter
{
	protected List<ProductBean> list;
	protected Context context;
	public BuyListAdapter(Context context) {
		this.list = new ArrayList<ProductBean>();
		this.context = context;
	}
	/**更新适配器*/
	public void updateItem(ProductBean obj,int position){
		list.set(position, obj);
		notifyDataSetChanged();
	}
	public void updateItem(List<ProductBean> list){
		for (int i = 0; (list!=null)&&(i < list.size()); i++){
			ProductBean bean = list.get(i);
			this.list.set(i, bean);
		}
		notifyDataSetChanged();
	}
	public void addItem(ProductBean obj){
		list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<ProductBean> list){
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
	public List<ProductBean> getList(){
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_buy_list, parent, false);
			holder.tvDescribe =(TextView) convertView.findViewById(R.id.tv_product_name);
			holder.tvSalesVolume =(TextView) convertView.findViewById(R.id.tv_product_sales_volume);
			holder.ivLogo = (ImageView) convertView.findViewById(R.id.iv_product_logo);
			holder.tvPricce = (TextView) convertView.findViewById(R.id.tv_product_price);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		ViewUtil.setText2TextView(holder.tvDescribe, list.get(position).goods_name);
		ViewUtil.setText2TextView(holder.tvSalesVolume, "共"+list.get(position).goods_number+"件");
		ViewUtil.setText2TextView(holder.tvPricce, "￥"+list.get(position).shop_price);
		Picasso.with(context).load(list.get(position).thumb).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(holder.ivLogo);
		return convertView;
	}
	protected final class ViewHolder{
		public TextView tvDescribe;
		public TextView tvSalesVolume;
		public TextView tvPricce;
		public ImageView ivLogo;
	}
}
