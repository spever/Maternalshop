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
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.util.LogUtils;
import com.squareup.picasso.Picasso;
import com.subzero.common.utils.ViewUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.productdetail.ProductDetailActivity;
import com.subzero.maternalshop.bean.ProductBean;
import com.subzero.maternalshop.bean.shop.ShopDetailProductSortListBean;
import com.subzero.maternalshop.config.App;
/**商品详情分类列表，适配器
 * 宿主：
 * 	商品详情：ShopDetailActivity 
 * 启动项：
 * 	商品详情：ProductDetailActivity
 * */
public class ShopDetailProductListAdapter extends BaseAdapter
{
	protected List<ShopDetailProductSortListBean> list;
	protected Context context;
	public ShopDetailProductListAdapter(Context context) {
		this.list = new ArrayList<ShopDetailProductSortListBean>();
		this.context = context;
	}
	/**更新适配器*/
	public void updateItem(ShopDetailProductSortListBean obj,int position){
		list.set(position, obj);
		notifyDataSetChanged();
	}
	public void updateItem(List<ShopDetailProductSortListBean> list){
		for (int i = 0; (list!=null)&&(i < list.size()); i++){
			ShopDetailProductSortListBean bean = list.get(i);
			this.list.set(i, bean);
		}
		notifyDataSetChanged();
	}
	public void addItem(ShopDetailProductSortListBean obj){
		list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<ShopDetailProductSortListBean> list){
		if(list!=null){
			this.list.addAll(list);
		}
		notifyDataSetChanged();
	}
	public void refreshItem(List<ShopDetailProductSortListBean> list){
		if(list!=null){
			this.list.clear();
			this.list.addAll(list);
		}
		notifyDataSetChanged();
	}
	public void removeItem(int position){
		if(list == null || list.size()<=0 || position >= list.size() || position<0){
			return;
		}
		this.list.remove(position);
		notifyDataSetChanged();
	}
	public List<ShopDetailProductSortListBean> getList(){
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_shop_detail, parent, false);
			holder.tvProductNameLeft =(TextView) convertView.findViewById(R.id.tv_product_name);
			holder.tvProductNameRight =(TextView) convertView.findViewById(R.id.tv_product_name_right);
			holder.tvProductPriceLeft =(TextView) convertView.findViewById(R.id.tv_product_price);
			holder.tvProductPriceRight =(TextView) convertView.findViewById(R.id.tv_product_price_right);
			holder.ivProductLogoLeft =(ImageView) convertView.findViewById(R.id.iv_product_logo);
			holder.ivProductLogoRight =(ImageView) convertView.findViewById(R.id.iv_product_logo_right);
			holder.layoutBodyRight = convertView.findViewById(R.id.layout_body_riht);
			holder.layoutBodyLeft = convertView.findViewById(R.id.layout_body_left);
			holder.lineLast = convertView.findViewById(R.id.line_last); 
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		/*解决 最后一条 线的 展示以隐藏*/
		if(position != (list.size()-1)){
			holder.lineLast.setVisibility(View.GONE);
		}else{
			holder.lineLast.setVisibility(View.VISIBLE);
		}
		/*解决 错位问题*/
		ProductBean leftBean = list.get(position).leftBean;
		ProductBean rightBean = list.get(position).rightBean;
		holder.ivProductLogoLeft.setTag(leftBean.goods_id);
		String idTag = (String) holder.ivProductLogoLeft.getTag();
		if(idTag.equalsIgnoreCase(leftBean.goods_id)){
			Picasso.with(context).load(leftBean.url).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(holder.ivProductLogoLeft);
			if(rightBean!=null){
				Picasso.with(context).load(rightBean.url).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(holder.ivProductLogoRight);
			}
		}
		ViewUtil.setText2TextView(holder.tvProductNameLeft, leftBean.goods_name);
		ViewUtil.setText2TextView(holder.tvProductPriceLeft, App.getShopPromotePrice(leftBean));
		if(list.get(position).rightBean!=null){
			holder.layoutBodyRight.setVisibility(View.VISIBLE);
			ViewUtil.setText2TextView(holder.tvProductNameRight, rightBean.goods_name);
			ViewUtil.setText2TextView(holder.tvProductPriceRight, App.getShopPromotePrice(rightBean));
			LogUtils.e("rightBean.shop_price = "+rightBean.shop_price);
		}else{
			holder.layoutBodyRight.setVisibility(View.INVISIBLE);
		}
		holder.layoutBodyLeft.setOnClickListener(new MyOnClickListener(position, App.clickLeft));
		holder.layoutBodyRight.setOnClickListener(new MyOnClickListener(position, App.clickRight));
		return convertView;
	}
	private final class MyOnClickListener implements OnClickListener
	{
		private int position;
		private String clickType;
		public MyOnClickListener(int position, String clickType) {
			this.position = position;
			this.clickType  = clickType;
		}
		@Override
		public void onClick(View v)
		{
			ProductBean bean = null;
			if(App.clickLeft.equalsIgnoreCase(clickType)){
				bean = list.get(position).leftBean;
			}else{
				bean = list.get(position).rightBean;
			}
			Intent intent = new Intent(context, ProductDetailActivity.class);
			intent.putExtra("goodsID", bean.goods_id);
			context.startActivity(intent);
		}
	}
	protected final class ViewHolder{
		public TextView tvProductNameLeft;
		public TextView tvProductNameRight;
		public TextView tvProductPriceLeft;
		public TextView tvProductPriceRight;
		public ImageView ivProductLogoLeft;
		public ImageView ivProductLogoRight;
		public View layoutBodyRight;
		public View layoutBodyLeft;
		public View lineLast;
	}
}
