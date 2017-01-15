package com.subzero.maternalshop.adapter.baseadapter.order;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.TextActivity;
import com.subzero.maternalshop.activity.order.CommentActivity;
import com.subzero.maternalshop.activity.order.OrderListActivity;
import com.subzero.maternalshop.activity.order.OrderRefundActivity;
import com.subzero.maternalshop.activity.productdetail.ProductDetailActivity;
import com.subzero.maternalshop.adapter.baseadapter.BaseHttpAdapter;
import com.subzero.maternalshop.bean.ProductBean;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.CallBackType;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.util.JsonCommonUtil;
/**使用介绍，适配器
 * 宿主：
 * 		订单列表-商品列表：OrderSubListActivity
 * 启动项：
 * 		申请退款：OrderRefundActivity
 * 		评论：CommentActivity
 * */
public class OrderSubListAdapter extends BaseHttpAdapter
{
	protected List<ProductBean> list;
	private String actionType;
	private String orderID;
	public OrderSubListAdapter(Context context) {
		this.list = new ArrayList<ProductBean>();
		this.context = context;
		initDialogLoading();
		initHttputils();
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
	public void setOrderID(String orderID){
		this.orderID = orderID;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_order_sub_list, parent, false);
			holder.tvProductName = (TextView) convertView.findViewById(R.id.tv_product_name);
			holder.tvProductPrice = (TextView) convertView.findViewById(R.id.tv_product_price);
			holder.tvSalseNum = (TextView) convertView.findViewById(R.id.tv_product_sales_volume);
			holder.tvProductCount = (TextView) convertView.findViewById(R.id.tv_product_count);
			holder.ivLogo = (ImageView) convertView.findViewById(R.id.iv_product_logo);
			holder.tvRight = (TextView) convertView.findViewById(R.id.tv_right);
			holder.tvLeft = (TextView) convertView.findViewById(R.id.tv_left);
			holder.layoutBody = convertView.findViewById(R.id.layout_body);
			holder.lineAction = convertView.findViewById(R.id.line_action);
			holder.layoutAction = convertView.findViewById(R.id.layout_action);
			//
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		ProductBean productBean = list.get(position);
		Picasso.with(context).load(productBean.url).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(holder.ivLogo);
		/*底部两个 按钮的显示问题*/
		ViewUtil.setText2TextView(holder.tvProductName, productBean.goods_name);
		ViewUtil.setText2TextView(holder.tvProductPrice, "￥"+productBean.shop_price);
		ViewUtil.setText2TextView(holder.tvSalseNum, App.getSalesVolumeLable(productBean.salesnum));
		ViewUtil.setText2TextView(holder.tvProductCount, "共"+productBean.goods_number+"件");
		ViewUtil.setText2TextView(holder.tvRight, App.getActionTypeLabel(actionType));

		if(App.actionNoPayed.equalsIgnoreCase(actionType) || App.actionGotoRefundOrder.equalsIgnoreCase(actionType)||App.actionGotoReceive.equalsIgnoreCase(actionType)||App.actionNoCommented.equalsIgnoreCase(actionType)){
			/*申请退款 已付款 未发货 去收货 已评论*/
			holder.tvRight.setText("申请退款");
			holder.tvLeft.setVisibility(View.INVISIBLE);
			holder.tvRight.setVisibility(View.VISIBLE);
		}else if(App.actionGotoComment.equalsIgnoreCase(actionType)){
			/*去评论*/
			holder.tvLeft.setText("申请退款");
			holder.tvRight.setText("去评论");
			holder.tvRight.setVisibility(View.VISIBLE);
		}else if(App.actionNoRefunding.equalsIgnoreCase(actionType) || App.actionNoRefunded.equalsIgnoreCase(actionType)||App.actionGotoPay.equalsIgnoreCase(actionType) || App.actionNoCanceled.equalsIgnoreCase(actionType)){
			/*退款中 已退款  去支付  已取消 */
			holder.layoutAction.setVisibility(View.GONE);
			holder.lineAction.setVisibility(View.GONE);
		}
		holder.tvLeft.setOnClickListener(new MyOnClickListener(position,App.clickLeft));
		holder.tvRight.setOnClickListener(new MyOnClickListener(position,App.clickRight));
		holder.layoutBody.setOnClickListener(new MyOnClickListener(position, App.clickBody));
		return convertView;
	}
	public void setActionType(String actionType){
		this.actionType = actionType;
	}
	private final class MyOnClickListener implements OnClickListener
	{
		private int position;
		private String clickType;
		/**@param position 位置
		 * @param clickType 点击的位置  clickLeft  clickRight  clickBody
		 * */
		public MyOnClickListener(int position, String clickType) {
			this.clickType = clickType;
		}
		@Override
		public void onClick(View v)
		{
			Intent intent = null;
			ProductBean bean = list.get(position);
			if(App.clickRight.equalsIgnoreCase(clickType)){
				if(App.actionGotoComment.equalsIgnoreCase(actionType)){
					/*已经收货  右按钮  去评论*/
					intent = new Intent(context, TextActivity.class);
					intent.putExtra("goodsID", bean.goods_id);
					context.startActivity(intent);
				}else if(App.actionNoPayed.equalsIgnoreCase(actionType) || App.actionGotoRefundOrder.equalsIgnoreCase(actionType)||App.actionGotoReceive.equalsIgnoreCase(actionType)||App.actionNoCommented.equalsIgnoreCase(actionType)){
					/*申请退款 已付款 未发货 去收货 已评论*/
					startOrderRefundActivity(list.get(position).goods_id);
				}else if(App.actionGotoComment.equalsIgnoreCase(actionType)){
					intent = new Intent(context, CommentActivity.class);
					intent.putExtra("goodsID", list.get(position).goods_id);
					context.startActivity(intent);
				}
			}else if(App.clickLeft.equalsIgnoreCase(clickType)){
				if(App.actionGotoComment.equalsIgnoreCase(actionType)){
					startOrderRefundActivity(list.get(position).goods_id);
				}
			}else if(App.clickBody.equalsIgnoreCase(clickType)){
				intent = new Intent(context, ProductDetailActivity.class);
				intent.putExtra("goods_id", list.get(position).goods_id);
				context.startActivity(intent);
			}
		}
	}
	private void startOrderRefundActivity(String goodsID)
	{
		Intent intent = new Intent(context, OrderRefundActivity.class);
		intent.putExtra("orderID", orderID);
		intent.putExtra("goodsID", goodsID);
		context.startActivity(intent);
	}
	public void requestHttp(String url, int position, CallBackType callBackType){
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("uid", User.getUserId(context));
		params.addQueryStringParameter("sid", User.getSID(context));
		httpUtils.send(HttpMethod.GET, url, params , new OrderCallBack(position, callBackType));
	}
	/**订单列表的 回调
	 * */
	private final class OrderCallBack extends RequestCallBack<String>
	{
		private CallBackType callBackType;
		private int position;
		public OrderCallBack(int position, CallBackType callBackType) {
			this.callBackType = callBackType;
			this.position = position;
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
			dialogLoading.dismiss();
			ToastUtil.longAtCenterInThread(context, XUtil.getErrorInfo(error));
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			String result = info.result;
			dialogLoading.dismiss();
			LogUtils.e("result = "+result);
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				((OrderListActivity)context).startLoginActvitiy((Activity)context, false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ToastUtil.shortAtCenterInThread(context, JsonCommonUtil.getCommonErrorInfo(result));
				return;
			}
			if(CallBackType.cancelOrder == callBackType){
				ToastUtil.shortAtTop(context, "取消成功");
				ProductBean bean = list.get(position);
				updateItem(bean, position);
			}else if(CallBackType.affirmReceived == callBackType){
				ToastUtil.shortAtTop(context, "确认收货");
			}
		}
	}
	protected final class ViewHolder{
		/**产品名称*/
		public TextView tvProductName;
		/**产品价格*/
		public TextView tvProductPrice;
		/**购买 数量*/
		public TextView tvSalseNum;
		/**购买 数量*/
		public TextView tvProductCount;
		/**去支付  去评价  已评价*/
		public TextView tvRight;
		public TextView tvLeft;
		public ImageView ivLogo;
		public View layoutBody;
		public View lineAction;
		public View layoutAction;
	}
}
