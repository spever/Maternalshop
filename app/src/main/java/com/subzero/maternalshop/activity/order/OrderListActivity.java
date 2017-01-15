package com.subzero.maternalshop.activity.order;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import subzero.maxwin.XListView;
import subzero.maxwin.XListView.IXListViewListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;

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
import com.subzero.common.utils.ObjUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.adapter.baseadapter.order.OrderListAdapter;
import com.subzero.maternalshop.bean.ProductBean;
import com.subzero.maternalshop.bean.order.OrderListBean;
import com.subzero.maternalshop.bean.order.OrderListBeanType;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.Cache;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.util.JsonCommonUtil;
/**订单列表
 * 启动者：
 * 		我的：MineFragment
 * 启动项：
 * 		订单列表-商品列表：OrderSubListActivity
 * 		去支付：OrderPayActivity
 * 		商品评价：CommentActivity
 * 		订单详情：OrderDetailActivity
 * 适配器：OrderListAdapter*/
public class OrderListActivity extends BaseActivity
{
	private OrderListAdapter adapter;
	/**更新评论*/
	public static int requestCodeUpdateComment = 100;
	/**支付完成，numberInCart必须为零 */
	public static final int requestCodePayFinish = 101;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_list);
		pageindex = 1;
		App.orderListMustRefresh = false;
		initView();
		loadJsonData();
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		XListView xlistView = (XListView) findViewById(R.id.xlv);
		xlistView.setPullLoadEnable(true);
		xlistView.setXListViewListener(new MyIXListViewListener(xlistView));
		adapter = new OrderListAdapter(context);
		xlistView.setAdapter(adapter);
	}
	/**TODO: 下拉刷新
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
			pageindex = 1;
			hasMore = true;
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
	@Override
	protected void onResume()
	{
		super.onResume();
		if(App.orderListMustRefresh){
			/*在 OrderPayActivity 支付完成  App.orderListMustRefresh = true;  */
			loadType = App.loadFirst;
			pageindex = 1;
			hasMore = true;
			App.orderListMustRefresh = false;
			LogUtils.e("在 订单的支付页面 完成支付功能  现在 开始刷新  订单列表");
			loadJsonData();
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(data == null || data.getExtras() == null){
			return ;
		}
		boolean commentFinish = data.getBooleanExtra("mustRefreshOederList",false);
		boolean payFinish = data.getBooleanExtra("payFinish", false);
		if((requestCode == requestCodeUpdateComment) || (requestCode == requestCodePayFinish)){
			if(commentFinish){
				KLog.e("评论成功  刷新订单列表！！");
			}else if(payFinish){
				KLog.e("支付成功  刷新订单列表！！");
			}
			if(commentFinish || payFinish){
				loadType = App.loadFirst;
				pageindex = 1;
				hasMore = true;
				loadJsonData();
			}
		}
	}
	/**@category  加载 订单列表数据*/
	private void loadJsonData()
	{
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("uid", User.getUserId(context));
		params.addQueryStringParameter("sid", User.getSID(context));
		params.addQueryStringParameter("page", pageindex+"");
		params.addQueryStringParameter("count", Url.rows+"");
		httpHandler = httpUtils.send(HttpMethod.GET, Url.orderListApi, params , new OrderListCallBack());
	}
	/**订单列表的 回调S
	 * */
	private final class OrderListCallBack extends RequestCallBack<String>
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
			((XListView)findViewById(R.id.xlv)).setVisibility(View.GONE);
			findViewById(R.id.iv_data_empty).setVisibility(View.VISIBLE);
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			dialogDismiss(dialogLoading);
			XListView xListView = (XListView)findViewById(R.id.xlv);
			String result = info.result;
			//KLog.e("result = "+result);
			Cache.saveTmpFile(result);
			/*Session 过期的异常*/
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				startLoginActvitiy(activity, false);
				xListView.stopXListView();
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ToastUtil.shortAtCenterInThread(context, JsonCommonUtil.getCommonErrorInfo(result));
				xListView.setVisibility(View.GONE);
				findViewById(R.id.iv_data_empty).setVisibility(View.VISIBLE);
				return;
			}
			JSONArray jsonArray = JsonSmartUtil.getJsonArray(result, "data");
			List<OrderListBean> list = new ArrayList<OrderListBean>();
			for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray)) && (i < jsonArray.size()); i++)
			{
				JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonArray, i);
				OrderListBean bean = new OrderListBean();
				bean.order_id = JsonSmartUtil.getString(jsonObject, "order_id");
				bean.order_status = JsonSmartUtil.getString(jsonObject, "order_status");
				bean.pay_status = JsonSmartUtil.getString(jsonObject, "pay_status");
				bean.pay_id = JsonSmartUtil.getString(jsonObject, "pay_id");
				bean.order_sn = JsonSmartUtil.getString(jsonObject, "order_sn");
				bean.order_time = JsonSmartUtil.getString(jsonObject, "order_time");
				bean.shipping_status = JsonSmartUtil.getString(jsonObject, "shipping_status");
				bean.order_time = JsonSmartUtil.getString(jsonObject, "order_time");
				bean.total_fee = JsonSmartUtil.getString(jsonObject, "total_fee");
				bean.is_back = JsonSmartUtil.getString(jsonObject, "is_back");
				JSONArray jsonArray_goods_list = JsonSmartUtil.getJsonArray(jsonObject, "goods_list");
				List<ProductBean> listProductBean = new ArrayList<ProductBean>();
				int totalCommodity = 0;
				for (int j = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray_goods_list)) && (j<jsonArray_goods_list.size()); j++)
				{
					JSONObject jsonObject_goods = JsonSmartUtil.getJsonObject(jsonArray_goods_list, j);
					ProductBean productBean = new ProductBean();
					productBean.goods_id = JsonSmartUtil.getString(jsonObject_goods, "goods_id");
					productBean.goods_name = JsonSmartUtil.getString(jsonObject_goods, "goods_name");
					productBean.goods_number = JsonSmartUtil.getString(jsonObject_goods, "goods_number");
					totalCommodity += Integer.parseInt(productBean.goods_number);
					productBean.goods_comment = JsonSmartUtil.getString(jsonObject_goods, "goods_comment");
					productBean.shop_price = JsonSmartUtil.getString(jsonObject_goods, "shop_price");
					productBean.salesnum = JsonSmartUtil.getString(jsonObject_goods, "salesnum");
					productBean.thumb = JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(jsonObject_goods, "img"), "thumb");
					productBean.url = JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(jsonObject_goods, "img"), "url");
					listProductBean.add(productBean);
				}
				bean.totalCommodity = totalCommodity+"";
				bean.listProduct = listProductBean;
				if(listProductBean.size() == 1){
					bean.orderListBeanType = OrderListBeanType.one;
				}else{
					bean.orderListBeanType = OrderListBeanType.multiple;
				}
				list.add(bean);
			}
			if(!hasMore){
				ToastUtil.shortAtCenterInThread(context, "没有更多数据");
				xListView.stopXListView();
				return ;
			}
			if(App.loadFirst.equalsIgnoreCase(loadType)){
				if(ObjUtil.isListEmpty(list)){
					findViewById(R.id.iv_data_empty).setVisibility(View.VISIBLE);
					xListView.setVisibility(View.GONE);
					return ;
				}else{
					adapter.refreshItem(list);
				}
				xListView.setSelection(0);
			}else if(App.loadRefresh.equalsIgnoreCase(loadType)){
				adapter.refreshItem(list);
				xListView.setSelection(0);
			}else if(App.loadMore.equalsIgnoreCase(loadType)){
				adapter.addItem(list);
			}
			String more = JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "paginated"), "more");
			//LogUtils.e("more = "+more);
			hasMore = "1".equalsIgnoreCase(more);
			xListView.setVisibility(View.VISIBLE);
			xListView.stopXListView();
		}
	}
}
