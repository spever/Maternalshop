package com.subzero.maternalshop.activity.sort;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import subzero.maxwin.XListView;
import subzero.maxwin.XListView.IXListViewListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.subzero.common.utils.ActivityUtil;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.KeyPadUtil;
import com.subzero.common.utils.ObjUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.common.view.CheckTextView;
import com.subzero.common.view.CheckTextView.OnCheckedClickListener;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.adapter.baseadapter.SortDetailListAdapter;
import com.subzero.maternalshop.bean.ProductBean;
import com.subzero.maternalshop.bean.SortDetailListBean;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.Cache;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.util.JsonCommonUtil;
/**店铺详情 宝贝分类详情 二级目录
 * 搜索页面
 * 启动者：
 * 	分类：SortFragment
 * 	首页：HomepageFragment
 * 	店铺详情 宝贝分类 一级目录： SortListActivity(SortListAdapter)
 * 启动项：
 * 	商品详情：ProductDetailActivity
 * 适配器：SortDetailListAdapter*/
public class SortDetailListActivity extends BaseActivity
{
	private SortDetailListAdapter adapter;
	private List<Integer> listTvResId;
	/**顶部筛选条件的 下标*/
	private int indexTopNavi;
	/**第一次点击 顶部筛选条件*/
	private boolean isFistClickTopNavi;
	/**分类的  id*/
	private String catID;
	private String catName;
	/**店铺的  id*/
	private String suppID;
	/**当前 分类类型*/
	private String sortType;
	/**上一次 分类类型*/
	private String sortTypeLast;
	/**第一次点击 切换顶部标签*/
	private boolean isFistToggleNavi;
	/**功能*/
	private String functionType;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sort_detail_list);
		ActivityUtil.pushActivity(activity);
		pageindex = 1;
		Intent intent = getIntent();
		moduleName = intent.getStringExtra(App.keyModuleName);
		functionType = intent.getStringExtra("functionType");
		catID = intent.getStringExtra("catID");
		catName = intent.getStringExtra("catName");
		suppID = intent.getStringExtra("suppID");
		listTvResId = new ArrayList<Integer>();
		indexTopNavi = -1;
		isFistClickTopNavi = true;
		isFistToggleNavi = true;
		sortType = App.sortTypeMultiple;
		sortTypeLast = "";
		initView();
		if(App.functionTypeSearch.equalsIgnoreCase(functionType)){

		}else{
			loadJsonData();
		}
	}
	@Override
	public void initView()
	{
		if(App.functionTypeSearch.equalsIgnoreCase(functionType)){
			findViewById(R.id.rl_top_search).setVisibility(View.VISIBLE);
			findViewById(R.id.rl_top_title).setVisibility(View.GONE);
			findViewById(R.id.layout_top_navi).setVisibility(View.GONE);
			findViewById(R.id.xlv).setVisibility(View.GONE);
		}else if((App.functionTypeSortInOneShop.equalsIgnoreCase(functionType)) || (App.functionTypeSortLevel2.equalsIgnoreCase(functionType))){
			findViewById(R.id.rl_top_search).setVisibility(View.GONE);
			findViewById(R.id.rl_top_title).setVisibility(View.VISIBLE);
			findViewById(R.id.layout_top_navi).setVisibility(View.VISIBLE);
		}
		ViewUtil.setText2TextView(findViewById(R.id.tv_title), catName);
		listTvResId.add(R.id.tv_sort_multiple);
		listTvResId.add(R.id.tv_sort_sales_volume);
		listTvResId.add(R.id.tv_sort_price);
		for (int i = 0; i < listTvResId.size(); i++) {
			findViewById(listTvResId.get(i)).setTag(true);
		}
		findViewById(R.id.iv_back).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.iv_back_search).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.layout_sort_price).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_sort_price).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.layout_sort_sales_volume).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_sort_sales_volume).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.layout_sort_multiple).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_sort_multiple).setOnClickListener(new MyOnClickListener());
		((CheckTextView)findViewById(R.id.ctv_edit)).setOnCheckedClickListener(new MyOnCheckedClickListener());
		((TextView)findViewById(R.id.tv_sort_multiple)).setSelected(true);
		XListView xlistView = (XListView) findViewById(R.id.xlv);
		/*外壳*/
		shellView = LayoutInflater.from(context).inflate(R.layout.layout_shell_data_empty, null);
		shellView.findViewById(R.id.shell_layou_empty).setOnClickListener(new MyOnClickListener());
		ViewUtil.addShell2View(xlistView, context, shellView);
		
		xlistView.setPullLoadEnable(true);
		xlistView.setXListViewListener(new MyIXListViewListener(xlistView));
		adapter = new SortDetailListAdapter(context);
		adapter.setModuleName(moduleName);
		xlistView.setAdapter(adapter);
	}
	private final class MyOnCheckedClickListener implements OnCheckedClickListener
	{
		@Override
		public void onCheckClick(View v)
		{
			String keyWords = ((EditText)findViewById(R.id.et_search)).getText().toString();
			if(TextUtils.isEmpty(keyWords)){
				ToastUtil.shortAtCenter(context, "请输入内容");
				return ;
			}
			findViewById(R.id.xlv).setVisibility(View.GONE);
			hasMore = true;
			loadType = App.loadFirst;
			loadJsonData();
		}
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
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(R.id.shell_layou_empty == v.getId()){
				loadJsonData();
				return ;
			}
			if((R.id.iv_back == v.getId()) || (R.id.iv_back_search == v.getId())){
				KeyPadUtil.closeKeybord((EditText) findViewById(R.id.et_search), context);
				finish();
				return ;
			}
			int k = 0;
			if((R.id.layout_sort_multiple == v.getId()) || (R.id.tv_sort_multiple == v.getId())){
				k = 0;
			}else if((R.id.layout_sort_sales_volume== v.getId()) || (R.id.tv_sort_sales_volume == v.getId())){
				k = 1;
			}else if((R.id.layout_sort_price == v.getId()) || (R.id.tv_sort_price == v.getId())){
				k = 2;
			}
			TextView textView = (TextView)findViewById(listTvResId.get(k));
			boolean isDown = false;
			Drawable drawableRight = null;
			isDown = (boolean) textView.getTag();
			if(indexTopNavi!=k){
				/*以前 没有点击这个*/
				indexTopNavi = k;
			}else{
				/*在同一个位置 多次点击*/
				isDown = !isDown;
			}
			if(isFistClickTopNavi && (indexTopNavi==0)){
				isFistClickTopNavi = false;
				isDown = !isDown;
			}
			if(isDown){
				drawableRight = getResources().getDrawable(R.drawable.selector_view_triangle_down);
			}else{
				drawableRight = getResources().getDrawable(R.drawable.selector_view_triangle_up);
			}
			if((k == 0) || (k == 1)){
				drawableRight = getResources().getDrawable(R.drawable.selector_view_triangle_down);
			}
			drawableRight.setBounds(0, 0, drawableRight.getMinimumWidth(), drawableRight.getMinimumHeight()); 
			textView.setCompoundDrawables(null, null, drawableRight, null);
			textView.setTag(isDown);
			switchLable(k);
			if(k==0 && isDown){
				//LogUtils.e("综合 向下");
				sortType = App.sortTypeMultiple;
			}else if(k==0 && (!isDown)){
				//LogUtils.e("综合 向上");
				sortType = App.sortTypeMultiple;
			}else if(k==1 && (isDown)){
				//LogUtils.e("销量 向下");
				sortType = App.sortTypeSalesVolume;
			}else if(k==1 && (!isDown)){
				//LogUtils.e("销量 向上");
				sortType = App.sortTypeSalesVolume;
			}else if(k==2 && (isDown)){
				//LogUtils.e("价格 向下");
				sortType = App.sortTypePriceDesc;
			}else if(k==2 && (!isDown)){
				//LogUtils.e("价格 向上");
				sortType = App.sortTypePriceAsc;
			}
			if(sortType.equalsIgnoreCase(sortTypeLast)){
				LogUtils.e("上一次 点过  "+sortType);
			}else{
				hasMore = true;
				sortTypeLast = sortType;
				loadType = App.loadFirst;
				pageindex = 1;
				if(isFistToggleNavi && (k==0)){
					isFistToggleNavi = false;
				}else if(k!=0){
					isFistToggleNavi = false;
					loadJsonData();
				}else{
					loadJsonData();
				}
			}
		}
	}
	public void switchLable(int k)
	{
		for (int i = 0; i < listTvResId.size(); i++)
		{
			TextView textView = (TextView) findViewById(listTvResId.get(i));
			if(k == i){
				textView.setSelected(true);
			}else{
				textView.setSelected(false);
			}
		}
	}
	/**@category 加载数据  |  开始搜索
	 * */
	private void loadJsonData()
	{
		RequestParams params = new RequestParams(XUtil.charset);
		String url = "";
		params.addQueryStringParameter("page", pageindex+"");
		params.addQueryStringParameter("count", Url.rows+"");
		if(App.sortTypeMultiple.equalsIgnoreCase(sortType)){
			//sort_by  综合时为空
			LogUtils.e("分类 形式 综合");
		}else if(App.sortTypeSalesVolume.equalsIgnoreCase(sortType)){
			/*销量*/
			LogUtils.e("分类 形式 销量");
			params.addQueryStringParameter("sort_by", "salesnum_desc");
		}else if(App.sortTypePriceDesc.equalsIgnoreCase(sortType)){
			/*价格 降序*/
			LogUtils.e("分类 形式 价格  降序");
			params.addQueryStringParameter("sort_by", "price_desc");
		}else if(App.sortTypePriceAsc.equalsIgnoreCase(sortType)){
			/*价格 升序*/
			LogUtils.e("分类 形式 价格  升序");
			params.addQueryStringParameter("sort_by", "price_asc");
		}
		/*添加关键字*/
		String keyWords = ((EditText)findViewById(R.id.et_search)).getText().toString();
		if(!TextUtils.isEmpty(keyWords)){
			LogUtils.e("keywords");
			params.addQueryStringParameter("keywords", keyWords);
		}
		if(App.functionTypeSearch.equalsIgnoreCase(functionType)){
			url = Url.searchProductApi;
			LogUtils.e("在商品分类列表 进行搜索 ");
			params.addQueryStringParameter("province",User.getCityId(context));
		}else 	if(App.functionTypeSortLevel2.equalsIgnoreCase(functionType)){
			LogUtils.e("商品分类的 二级分类页面");
			url = Url.sortLevel2Api;
			//url=/search&format=json&cat_id=&sort_by=&keywords=&province=&page=&count=
			params.addQueryStringParameter("province",User.getCityId(context));
			params.addQueryStringParameter("cat_id", catID);
			//LogUtils.e("province = "+User.getCityId(context)+" cat_id = "+catID);
		}else if(App.functionTypeSortInOneShop.equalsIgnoreCase(functionType)){
			LogUtils.e("从 店铺分类 进来");
			//http://muyin.cooltou.cn/zapi/?url=/supplier/data&format=json&action=category
			url = Url.sortInOneShopApi;
			params.addQueryStringParameter("suppId", suppID);
			params.addQueryStringParameter("cat_id", catID);
			//LogUtils.e("suppId = "+suppID+" cat_id = "+catID);
		}
		// http://muyin.cooltou.cn/zapi/?url=/search&format=json
		LogUtils.e("params: "+XUtil.params2String(params));
		httpHandler = httpUtils.send(HttpMethod.GET, url, params , new SortDetailListCallBack());
	}
	/**@category 分类详情 列表的 回调
	 * */
	private final class SortDetailListCallBack extends RequestCallBack<String>
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
			ViewUtil.setText2TextView(shellView.findViewById(R.id.shell_tv_empty), XUtil.getErrorInfo(error));
			shellView.setVisibility(View.VISIBLE);
			dialogDismiss(dialogLoading);
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			dialogDismiss(dialogLoading);
			XListView xListView = ((XListView)findViewById(R.id.xlv));
			xListView.stopXListView();
			String result = info.result;
			Cache.saveTmpFile(result);
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				startLoginActvitiy(activity, false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "status"), "code"))){
				LogUtils.e("result = "+result);
				ViewUtil.setText2TextView(shellView.findViewById(R.id.shell_tv_empty), JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "status"), "error_desc"));
				shellView.setVisibility(View.VISIBLE);
				return ;
			}
			if(!hasMore){
				ToastUtil.shortAtCenterInThread(context, "没有更多数据");
				return ;
			}
			JSONArray jsonArray = JsonSmartUtil.getJsonArray(result, "data");
			List<ProductBean> listProductBean = new ArrayList<ProductBean>();
			List<SortDetailListBean> listSortDetailListBeanOld = adapter.getList();
			if(App.loadMore.equalsIgnoreCase(loadType) && (listSortDetailListBeanOld.get(listSortDetailListBeanOld.size()-1).rightBean==null)){
				SortDetailListBean sortDetailListBean = listSortDetailListBeanOld.get(listSortDetailListBeanOld.size()-1);
				listProductBean.add(sortDetailListBean.leftBean);
				listSortDetailListBeanOld.remove(listSortDetailListBeanOld.size()-1);
			}
			for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray) && (i < jsonArray.size())); i++)
			{
				JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonArray, i);
				ProductBean bean = new ProductBean();
				bean.goods_id = JsonSmartUtil.getString(jsonObject, "goods_id");
				bean.goods_name = JsonSmartUtil.getString(jsonObject, "name");
				bean.market_price = JsonSmartUtil.getString(jsonObject, "market_price");
				bean.shop_price = JsonSmartUtil.getString(jsonObject, "shop_price");
				bean.promote_price = JsonSmartUtil.getString(jsonObject, "promote_price");
				JSONObject jsonObject_img = JsonSmartUtil.getJsonObject(jsonObject, "img");
				bean.thumb = JsonSmartUtil.getString(jsonObject_img, "thumb");
				bean.url = JsonSmartUtil.getString(jsonObject_img, "url");
				listProductBean.add(bean);
			}
			List<SortDetailListBean> listSortDetailListBean = new ArrayList<SortDetailListBean>();
			for (int i = 0; i < listProductBean.size(); )
			{
				SortDetailListBean shopDetailProductSortListBean = new SortDetailListBean();
				shopDetailProductSortListBean.leftBean = listProductBean.get(i);
				if(i+1 <listProductBean.size()){
					shopDetailProductSortListBean.rightBean = listProductBean.get(i+1);
				}
				i = i+2;
				listSortDetailListBean.add(shopDetailProductSortListBean);
			}
			findViewById(R.id.layout_top_navi).setVisibility(View.VISIBLE);
			if(App.loadFirst.equalsIgnoreCase(loadType)){
				adapter.refreshItem(listSortDetailListBean);
				xListView.setSelection(0);
				if(ObjUtil.isListEmpty(listSortDetailListBean)){
					shellView.setVisibility(View.VISIBLE);
					xListView.setVisibility(View.GONE);
					return ;
				}
			}else if(App.loadRefresh.equalsIgnoreCase(loadType)){
				adapter.refreshItem(listSortDetailListBean);
				xListView.setSelection(0);
			}else if(App.loadMore.equalsIgnoreCase(loadType)){
				adapter.addItem(listSortDetailListBean);
			}
			String more = JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "paginated"), "more");
			hasMore = "1".equalsIgnoreCase(more);
			shellView.setVisibility(View.GONE);
			xListView.setVisibility(View.VISIBLE);
		}
	}
}
