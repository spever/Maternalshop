package com.subzero.maternalshop.adapter.baseadapter.order;
import java.io.Serializable;
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
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;
import com.subzero.common.utils.ClassUtil;
import com.subzero.common.utils.ObjUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.order.CommentActivity;
import com.subzero.maternalshop.activity.order.OrderDetailActivity;
import com.subzero.maternalshop.activity.order.OrderListActivity;
import com.subzero.maternalshop.activity.order.OrderPayActivity;
import com.subzero.maternalshop.activity.order.OrderRefundActivity;
import com.subzero.maternalshop.activity.order.OrderSubListActivity;
import com.subzero.maternalshop.activity.productdetail.ProductDetailActivity;
import com.subzero.maternalshop.adapter.baseadapter.BaseHttpAdapter;
import com.subzero.maternalshop.bean.ProductBean;
import com.subzero.maternalshop.bean.order.OrderListBean;
import com.subzero.maternalshop.bean.order.OrderListBeanType;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.CallBackType;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.util.JsonCommonUtil;
/**使用介绍，适配器
 * 宿主：
 * 	订单列表：OrderListActivity
 * 启动项：
 * 	订单列表-商品列表：OrderSubListActivity
 * 	订单支付：OrderPayActivity
 * 	商品详情：ProductDetailActivity
 * 	商品评价：CommentActivity
 * 	订单详情：OrderDetailActivity
 * 	申请退款：OrderRefundActivity
 * */
