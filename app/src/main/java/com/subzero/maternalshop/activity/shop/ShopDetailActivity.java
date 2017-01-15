package com.subzero.maternalshop.activity.shop;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import subzero.angeldevil.AutoScrollViewPager;
import subzero.angeldevil.AutoScrollViewPager.OnPageClickListener;
import subzero.maxwin.XListView;
import subzero.maxwin.XListView.IXListViewListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import cn.sharesdk.framework.ShareSDK;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.squareup.picasso.Picasso;
import com.subzero.common.helpers.sharesdk.ShareSDKHelper;
import com.subzero.common.helpers.sharesdk.ShareSDKHelper.OnListenerType;
import com.subzero.common.helpers.sharesdk.ShareSDKHelper.OnOneKeyShareListener;
import com.subzero.common.utils.ActivityUtil;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.ScreenUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.common.view.CheckImageView;
import com.subzero.common.view.CircleIndicator;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.activity.WebViewActivity;
import com.subzero.maternalshop.adapter.baseadapter.shop.ShopDetailProductListAdapter;
import com.subzero.maternalshop.adapter.pageradapter.WelcomeAdapter;
import com.subzero.maternalshop.bean.ProductBean;
import com.subzero.maternalshop.bean.shop.ShopDetailProductSortListBean;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.Cache;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.util.JsonCommonUtil;
/**店铺详情
 * 启动者：
 * 	主页：HomepageFragment
 * 	商品详情：ProductDetailActivity
 * 启动项：
 * 	店铺详情 商品分类 一级页面 ：SortListInOneShopActivity
 * 	联系人信息：ShopContactInfoActivity
 * 适配器：
 * 	商品详情 分类列表：ShopDetailProductListAdapter
 * */
