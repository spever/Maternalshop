package com.subzero.maternalshop.activity.order;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;
import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.common.utils.ActivityUtil;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.common.view.CountLayout;
import com.subzero.common.view.CountLayout.OnCountClickListener;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.activity.IndexActivity;
import com.subzero.maternalshop.activity.productdetail.ProductDetailActivity;
import com.subzero.maternalshop.bean.RedPacketBean;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.Cache;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.fragment.CartFragment;
import com.subzero.maternalshop.util.JsonCommonUtil;
import com.subzero.userman.alteruserinfo.AddrListActivity;
/**订单列表
 * 启动者：
 * 	我的：CartFragment
 * 	商品详情：ProductDetailActivity
 * 启动项：
 * 	配送方式：DistributionActivity
 * 	支付方式：PayTypeActivity
 * 	收货人：AddrListActivity
 * 	使用红包：SelectRedPacketActivity
 * 	已购买，代付款的商品清单：BuyListActivity
 * 	订单支付：OrderPayActivity
 * 适配器：OrderListAdapter*/
public class OrderSubmitActivity extends BaseActivity
{
	/**购物车ID*/
	private String recID;
	/**购物中的 商品在 总 数量*/
	private String numberInCart;
	private String numberInCartNew;
	/**修改 收货人 收货地*/
	public static int requestCodeReceiver = 100;
	/**修改 收货人 配送方式*/
	public static int requestCodeDistribution = 101;
	/**选择 支付方式*/
	public static int requestCodePayType = 102;
	/**支付成功*/
	public static int requestCodePayFinish = 103;
	/**选择红包*/
	public static int requestCodeRedPacket = 104;
	/**配送方式的 Json数据*/
	private String shippingList;
	/**配送方式  的 id*/
	private String shippingID;
	/**支付方式  的 id*/
	private String payID;
	/**支付方式 json数据*/
	public String paymentList;
	/**所购买的 商品的 列表*/
	private String goodsList;
	/**是从 哪个模块 进来的*/
	private String moduleName;
	private List<RedPacketBean> listRedPacketBean;
	private String shoppingMoney;
	private String bonusId; 
	public String typeMoney;
	@SuppressWarnings("unused")
	private float shippingFee;
	/**红包 金额*/
	private float reduce;
	/**可以修改商品的数量*/
	private boolean canUpdateCount;
	/**积分兑换 红包*/
	private String myPoint;
	private boolean isSelectedRedPoint;
	private ImageView ivRedPoint;
	private CountLayout countLayout;
	/**商品 单价*/
	private float shopPrice;
	/**红包积分兑换成的 钱数*/
	private float redPointMoney;
	/**优惠减免的金额*/
	private float discount;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_submit);
		App.isCartFragmentBackFromeDetail = true;
		isSelectedRedPoint = false;
		listRedPacketBean = new ArrayList<RedPacketBean>();
		Intent intent = getIntent();
		recID = intent.getStringExtra("recID");
		numberInCart = intent.getStringExtra("numberInCart");
		moduleName = intent.getStringExtra(App.keyModuleName);
		canUpdateCount = intent.getBooleanExtra("canUpdateCount", false);
		LogUtils.e("numberInCart = "+numberInCart);
		initView();
		loadJsonData();
	}
	private void loadJsonData()
	{
		/*订单确认*/
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("uid", User.getUserId(context));
		params.addQueryStringParameter("sid", User.getSID(context));
		params.addQueryStringParameter("rec_id", recID);
		httpHandler = httpUtils.send(HttpMethod.GET, Url.cartSubmitApi, params , new OrderInfoCallBack());
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		findViewById(R.id.bt_submit).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.layout_distribution).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_distribution).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.layout_pay_type).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_pay_type).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.layout_red_packet).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_red_packet).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.layout_buy_count_body).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.layout_addr).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.layout_receiver).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.layout_phone_num).setOnClickListener(new MyOnClickListener());
		ivRedPoint = (ImageView) findViewById(R.id.iv_red_point);
		ivRedPoint.setOnClickListener(new MyOnClickListener());
		countLayout = (CountLayout)findViewById(R.id.cl_product);
		countLayout.setOnCountClickListener(new MyOnCountClickListener());
		countLayout.getEditText().setText(numberInCart+"");
		ViewUtil.setText2TextView(findViewById(R.id.tv_count), "共"+numberInCart+"件");
		if(canUpdateCount){
			findViewById(R.id.layout_count).setVisibility(View.VISIBLE);
			findViewById(R.id.line_count).setVisibility(View.VISIBLE);
			countLayout.setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.layout_count).setVisibility(View.GONE);
			findViewById(R.id.line_count).setVisibility(View.GONE);
			countLayout.setVisibility(View.GONE);
		}
	}
	private final class MyOnCountClickListener implements OnCountClickListener
	{
		@Override
		public void onCountClick(int count, boolean isAdd)
		{
			numberInCartNew = count+"";
			RequestParams params = new RequestParams(XUtil.charset);
			//?url=/cart&format=json&action=update&rec_id=&new_number=&uid=&sid=
			params.addQueryStringParameter("rec_id", recID);
			params.addQueryStringParameter("new_number", count+"");
			params.addQueryStringParameter("uid", User.getUserId(context));
			params.addQueryStringParameter("sid", User.getSID(context));
			LogUtils.e(""+XUtil.params2String(params));
			httpUtils.send(HttpMethod.GET, Url.cartUpdateApi, params , new CartUpdateCallBack());
		}
	}
	private final class CartUpdateCallBack extends RequestCallBack<String>
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
			LogUtils.e("onFailure = "+XUtil.getErrorInfo(error));
			ToastUtil.longAtCenterInThread(context, XUtil.getErrorInfo(error));
			dialogDismiss(dialogLoading);
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			dialogDismiss(dialogLoading);
			String result = info.result;
			LogUtils.e("result = "+result);
			/*Session 过期的异常*/
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			/*普通 异常信息*/
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				startLoginActvitiy(activity, false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				LogUtils.e("code = "+JsonCommonUtil.getCode(result));
				ToastUtil.shortAtCenterInThread(context, JsonCommonUtil.getCommonErrorInfo(result));
				//startActivityForResultByAddrListActivity();
				return;
			}
			loadJsonData();
			numberInCart = numberInCartNew; 
			ViewUtil.setText2TextView(findViewById(R.id.tv_count), "共"+numberInCart+"件");
			ViewUtil.setText2TextView(findViewById(R.id.tv_product_price), "￥"+(shoppingMoney));
			diaplayAmmount();
		}
	}
	/**@category 获取订单数据
	 * 商品详情  点击 立即购买 在本页面 立即获取订单数据
	 * */
	private final class OrderInfoCallBack extends RequestCallBack<String>
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
			LogUtils.e("onFailure = "+XUtil.getErrorInfo(error));
			ToastUtil.longAtCenterInThread(context, XUtil.getErrorInfo(error));
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			dialogDismiss(dialogLoading);
			String result = info.result;
			//LogUtils.e("result = "+result);
			/*Session 过期的异常*/
			Cache.saveTmpFile(result);

			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			/*普通 异常信息*/
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				startLoginActvitiy(activity, false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ToastUtil.shortAtCenterInThread(context, JsonCommonUtil.getCommonErrorInfo(result));
				//startActivityForResultByAddrListActivity();
				return;
			}
			JSONObject jsonObject_data = JsonSmartUtil.getJsonObject(result, "data");
			if(JsonSmartUtil.getInt(jsonObject_data, "goods_count")>0){
				numberInCart = JsonSmartUtil.getString(jsonObject_data, "goods_count");
				countLayout.getEditText().setText(numberInCart+"");
			}
			//listRedPacketBean
			JSONArray jsonArray_bonus = JsonSmartUtil.getJsonArray(jsonObject_data, "bonus");
			for (int i = 0; (jsonArray_bonus!=null) && (i<jsonArray_bonus.size()); i++)
			{
				JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonArray_bonus, i);
				RedPacketBean bean = new RedPacketBean();
				bean.bonus_id = JsonSmartUtil.getString(jsonObject, "bonus_id");
				bean.bonus_money_formated = JsonSmartUtil.getString(jsonObject, "bonus_money_formated");
				bean.supplier_id = JsonSmartUtil.getString(jsonObject, "supplier_id");
				bean.type_id = JsonSmartUtil.getString(jsonObject, "type_id");
				bean.type_money = JsonSmartUtil.getString(jsonObject, "type_money");
				bean.type_name = JsonSmartUtil.getString(jsonObject, "type_name");
				listRedPacketBean.add(bean);
			}
			if((listRedPacketBean!=null) && (listRedPacketBean.size()>0)){
				findViewById(R.id.layout_red_packet).setVisibility(View.VISIBLE);
				findViewById(R.id.line_red_packet).setVisibility(View.VISIBLE);
				findViewById(R.id.line_red_packet_2).setVisibility(View.VISIBLE);
			}
			String your_integral = JsonSmartUtil.getString(jsonObject_data, "your_integral");
			float your = 0;
			if(!TextUtils.isEmpty(your_integral)){
				your = Float.parseFloat(your_integral);
			}
			String order_max_integral = JsonSmartUtil.getString(jsonObject_data, "order_max_integral");
			String integral_scale = JsonSmartUtil.getString(jsonObject_data, "integral_scale");

			float scale = 1;
			if(!TextUtils.isEmpty(integral_scale)){
				scale = Float.parseFloat(integral_scale);
			}
			float max = 0;
			if(!TextUtils.isEmpty(order_max_integral)){
				max = Float.parseFloat(order_max_integral);
			}
			float point = Math.min(your, max);
			if(point>0){
				findViewById(R.id.layout_red_point).setVisibility(View.VISIBLE);
				findViewById(R.id.line_red_point).setVisibility(View.VISIBLE);
				redPointMoney = point/100;
				float pointScale = (float)(Math.round(point*scale/100*100))/100;
				//
				((TextView)findViewById(R.id.tv_red_point)).setText("可用"+point+"积分抵￥"+pointScale);
			}
			myPoint = order_max_integral;
			JSONArray jsonArray_goods_list = JsonSmartUtil.getJsonArray(jsonObject_data, "goods_list");
			if(!JsonSmartUtil.isJsonArrayEmpty(jsonArray_goods_list)){
				goodsList = jsonArray_goods_list.toJSONString();
			}
			for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray_goods_list)) && (i<jsonArray_goods_list.size()) && (i<3); i++)
			{
				JSONObject jsonObject_goods = JsonSmartUtil.getJsonObject(JsonSmartUtil.getJsonArray(jsonObject_data, "goods_list"), i);
				ImageView imageView = null;
				if(i ==0){
					imageView = (ImageView) findViewById(R.id.iv_product_logo);
					imageView.setVisibility(View.VISIBLE);
				}else if(i==1){
					imageView = (ImageView) findViewById(R.id.iv_product_logo_2);
					imageView.setVisibility(View.VISIBLE);
				}else if(i == 2){
					imageView = (ImageView) findViewById(R.id.iv_product_logo_3);
					imageView.setVisibility(View.VISIBLE);
				}
				String shop_price = JsonSmartUtil.getString(jsonObject_goods, "shop_price");
				if(!TextUtils.isEmpty(shop_price)){
					shopPrice += Float.parseFloat(shop_price);
				}
				Picasso.with(context).load(JsonSmartUtil.getString(jsonObject_goods, "img")).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(imageView);
			}
			if(jsonArray_goods_list.size()>1){
				findViewById(R.id.layout_count).setVisibility(View.GONE);
				findViewById(R.id.line_count).setVisibility(View.GONE);
				countLayout.setVisibility(View.GONE);
			}
			LogUtils.e("shopPrice = "+shopPrice);
			ViewUtil.setText2TextView(findViewById(R.id.tv_count), "共"+numberInCart+"件");
			JSONObject jsonObject_consignee = JsonSmartUtil.getJsonObject(jsonObject_data, "consignee");
			String province_name = JsonSmartUtil.getString(jsonObject_consignee, "province_name");
			String city_name = JsonSmartUtil.getString(jsonObject_consignee, "city_name");
			String district_name = JsonSmartUtil.getString(jsonObject_consignee, "district_name");
			String address = JsonSmartUtil.getString(jsonObject_consignee, "address");
			ViewUtil.setText2TextView(findViewById(R.id.tv_addr), province_name+""+city_name+""+district_name+""+address);
			ViewUtil.setText2TextView(findViewById(R.id.tv_receiver), JsonSmartUtil.getString(jsonObject_consignee, "consignee"));
			ViewUtil.setText2TextView(findViewById(R.id.tv_phone_num), JsonSmartUtil.getString(jsonObject_consignee, "mobile"));
			shoppingMoney = JsonSmartUtil.getString(jsonObject_data, "shopping_money");
			LogUtils.e("shoppingMoney = "+shoppingMoney);
			LogUtils.e("shopPrice = "+shopPrice+" numberInCart = "+numberInCart);
			ViewUtil.setText2TextView(findViewById(R.id.tv_product_price), "￥"+(shoppingMoney));
			ViewUtil.setText2TextView(findViewById(R.id.bt_order_price), "合计：￥"+shoppingMoney);
			/*
			 * TODO: 获取配送方式的 json 数据[,,]
			 * */
			shippingList = JsonSmartUtil.getString(jsonObject_data, "shipping_list");
			//KLog.e("shippingList = "+shippingList);
			JSONArray jsonArray_shipping_list = JsonSmartUtil.getJsonArray(jsonObject_data, "shipping_list");
			if(!JsonSmartUtil.isJsonArrayEmpty(jsonArray_shipping_list)){
				JSONObject jsonObject_shipping = JsonSmartUtil.getJsonObject(jsonArray_shipping_list, 0);
				String shipping_name = JsonSmartUtil.getString(jsonObject_shipping, "shipping_name");
				shippingID = JsonSmartUtil.getString(jsonObject_shipping, "shipping_id");
				ViewUtil.setText2TextView(findViewById(R.id.tv_distribution), shipping_name);
				shippingFee = Float.parseFloat(JsonSmartUtil.getString(jsonObject_shipping, "shipping_fee"));
				//ViewUtil.setText2TextView(findViewById(R.id.tv_order_carriage), "￥"+shippingFee);
			}
			/*
			 * TODO: 获取支付方式的 json数据[,,]
			 * */
			paymentList = JsonSmartUtil.getString(jsonObject_data, "payment_list");
			//KLog.e("paymentList = "+paymentList);
			JSONArray jsonArray_payment_list = JsonSmartUtil.getJsonArray(jsonObject_data, "payment_list");
			if(!JsonSmartUtil.isJsonArrayEmpty(jsonArray_shipping_list)){
				JSONObject jsonObject_payment = JsonSmartUtil.getJsonObject(jsonArray_payment_list, 0);
				String shipping_name = JsonSmartUtil.getString(jsonObject_payment, "pay_name");
				payID = JsonSmartUtil.getString(jsonObject_payment, "pay_id");
				ViewUtil.setText2TextView(findViewById(R.id.tv_pay_type), shipping_name);
			}
			ViewUtil.setText2TextView(findViewById(R.id.tv_count), "共"+numberInCart+"件");
			ViewUtil.setText2TextView(findViewById(R.id.tv_product_price), "￥"+(shoppingMoney));
			JSONObject jsonObject_total = JsonSmartUtil.getJsonObject(jsonObject_data, "total");
			String discountStr = JsonSmartUtil.getString(jsonObject_total, "discount");
			if(!TextUtils.isEmpty(discountStr)){
				discount = Float.parseFloat(discountStr);
			}
			ViewUtil.setText2TextView(findViewById(R.id.tv_product_reuce), "￥"+discountStr);
			diaplayAmmount();
		}
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = null;
			if(R.id.bt_submit == v.getId()){
				/*确认提交*/
				intent = new Intent(context, OrderPayActivity.class);
				intent.putExtra("recID", recID);
				LogUtils.e("购物车 的 id = "+recID);
				intent.putExtra("shippingID", shippingID);
				intent.putExtra("payID", payID);
				LogUtils.e("bonusId = "+bonusId);
				intent.putExtra("bonusId", bonusId);
				if(isSelectedRedPoint){
					LogUtils.e("myPoint = "+myPoint);
					intent.putExtra("myPoint", myPoint);
				}
				intent.putExtra(App.keyModuleName, OrderSubmitActivity.class.getSimpleName());
				intent.putExtra("orderPayType", App.orderPayTypeNeedSubmit);
				startActivityForResult(intent, requestCodePayFinish);
			}else if((R.id.layout_distribution == v.getId()) || (R.id.tv_distribution == v.getId())){
				/*配货方式*/
				intent = new Intent(context, DistributionActivity.class);
				intent.putExtra("shippingList", shippingList);
				startActivityForResult(intent, requestCodeDistribution);
			}else if((R.id.layout_pay_type == v.getId()) || R.id.tv_pay_type == v.getId()){
				/*支付方式*/
				intent = new Intent(context, PayTypeActivity.class);
				intent.putExtra("paymentList", paymentList);
				startActivityForResult(intent, requestCodePayType);
			}else if(R.id.layout_buy_count_body == v.getId()){
				/*清单 列表*/
				intent = new Intent(context, BuyListActivity.class);
				intent.putExtra("goodsList", goodsList);
				intent.putExtra("numberInCart", numberInCart);
				startActivity(intent);
			}else if((R.id.layout_red_packet == v.getId()) || (R.id.tv_red_packet == v.getId())){
				intent = new Intent(context, SelectRedPacketActivity.class);
				intent.putExtra("listRedPacketBean", (Serializable)listRedPacketBean);
				startActivityForResult(intent, requestCodeRedPacket);
			}else if(R.id.iv_red_point == v.getId()){
				isSelectedRedPoint = !isSelectedRedPoint;
				diaplayAmmount();
				ivRedPoint.setSelected(isSelectedRedPoint);
			}else{
				startActivityForResultByAddrListActivity();
			}
		}
	}
	/**TODO: 选择收货联系*/
	private void startActivityForResultByAddrListActivity()
	{
		Intent intent;
		/*选择收货联系*/
		intent = new Intent(context, AddrListActivity.class);
		intent.putExtra("moduleName", OrderSubmitActivity.class.getSimpleName());
		startActivityForResult(intent, requestCodeReceiver);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(data == null){
			return;
		}
		if(requestCodeReceiver == requestCode){
			String province_name = data.getStringExtra("province_name");
			String city_name = data.getStringExtra("city_name");
			String district_name = data.getStringExtra("district_name");
			String address = data.getStringExtra("address");
			String mobile = data.getStringExtra("mobile");
			String consignee = data.getStringExtra("consignee");
			ViewUtil.setText2TextView(findViewById(R.id.tv_phone_num_value), mobile);
			ViewUtil.setText2TextView(findViewById(R.id.tv_addr), province_name+""+city_name+""+district_name+""+address);
			ViewUtil.setText2TextView(findViewById(R.id.tv_receiver), consignee);
			loadJsonData();
		}else if(requestCodeDistribution == requestCode){
			shippingID = data.getStringExtra("shipping_id");
			String shipping_name = data.getStringExtra("shipping_name");
			shippingFee = Float.parseFloat(data.getStringExtra("shipping_fee"));
			ViewUtil.setText2TextView(findViewById(R.id.tv_distribution), shipping_name);
			//ViewUtil.setText2TextView(findViewById(R.id.tv_order_carriage), "￥"+shippingFee);
		}else if(requestCodePayType == requestCode){
			payID = data.getStringExtra("pay_id");
			String pay_name = data.getStringExtra("pay_name");
			LogUtils.e("pay_name = "+pay_name);
			ViewUtil.setText2TextView(findViewById(R.id.tv_pay_type), pay_name);
		}else if(requestCodePayFinish == requestCode){
			boolean orderSubmitActivityMustFinish = data.getBooleanExtra("orderSubmitActivityMustFinish", false);
			if(orderSubmitActivityMustFinish)
			{
				/**
				 * TODO: 从 商品详情来，要携带参数；
				 * 购物车列表类，不需要携带参数
				 * */
				Intent dataBack = null;
				if(ProductDetailActivity.class.getSimpleName().equalsIgnoreCase(moduleName)){
					dataBack = new Intent(context, ProductDetailActivity.class);
				}else if(OrderListActivity.class.getSimpleName().equalsIgnoreCase(moduleName)){
					dataBack = new Intent(context, OrderListActivity.class);
				}else if(CartFragment.class.getSimpleName().equalsIgnoreCase(moduleName)){
					dataBack = new Intent(context, IndexActivity.class);
				}
				dataBack.putExtra("payFinish", true);
				setResult(RESULT_OK, dataBack);
				finish();
			}
		}else if(requestCodeRedPacket == requestCode){
			bonusId = data.getStringExtra("bonusId");
			LogUtils.e("bonusId = "+bonusId);
			String typeInfo = data.getStringExtra("typeInfo");
			typeMoney = data.getStringExtra("typeMoney");
			reduce = Float.parseFloat(typeMoney);
			KLog.e("bonusId = "+bonusId);
			KLog.e("typeInfo = "+typeInfo);
			KLog.e("typeMoney = "+typeMoney+" reduce = "+reduce);
			ViewUtil.setText2TextView(findViewById(R.id.tv_red_packet), typeInfo);
		}
		diaplayAmmount();
	}
	/**@category 展示合计的价格*/
	private void diaplayAmmount(){
		if(isSelectedRedPoint){
			LogUtils.e("选中 积分了");
			if(!TextUtils.isEmpty(shoppingMoney)){
				float  orderPriceFormat   =  (float)(Math.round((Float.parseFloat(shoppingMoney)-reduce-discount-redPointMoney)*100))/100;
				ViewUtil.setText2TextView(findViewById(R.id.bt_order_price), "合计：￥"+orderPriceFormat);
			}
		}else{
			if(!TextUtils.isEmpty(shoppingMoney)){
				float  orderPriceFormat   =  (float)(Math.round((Float.parseFloat(shoppingMoney)-reduce-discount)*100))/100;
				ViewUtil.setText2TextView(findViewById(R.id.bt_order_price), "合计：￥"+orderPriceFormat);
			}
		}
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ActivityUtil.clearAllActivity(false);
	}
}