public class OrderListAdapter extends BaseHttpAdapter
{
	protected List<OrderListBean> list;
	public OrderListAdapter(Context context) {
		this.list = new ArrayList<OrderListBean>();
		this.context = context;
		initDialogLoading();
		initHttputils();
	}
	/**更新适配器*/
	public void updateItem(OrderListBean obj,int position){
		list.set(position, obj);
		notifyDataSetChanged();
	}
	public void updateItem(List<OrderListBean> list){
		for (int i = 0; (list!=null)&&(i < list.size()); i++){
			OrderListBean bean = list.get(i);
			this.list.set(i, bean);
		}
		notifyDataSetChanged();
	}
	public void addItem(OrderListBean obj){
		list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<OrderListBean> list){
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void refreshItem(List<OrderListBean> list){
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
	public List<OrderListBean> getList(){
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
		return list.get(position).orderListBeanType;
	}
	@Override
	public int getViewTypeCount() {
		return ClassUtil.getFieldsCount(OrderListBeanType.class);
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
				convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_order_list_one, parent, false);
				holder.tvProductName = (TextView) convertView.findViewById(R.id.tv_product_name);
				holder.tvProductPrice = (TextView) convertView.findViewById(R.id.tv_product_price);
				holder.tvNumInCart = (TextView) convertView.findViewById(R.id.tv_product_sales_volume);

			}else if(getItemViewType(position) == OrderListBeanType.multiple){
				convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_order_list_mulpitle, parent, false);
				holder.tvNumInCart = (TextView) convertView.findViewById(R.id.tv_count);
				holder.ivLogo2 = (ImageView) convertView.findViewById(R.id.iv_product_logo_2);
				holder.ivLogo3 = (ImageView) convertView.findViewById(R.id.iv_product_logo_3);
			}
			holder.ivLogo = (ImageView) convertView.findViewById(R.id.iv_product_logo);
			holder.tvOrderNO =(TextView) convertView.findViewById(R.id.tv_order_no_value);
			holder.tvOrderStatus = (TextView) convertView.findViewById(R.id.tv_order_status);
			holder.tvLeft = (TextView) convertView.findViewById(R.id.tv_left);
			holder.tvRight = (TextView) convertView.findViewById(R.id.tv_right);
			holder.lineLast = convertView.findViewById(R.id.line_last);
			holder.layoutOrder = convertView.findViewById(R.id.layout_order);
			holder.layoutBody = convertView.findViewById(R.id.layout_body);
			//
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}

		OrderListBean orderListBean = list.get(position);
		List<ProductBean> listProduct = orderListBean.listProduct;
		/*解决 错位问题*/
		holder.ivLogo.setTag(orderListBean.order_id);
		String idTag = (String) holder.ivLogo.getTag();
		if(idTag.equalsIgnoreCase(orderListBean.order_id)){
			if(!ObjUtil.isListEmpty(listProduct)){
				//LogUtils.e("listProduct = "+listProduct.size());
				Picasso.with(context).load(listProduct.get(0).url).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(holder.ivLogo);
				if(listProduct.size()==2){
					Picasso.with(context).load(listProduct.get(1).url).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(holder.ivLogo2);
					holder.ivLogo3.setVisibility(View.GONE);
				}else if(listProduct.size()==3){
					Picasso.with(context).load(listProduct.get(1).url).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(holder.ivLogo2);
					Picasso.with(context).load(listProduct.get(2).url).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(holder.ivLogo3);
					holder.ivLogo2.setVisibility(View.VISIBLE);
					holder.ivLogo3.setVisibility(View.VISIBLE);
				}
			}
		}
		/*最后一条线*/
		if(position == (list.size()-1)){
			holder.lineLast.setVisibility(View.VISIBLE);
		}else{
			holder.lineLast.setVisibility(View.GONE);
		}
		/*未付款  付款中  已付款*/
		String pay_status = orderListBean.pay_status;
		/*未确认  已确认*/
		String order_status = orderListBean.order_status;
		/*未发货  配货中  已发货  收货确认*/
		String shipping_status = orderListBean.shipping_status;
		/*1 已评论    0 未评论*/
		String goods_comment = "0";
		if(!ObjUtil.isListEmpty(listProduct)){
			goods_comment = listProduct.get(0).goods_comment;
		}
		String actionTypeRightButton = null;
		String actionTypeLeftButton = null;
		if(App.orderStatusun.equalsIgnoreCase(order_status)){
			/*未确认*/
			ViewUtil.setText2TextView(holder.tvOrderStatus, "未完成订单");
			holder.tvLeft.setVisibility(View.VISIBLE);
			ViewUtil.setText2TextView(holder.tvLeft, "取消订单");
			//holder.tvLeft.setBackgroundResource(R.drawable.selector_activity_order_list_action_evaluating);
			ViewUtil.setText2TextView(holder.tvRight, "去付款");
			holder.tvRight.setBackgroundResource(R.drawable.selector_activity_order_list_action_paying);
			/*给 按钮 分配点击事件  订单未确认  右按钮  去支付   左按钮   取消订单*/
			actionTypeRightButton = App.actionGotoPay;
			actionTypeLeftButton = App.actionGotoPayCancle;
		}else if(App.orderStatused.equalsIgnoreCase(order_status)){
			/*已确认*/
			ViewUtil.setText2TextView(holder.tvOrderStatus, "已完成订单");
			if(App.payStatusun.equalsIgnoreCase(pay_status)){
				/*已确认 + 未付款 			 ----> 			去付款*/
				ViewUtil.setText2TextView(holder.tvOrderStatus, "已确认");
				ViewUtil.setText2TextView(holder.tvLeft, "取消订单");
				ViewUtil.setText2TextView(holder.tvRight, "去付款");
				holder.tvRight.setBackgroundResource(R.drawable.selector_activity_order_list_action_paying);
				/*给 按钮 分配点击事件  订单未确认  右按钮  去支付   左按钮   取消订单*/
				actionTypeRightButton = App.actionGotoPay;
				actionTypeLeftButton = App.actionGotoPayCancle;
			}else if(App.shippingStatusun.equalsIgnoreCase(shipping_status) || App.shippingStatusing.equalsIgnoreCase(shipping_status)){
				/*已确认 + 配货中|未发货 		 ----> 		已付款*/
				ViewUtil.setText2TextView(holder.tvOrderStatus, shipping_status);
				ViewUtil.setText2TextView(holder.tvRight, pay_status);
				ViewUtil.setText2TextView(holder.tvLeft, "申请退款");
				actionTypeRightButton = App.actionNoPayed;
				actionTypeLeftButton = App.actionGotoRefundOrder;
			}else if(App.shippingStatused.equalsIgnoreCase(shipping_status)){
				ViewUtil.setText2TextView(holder.tvOrderStatus, shipping_status);
				ViewUtil.setText2TextView(holder.tvRight, "确认收货");
				holder.tvRight.setBackgroundResource(R.drawable.selector_activity_order_list_action_paying);
				ViewUtil.setText2TextView(holder.tvLeft, "申请退款");
				/*已确认 + 已发货 		 ----> 		去收货*/
				actionTypeRightButton = App.actionGotoReceive;
				actionTypeLeftButton = App.actionGotoRefundOrder;
			}else if(App.shippingStatusFinished.equalsIgnoreCase(shipping_status)){
				ViewUtil.setText2TextView(holder.tvOrderStatus, "已完成订单");
				//holder.tvLeft.setVisibility(View.GONE);
				ViewUtil.setText2TextView(holder.tvLeft, "申请退款");
				if(!"0".equals(goods_comment)){
					/*已确认 + 已经收货 + 已经评论   	---->	  已评论 */
					ViewUtil.setText2TextView(holder.tvRight, "已评论");
					actionTypeRightButton = App.actionNoCommented;
				}else{
					/*已确认 + 已经收货 + 没有评论   	---->	  去评论 */
					ViewUtil.setText2TextView(holder.tvRight, "去评价");
					holder.tvRight.setBackgroundResource(R.drawable.selector_activity_order_list_action_paying);
					actionTypeRightButton = App.actionGotoComment;
				}
				actionTypeLeftButton = App.actionGotoRefundOrder;
			}
			if("1".equalsIgnoreCase(orderListBean.is_back)){
				/*退款中*/
				holder.tvLeft.setVisibility(View.GONE);
				holder.tvRight.setText("退款中");
				actionTypeRightButton=App.actionNoRefunding;
			}else if("2".equalsIgnoreCase(orderListBean.is_back)){
				/*已退款*/
				holder.tvLeft.setVisibility(View.GONE);
				holder.tvRight.setText("已退款");
				actionTypeRightButton=App.actionNoRefunded;
			}
		}else if(App.orderStatusCanceled.equalsIgnoreCase(order_status)){
			/*已取消   	---->	  已取消 */
			ViewUtil.setText2TextView(holder.tvOrderStatus, "已取消");
			holder.tvLeft.setVisibility(View.GONE);
			ViewUtil.setText2TextView(holder.tvRight, "已取消");
			actionTypeRightButton = App.actionNoCanceled;
		}
		ViewUtil.setText2TextView(holder.tvOrderNO, orderListBean.order_sn);
		ProductBean productBean0 = listProduct.get(0);
		ViewUtil.setText2TextView(holder.tvProductName, productBean0.goods_name);
		if(!ObjUtil.isListEmpty(listProduct)){
			ViewUtil.setText2TextView(holder.tvProductPrice,"￥"+productBean0.shop_price);
			ViewUtil.setText2TextView(holder.tvNumInCart, "共"+productBean0.goods_number+"件");
		}
		if(getItemViewType(position) == OrderListBeanType.multiple){
			ViewUtil.setText2TextView(holder.tvNumInCart, "共"+orderListBean.totalCommodity+"件");
		}
		holder.layoutOrder.setOnClickListener(new MyOnClickListener(position, App.clickOrder,actionTypeRightButton));
		holder.layoutBody.setOnClickListener(new MyOnClickListener(position, App.clickBody,actionTypeRightButton));
		holder.tvLeft.setOnClickListener(new MyOnClickListener(position, App.clickLeft, actionTypeLeftButton));
		holder.tvRight.setOnClickListener(new MyOnClickListener(position,App.clickRight, actionTypeRightButton));
		return convertView;
	}
	private final class MyOnClickListener implements OnClickListener
	{
		private int position;
		private String clickType;
		private String actionType;
		/**@param position 位置
		 * @param clickType 点击的位置  clickLeft  clickRight  clickBody
		 * @param actionType 事件类型 
		 * */
		public MyOnClickListener(int position, String clickType, String actionType) {
			this.position = position;
			this.clickType = clickType;
			this.actionType = actionType;
		}
		@Override
		public void onClick(View v)
		{
			Intent intent = null;
			if(App.clickLeft.equalsIgnoreCase(clickType)){
				if(App.actionGotoPayCancle.equalsIgnoreCase(actionType)){
					KLog.e("未确认的订单  ---->  取消订单");
					requestHttp(Url.orderCancelApi, position,CallBackType.cancelOrder);
				}else if(App.actionGotoRefundOrder.equalsIgnoreCase(actionType)){
					KLog.e("要退款的订单  ---->  申请退款");
					OrderListBean bean = list.get(position);
					if(bean.listProduct.size() == 1){
						intent = new Intent(context, OrderRefundActivity.class);
						intent.putExtra("orderID", bean.order_id);
						intent.putExtra("goodsID", bean.listProduct.get(0).goods_id);
						context.startActivity(intent);
					}else{
						List<ProductBean> listProduct = list.get(position).listProduct;
						startOrderSubListActivity(bean.order_id, listProduct, actionType);
					}
				}
			}else if(App.clickRight.equalsIgnoreCase(clickType)){
				if(App.actionGotoPay.equalsIgnoreCase(actionType)){
					KLog.e("未确认的订单  ---->  去支付");
					OrderListBean bean = list.get(position);
					intent = new Intent(context, OrderPayActivity.class);
					LogUtils.e("recID = "+App.recIDList2String(list.get(position).listProduct));
					intent.putExtra("recID", App.recIDList2String(list.get(position).listProduct));
					intent.putExtra("payID", bean.pay_id);
					intent.putExtra("orderID", bean.order_id);
					intent.putExtra("orderNO", bean.order_sn);
					intent.putExtra("orderTime", bean.order_time);
					intent.putExtra("orderAmount", bean.total_fee);
					intent.putExtra("alipaySubject", bean.listProduct.get(0).goods_name+"等");
					intent.putExtra("alipayBody", bean.listProduct.get(0).goods_name+"等");
					intent.putExtra("orderPayType", App.orderPayTypeNOSubmit);
					intent.putExtra(App.keyModuleName, OrderListActivity.class.getSimpleName());
					((OrderListActivity)context).startActivityForResult(intent, OrderListActivity.requestCodePayFinish);
				}else if(App.actionGotoReceive.equalsIgnoreCase(actionType)){
					KLog.e("已确认 + 已付款 + 已发货 的订单  ---->  去收货");
					requestHttp(Url.orderAffirmReceived, position,CallBackType.affirmReceived);
				}else if(App.actionGotoComment.equalsIgnoreCase(actionType)){
					KLog.e("已确认 + 已付款 + 已发货 + 已收货 + 未评论的订单  ---->  去评论");
					intent = new Intent(context, CommentActivity.class);
					intent.putExtra("goodsID", list.get(position).listProduct.get(0).goods_id);
					((Activity)context).startActivityForResult(intent, OrderListActivity.requestCodeUpdateComment);
				}else{
					LogUtils.e("去 商品列表");
					List<ProductBean> listProduct = list.get(position).listProduct;
					startOrderSubListActivity(list.get(position).order_id, listProduct, actionType);
				}
			}else if(App.clickBody.equalsIgnoreCase(clickType)){
				List<ProductBean> listProduct = list.get(position).listProduct;
				if(listProduct.size() <=1){
					intent = new Intent(context, ProductDetailActivity.class);
					intent.putExtra("goodsID", listProduct.get(0).goods_id);
					context.startActivity(intent);
				}else {
					startOrderSubListActivity(list.get(position).order_id, listProduct, actionType);
				}
			}else if(App.clickOrder.equalsIgnoreCase(clickType)){
				OrderListBean bean = list.get(position);
				intent = new Intent(context, OrderDetailActivity.class);
				intent.putExtra("orderID", bean.order_id);
				intent.putExtra("actionType", actionType);
				intent.putExtra("goodsID", bean.listProduct.get(0).goods_id);
				intent.putExtra("isSingle", (bean.listProduct.size()==1));
				context.startActivity(intent);
			}
		}
	}
	/**打开列表页*/
	public void startOrderSubListActivity(String orderID, List<ProductBean> listProduct, String actionType){
		int productCount = 0;
		for (int i = 0; (listProduct!=null) && (i<listProduct.size()); i++){
			ProductBean bean = listProduct.get(i);
			productCount += Integer.parseInt(bean.goods_number);
		}
		LogUtils.e("productCount = "+productCount);
		Intent intent = new Intent(context, OrderSubListActivity.class);
		intent.putExtra("listProduct", (Serializable)listProduct);
		intent.putExtra("productCount", productCount+"");
		intent.putExtra("actionType", actionType);
		intent.putExtra("orderID", orderID);
		context.startActivity(intent);
	}
	public void requestHttp(String url, int position, CallBackType callBackType){
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("order_id", list.get(position).order_id);
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
				OrderListBean bean = list.get(position);
				bean.order_status = App.orderStatusCanceled;
				updateItem(bean, position);
			}else if(CallBackType.affirmReceived == callBackType){
				ToastUtil.shortAtTop(context, "确认收货");
				OrderListBean bean = list.get(position);
				bean.shipping_status = App.shippingStatusFinished;
				updateItem(bean, position);
			}
		}
	}
	protected final class ViewHolder{
		public View layoutOrder;
		/**产品名称*/
		public TextView tvProductName;
		/**做分隔线*/
		public View lineLast;
		/**订单编号*/
		public TextView tvOrderNO;
		/**订单状态*/
		public TextView tvOrderStatus;
		/**产品价格*/
		public TextView tvProductPrice;
		/**购买 数量*/
		public TextView tvNumInCart;
		/**去支付  去评价  已评价*/
		public TextView tvRight;
		/**去支付  去评价  已评价*/
		public TextView tvLeft;
		public ImageView ivLogo;
		public ImageView ivLogo2;
		public ImageView ivLogo3;
		public View layoutBody;
	}
}
