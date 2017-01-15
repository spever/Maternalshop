package com.subzero.maternalshop.activity.productdetail;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import subzero.maxwin.XListView;
import subzero.maxwin.XListView.IXListViewListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.LinearLayout;
import cn.sharesdk.framework.ShareSDK;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.subzero.common.dialog.BaseDialog.AnimType;
import com.subzero.common.helpers.sharesdk.ShareSDKHelper;
import com.subzero.common.helpers.sharesdk.ShareSDKHelper.OnListenerType;
import com.subzero.common.helpers.sharesdk.ShareSDKHelper.OnOneKeyShareListener;
import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.common.utils.ActivityUtil;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.NetWorkUtil;
import com.subzero.common.utils.SPUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.common.utils.WvUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.common.view.CheckImageView;
import com.subzero.common.view.CheckTextView;
import com.subzero.common.view.CheckTextView.OnCheckedClickListener;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.activity.order.OrderSubmitActivity;
import com.subzero.maternalshop.activity.shop.ShopDetailActivity;
import com.subzero.maternalshop.adapter.baseadapter.productdetail.ProductEvaluateAdapter;
import com.subzero.maternalshop.bean.AttrBean;
import com.subzero.maternalshop.bean.order.OrderListBeanType;
import com.subzero.maternalshop.bean.productdetail.EvaluateBean;
import com.subzero.maternalshop.bean.productdetail.ParamsBean;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.Cache;
import com.subzero.maternalshop.config.CallBackType;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.dialog.SelectProductDialog;
import com.subzero.maternalshop.dialog.SelectProductDialog.OnAttrSelectorListener;
import com.subzero.maternalshop.util.JsonCommonUtil;
/**商品详情
 * 启动者：
 * 	购物车：CartFragment
 * 	分类详情：SortDetailListActivity
 * 启动项：
 * 	确认订单：OrderSubmitActivity
 * 	店铺详情：ShopDetailActivity
 * 适配器：
 * 	商品评价：ProductEvaluateAdapter
 * */
