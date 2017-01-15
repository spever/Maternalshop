package com.subzero.maternalshop.activity.order;
import net.minidev.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.PhoneUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.bean.multiplepay.MultiplePayBean;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.Cache;
import com.subzero.maternalshop.config.SDK;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.util.JsonCommonUtil;
import com.subzero.maternalshop.wxapi.WXPayEntryActivity;
/**支付订单
 * 启动者：
 * 	确认订单：OrderSubmitActivity
 * 	订单列表：OrderListActivity(OrderListAdapter)
 * 	支付请求：WXPayEntryActivity
 * 适配器：OrderListAdapter*/
public class OrderPayActivity extends BaseActivity
{
	/**需要获取在服务器 的 订单信息  时  传入  改属性*/
	private String recID;
	/**配送方式  的 id*/
	private String shippingID;
	private String bonusId;
	/**支付方式  的 id*/
	private String payID;
	private String orderID = "2015-1223-1750";
	/**订单 编号*/
	private String orderNO = "NO2015-1223-1750";
	/**订单 总金额*/
	private String orderAmount;
	/**订单时间*/
	private String orderTime;
	/**订单支付前，进行订单提交认证？
	 * (1)从 订单列表 进来   不需要 订单认证
	 * (2)从 订单确认页面  进来 需要 订单提交认证
	 * */
	private String orderPayType;
	private String weChatAppPID;
	private String noncestr;
	private String partnerid;
	private String prepayid;
	private String timestamp;
	private String sign;
	public static int requestCodeMutiplePay = 100;
	/**支付宝 商品详情*/
	private String alipayBody;
	/**支付宝 订单信息(调出支付宝客户端 只能看到这个标题)*/
	private String alipaySubject;
	/**用户积分*/
	/**积分兑换 红包*/
	private String myPoint;
	private static String payIDAliPay = "2";
	private static String payIDWeChatPay = "10";
	private static String payIDUnionPay = "3";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_pay);
		Intent intent = getIntent();
		recID = intent.getStringExtra("recID");
		payID = intent.getStringExtra("payID");
		bonusId = intent.getStringExtra("bonusId");
		orderNO = intent.getStringExtra("orderNO");
		orderID = intent.getStringExtra("orderID");
		shippingID = intent.getStringExtra("shippingID");
		orderTime = intent.getStringExtra("orderTime");
		orderAmount = intent.getStringExtra("orderAmount");
		orderPayType = intent.getStringExtra("orderPayType");
		moduleName = intent.getStringExtra(App.keyModuleName);
		alipayBody = intent.getStringExtra("alipayBody");
		alipaySubject = intent.getStringExtra("alipaySubject");
		myPoint = intent.getStringExtra("myPoint");
		initView();
		if(App.orderPayTypeNOSubmit.equalsIgnoreCase(orderPayType)){
			LogUtils.e("从 订单列表 进来   不需要 订单认证");
			if(payIDUnionPay.equals(payID)){
				LogUtils.e("调起银联支付");
				loadUnionPay();
			}else if(payIDWeChatPay.equals(payID)){
				LogUtils.e("调起微信支付");
				loadWeChatPayOrder();
			}else if(payIDAliPay.equals(payID)){
				LogUtils.e("调起支付宝支付");
				startAliPay();
			}     
			findViewById(R.id.sv).setVisibility(View.VISIBLE);
		}else{
			loadJsonData();
		}
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		findViewById(R.id.bt_submit).setOnClickListener(new MyOnClickListener());
		ViewUtil.setText2TextView(findViewById(R.id.tv_order_no), orderNO);
		ViewUtil.setText2TextView(findViewById(R.id.tv_order_price), orderAmount);
		ViewUtil.setText2TextView(findViewById(R.id.tv_order_time), orderTime);
	}
	/**@category 获取银联支付订单*/
	private void loadUnionPay()
	{
		/*立即购买*/
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("merId", SDK.unionID);
//		params.addQueryStringParameter("reqReserved", PhoneUtil.getDevice());
		params.addQueryStringParameter("txnAmt",orderAmount);
		//params.addQueryStringParameter("orderId", orderID);
		params.addQueryStringParameter("reqReserved", orderID);
		//?payment=wxpay/beforepay&format=json&order_id=&device_info=
		LogUtils.e("获取银联支付订单");
		httpHandler = httpUtils.send(HttpMethod.GET, Url.UnionPayApi, params , new PayOrderCallBack());
	}
	/**@category 获取微信支付订单*/
	private void loadWeChatPayOrder()
	{
		/*立即购买*/
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("order_id", orderID);
		params.addQueryStringParameter("device_info", PhoneUtil.getDevice());
		//?payment=wxpay/beforepay&format=json&order_id=&device_info=
		LogUtils.e("获取微信支付订单    orderAmount = "+orderAmount);
		params.addQueryStringParameter("money", orderAmount);
		httpHandler = httpUtils.send(HttpMethod.GET, Url.weChatPayOrderApi, params , new PayOrderCallBack());
	}
	/**@category 微信  银联  预支付订单回调
	 * */
	private final class PayOrderCallBack extends RequestCallBack<String>
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
			LogUtils.e("result = "+result);
			/*Session 过期的异常*/
			//LogUtils.e("result = "+result);
			Cache.saveTmpFile("微信支付预支付订单回调\n "+result);
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			/*普通 异常信息*/
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				startLoginActvitiy(activity, false);
				findViewById(R.id.sv).setVisibility(View.GONE);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ToastUtil.shortAtCenterInThread(context, JsonCommonUtil.getCommonErrorInfo(result));
				findViewById(R.id.sv).setVisibility(View.GONE);
				return;
			}
			findViewById(R.id.sv).setVisibility(View.VISIBLE);
			JSONObject jsonObject = JsonSmartUtil.getJsonObject(result, "data");
			weChatAppPID = JsonSmartUtil.getString(jsonObject, "appid");
			noncestr = JsonSmartUtil.getString(jsonObject, "noncestr");
			partnerid = JsonSmartUtil.getString(jsonObject, "partnerid");
			prepayid = JsonSmartUtil.getString(jsonObject, "prepayid");
			sign = JsonSmartUtil.getString(jsonObject, "sign");
			timestamp = JsonSmartUtil.getString(jsonObject, "timestamp");
			String tn  = JsonSmartUtil.getString(jsonObject, "tn");
			LogUtils.e("tn = "+tn);
			Intent intent = new Intent(context, WXPayEntryActivity.class);
			MultiplePayBean bean  = new MultiplePayBean();
			if("3".equalsIgnoreCase(payID)){
				intent.putExtra(WXPayEntryActivity.keyPayType, WXPayEntryActivity.payTypeUnionPay);
				bean.unionTN = tn;
				bean.unionMode = "00";
				LogUtils.e("即将调起   银联  支付。。。。。");
			}else if("10".equalsIgnoreCase(payID)){
				intent.putExtra(WXPayEntryActivity.keyPayType, WXPayEntryActivity.payTypeWeChatPay);
				bean.weChatNoncestr = noncestr;
				bean.weChatPartnerid = partnerid;
				bean.weChatPrepayid = prepayid;
				bean.weChatSign = sign;
				bean.weChatTimestamp = timestamp;
				bean.weChatAppID = weChatAppPID;
				LogUtils.e("即将调起  微信  支付。。。。。");
			}
			intent.putExtra(WXPayEntryActivity.keyMultiplePayBean, bean);
			intent.putExtra(WXPayEntryActivity.keyClassName, OrderPayActivity.class.getName());
			intent.putExtra(WXPayEntryActivity.keyPayTitle, "努力地支付中 请稍等...");
			startActivityForResult(intent , requestCodeMutiplePay);
		}
	}
	private void loadJsonData()
	{
		/*获取订单信息*/
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("uid", User.getUserId(context));
		params.addQueryStringParameter("sid", User.getSID(context));
		LogUtils.e("购物车 的 id = "+recID);
		params.addQueryStringParameter("rec_id", recID);
		params.addBodyParameter("shipping_id", shippingID);
		params.addBodyParameter("pay_id", payID);
		LogUtils.e("bonusId = "+bonusId+" myPoint = "+myPoint);
		if(!TextUtils.isEmpty(bonusId)){
			params.addBodyParameter("bonus_id", bonusId);
		}
		if(!TextUtils.isEmpty(myPoint)){
			params.addBodyParameter("integral", myPoint);
		}
		LogUtils.e("params: "+XUtil.params2String(params));
		
		httpHandler = httpUtils.send(HttpMethod.POST, Url.orderSubmitApi, params , new OrderInfoCallBack());
	}
	/**@category 订单信息 的回调*/
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
			//KLog.e("result = "+result);
			/*Session 过期的异常*/
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			/*普通 异常信息*/
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				startLoginActvitiy(activity, false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ToastUtil.shortAtCenterInThread(context, JsonCommonUtil.getCommonErrorInfo(result));
				return;
			}
			findViewById(R.id.sv).setVisibility(View.VISIBLE);
			JSONObject jsonObject_order_info = JsonSmartUtil.getJsonObject(JsonSmartUtil.getJsonObject(result, "data"), "order_info");
			ViewUtil.setText2TextView(findViewById(R.id.tv_order_no), JsonSmartUtil.getString(jsonObject_order_info, "order_sn"));
			orderID = JsonSmartUtil.getString(jsonObject_order_info, "order_id");
			orderAmount = JsonSmartUtil.getString(jsonObject_order_info, "order_amount");
			alipayBody = JsonSmartUtil.getString(jsonObject_order_info, "desc");
			alipaySubject = JsonSmartUtil.getString(jsonObject_order_info, "subject");
			ViewUtil.setText2TextView(findViewById(R.id.tv_order_price), "￥"+JsonSmartUtil.getString(jsonObject_order_info, "order_amount"));
			ViewUtil.setText2TextView(findViewById(R.id.tv_order_time), JsonSmartUtil.getString(jsonObject_order_info, "order_time"));
		}
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(R.id.bt_submit == v.getId()){
				LogUtils.e("payID = "+payID);
				if(payIDAliPay.equalsIgnoreCase(payID)){
					startAliPay();
				}else if(payIDWeChatPay.equalsIgnoreCase(payID)){
					loadWeChatPayOrder();
				}else if(payIDUnionPay.equalsIgnoreCase(payID)){
					loadUnionPay();
				}
			}
		}
	}
	/**@category 调起支付宝支付*/
	private void startAliPay()
	{
		Intent intent = new Intent(context, WXPayEntryActivity.class);
		MultiplePayBean bean  = new MultiplePayBean();
		intent.putExtra(WXPayEntryActivity.keyPayType, WXPayEntryActivity.payTypeAliPay);
		bean.alipayPID = SDK.alipayPID;
		bean.alipayRsaPrivate = SDK.alipayRsaPrivate;
		bean.alipaySeller = SDK.alipaySeller;
		bean.alipayBody = alipayBody;
		bean.alipaySubject = alipaySubject;
		bean.alipayUrl = SDK.alipayUrl;
		bean.alipayPrice = orderAmount;
		LogUtils.e("alipayBody = "+alipayBody+" alipaySubject = "+alipaySubject+" orderAmount = "+orderAmount);
		intent.putExtra(WXPayEntryActivity.keyMultiplePayBean, bean);
		intent.putExtra(WXPayEntryActivity.keyClassName, OrderPayActivity.class.getName());
		intent.putExtra(WXPayEntryActivity.keyPayTitle, "努力地支付中 请稍等...");
		startActivityForResult(intent , requestCodeMutiplePay);
	}
	/**TODO:告诉服务器  支付成功*/
	private void requestPayFinish()
	{
		/*立即购买*/
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("uid", User.getUserId(context));
		params.addQueryStringParameter("sid", User.getSID(context));
		params.addQueryStringParameter("order_id", orderID);
		params.addQueryStringParameter("order_amount", orderAmount);
		//LogUtils.e("uid = "+User.getUserId(context)+" sid = "+User.getSID(context)+" order_id = "+orderID+" order_amount = "+orderAmount);
		//http://muyin.cooltou.cn/zapi/?url=/order/pay_notify&format=json&order_id=&order_amount=&uid=&sid=
		httpHandler = httpUtils.send(HttpMethod.GET, Url.payFinishApi, params , new PayFinishCallBack());
	}
	/**支付完成的 接口回调
	 * */
	private final class PayFinishCallBack extends RequestCallBack<String>
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
			/*Session 过期的异常*/
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			/*普通 异常信息*/
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				startLoginActvitiy(activity, false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ToastUtil.shortAtCenterInThread(context, JsonCommonUtil.getCommonErrorInfo(result));
				return;
			}
			ToastUtil.shortAtCenterInThread(context, "支付完成");
			if(OrderListActivity.class.getSimpleName().equalsIgnoreCase(moduleName)){
				App.orderListMustRefresh = true;
			}else if(OrderSubmitActivity.class.getSimpleName().equalsIgnoreCase(moduleName)){
				Intent data = new Intent(context, OrderSubmitActivity.class);
				data.putExtra("orderSubmitActivityMustFinish", true);
				setResult(RESULT_OK, data);
			}
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(data == null || data.getExtras() == null){
			return ;
		}
		String payResult = data.getStringExtra(WXPayEntryActivity.keyPayResult);
		if(WXPayEntryActivity.payResultCancle.equalsIgnoreCase(payResult)){
			LogUtils.e("取消支付");
		}else if(WXPayEntryActivity.payResultFail.equalsIgnoreCase(payResult)){
			LogUtils.e("支付失败");
		}else if(WXPayEntryActivity.payResultSuccess.equalsIgnoreCase(payResult)){
			LogUtils.e("支付成功");
			//SPUtil.remove(context, App.rec_id);
			requestPayFinish();
		}else if(WXPayEntryActivity.payResultDealing.equalsIgnoreCase(payResult)){
			LogUtils.e("支付处理中");
			requestPayFinish();
		}
	}
}