public class ShopDetailActivity extends BaseActivity
{
	private ShopDetailProductListAdapter adapter;
	private AutoScrollViewPager autoScrollViewPager;
	/**店铺的 id*/
	private String suppId;
	/**店铺 宝贝分类 的json串*/
	private String categories;
	/**用作分享 的 url*/
	private String shareUrl;
	private String qq;
	private String phone;
	private String email;
	private String shareSDKImg;
	private LayoutParams imageParams;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_detail);
		ActivityUtil.pushActivity(activity);
		suppId = getIntent().getStringExtra("suppId");
		initImageParams(activity);
		initView();
		loadJsonData();
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_sort).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_contact).setOnClickListener(new MyOnClickListener());
		XListView xlistView = (XListView) findViewById(R.id.xlv);
		xlistView.setPullLoadEnable(false);
		xlistView.setPullRefreshEnable(true);
		xlistView.setXListViewListener(new MyIXListViewListener(xlistView));
		((CheckImageView)findViewById(R.id.civ_share)).setOnCheckedClickListener(new ImageOnCheckedClickListener());
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
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = null;
			if(R.id.tv_sort == v.getId()){
				intent = new Intent(context, SortListInOneShopActivity.class);
				intent.putExtra("categories", categories);
				intent.putExtra("suppId", suppId);
				startActivity(intent);
			}else if(R.id.tv_contact == v.getId()){
				//shopTel
				intent = new Intent(context,ShopContactInfoActivity.class);
				intent.putExtra("qq", qq);
				intent.putExtra("phone", phone);
				intent.putExtra("email", email);
				startActivity(intent);
			}else if(R.id.iv_back == v.getId()){
				finish();
			}
		}
	}
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
			pageindex = 1;
			loadJsonData();
		}
		@Override
		public void onLoadMore()
		{
			loadType = App.loadMore;
			pageindex++;
			loadJsonData();
		}
	}
	/**@category 加载店铺的 详细json数据
	 * */
	private void loadJsonData()
	{
		//currLatLng(天通苑北三区) = 40.075224 116.431224
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("suppId", suppId);
		//http://muyin.cooltou.cn/zapi/?url=/supplier/data&format=json&suppId=1
		httpHandler = httpUtils.send(HttpMethod.GET, Url.shopDetailApi, params , new ShopDetailCallBack());
	}
	/**@category 商店详情的 回调
	 * */
	private final class ShopDetailCallBack extends RequestCallBack<String>
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
			dialogDismiss(dialogLoading);
			ToastUtil.longAtCenterInThread(context, XUtil.getErrorInfo(error));
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			dialogDismiss(dialogLoading);
			String result = info.result;
			Cache.saveTmpFile(result);
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				startLoginActvitiy(activity, false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ToastUtil.shortAtCenterInThread(context, JsonCommonUtil.getCommonErrorInfo(result));
				findViewById(R.id.xlv).setVisibility(View.GONE);
				findViewById(R.id.iv_data_empty).setVisibility(View.VISIBLE);
				return;
			}
			findViewById(R.id.layout_foot_navi).setVisibility(View.VISIBLE);
			findViewById(R.id.civ_share).setVisibility(View.VISIBLE);
			findViewById(R.id.iv_data_empty).setVisibility(View.GONE);
			setupListView(result);
		}
	}
	/**加载店铺详情的 headView 以及 ListView的 Adapter*/
	private void setupListView(String result)
	{
		//KLog.e("result = "+result);
		JSONObject jsonObject = JsonSmartUtil.getJsonObject(result, "data");
		JSONObject jsonObject_supplier = JsonSmartUtil.getJsonObject(jsonObject, "supplier");
		categories = JsonSmartUtil.getString(jsonObject, "categories");
		shareUrl = JsonSmartUtil.getString(jsonObject, "share_url");
		View headView = LayoutInflater.from(context).inflate(R.layout.headview_activity_shop, null);
		/**店铺头像*/
		shareSDKImg = Url.doMain+JsonSmartUtil.getString(jsonObject_supplier, "shoplogo");
		Picasso.with(context).load(shareSDKImg).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into((ImageView)headView.findViewById(R.id.iv_shop_logo));
		Picasso.with(context).load(Url.doMain+JsonSmartUtil.getString(jsonObject_supplier, "shopbg")).placeholder(R.drawable.logo_shop_background).error(R.drawable.logo_shop_background).into((ImageView)headView.findViewById(R.id.iv_shop_background));
		
		/**店铺名称*/
		ViewUtil.setText2TextView(headView.findViewById(R.id.tv_shop_name), JsonSmartUtil.getString(jsonObject_supplier, "shopname"));
		/**主营*/
		ViewUtil.setText2TextView(headView.findViewById(R.id.tv_sales_type), "主营: "+JsonSmartUtil.getString(jsonObject_supplier, "company_intro"));
		/**34件 商品*/
		ViewUtil.setText2TextView(headView.findViewById(R.id.tv_product_count), JsonSmartUtil.getString(jsonObject_supplier, "goodsnum")+"件 商品");
		/** 56% 好评*/
		ViewUtil.setText2TextView(headView.findViewById(R.id.tv_evaluate_count), JsonSmartUtil.getString(jsonObject_supplier, "comment")+" 好评");
		ViewUtil.setText2TextView(headView.findViewById(R.id.tv_identify), JsonSmartUtil.getString(jsonObject_supplier, "shop_auth"));
		qq = JsonSmartUtil.getString(jsonObject_supplier, "supplier_qq");
		email = JsonSmartUtil.getString(jsonObject_supplier, "supplier_email");
		phone = JsonSmartUtil.getString(jsonObject_supplier, "supplier_phone");
		autoScrollViewPager = (AutoScrollViewPager) headView.findViewById(R.id.asvp);
		CircleIndicator circleIndicator = (CircleIndicator) headView.findViewById(R.id.ci);
		List<ImageView> listTopImg = new ArrayList<ImageView>();
		JSONArray jsonArray_flashad = JsonSmartUtil.getJsonArray(jsonObject, "flashad");

		List<String> listHeadAd = new ArrayList<String>();
		for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray_flashad)) && (i<jsonArray_flashad.size()); i++)
		{
			JSONObject jsonObject_flashad = JsonSmartUtil.getJsonObject(jsonArray_flashad, i);
			ImageView imageView = new ImageView(context);
			listHeadAd.add(JsonSmartUtil.getString(jsonObject_flashad, "url"));
			Picasso.with(context).load(Url.doMain+JsonSmartUtil.getString(jsonObject_flashad, "src")).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(imageView);
			imageView.setScaleType(App.getScaleType());
			imageView.setLayoutParams(imageParams);
			listTopImg.add(imageView);
		}
		/*顶部广告*/
		WelcomeAdapter adapter = new WelcomeAdapter(listTopImg);
		autoScrollViewPager.setAdapter(adapter);
		autoScrollViewPager.setInterval(1000 *3);
		autoScrollViewPager.startAutoScroll(1000 *3);
		autoScrollViewPager.setOnPageClickListener(new MyOnPageChangeListener(listHeadAd));
		if(adapter.getCount()>0){
			if((listTopImg!=null) && (listTopImg.size()>0)){
				circleIndicator.setViewPager(autoScrollViewPager);
			}
		}
		if(App.loadFirst.equalsIgnoreCase(loadType)){
			((XListView)findViewById(R.id.xlv)).addHeaderView(headView);
		}
		/*解析出 商品分类 列表*/
		List<ProductBean> listShopDetailProductSortListCellBean = new ArrayList<ProductBean>();

		JSONArray jsonArray_goods = JsonSmartUtil.getJsonArray(jsonObject, "goods");
		for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray_goods)) && (i < jsonArray_goods.size()); i++)
		{
			ProductBean bean = new ProductBean();
			JSONObject jsonObject_goods = JsonSmartUtil.getJsonObject(jsonArray_goods, i);
			bean.goods_id = JsonSmartUtil.getString(jsonObject_goods, "goods_id");
			bean.goods_name = JsonSmartUtil.getString(jsonObject_goods, "name");
			bean.market_price = JsonSmartUtil.getString(jsonObject_goods, "market_price");
			bean.shop_price = JsonSmartUtil.getString(jsonObject_goods, "shop_price");
			bean.promote_price = JsonSmartUtil.getString(jsonObject_goods, "promote_price");
			JSONObject jsonObject_img = JsonSmartUtil.getJsonObject(jsonObject_goods, "img");
			bean.thumb = JsonSmartUtil.getString(jsonObject_img, "thumb");
			bean.url = JsonSmartUtil.getString(jsonObject_img, "url");
			listShopDetailProductSortListCellBean.add(bean);
		}
		setupAdapter(listShopDetailProductSortListCellBean);
	}
	/**加载 Adapter
	 * */
	private void setupAdapter(List<ProductBean> listShopDetailProductListCellBean)
	{
		if(adapter == null){
			adapter =  new ShopDetailProductListAdapter(context);
			((XListView)findViewById(R.id.xlv)).setAdapter(adapter);
		}
		List<ShopDetailProductSortListBean> listShopDetailProductListBean = new ArrayList<ShopDetailProductSortListBean>();
		for (int i = 0; i < listShopDetailProductListCellBean.size(); )
		{
			ShopDetailProductSortListBean shopDetailProductSortListBean = new ShopDetailProductSortListBean();
			shopDetailProductSortListBean.leftBean = listShopDetailProductListCellBean.get(i);
			if(i+1 <listShopDetailProductListCellBean.size()){
				shopDetailProductSortListBean.rightBean = listShopDetailProductListCellBean.get(i+1);
			}
			i = i+2;
			listShopDetailProductListBean.add(shopDetailProductSortListBean);
		}
		if(App.loadFirst.equalsIgnoreCase(loadType)){
			adapter.addItem(listShopDetailProductListBean);
		}else if(App.loadRefresh.equalsIgnoreCase(loadType)){
			adapter.refreshItem(listShopDetailProductListBean);
		}else if(App.loadMore.equalsIgnoreCase(loadType)){
			adapter.addItem(listShopDetailProductListBean);
		}
		((XListView) findViewById(R.id.xlv)).stopXListView();
	}
	private void showShare()
	{
		ShareSDKHelper shareSDKHelper = new ShareSDKHelper(context, new MyOnOneKeyShareListener());
		shareSDKHelper.shareImgUrl = shareSDKImg;
		shareSDKHelper.shareUrl = shareUrl;
		shareSDKHelper.site = getString(R.string.app_name);
		shareSDKHelper.text = "我在【附近逛】入住店铺了，很赞的！\n"+shareUrl;
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
				ToastUtil.shortAtCenter(context, "分享成功");
			}else if(OnListenerType.fail == onListenerType){
				ToastUtil.shortAtCenter(context, "分享失败");
			}
		}
	}
	private final class MyOnPageChangeListener implements OnPageClickListener
	{
		private List<String> list;
		public MyOnPageChangeListener(List<String> list) {
			this.list = list;
		}
		@Override
		public void onPageClick(AutoScrollViewPager pager, int position)
		{
			Intent intent = new Intent(context, WebViewActivity.class);
			intent.putExtra("url", list.get(position));
			startActivity(intent );
		}
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if(autoScrollViewPager!=null){
			autoScrollViewPager.stopAutoScroll();
			autoScrollViewPager = null;
		}
		ShareSDK.stopSDK(context);
	}
	private void initImageParams(Context context)
	{
		int screenWidth = ScreenUtil.getScreenWidth(context);
		int imageHeight = 9*screenWidth / 16;
		imageParams = new LayoutParams(screenWidth,imageHeight);
	}
}
