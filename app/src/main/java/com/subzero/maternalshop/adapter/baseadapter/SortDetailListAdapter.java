package com.subzero.maternalshop.adapter.baseadapter;
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
import com.subzero.maternalshop.activity.shop.SortListInOneShopActivity;
import com.subzero.maternalshop.bean.ProductBean;
import com.subzero.maternalshop.bean.SortDetailListBean;
import com.subzero.maternalshop.config.App;
/**分类详情，列表，适配器
 * 宿主：
 * 	使用介绍：SortDetailListActivity
 * 启动项：
 * 	商品详情：ProductDetailActivity
 * */
public class SortDetailListAdapter extends BaseAdapter
{
	protected List<SortDetailListBean> list;
	protected Context context;
	/**从 哪个模块 进到 这个 模块的*/
	protected String moduleName;
	public SortDetailListAdapter(Context context) {
		this.list = new ArrayList<SortDetailListBean>();
		this.context = context;
	}
	/**更新适配器*/
	public void updateItem(SortDetailListBean obj,int position){
		list.set(position, obj);
		notifyDataSetChanged();
	}
	public void updateItem(List<SortDetailListBean> list){
		for (int i = 0; (list!=null)&&(i < list.size()); i++){
			SortDetailListBean bean = list.get(i);
			this.list.set(i, bean);
		}
		notifyDataSetChanged();
	}
	public void addItem(SortDetailListBean obj){
		list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<SortDetailListBean> list){
		if(list!=null){
			this.list.addAll(list);
		}
		notifyDataSetChanged();
	}
	public void refreshItem(List<SortDetailListBean> list){
		if(this.list!=null){
			this.list.clear();
		}
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
	public List<SortDetailListBean> getList(){
		return list;
	}
	public void removeItem(){
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
	public void setModuleName(String moduleName){
		this.moduleName = moduleName;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_sort, parent, false);
			holder.tvNameLeft =(TextView) convertView.findViewById(R.id.tv_product_name);
			holder.tvNameRight =(TextView) convertView.findViewById(R.id.tv_product_name_right);
			holder.tvPriceLeft =(TextView) convertView.findViewById(R.id.tv_product_price);
			holder.tvPriceRight =(TextView) convertView.findViewById(R.id.tv_product_price_right);
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
		ProductBean leftBean = list.get(position).leftBean;
		ProductBean rightBean = list.get(position).rightBean;
		/*解决 错位问题*/
		holder.ivProductLogoLeft.setTag(leftBean.goods_id);
		String idTag = (String) holder.ivProductLogoLeft.getTag();
		if(idTag.equalsIgnoreCase(leftBean.goods_id)){
			Picasso.with(context).load(leftBean.url).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(holder.ivProductLogoLeft);
			if(rightBean!=null){
				Picasso.with(context).load(rightBean.url).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(holder.ivProductLogoRight);
			}
		}
		ViewUtil.setText2TextView(holder.tvNameLeft, leftBean.goods_name);
		ViewUtil.setText2TextView(holder.tvPriceLeft, App.getShopPromotePrice(leftBean));
		/*展示 右半边数据*/
		if(rightBean!=null){
			holder.layoutBodyRight.setVisibility(View.VISIBLE);
			ViewUtil.setText2TextView(holder.tvNameRight, rightBean.goods_name);
			ViewUtil.setText2TextView(holder.tvPriceRight, App.getShopPromotePrice(rightBean));
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
			//LogUtils.e("goods_id = "+bean.goods_id);
			intent.putExtra("goodsID", bean.goods_id);
			LogUtils.e("moduleName = "+moduleName);
			if(SortListInOneShopActivity.class.getSimpleName().equalsIgnoreCase(moduleName)){
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			context.startActivity(intent);
		}
	}
	protected final class ViewHolder{
		public TextView tvNameLeft;
		public TextView tvNameRight;
		public TextView tvPriceLeft;
		public TextView tvPriceRight;
		public ImageView ivProductLogoLeft;
		public ImageView ivProductLogoRight;
		public View layoutBodyRight;
		public View layoutBodyLeft;
		public View lineLast;
	}
}
