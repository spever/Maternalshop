package com.subzero.maternalshop.adapter.baseadapter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.util.LogUtils;
import com.squareup.picasso.Picasso;
import com.subzero.common.utils.ScreenUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.bean.SortListBean;
/**使用介绍，适配器
 * 宿主：
 * 	使用介绍：OperationNoticeActivity
 * */
public class SortFragmentAdapter extends BaseAdapter
{
	protected List<SortListBean> list;
	protected Context context;
	private LayoutParams imageParams;
	
	public SortFragmentAdapter(Context context) {
		this.list = new ArrayList<SortListBean>();
		this.context = context;
		initImageParams(context);
	}
	public SortFragmentAdapter(Context context, List<SortListBean> list) {
		this.list = list;
		this.context = context;
		initImageParams(context);
	}
	private void initImageParams(Context context)
	{
		int screenWidth = ScreenUtil.getScreenWidth(context);
		int imageHeight = 9*screenWidth / 16;
		imageParams = new android.widget.LinearLayout.LayoutParams(screenWidth,imageHeight);
	}
	/**更新适配器*/
	public void updateItem(SortListBean obj,int position){
		list.set(position, obj);
		notifyDataSetChanged();
	}
	public void updateItem(List<SortListBean> list){
		for (int i = 0; (list!=null)&&(i < list.size()); i++){
			SortListBean bean = list.get(i);
			this.list.set(i, bean);
		}
		notifyDataSetChanged();
	}
	public void addItem(SortListBean obj){
		list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<SortListBean> list){
		if(list!=null){
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
	public List<SortListBean> getList(){
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_fragment_sort, parent, false);
			holder.tvDescribe =(TextView) convertView.findViewById(R.id.tv_describe);
			holder.ivProductLogo =(ImageView) convertView.findViewById(R.id.iv_product_logo);
			holder.lineLast = convertView.findViewById(R.id.line_last); 
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		/*解决 错位问题*/
		holder.ivProductLogo.setTag(list.get(position).cat_id);
		holder.ivProductLogo.setLayoutParams(imageParams);
		String idTag = (String) holder.ivProductLogo.getTag();
		if(idTag.equalsIgnoreCase(list.get(position).cat_id)){
			LogUtils.e("cat_img = "+list.get(position).cat_img);
			Picasso.with(context).load(list.get(position).cat_img).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(holder.ivProductLogo);
		}	
		/*解决 最后一条 线的 展示以隐藏*/
		if(position != (list.size()-1)){
			holder.lineLast.setVisibility(View.GONE);
		}else{
			holder.lineLast.setVisibility(View.VISIBLE);
		}
		ViewUtil.setText2TextView(holder.tvDescribe, list.get(position).cat_name);
		return convertView;
	}
	protected final class ViewHolder{
		public TextView tvDescribe;
		public ImageView ivProductLogo;
		public View lineLast;
	}
}
