package com.subzero.maternalshop.activity.order;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import subzero.maxwin.XListView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.squareup.picasso.Picasso;
import com.subzero.common.dialog.BaseDialog.AnimType;
import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.bean.ProductBean;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.Cache;
import com.subzero.maternalshop.config.CallBackType;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.dialog.ContentDialog;
import com.subzero.maternalshop.util.JsonCommonUtil;
/**
 * 回复
 * 启动者：
 * 		PendingActivity(PendingAdapter)
 * 		ProductDetailEditActivity
 * 启动项：
 * 		商品列表：OrderSubListActivity
 * 		申请退款：OrderRefundActivity
 * 		订单支付：OrderPayActivity
 * */
public class OrderDetailActivity extends BaseActivity
{
	private String payID = null;
	private ContentDialog dialogAddr;
	private String orderID = null;
	private String goodsID;
	/**是 一种 商品*/
	private boolean isSingle;
	private String address;
	private String addTime = null;
	private String actionType;
	private int productCount;
	private List<Integer> listResIDIV;
	private ArrayList<ProductBean> listProductBean;
	private String orderNO;
	private String orderTime;
	private String orderAmount;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);
		Intent intent = getIntent();
		moduleName = intent.getStringExtra(App.keyModuleName);
		orderID = intent.getStringExtra("orderID");
		actionType = intent.getStringExtra("actionType");
		goodsID = intent.getStringExtra("goodsID");
		isSingle = intent.getBooleanExtra("isSingle", false);
		LogUtils.e("actionType = "+actionType);
		listResIDIV = new ArrayList<Integer>();
		initDialogAddr();
		initView();
		loadJsonData();
	}
	private void initDialogAddr()
	{
		dialogAddr = new ContentDialog(context);
		dialogAddr.setScaleWidth(0.8F);
	}
	private void loadJsonData()
	{
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("uid", User.getUserId(context));
		params.addQueryStringParameter("sid", User.getSID(context));
		params.addQueryStringParameter("order_id", orderID);
		LogUtils.e(XUtil.params2String(params));
		httpHandler = httpUtils.send(HttpMethod.POST, Url.orderDetail, params , new OrderDetailCallBack());
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		if(App.orderListMustRefresh){
			finish();
		}
	}
	@Override
	public void initView()
	{
		listResIDIV.add(R.id.iv_product_logo);
		listResIDIV.add(R.id.iv_product_logo_2);
		listResIDIV.add(R.id.iv_product_logo_3);
		TextView tvSubmit = (TextView) findViewById(R.id.tv_buy);
		if(App.actionNoCanceled.equalsIgnoreCase(actionType)){
			tvSubmit.setText("已取消");
		}else if(App.actionNoPayed.equalsIgnoreCase(actionType)){
			tvSubmit.setText("申请退款");
		}else if(App.actionNoCommented.equalsIgnoreCase(actionType)){
			tvSubmit.setText("申请退款");
		}else if(App.actionGotoComment.equalsIgnoreCase(actionType)){
			tvSubmit.setText("去评价");
		}else if(App.actionGotoPay.equalsIgnoreCase(actionType)){
			tvSubmit.setText("去支付");
		}else if(App.actionGotoRefundOrder.equalsIgnoreCase(actionType)){
			tvSubmit.setText("申请退款");
		}else if(App.actionGotoReceive.equalsIgnoreCase(actionType)){
			tvSubmit.setText("去收货");
		}else if(App.actionNoRefunding.equalsIgnoreCase(actionType)){
			tvSubmit.setText("退款中");
		}else if(App.actionNoRefunded.equalsIgnoreCase(actionType)){
			tvSubmit.setText("已退款");
		}
		tvSubmit.setOnClickListener(new MyOnClickListener());
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context,CommonClickType.back));
		findViewById(R.id.layout_addr).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_addr).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_addr_left).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.layout_order_detail_one).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.layout_order_detail_multiple).setOnClickListener(new MyOnClickListener());
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if((R.id.tv_addr == v.getId()) || (R.id.tv_addr_left == v.getId()) || (R.id.layout_addr == v.getId())){
				dialogAddr.setContent(address);
				dialogAddr.show(AnimType.centerScale);
			}else if(R.id.layout_order_detail_one == v.getId()){
				//startProductListActivity();
			}else if(R.id.layout_order_detail_multiple == v.getId()){
				startProductListActivity();
			}else if(R.id.tv_buy == v.getId()){
				requestHttp();
			}
		}
	}
	public void requestHttp()
	{
		Intent intent = null;
		CallBackType callBackType = null;
		String url = null;
		if(App.actionNoCanceled.equalsIgnoreCase(actionType)){
			if(!isSingle){
				startProductListActivity();
			}else{
				ToastUtil.shortAtCenter(context, "已取消");
			}
			return ;
		}else if(App.actionGotoComment.equalsIgnoreCase(actionType)){
			if(!isSingle){
				startProductListActivity();
			}else{
				intent = new Intent(context, CommentActivity.class);
				intent.putExtra("goodsID", goodsID);
				startActivity(intent);
			}
			return ;
		}else if(App.actionGotoPay.equalsIgnoreCase(actionType)){
			intent = new Intent(context, OrderPayActivity.class);
			intent.putExtra("recID", App.recIDList2String(listProductBean));
			intent.putExtra("payID", payID);
			intent.putExtra("orderID", orderID);
			intent.putExtra("orderNO", orderNO);
			intent.putExtra("orderTime", orderTime);
			intent.putExtra("orderAmount", orderAmount);
			intent.putExtra("alipaySubject", listProductBean.get(0).goods_name+"等");
			intent.putExtra("alipayBody", listProductBean.get(0).goods_name+"等");
			intent.putExtra("orderPayType", App.orderPayTypeNOSubmit);
			LogUtils.e("recID = "+App.recIDList2String(listProductBean)+" payID = "+payID+" orderID = "+orderID+" orderNO = "+orderNO+" orderTime = "+orderTime+" orderAmount = "+orderAmount);
			intent.putExtra(App.keyModuleName, OrderDetailActivity.class.getSimpleName());
			startActivity(intent);
			return ;
		}else if(App.actionGotoRefundOrder.equalsIgnoreCase(actionType) || App.actionNoPayed.equalsIgnoreCase(actionType)||App.actionNoCommented.equalsIgnoreCase(actionType)){
			/*申请退款*/
			if(!isSingle){
				startProductListActivity();
			}else{
				intent = new Intent(context, OrderRefundActivity.class);
				intent.putExtra("orderID", orderID);
				intent.putExtra("goodsID", goodsID);
				startActivity(intent);
			}
			return ;
		}else if(App.actionGotoReceive.equalsIgnoreCase(actionType)){
			//tvSubmit.setText("去收货");

		}else if(App.actionNoRefunding.equalsIgnoreCase(actionType)){
			if(!isSingle){
				startProductListActivity();
			}else{
				ToastUtil.shortAtCenter(context, "退款中");
			}
			return ;
		}else if(App.actionNoRefunded.equalsIgnoreCase(actionType)){
			if(!isSingle){
				startProductListActivity();
			}else{
				ToastUtil.shortAtCenter(context, "已退款");
			}
			return ;
		}

		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("order_id", orderID);
		params.addQueryStringParameter("uid", User.getUserId(context));
		params.addQueryStringParameter("sid", User.getSID(context));
		httpUtils.send(HttpMethod.GET, url, params , new OrderCallBack(callBackType));
	}
	private void startProductListActivity()
	{
		Intent intent = new Intent(context, OrderSubListActivity.class);
		intent.putExtra("listProduct", (Serializable)listProductBean);
		intent.putExtra("productCount", productCount+"");
		intent.putExtra("actionType", actionType);
		LogUtils.e("orderID = "+orderID);
		intent.putExtra("orderID", orderID);
		startActivity(intent);
	}
	/**订单列表的 回调
	 * */
	private final class OrderCallBack extends RequestCallBack<String>
	{
		private CallBackType callBackType;
		public OrderCallBack(CallBackType callBackType) {
			this.callBackType = callBackType;
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
			}else if(CallBackType.affirmReceived == callBackType){
				ToastUtil.shortAtTop(context, "确认收货");
			}
		}
	}
	/**@category 订单详情的 回调
	 * */
	private final class OrderDetailCallBack extends RequestCallBack<String>
	{
		@Override
		public void onStart()
		{
			super.onStart();
			dialogShow(dialogLoading, context);
		}
		@Override
		public void onFailure(HttpException error, String msg)
		{
			dialogDismiss(dialogLoading);
			ToastUtil.longAtCenterInThread(context, XUtil.getErrorInfo(error));
			((XListView)findViewById(R.id.xlv)).stopXListView();
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			dialogDismiss(dialogLoading);
			String result = info.result;
			//KLog.e("result = "+result);
			Cache.saveTmpFile(result);
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			if(sessionErrorInfo != null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				startLoginActvitiy(activity, false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "status"), "code"))){
				LogUtils.e("result = "+result);
				ToastUtil.longAtCenterInThread(context, JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "status"), "error_desc"));
				return ;
			}
			JSONObject jsonObject = JsonSmartUtil.getJsonObject(result, "data");

			JSONObject jsonObject_consignee = JsonSmartUtil.getJsonObject(jsonObject, "consignee");
			ViewUtil.setText2TextView(findViewById(R.id.tv_buyer), JsonSmartUtil.getString(jsonObject_consignee, "consignee"));
			ViewUtil.setText2TextView(findViewById(R.id.tv_phone_num), JsonSmartUtil.getString(jsonObject_consignee, "mobile"));

			JSONObject jsonObject_order_info = JsonSmartUtil.getJsonObject(jsonObject, "order_info");
			orderID = JsonSmartUtil.getString(jsonObject_order_info, "order_id");
			orderNO= JsonSmartUtil.getString(jsonObject_order_info, "order_sn");
			orderTime = JsonSmartUtil.getString(jsonObject_order_info, "order_time");
			payID = JsonSmartUtil.getString(jsonObject_order_info, "pay_id");
			orderAmount = JsonSmartUtil.getString(jsonObject_order_info, "total_fee");
			ViewUtil.setText2TextView(findViewById(R.id.tv_ship_type), JsonSmartUtil.getString(jsonObject_order_info, "shipping_name"));
			ViewUtil.setText2TextView(findViewById(R.id.tv_shop_name), JsonSmartUtil.getString(jsonObject_order_info, "shop_name"));
			String province_name = JsonSmartUtil.getString(jsonObject_consignee, "province_name");
			String city_name = JsonSmartUtil.getString(jsonObject_consignee, "city_name");
			String district_name = JsonSmartUtil.getString(jsonObject_consignee, "district_name");
			address = JsonSmartUtil.getString(jsonObject_consignee, "address");
			address = province_name+" "+city_name+" "+district_name+" "+address;
			ViewUtil.setText2TextView(findViewById(R.id.tv_addr), address);
			addTime = JsonSmartUtil.getString(jsonObject, "add_time");
			ViewUtil.setText2TextView(findViewById(R.id.tv_add_time), addTime);

			JSONArray jsonArray_goods_list = JsonSmartUtil.getJsonArray(jsonObject, "goods_list");
			listProductBean = new ArrayList<ProductBean>();
			productCount = 0;
			for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray_goods_list) && (i<jsonArray_goods_list.size())); i++)
			{
				ProductBean bean = new ProductBean();
				JSONObject jsonObject_goods = JsonSmartUtil.getJsonObject(jsonArray_goods_list, i);
				bean.goods_id = JsonSmartUtil.getString(jsonObject_goods, "goods_id");
				bean.goods_name = JsonSmartUtil.getString(jsonObject_goods, "goods_name");
				bean.goods_number = JsonSmartUtil.getString(jsonObject_goods, "goods_number");
				productCount += Integer.parseInt(bean.goods_number);
				bean.shop_price = JsonSmartUtil.getString(jsonObject_goods, "shop_price");
				bean.thumb = JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(jsonObject_goods, "img"), "thumb");
				bean.url = JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(jsonObject_goods, "img"), "url");
				if(!JsonSmartUtil.isJsonObjectEmpty(jsonObject_goods) && (jsonArray_goods_list.size() == 1)){
					ViewUtil.setText2TextView(findViewById(R.id.tv_product_name), bean.goods_name);
					ViewUtil.setText2TextView(findViewById(R.id.tv_product_price), "￥"+bean.shop_price);
				}
				findViewById(listResIDIV.get(i)).setVisibility(View.VISIBLE);
				Picasso.with(context).load(bean.url).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into((ImageView) findViewById(listResIDIV.get(i)));
				listProductBean.add(bean);
			}
			if((!JsonSmartUtil.isJsonArrayEmpty(jsonArray_goods_list)) && (jsonArray_goods_list.size() == 1)){
				findViewById(R.id.layout_order_detail_one).setVisibility(View.VISIBLE);
				ViewUtil.setText2TextView(findViewById(R.id.tv_order_no), JsonSmartUtil.getString(jsonObject_order_info, "order_sn"));
				ViewUtil.setText2TextView(findViewById(R.id.tv_product_count), "共"+productCount+"件");
			}else if((!JsonSmartUtil.isJsonArrayEmpty(jsonArray_goods_list)) && (jsonArray_goods_list.size() > 1)){
				findViewById(R.id.layout_order_detail_multiple).setVisibility(View.VISIBLE);
				ViewUtil.setText2TextView(findViewById(R.id.tv_product_count_multiple), "共"+productCount+"件");
				ViewUtil.setText2TextView(findViewById(R.id.tv_order_no_mutiple), JsonSmartUtil.getString(jsonObject_order_info, "order_sn"));
			}
			ViewUtil.setText2TextView(findViewById(R.id.tv_totle), "￥"+JsonSmartUtil.getString(jsonObject_order_info, "total_fee"));
			ViewUtil.setText2TextView(findViewById(R.id.tv_pay_status),JsonSmartUtil.getString(jsonObject_order_info, "pay_status"));
		}
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if(dialogAddr!=null){
			dialogAddr.dismiss();
		}
	}
}
