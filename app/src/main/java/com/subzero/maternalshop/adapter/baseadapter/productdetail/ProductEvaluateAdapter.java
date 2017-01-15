package com.subzero.maternalshop.adapter.baseadapter.productdetail;
import java.util.ArrayList;
import java.util.List;

import subzero.angeldevil.AutoScrollViewPager;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.subzero.common.utils.ClassUtil;
import com.subzero.common.utils.ScreenUtil;
import com.subzero.common.utils.StringUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.common.view.CircleIndicator;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.adapter.pageradapter.WelcomeAdapter;
import com.subzero.maternalshop.bean.order.OrderListBeanType;
import com.subzero.maternalshop.bean.productdetail.EvaluateBean;
import com.subzero.maternalshop.config.App;
/**商品 评价，适配器
 * 宿主：
 * 	使用介绍：ProductDetailActivity
 * 启动项：
 * 	订单详情：OrderDetailActivity
 * */
public class ProductEvaluateAdapter extends BaseAdapter
{
	private String shopName;
	private String shopCity;
	/**促销价格*/
	private String promotion;
	protected List<EvaluateBean> list;
	protected Context context;
	private LayoutParams imageParams;
	public ProductEvaluateAdapter(Context context) {
		this.list = new ArrayList<EvaluateBean>();
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
	public void updateItem(EvaluateBean obj,int position){
		list.set(position, obj);
		notifyDataSetChanged();
	}
	public void updateItem(List<EvaluateBean> list){
		for (int i = 0; (list!=null)&&(i < list.size()); i++){
			EvaluateBean bean = list.get(i);
			this.list.set(i, bean);
		}
		notifyDataSetChanged();
	}
	public void addItem(EvaluateBean obj){
		list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<EvaluateBean> list){
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void refreshItem(List<EvaluateBean> list){
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
	public List<EvaluateBean> getList(){
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
	public int getItemViewType(int position) {
		if(list == null || list.size()<=0){
			return 0;
		}
		return list.get(position).listBeanType;
	}
	@Override
	public int getViewTypeCount() {
		return ClassUtil.getFieldsCount(OrderListBeanType.class);
	}
	public void setShopCity(String shopCity){
		this.shopCity = shopCity;
	}
	public void setShopName(String shopName){
		this.shopName = shopName;
	}
	public void setPromotion(String promotion){
		this.promotion = promotion;
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
			if(getItemViewType(position) == OrderListBeanType.one){
				convertView = LayoutInflater.from(context).inflate(R.layout.headview_activity_product_describe, null);
				holder.tvProductName = (TextView) convertView.findViewById(R.id.tv_product_name);
				holder.tvProductPrice = (TextView) convertView.findViewById(R.id.tv_product_price);
				holder.tvGiveIntegral = (TextView) convertView.findViewById(R.id.tv_points);
				holder.tvShippingFee = (TextView) convertView.findViewById(R.id.tv_express_fee);
				holder.tvSalesNum = (TextView) convertView.findViewById(R.id.tv_sale_count);
				holder.tvShippingCity = (TextView) convertView.findViewById(R.id.tv_city);
				holder.tvShippingAddress = (TextView) convertView.findViewById(R.id.tv_addr_goal);
				holder.tvShippingFuwu = (TextView) convertView.findViewById(R.id.tv_service);
				holder.tvComment = (TextView) convertView.findViewById(R.id.tv_comment);
				holder.tvShopName = (TextView) convertView.findViewById(R.id.tv_shop_name);
				holder.tvPromotion = (TextView) convertView.findViewById(R.id.tv_promotion);
				holder.autoScrollViewPager = (AutoScrollViewPager) convertView.findViewById(R.id.asvp);
				holder.circleIndicator = (CircleIndicator) convertView.findViewById(R.id.ci);
			}else if(getItemViewType(position) == OrderListBeanType.multiple){
				convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_product_detail_describe, parent, false);
				holder.tvTitle =(TextView) convertView.findViewById(R.id.tv_title);
				holder.tvContent =(TextView) convertView.findViewById(R.id.tv_content);
				holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
				holder.lineLast = convertView.findViewById(R.id.line_last);
			}
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		/*最后一根线的隐藏于显示*/
		if(position == (list.size()-1)){
			if(holder.lineLast!=null){
				holder.lineLast.setVisibility(View.VISIBLE);
			}
		}else{
			if(holder.lineLast!=null){
				holder.lineLast.setVisibility(View.GONE);
			}
		}
		EvaluateBean evaluateBean = list.get(position);
		if(list.get(position).listBeanType == OrderListBeanType.one){
			ViewUtil.setText2TextView(holder.tvProductName, evaluateBean.goods_name);
			ViewUtil.setText2TextView(holder.tvGiveIntegral, evaluateBean.give_integral+"积分");
			ViewUtil.setText2TextView(holder.tvShippingFee, "免运费");
			ViewUtil.setText2TextView(holder.tvSalesNum, App.getSalesVolumeLable(evaluateBean.salesnum));
			ViewUtil.setText2TextView(holder.tvShippingCity, shopCity);
			ViewUtil.setText2TextView(holder.tvShippingFuwu, evaluateBean.shipping_fuwu);
			ViewUtil.setText2TextView(holder.tvShopName, shopName);
			ViewUtil.setText2TextView(holder.tvPromotion, promotion);
			ViewUtil.setText2TextView(holder.tvComment, App.getCommentInProductDetailLable((list.size()-1)+""));
			/*售价：促销价*/
			String promote_price = evaluateBean.promote_price;
			/*售价：本店售价*/
			String shop_price = evaluateBean.shop_price;
			if((!TextUtils.isEmpty(promote_price)) && (!"0".equalsIgnoreCase(promote_price))){
				shop_price = promote_price;			
			}
			ViewUtil.setText2TextView(holder.tvProductPrice, "￥"+shop_price);		
			List<ImageView> listTopImg = new ArrayList<ImageView>();
			for (int i = 0; (evaluateBean.listImgUrl!=null)&&(i < evaluateBean.listImgUrl.size()); i++)
			{
				ImageView imageView = new ImageView(context);
				Picasso.with(context).load(evaluateBean.listImgUrl.get(i)).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(imageView);
				imageView.setScaleType(App.getScaleType());
				imageView.setLayoutParams(imageParams);
				listTopImg.add(imageView);
			}
			/*顶部广告*/
			WelcomeAdapter adapter = new WelcomeAdapter(listTopImg);
			holder.autoScrollViewPager.setAdapter(adapter);
			holder.autoScrollViewPager.setInterval(1000 *3);
			holder.autoScrollViewPager.startAutoScroll(1000 *3);
			if((listTopImg!=null) && (listTopImg.size()>0)){
				holder.circleIndicator.setViewPager(holder.autoScrollViewPager);
			}
		}else{
			ViewUtil.setText2TextView(holder.tvTitle, evaluateBean.title);
			ViewUtil.setText2TextView(holder.tvContent, evaluateBean.content);
			ViewUtil.setText2TextView(holder.tvTime, StringUtil.subStringBegin(evaluateBean.create, 10));
		}
		/*解决 错位问题*/
		return convertView;
	}
	protected final class ViewHolder{
		public AutoScrollViewPager autoScrollViewPager;
		public CircleIndicator circleIndicator;
		public TextView tvTitle;
		public TextView tvContent;
		public TextView tvProductName;
		/**积分*/
		public TextView tvGiveIntegral;
		/**月销量*/
		public TextView tvSalesNum;
		public TextView tvProductPrice;
		/**运费*/
		public TextView tvShippingFee;
		/**服务城市*/
		public TextView tvShippingCity;
		/**送至*/
		public TextView tvShippingAddress;
		/**服务*/
		public TextView tvShippingFuwu;
		public TextView tvComment;
		/**店铺名称*/
		public TextView tvShopName;
		/**促销活动*/
		public TextView tvPromotion;
		public TextView tvTime;
		public View lineLast;
	}
}
