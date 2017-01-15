package com.subzero.maternalshop.adapter.baseadapter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.squareup.picasso.Picasso;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.common.view.CountLayout;
import com.subzero.common.view.CountLayout.OnCountClickListener;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.productdetail.ProductDetailActivity;
import com.subzero.maternalshop.bean.ProductBean;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
/**使用介绍，适配器
 * 宿主：
 * 	使用介绍：OperationNoticeActivity
 * 启动项：
 * 	商品详情：ProductDetailActivity
 * */
public class CartAdapter extends BaseHttpAdapter
{
	protected List<ProductBean> list;
	private TextView tvAmount;
	public CartAdapter(Context context) {
		this.list = new ArrayList<ProductBean>();
		this.context = context;
		initHttputils();
		initDialogLoading();
	}
	public void setAmount(TextView textView)	{
		this.tvAmount = textView;
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
	public void refreshItem(List<ProductBean> list){
		this.list.clear();
		this.list.addAll(list);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_fragment_cart, parent, false);
			holder.tvProductName =(TextView) convertView.findViewById(R.id.tv_product_name);
			holder.ivProductLogo =(ImageView) convertView.findViewById(R.id.iv_product_logo);
			holder.tvSalesVolume = (TextView) convertView.findViewById(R.id.tv_product_sales_volume);
			holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_product_price);
			holder.countLayout = (CountLayout) convertView.findViewById(R.id.cl_product);
			holder.lineTop = convertView.findViewById(R.id.line_top);
			holder.ivEdit = (ImageView) convertView.findViewById(R.id.iv_edit);
			holder.layoutBody = convertView.findViewById(R.id.layout_body);
			holder.lineLast = convertView.findViewById(R.id.line_last); 
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		/*解决 错位问题*/
		holder.lineTop.setTag(list.get(position).goods_id);
		String idTag = (String) holder.lineTop.getTag();
		holder.ivEdit.setSelected(false);
		if(idTag.equalsIgnoreCase(list.get(position).goods_id)){
			Picasso.with(context).load(list.get(position).url).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(holder.ivProductLogo);
			holder.ivEdit.setSelected(list.get(position).isSelected);
			ViewUtil.setText2TextView(holder.tvSalesVolume, App.getSalesVolumeLable(list.get(position).market_price));
			holder.countLayout.getEditText().setText(list.get(position).countIncart);
		}
		/*第一条线  的 上 间距；左间距*/
		if(position == 0){
			holder.lineTop.setVisibility(View.VISIBLE);
		}else{
			holder.lineTop.setVisibility(View.GONE);
		}
		/*解决 最后一条 线的 展示以隐藏*/
		if(position != (list.size()-1)){
			holder.lineLast.setVisibility(View.GONE);
		}else{
			holder.lineLast.setVisibility(View.VISIBLE);
		}
		ViewUtil.setText2TextView(holder.tvPrice, "￥"+list.get(position).shop_price);
		holder.tvProductName.setText(list.get(position).goods_name);
		holder.ivEdit.setOnClickListener(new MyOnClickListener(position,App.clickEditSelected));
		holder.layoutBody.setOnClickListener(new MyOnClickListener(position,App.clickBody));
		holder.countLayout.setOnCountClickListener(new MyOnCountClickListener(position));
		return convertView;
	}
	private final class MyOnCountClickListener implements OnCountClickListener
	{
		private int position;
		public MyOnCountClickListener(int position) {
			this.position = position;
		}
		@Override
		public void onCountClick(int count, boolean isAdd)
		{
			RequestParams params = new RequestParams(XUtil.charset);
			//?url=/cart&format=json&action=update&rec_id=&new_number=&uid=&sid=
			params.addQueryStringParameter("rec_id", list.get(position).rec_id);
			params.addQueryStringParameter("new_number", count+"");
			params.addQueryStringParameter("uid", User.getUserId(context));
			params.addQueryStringParameter("sid", User.getSID(context));
			LogUtils.e("uid = "+User.getUserId(context)+" sid = "+User.getSID(context));
			httpUtils.send(HttpMethod.GET, Url.cartUpdateApi, params , new CartUpdateCallBack(position, list.get(position).countIncart, count+""));
		}
	}
	/**xUtils.HttpUtils的回调
	 * */
	private final class CartUpdateCallBack extends RequestCallBack<String>
	{
		private int position;
		private String countNew;
		private String countOld;
		public CartUpdateCallBack(int position, String countOld, String countNew) {
			this.position = position;
			this.countOld = countOld;
			this.countNew = countNew;
		}
		@Override
		public void onStart()
		{
			super.onStart();
			dialogLoading.show();
		}
		@Override
		public void onFailure(HttpException error, String msg)
		{
			LogUtils.e("onFailure = "+XUtil.getErrorInfo(error));
			ToastUtil.longAtCenterInThread(context, XUtil.getErrorInfo(error));
			dialogLoading.dismiss();
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			dialogLoading.dismiss();
			String result = info.result;
			//LogUtils.e("result = "+result);
			JSONObject jsonObject = JsonSmartUtil.getJsonObject(result, "status");
			String code = JsonSmartUtil.getString(jsonObject, "code");
			if("200".equalsIgnoreCase(code)){
				ProductBean bean = list.get(position);
				bean.countIncart = countNew+"";
				updateItem(bean, position);
				calculateFormat(2);
			}else if("400".equalsIgnoreCase(code)){
				ProductBean bean = list.get(position);
				bean.countIncart = countOld+"";
				updateItem(bean, position);
				ToastUtil.longAtCenterInThread(context, JsonSmartUtil.getString(jsonObject, "error_desc"));
			}
		}
	}
	private final class MyOnClickListener implements OnClickListener
	{
		private String clickType;
		private int position;
		public MyOnClickListener(int position, String clickType) {
			this.position = position;
			this.clickType = clickType;
		}
		@Override
		public void onClick(View v)
		{
			if(App.clickEditSelected.equalsIgnoreCase(clickType)){
				/*点击 选中 */
				ProductBean bean = list.get(position);
				bean.isSelected = !bean.isSelected;
				updateItem(bean, position);
				calculateFormat(2);
			}else if(App.clickBody.equalsIgnoreCase(clickType)){
				Intent intent = new Intent(context, ProductDetailActivity.class);
				/*点击 布局主体 */
				context.startActivity(intent );
			}
		}
	}

	/**计算并格式化  仅保留 decimal 个小数*/
	public String calculateFormat(int decimal)
	{
		double banalce = 0;
		for (int i = 0; i < list.size(); i++)
		{
			ProductBean bean = list.get(i);
			if(bean.isSelected){
				LogUtils.e("shop_price = "+bean.shop_price);
				banalce = banalce + (Double)Double.parseDouble(bean.shop_price) * Integer.parseInt(bean.countIncart);
			}
		}

		NumberFormat numberFormat=NumberFormat.getNumberInstance() ;
		numberFormat.setMaximumFractionDigits(decimal+1); 
		String format = numberFormat.format(banalce);
		if(tvAmount!=null){
			tvAmount.setText(format);
		}
		return format;
	}
	protected final class ViewHolder{
		public TextView tvProductName;
		public ImageView ivProductLogo;
		/**月销量*/
		public TextView tvSalesVolume;
		/**价格*/
		public TextView tvPrice;
		public CountLayout countLayout;
		/**做分隔线*/
		public View lineTop;
		public ImageView ivEdit;
		public View layoutBody;
		public View lineLast;
	}
}
