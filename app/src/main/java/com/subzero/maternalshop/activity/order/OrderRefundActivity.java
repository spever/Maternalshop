package com.subzero.maternalshop.activity.order;
import java.io.File;
import java.util.ArrayList;

import net.minidev.json.JSONObject;
import subzero.nereo.MultiImageSelectorActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.socks.library.KLog;
import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.common.view.CircleImageView;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.CallBackType;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.util.JsonCommonUtil;
/**
 * 申请退单
 * 启动者：
 * 	PendingActivity(PendingAdapter)
 * 	ProductDetailEditActivity
 * 启动项：
 * 	商品列表：OrderDetailProductListActivity
 * */
public class OrderRefundActivity extends BaseActivity
{
	private String orderID;
	private String goodsID;
	private EditText editText;
	private String imgPath;
	/**商品 配图*/
	private File fileProductLogo;
	/**商品配图*/
	public static final int requestCodePhoto = 100;
	private CircleImageView circleImageView;
	public String back_type;
	public String tui_goods_price;
	public String product_id_tui;
	public String goods_attr_tui;
	public String tui_goods_number;
	public String back_pay;
	public String country;
	public String province;
	public String city;
	public String district;
	public String back_consignee;
	public String back_address;
	public String back_mobile;
	public String order_sn;
	public String goods_name;
	public String goods_sn;
	public String old_goods_number;
	private AbHttpUtil httpUtil = null;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_refund);
		Intent intent = getIntent();
		orderID = intent.getStringExtra("orderID");
		goodsID = intent.getStringExtra("goodsID");
		initHttpUtil();
		initView();
		loadJsonData();
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context,CommonClickType.back));
		findViewById(R.id.bt_submit).setOnClickListener(new MyOnClickListener());
		circleImageView = (CircleImageView) findViewById(R.id.civ_logo);
		circleImageView.setOnClickListener(new MyOnClickListener());
		editText  = (EditText) findViewById(R.id.et);
	}
	private void initHttpUtil()
	{
		httpUtil = AbHttpUtil.getInstance(this);
		httpUtil.setTimeout(10000);
	}
	private void loadJsonData()
	{
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("uid", User.getUserId(context));
		params.addQueryStringParameter("sid", User.getSID(context));
		params.addQueryStringParameter("order_id", orderID);
		params.addQueryStringParameter("goods_id", goodsID);
		LogUtils.e(XUtil.params2String(params));
		httpHandler = httpUtils.send(HttpMethod.POST, Url.orderRefundGetOrderInfo, params , new OrderRefundCallBack(CallBackType.loadJsonData));
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			String reason = editText.getText().toString();
			if(R.id.bt_submit == v.getId()){
				if(TextUtils.isEmpty(reason)){
					ToastUtil.shortAtCenter(context, "请填写退款理由");
					return ;
				}
				AbRequestParams params = new AbRequestParams();
				LogUtils.e("uid = "+User.getUserId(context));
				LogUtils.e("sid ="+User.getSID(context));
				params.put("back_type", back_type);
				params.put("tui_goods_price", tui_goods_price);
				params.put("product_id_tui", product_id_tui);
				params.put("goods_attr_tui", goods_attr_tui);
				params.put("tui_goods_number", tui_goods_number);
				params.put("imgs", fileProductLogo);
				params.put("back_reason", reason);
				params.put("back_pay", back_pay);
				params.put("country", country);
				params.put("province", province);
				params.put("city", city);
				params.put("district", district);
				params.put("back_consignee", back_consignee);
				params.put("back_address", back_address);
				params.put("back_mobile", back_mobile);
				params.put("order_id", orderID);
				params.put("order_sn", order_sn);
				params.put("goods_id", goodsID);
				params.put("goods_name", goods_name);
				params.put("goods_sn", goods_sn);
				params.put("old_goods_number", old_goods_number);
				httpUtil.post(Url.orderRefund+"&uid="+User.getUserId(context)+"&sid="+User.getSID(context), params , new StringHttpResponseListener());
			}else if(R.id.civ_logo == v.getId()){
				/*选择头像*/
				Intent intent = new Intent(context, MultiImageSelectorActivity.class);
				intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
				intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
				intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
				startActivityForResult(intent , requestCodePhoto);
			}
		}
	}
	private final class StringHttpResponseListener extends AbStringHttpResponseListener
	{

		@Override
		public void onSuccess(int statusCode, String content)
		{
			dialogDismiss(dialogLoading);
			LogUtils.e("成功  statusCode = "+statusCode+"  content = "+new String(content));
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(content);
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				startLoginActvitiy((Activity)context, false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(content))){
				ToastUtil.shortAtCenterInThread(context, JsonCommonUtil.getCommonErrorInfo(content));
				return;
			}
			App.orderListMustRefresh = true;
			ToastUtil.shortAtCenterInThread(context, "申请成功，请耐心等待商家回复");
			finish();
		}
		@Override
		public void onFailure(int statusCode, String content, Throwable error){
			dialogDismiss(dialogLoading);
			LogUtils.e("失败 statusCode = "+statusCode+"  content = "+content );
			ToastUtil.shortAtCenterInThread(context, "网络异常");
		}

		@Override
		public void onFinish(){
			dialogDismiss(dialogLoading);
		}
		@Override
		public void onStart(){
			dialogShow(dialogLoading, context);
		}
	}
	/**订单列表的 回调
	 * */
	private final class OrderRefundCallBack extends RequestCallBack<String>
	{
		private CallBackType callBackType;
		public OrderRefundCallBack(CallBackType callBackType) {
			this.callBackType = callBackType;
		}
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
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			String result = info.result;
			dialogDismiss(dialogLoading);
			
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				startLoginActvitiy((Activity)context, false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ToastUtil.shortAtCenterInThread(context, JsonCommonUtil.getCommonErrorInfo(result));
				return;
			}
			JSONObject jsonObject = JsonSmartUtil.getJsonObject(JsonSmartUtil.getJsonObject(result), "data");
			JSONObject jsonObject_order = JsonSmartUtil.getJsonObject(jsonObject, "order");
			JSONObject jsonObject_goods = JsonSmartUtil.getJsonObject(jsonObject, "goods");
			tui_goods_price = JsonSmartUtil.getString(jsonObject_goods, "goods_price");
			LogUtils.e("tui_goods_price = "+tui_goods_price);
			product_id_tui = JsonSmartUtil.getString(jsonObject_goods, "product_id");
			LogUtils.e("product_id_tui = "+product_id_tui);
			goods_attr_tui = JsonSmartUtil.getString(jsonObject_goods, "goods_attr");
			LogUtils.e("goods_attr_tui = "+goods_attr_tui);
			tui_goods_number = JsonSmartUtil.getString(jsonObject_goods, "goods_number");
			LogUtils.e("tui_goods_number = "+tui_goods_number);
			country = JsonSmartUtil.getString(jsonObject_order, "country");
			LogUtils.e("country = "+country);
			city = JsonSmartUtil.getString(jsonObject_order, "city");
			LogUtils.e("city = "+city);
			district = JsonSmartUtil.getString(jsonObject_order, "district");
			LogUtils.e("district = "+district);
			back_address = JsonSmartUtil.getString(jsonObject_order, "address");
			LogUtils.e("back_address = "+back_address);
			back_consignee = JsonSmartUtil.getString(jsonObject_order, "consignee");
			LogUtils.e("back_consignee = "+back_consignee);
			back_mobile = JsonSmartUtil.getString(jsonObject_order, "mobile");
			LogUtils.e("back_mobile = "+back_mobile);
			order_sn = JsonSmartUtil.getString(jsonObject_order, "order_sn");
			LogUtils.e("order_sn = "+order_sn);
			goods_name = JsonSmartUtil.getString(jsonObject_goods, "goods_name");
			LogUtils.e("goods_name = "+goods_name);
			goods_sn = JsonSmartUtil.getString(jsonObject_goods, "goods_sn");
			LogUtils.e("goods_sn = "+goods_sn);
			old_goods_number = JsonSmartUtil.getString(jsonObject_goods, "goods_number");
			LogUtils.e("old_goods_number = "+old_goods_number);
			back_type = "1";
			back_pay = "2";
			
			if(CallBackType.loadJsonData == callBackType){
				
			}else if(CallBackType.orderRefund == callBackType){
				KLog.e("result = "+result);
			}
			
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(data == null || data.getExtras() == null){
			return ;
		}
		if(requestCode == requestCodePhoto){
			ArrayList<String> listUri = data.getExtras().getStringArrayList(MultiImageSelectorActivity.EXTRA_RESULT);
			if(listUri==null || listUri.size()<=0){
				return ;
			}
			imgPath = listUri.get(0);
			
			fileProductLogo = new File(imgPath);
			LogUtils.e("imgPath = "+imgPath+"  fileProductLogo  = "+fileProductLogo.getAbsolutePath());
			Options opts = new Options();
			opts.inSampleSize = 4;
			Bitmap bitmap = BitmapFactory.decodeFile(fileProductLogo.getAbsolutePath(), opts );
			circleImageView.setImageBitmap(bitmap);
			
		}
	}
}