public class ProductDetailActivity extends BaseActivity
{
	/**支付完成，numberInCart必须为零 */
	public static final int requestCodePayFinish = 100;
	private ProductEvaluateAdapter adapterEvaluate;
	/**商品的 id*/
	private String goodsID;
	/**店铺的 id*/
	private String suppID;
	/**商品在 购物中的 数量*/
	private String numberInCart;
	/**用作分享 的 url*/
	private String shareUrl;
	private EvaluateBean evaluateBean;
	private boolean hasLoadHtmlJson;
	private boolean isDetailError;
	private SelectProductDialog selectProductDialog;
	private CallBackType callBackType;
	/**商品在 购物车中的 id*/
	public String recID;
	/**所选购的 商品的 属性值*/
	public String spec;
	/**要分享出去的图片地址*/
	private String shareSDKImg;
	/**购物车中商品的属性值*/
	/**修改 收货人 支付成功*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_detail);
		ActivityUtil.pushActivity(activity);
		evaluateBean = new EvaluateBean();
		isDetailError = false;
		numberInCart = "1";
		getIntentData();
		initSelectProductDialog();
		initView();
	}
	@Override  
	protected void onNewIntent(Intent intent) {        
		super.onNewIntent(intent);  
		setIntent(intent); 
		getIntentData();
	}
	private void getIntentData()
	{
		App.isCartFragmentBackFromeDetail = true;
		hasLoadHtmlJson = false;
		goodsID = getIntent().getStringExtra("goodsID");
		LogUtils.e("从 店铺详情 的 分类 进来  goodsID = "+goodsID);
		loadProductDetailJson();
	}
	private void initSelectProductDialog()
	{
		selectProductDialog = new SelectProductDialog(context);
		selectProductDialog.setScaleWidth(1.0F);
		selectProductDialog.setCanceledOnTouchOutside(true);
		selectProductDialog.setOnAttrSelectorListener(new MyOnAttrSelectorListener());
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		findViewById(R.id.layout_add_cart).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_add_cart).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_buy).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.layout_shop).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_shop).setOnClickListener(new MyOnClickListener());
		adapterEvaluate = new ProductEvaluateAdapter(context);
		XListView xListView = (XListView) findViewById(R.id.xlv);
		xListView.setPullLoadEnable(true);
		xListView.setXListViewListener(new MyIXListViewListener(xListView));
		xListView.setAdapter(adapterEvaluate);
		((CheckTextView)findViewById(R.id.ctv_produce_describe)).setOnCheckedClickListener(new MyOnCheckedClickListener());
		((CheckTextView)findViewById(R.id.ctv_produce_params)).setOnCheckedClickListener(new MyOnCheckedClickListener());
		((CheckImageView)findViewById(R.id.civ_share)).setOnCheckedClickListener(new ImageOnCheckedClickListener());
	}
	/**@category 下拉刷新 
	 * */
	protected final class MyIXListViewListener implements IXListViewListener
	{
		private XListView xlistView;
		public MyIXListViewListener(XListView xlistView) {
			this.xlistView = xlistView;
		}
		@Override
		public void onRefresh()
		{
			String label = DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
			xlistView.setRefreshTime(label);
			loadType = App.loadRefresh;
			hasMore = true;
			pageindex = 1;
			loadProductDetailJson();
		}
		@Override
		public void onLoadMore()
		{
			loadType = App.loadMore;
			pageindex++;
			loadCommentList();
		}
	}
	private final class ImageOnCheckedClickListener implements CheckImageView.OnCheckedClickListener
	{
		@Override
		public void onCheckClick(View v)
		{
			if(R.id.civ_share == v.getId()){
				LogUtils.e("开始分享");
				showShare();
			}
		}
	}
	/**@category 切换文本详情参数详情*/
	private final class MyOnCheckedClickListener implements OnCheckedClickListener
	{
		@Override
		public void onCheckClick(View v)
		{
			LogUtils.e("isDetailError  ="+isDetailError);
			if(R.id.ctv_produce_params == v.getId()){
				((CheckTextView)findViewById(R.id.ctv_produce_describe)).setChecked(false);
				((CheckTextView)findViewById(R.id.ctv_produce_params)).setChecked(true);
				if(!isDetailError){
					LogUtils.e("参数  展示 ScroillView");
					findViewById(R.id.xlv).setVisibility(View.GONE);
					findViewById(R.id.sv).setVisibility(View.VISIBLE);
				}
				if(!hasLoadHtmlJson){
					hasLoadHtmlJson = true;
					loadProductDetailHtmlJson();
				}
			}else if(R.id.ctv_produce_describe == v.getId()){
				if(!isDetailError){
					LogUtils.e("描述  展示 ListView");
					findViewById(R.id.sv).setVisibility(View.GONE);
					findViewById(R.id.xlv).setVisibility(View.VISIBLE);
				}
				((CheckTextView)findViewById(R.id.ctv_produce_describe)).setChecked(true);
				((CheckTextView)findViewById(R.id.ctv_produce_params)).setChecked(false);
			}
		}
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = null;
			LogUtils.e("numberInCart = "+numberInCart);
			if(R.id.tv_buy == v.getId()){
				callBackType = CallBackType.cartOneKeyBuy;
				selectProductDialog.getEditText().setText(numberInCart+"");
				selectProductDialog.show(AnimType.bottom2top);
			}else if((R.id.layout_shop == v.getId()) || (R.id.tv_shop == v.getId())){
				/*店铺详情*/
				intent = new Intent(context, ShopDetailActivity.class);
				intent.putExtra("suppId", suppID);
				startActivity(intent);
			}else if((R.id.tv_add_cart == v.getId()) || (R.id.layout_add_cart == v.getId())){
				callBackType = CallBackType.cartAdd;
				selectProductDialog.getEditText().setText(numberInCart+"");
				selectProductDialog.show(AnimType.bottom2top);
			}
		}
	}
	/**@category  获取 商品Json参数
	 * */
	private void loadProductDetailJson()
	{
		LogUtils.e("即将 获取 商品的 json数据");
		RequestParams params = new RequestParams(XUtil.charset);
		//goodsID = "365";
		params.addQueryStringParameter("goods_id",goodsID);
		LogUtils.e(XUtil.params2String(params));
		httpHandler = httpUtils.send(HttpMethod.GET, Url.productDetailApi, params , new ProductDetailCallBack(CallBackType.productText));
	}
	/**@category 获取 商品H5参数
	 * */
	private void loadProductDetailHtmlJson()
	{
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("goods_id",goodsID);
		httpHandler = httpUtils.send(HttpMethod.GET, Url.productDetailHtml5Api, params , new ProductDetailCallBack(CallBackType.productHtml));
	}
	/**@category 商品详情 回调
	 * */
	private final class ProductDetailCallBack extends RequestCallBack<String>
	{
		private CallBackType callBackType;
		public ProductDetailCallBack(CallBackType callBackType) {
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
			LogUtils.e("onFailure = "+XUtil.getErrorInfo(error));
			findViewById(R.id.xlv).setVisibility(View.GONE);
			findViewById(R.id.sv).setVisibility(View.GONE);
			if((CallBackType.productText == callBackType) && App.loadFirst.equalsIgnoreCase(loadType)){
				isDetailError = true;
			}
			((XListView)findViewById(R.id.xlv)).stopXListView();
			findViewById(R.id.civ_share).setVisibility(View.GONE);
			ToastUtil.longAtCenterInThread(context, XUtil.getErrorInfo(error));
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			XListView xListView = (XListView) findViewById(R.id.xlv);
			String result = info.result;
			LogUtils.e("result0="+result);
			dialogDismiss(dialogLoading);
			//KLog.e("result = "+result);
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				startLoginActvitiy(activity, false);
				xListView.stopXListView();
				if((CallBackType.productText == callBackType) && App.loadFirst.equalsIgnoreCase(loadType)){
					isDetailError = true;
				}
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ToastUtil.shortAtCenterInThread(context, JsonCommonUtil.getCommonErrorInfo(result));
				findViewById(R.id.civ_share).setVisibility(View.GONE);
				findViewById(R.id.iv_data_empty).setVisibility(View.VISIBLE);
				xListView.setVisibility(View.GONE);
				xListView.stopXListView();
				findViewById(R.id.layout_foot_navi).setVisibility(View.GONE);
				findViewById(R.id.iv_data_empty).setVisibility(View.VISIBLE);
				if((CallBackType.productText == callBackType) && App.loadFirst.equalsIgnoreCase(loadType)){
					isDetailError = true;
				}
				return;
			}
			findViewById(R.id.layout_foot_navi).setVisibility(View.VISIBLE);
			findViewById(R.id.civ_share).setVisibility(View.VISIBLE);
			findViewById(R.id.iv_data_empty).setVisibility(View.GONE);
			if(CallBackType.productText == callBackType){
				//KLog.e("result = "+result);
				Cache.saveTmpFile(result);
				JSONObject jsonObject = JsonSmartUtil.getJsonObject(result, "data");
				StringBuilder builder = new StringBuilder();
				JSONArray jsonArray_promotion = JsonSmartUtil.getJsonArray(jsonObject, "promotion");
				JSONObject jsonObject_shop_info = JsonSmartUtil.getJsonObject(jsonObject, "shop_info");
				String shopName = JsonSmartUtil.getString(jsonObject_shop_info, "shop_name");
				String shopCity = JsonSmartUtil.getString(jsonObject_shop_info, "shop_region");

				adapterEvaluate.setShopName(shopName);
				adapterEvaluate.setShopCity(shopCity);
				for (int i = 0; (jsonArray_promotion!=null) && (i<jsonArray_promotion.size()); i++)
				{
					JSONObject jsonObject_promotion = JsonSmartUtil.getJsonObject(jsonArray_promotion, i);
					builder.append(JsonSmartUtil.getString(jsonObject_promotion, "act_name")+"  ");
				}
				List<AttrBean> list = new ArrayList<AttrBean>();
				JSONArray jsonArray = JsonSmartUtil.getJsonArray(jsonObject, "specification");
				for (int i = 0; (jsonArray!=null)&& (i<jsonArray.size()); i++)
				{
					AttrBean bean = new AttrBean();
					JSONObject jsonObject_specification = JsonSmartUtil.getJsonObject(jsonArray, i);
					String name = JsonSmartUtil.getString(jsonObject_specification, "name");
					bean.titleName = name;
					JSONArray jsonArray_content = JsonSmartUtil.getJsonArray(jsonObject_specification, "value");
					List<String> listContentId = new ArrayList<String>();
					List<String> listContentName = new ArrayList<String>();
					List<String> listContentFormatPrice = new ArrayList<String>();
					for (int j = 0; (jsonArray_content!=null) && (j<jsonArray_content.size()); j++)
					{
						JSONObject jsonObject_content = JsonSmartUtil.getJsonObject(jsonArray_content, j);
						listContentId.add(JsonSmartUtil.getString(jsonObject_content, "id"));
						listContentName.add(JsonSmartUtil.getString(jsonObject_content, "label"));
						listContentFormatPrice.add(JsonSmartUtil.getString(jsonObject_content, "format_price"));
					}
					bean.listContentId = listContentId;
					bean.listContentName = listContentName;
					bean.listContentFormatPrice = listContentFormatPrice;
					list.add(bean);
				}
				String actName = builder.toString();
				String price = JsonSmartUtil.getString(jsonObject, "shop_price");
				String promote_price = JsonSmartUtil.getString(jsonObject, "promote_price");
				LogUtils.e("promote_price = "+promote_price);
				if(!TextUtils.isEmpty(promote_price) && (!"0".equalsIgnoreCase(promote_price))){
					price = promote_price;
				}
				LogUtils.e("price = "+price);
				selectProductDialog.setPrice(price);
				if(!TextUtils.isEmpty(actName)){
					selectProductDialog.getSpecialTextView().setText(actName);
					adapterEvaluate.setPromotion(actName);
				}
				if(App.loadFirst.equals(loadType)){
					selectProductDialog.setDataSet(list);					
				}
				paserProductDetailJson(result);
				setupParamsLayout(result);
				loadCommentList();
			}else if(CallBackType.productHtml == callBackType){
				//KLog.e("result = "+result);
				String htmlText = JsonSmartUtil.getString(result, "data");
				findViewById(R.id.sv).setVisibility(View.VISIBLE);
				((WebView)findViewById(R.id.wv)).loadDataWithBaseURL(WvUtil.baseUrl, htmlText, WvUtil.mimeType, WvUtil.encoding, WvUtil.historyUrl);
			}
		}
	}
	/**@category 加载评论列表
	 * */
	private void loadCommentList()
	{
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("format", "json");
		params.addQueryStringParameter("goods_id",goodsID);
		params.addQueryStringParameter("page", pageindex+"");
		params.addQueryStringParameter("count", Url.rows+"");
		//LogUtils.e("goods_id = "+goodsID);
		//http://muyin.cooltou.cn/zapi/?url=/comments&format=json&page=1&count=10&goods_id=111
		//http://muyin.cooltou.cn/zapi/?url=/comments&format=json&goods_id=111
		httpHandler = httpUtils.send(HttpMethod.GET, Url.productCommentListApi, params , new CommentListCallBack());
	}
	/**@category  展示 商品参数
	 * */
	public void setupParamsLayout(String result)
	{
		//item_activity_product_detail_params
		JSONObject jsonObject = JsonSmartUtil.getJsonObject(result, "data");
		//品牌名称 goods_brand
		//数量 goods_number
		//重量 goods_weight
		List<ParamsBean> list = new ArrayList<ParamsBean>();
		list.add(new ParamsBean("品牌", JsonSmartUtil.getString(jsonObject, "goods_brand")));
		list.add(new ParamsBean("数量", JsonSmartUtil.getString(jsonObject, "goods_number")));
		list.add(new ParamsBean("重量", JsonSmartUtil.getString(jsonObject, "goods_weight")));
		LinearLayout layout = (LinearLayout) findViewById(R.id.layout_params);
		layout.removeAllViews();
		for (int i = 0; i < list.size(); i++)
		{
			View view = LayoutInflater.from(context).inflate(R.layout.item_activity_product_detail_params, null);
			if(i!=0){
				view.findViewById(R.id.line_top).setVisibility(View.GONE);
				view.findViewById(R.id.line_top_margin0).setVisibility(View.GONE);
			}
			ViewUtil.setText2TextView(view.findViewById(R.id.tv_params_key), list.get(i).key);
			ViewUtil.setText2TextView(view.findViewById(R.id.tv_params_values), list.get(i).value);
			layout.addView(view);
		}
	}
	/**@category 商品的评论 回调
	 * */
	private final class CommentListCallBack extends RequestCallBack<String>
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
			XListView xListView = (XListView) findViewById(R.id.xlv);
			xListView.setVisibility(View.VISIBLE);
			dialogDismiss(dialogLoading);
			String result = info.result;
			//Cache.saveTmpFile(result);
			//LogUtils.e(result);
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				startLoginActvitiy(activity, false);
				xListView.stopXListView();
				return ;
			}else if(!"200".equalsIgnoreCase(JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "status"), "code"))){
				//LogUtils.e("result = "+result);
				xListView.stopXListView();
				LogUtils.e("evaluateBean = "+(evaluateBean==null));
				if((App.loadFirst.equalsIgnoreCase(loadType)) && (evaluateBean!=null)){
					LogUtils.e("进来了!!!!!!!!");
					adapterEvaluate.addItem(evaluateBean);
				}
				if(App.loadMore.equalsIgnoreCase(loadType)){
					ToastUtil.shortAtCenterInThread(context, "没有更多评论");
				}
				return ;
			}
			JSONArray jsonArray = JsonSmartUtil.getJsonArray(result, "data");
			List<EvaluateBean> list = new ArrayList<EvaluateBean>();
			if((!App.loadMore.equalsIgnoreCase(loadType)) && (evaluateBean!=null)){
				list.add(evaluateBean);
			}
			for (int i = 0;(!JsonSmartUtil.isJsonArrayEmpty(jsonArray)) && ( i < jsonArray.size()); i++)
			{
				JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonArray, i);
				EvaluateBean bean = new EvaluateBean();
				bean.listBeanType = OrderListBeanType.multiple;
				bean.content = JsonSmartUtil.getString(jsonObject, "content");
				bean.id = JsonSmartUtil.getString(jsonObject, "id");
				bean.title = JsonSmartUtil.getString(jsonObject, "author");
				bean.create = JsonSmartUtil.getString(jsonObject, "create");
				list.add(bean);
			}
			if(!hasMore){
				ToastUtil.shortAtCenterInThread(context, "没有更多数据");
				xListView.stopXListView();
				return ;
			}

			if(App.loadFirst.equalsIgnoreCase(loadType) || App.loadRefresh.equalsIgnoreCase(loadType)){
				adapterEvaluate.refreshItem(list);
				xListView.setSelection(0);
			}else if(App.loadMore.equalsIgnoreCase(loadType)){
				adapterEvaluate.addItem(list);
			}
			String more = JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "paginated"), "more");
			//LogUtils.e("more = "+more);
			hasMore = "1".equalsIgnoreCase(more);
			xListView.stopXListView();
		}
	}
	/**@category 解析 商品的详情*/
	private void paserProductDetailJson(String result)
	{
		JSONObject jsonObject = JsonSmartUtil.getJsonObject(result, "data");
		suppID = JsonSmartUtil.getString(jsonObject, "suppliers_id");
		shareUrl = JsonSmartUtil.getString(jsonObject, "share_url");
		evaluateBean.listBeanType = OrderListBeanType.one;
		List<String> listImgUrl = new ArrayList<String>();
		JSONArray jsonArray_pictures = JsonSmartUtil.getJsonArray(jsonObject, "pictures");
		for (int i = 0; (jsonArray_pictures!=null) && (i<jsonArray_pictures.size()); i++)
		{
			JSONObject jsonObject_img = JsonSmartUtil.getJsonObject(jsonArray_pictures, i);
			listImgUrl.add(JsonSmartUtil.getString(jsonObject_img, "url"));
		}
		if((listImgUrl!=null) && (listImgUrl.size()>0)){
			shareSDKImg = listImgUrl.get(0);
		}
		evaluateBean.listImgUrl = listImgUrl;
		evaluateBean.goods_name = JsonSmartUtil.getString(jsonObject, "goods_name");
		evaluateBean.give_integral = JsonSmartUtil.getString(jsonObject, "give_integral");
		evaluateBean.salesnum = JsonSmartUtil.getString(jsonObject, "salesnum");
		evaluateBean.shop_price = JsonSmartUtil.getString(jsonObject, "shop_price");
		evaluateBean.promote_price = JsonSmartUtil.getString(jsonObject, "promote_price");
		JSONObject jsonObject_shipping_list = JsonSmartUtil.getJsonObject(jsonObject, "shipping_list");
		evaluateBean.shipping_fee = JsonSmartUtil.getString(jsonObject_shipping_list, "shipping_fee");
		evaluateBean.shipping_city = JsonSmartUtil.getString(jsonObject_shipping_list, "shipping_city");
		evaluateBean.shipping_address = JsonSmartUtil.getString(jsonObject_shipping_list, "shipping_address");
		evaluateBean.shipping_fuwu = JsonSmartUtil.getString(jsonObject_shipping_list, "shipping_fuwu");
		evaluateBean.url = JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(jsonObject, "img"), "url");
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ShareSDK.stopSDK(context);
	}
	private void showShare()
	{
		ShareSDKHelper shareSDKHelper = new ShareSDKHelper(context, new MyOnOneKeyShareListener());
		//http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg
		//shareSDKHelper.shareImgUrl = "https://mmbiz.qlogo.cn/mmbiz/aYraCxCcLHmFoGMMLXoGUZNnty5tKZJW49mgwsIYNVwc1YGOnsS7IamiaMXU4m9xzpichjcLkQHrF1F9ZJhdDGicg/0?wx_fmt=png";
		shareSDKHelper.shareImgUrl = shareSDKImg;
		shareSDKHelper.shareUrl = shareUrl;
		shareSDKHelper.site = getString(R.string.app_name);
		shareSDKHelper.text = "我在【附近逛】买到一个宝贝，很赞的！\n"+shareUrl;
		shareSDKHelper.title = getString(R.string.app_name);
		shareSDKHelper.titleUrl = shareUrl;
		shareSDKHelper.startOneKeyShare();
	}
	private final class MyOnOneKeyShareListener implements OnOneKeyShareListener
	{
		@Override
		public void onFinish(OnListenerType onListenerType)
		{
			LogUtils.e("onListenerType = "+onListenerType);
			ToastUtil.shortAtCenterInThread(context, "分享完成");
			if(OnListenerType.cancle == onListenerType){
				ToastUtil.shortAtCenter(context, "取消分享");
			}else if(OnListenerType.success == onListenerType){
				LogUtils.e("分享成功");
				//ToastUtil.shortAtCenter(context, "分享成功");
				ToastUtil.shortAtCenterInThread(context, "分享成功");
			}
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(data == null){
			return;
		}
		if(requestCodePayFinish == requestCode){
			boolean payFinish = data.getBooleanExtra("payFinish", false);
			if(payFinish){
				numberInCart = "1";
			}
		}
	}
	/**@category 选择属性的对话框的回调*/
	private final class MyOnAttrSelectorListener implements OnAttrSelectorListener
	{
		@Override
		public void onAttrSelector(String count, String spec, String price)
		{
			numberInCart = count;
			ProductDetailActivity.this.spec = spec;
			RequestParams params = new RequestParams(XUtil.charset);
			numberInCart = count;
			recID = SPUtil.getString(context, App.rec_id);
			params.addQueryStringParameter("rec_id", recID);
			params.addQueryStringParameter("uid", User.getUserId(context));
			params.addQueryStringParameter("sid", User.getSID(context));
			LogUtils.e(XUtil.params2String(params));
			httpHandler = httpUtils.send(HttpMethod.GET, Url.cartRemoveApi, params , new CartRemoveCallBack());
		}
	}
	/**@category 从购物车中移除的回调
	 * */
	private final class CartRemoveCallBack extends RequestCallBack<String>
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
			LogUtils.e("移除 购物车的返回数据 result = "+result);
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				((ProductDetailActivity)context).startLoginActvitiy(activity, false);
				return ;
			}
			RequestParams params = new RequestParams(XUtil.charset);
			String url = "";
			if(CallBackType.cartAdd == callBackType){
				url = Url.cartAddApi;
			}else if(CallBackType.cartOneKeyBuy == callBackType){
				url = Url.cartBuyApi;
				LogUtils.e(XUtil.params2String(params));
			}
			params.addQueryStringParameter("uid", User.getUserId(context));
			params.addQueryStringParameter("sid", User.getSID(context));
			params.addQueryStringParameter("goods_id",goodsID);
			params.addQueryStringParameter("spec", spec);
			params.addQueryStringParameter("number", numberInCart);
			httpHandler = httpUtils.send(HttpMethod.GET, url, params , new CartCallBack(callBackType));
		}
	}
	/**@category 一键购物和添加购物车的回调
	 * */
	private final class CartCallBack extends RequestCallBack<String>
	{
		private CallBackType callBackType;
		public CartCallBack(CallBackType callBackType) {
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
			if(!NetWorkUtil.checkNetWorkConnected(context)){
				ToastUtil.longAtCenterInThread(context, XUtil.getErrorInfo(error));
			}
			LogUtils.e("onFailure = "+XUtil.getErrorInfo(error));
			LogUtils.e("callBackType = "+callBackType);
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			dialogDismiss(dialogLoading);
			String result = info.result;
			/*Session 过期的异常*/
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				startLoginActvitiy(activity, false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ToastUtil.shortAtCenterInThread(context, JsonCommonUtil.getCommonErrorInfo(result));
				return;
			}
			if(CallBackType.cartAdd == callBackType){
				LogUtils.e("商品 添加到购物车 numberInCart = "+numberInCart);
				ToastUtil.shortAtCenterInThread(context, "加入购物车成功");
			}else if(CallBackType.cartOneKeyBuy == callBackType){
				LogUtils.e("一键购物得到返回数据  result = "+result);
				JSONObject jsonObject = JsonSmartUtil.getJsonObject(result, "data");
				recID = JsonSmartUtil.getString(jsonObject, "rec_id");
				SPUtil.putString(context, App.rec_id, recID);
				Intent intent = new Intent(context, OrderSubmitActivity.class);
				intent.putExtra("recID", recID);
				intent.putExtra("numberInCart", numberInCart);
				intent.putExtra("canUpdateCount", true);
				intent.putExtra(App.keyModuleName, ProductDetailActivity.class.getSimpleName());
				LogUtils.e("去 订单确认页面   numberInCart = "+numberInCart+"得到 购物车 id recID = "+recID);
				startActivityForResult(intent, requestCodePayFinish);
			}
		}
	}
}
