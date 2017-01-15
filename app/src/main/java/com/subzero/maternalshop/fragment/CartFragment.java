package com.subzero.maternalshop.fragment;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.subzero.common.listener.OnFragmentSelectedListener;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.ObjUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.common.view.CheckTextView;
import com.subzero.common.view.CheckTextView.OnCheckedChangeListener;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.IndexActivity;
import com.subzero.maternalshop.activity.order.OrderSubmitActivity;
import com.subzero.maternalshop.adapter.baseadapter.CartAdapter;
import com.subzero.maternalshop.bean.ProductBean;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.util.JsonCommonUtil;
/**购物车模块
 * 启动者：
 * 	默认首页：IndexActivity
 * 启动项：
 * 	商品详情：ProductDetailActivity
 * 	确认订单：OrderSubmitActivity
 * 适配器：
 * 	CartAdapter
 * */
public class CartFragment extends BaseFragment
{
	private CartAdapter adapter;
	/**去支付*/
	private boolean isGotoPay;
	/**总金额*/
	public TextView tvAmount;
	/**全选 默认为 true*/
	private boolean isSelecteAll;
	public boolean isFirstLoad;
	/**支付完成，numberInCart必须为零 */
	public static final int requestCodePayFinish = 100;
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		((IndexActivity)activity).addOnFragmentSelectedListener(new MyOnFragmentSelectedListener());
		canLoading = true;
		isSelecteAll = true;
		isGotoPay = true;
		isFirstLoad = true;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_cart, null);
			initView();
		}
		/*过滤Fragment重叠，如果是 Fragment嵌套Fragment，不能加这个*/
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;
	}
	@Override
	public void onResume()
	{
		super.onResume();
		/*只要进入 ProductDetailActivity 或者 OrderSubmitActivity
		 * App.isCartFragmentBackFromeDetail = true
		 * */
		if(App.isCartFragmentBackFromeDetail){
			App.isCartFragmentBackFromeDetail = false;
			loadJsonData();
		}
	}
	/**相应 接口回调*/
	private final class MyOnFragmentSelectedListener implements OnFragmentSelectedListener
	{
		@Override
		public void onFragmentSelected(int indexSelected)
		{
			if((indexSelected == 2) && canLoading){
				//canLoading = false;
				loadJsonData();
			}else{

			}
		}
	}
	@Override
	public void initView()
	{
		((CheckTextView)rootView.findViewById(R.id.ctv_edit)).setOnCheckedChangeListener(new MyOnCheckedChangeListener());
		rootView.findViewById(R.id.iv_cart_all).setSelected(isSelecteAll);
		tvAmount = (TextView) rootView.findViewById(R.id.tv_cart_foot_navi_left);
		rootView.findViewById(R.id.layout_cart_foot_navi_left).setOnClickListener(new MyOnClickListener());
		rootView.findViewById(R.id.tv_cart_foot_navi_left).setOnClickListener(new MyOnClickListener());
		rootView.findViewById(R.id.layout_cart_foot_navi_right).setOnClickListener(new MyOnClickListener());
		ListView listView = (ListView)rootView.findViewById(R.id.lv);
		/*外壳*/
		shellView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_shell_data_empty, null);
		shellView.findViewById(R.id.shell_layou_empty).setOnClickListener(new MyOnClickListener());
		ViewUtil.addShell2View(rootView.findViewById(R.id.lv), getActivity(), shellView);
		
		adapter = new CartAdapter(getActivity());
		adapter.setAmount(tvAmount);
		listView.setAdapter(adapter);
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if((R.id.layout_cart_foot_navi_left == v.getId()) || (R.id.tv_cart_foot_navi_left == v.getId())){
				isSelecteAll = !isSelecteAll;
				rootView.findViewById(R.id.iv_cart_all).setSelected(isSelecteAll);
				List<ProductBean> list = adapter.getList();
				for (int i = 0; i < list.size(); i++){
					ProductBean bean = list.get(i);
					bean.isSelected = isSelecteAll;
				}
				adapter.updateItem(list);
				tvAmount.setText(adapter.calculateFormat(2));
			} else if(R.id.layout_cart_foot_navi_right== v.getId()){
				List<ProductBean> list = adapter.getList();
				//?url=/cart&format=json&action=delete&rec_id=&uid=&sid=
				String rec_id = "";
				for (int i = 0; i < list.size(); i++)
				{
					ProductBean bean = list.get(i);
					if(bean.isSelected){
						rec_id += bean.rec_id+"|";
						//LogUtils.e("id = "+bean.id+" isSelected = "+bean.isSelected);
					}
				}
				if(TextUtils.isEmpty(rec_id)){
					ToastUtil.shortAtCenter(getActivity(), "先选则商品 再操作");
					return ;
				}
				String subString = rec_id.substring(0, rec_id.length()-1);
				LogUtils.e("substring = "+subString);
				if(isGotoPay){
					/*立即购买 定单确认*/
					Intent intent = new Intent(getActivity(), OrderSubmitActivity.class);
					intent.putExtra("recID", subString);
					intent.putExtra(App.keyModuleName, CartFragment.class.getSimpleName());
					startActivityForResult(intent, requestCodePayFinish);
				}else{
					/*做移除操作*/
					RequestParams params = new RequestParams(XUtil.charset);
					params.addQueryStringParameter("rec_id", subString);
					params.addQueryStringParameter("uid", User.getUserId(getActivity()));
					params.addQueryStringParameter("sid", User.getSID(getActivity()));
					LogUtils.e("uid = "+User.getUserId(getActivity())+" sid = "+User.getSID(getActivity()));
					httpHandler = httpUtils.send(HttpMethod.GET, Url.cartRemoveApi, params , new CartRemoveCallBack());
				}
			}else if(R.id.shell_layou_empty == v.getId()){
				loadJsonData();
			}
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

	}
	/**@category 从购物车中移除的回调
	 * */
	private final class CartRemoveCallBack extends RequestCallBack<String>
	{
		@Override
		public void onStart()
		{
			super.onStart();
			dialogShow(dialogLoading, getActivity());
		}
		@Override
		public void onFailure(HttpException error, String msg)
		{
			LogUtils.e("onFailure = "+XUtil.getErrorInfo(error));
			ToastUtil.longAtCenterInThread(getActivity(), XUtil.getErrorInfo(error));
			dialogDismiss(dialogLoading);
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			dialogDismiss(dialogLoading);
			String result = info.result;
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(getActivity(), sessionErrorInfo);
				((IndexActivity)getActivity()).startLoginActvitiy(getActivity(), false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ToastUtil.shortAtCenterInThread(getActivity(), JsonCommonUtil.getCommonErrorInfo(result));
				return;
			}
			LogUtils.e("result = "+result);
			List<ProductBean> list = adapter.getList();
			/*刷新之后，要展示的 列表*/
			List<ProductBean> listRefresh = new ArrayList<ProductBean>(); 
			for (int i = 0; i < list.size(); i++)
			{
				ProductBean bean = list.get(i);
				if(bean.isSelected){
					LogUtils.e("id = "+bean.goods_name+" isSelected = "+bean.isSelected);
				}else{
					listRefresh.add(bean);
				}
			}
			adapter.refreshItem(listRefresh);
			toggleFootNavi();
		}
	}
	private final class MyOnCheckedChangeListener implements OnCheckedChangeListener
	{
		@Override
		public void onCheckChange(View v, boolean isChecked)
		{
			String textRight = "";
			String textLeft = "";
			CheckTextView checkTextView = (CheckTextView) v;
			if(R.id.ctv_edit == v.getId()){
				isGotoPay = !isChecked;
				LogUtils.e("isGotoPay = "+isGotoPay);
				if(isChecked){
					textRight = "删除";
					textLeft = "全选";
					checkTextView.setText("完成");
					tvAmount.setVisibility(View.GONE);
				}else{
					textRight = "去结算";
					checkTextView.setText("编辑");
					tvAmount.setVisibility(View.VISIBLE);
				}
			}
			textLeft = adapter.calculateFormat(2);
			tvAmount.setText(textLeft);
			((TextView)rootView.findViewById(R.id.tv_cart_foot_navi_right)).setText(textRight);
		}
	}
	private void loadJsonData()
	{
		tvAmount.setText(adapter.calculateFormat(2));
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("uid", User.getUserId(getActivity()));
		params.addQueryStringParameter("sid", User.getSID(getActivity()));
		httpHandler = httpUtils.send(HttpMethod.GET, Url.cartListApi, params , new CartListCallBack());
	}
	/**@category 购物车列表的 回调
	 * */
	private final class CartListCallBack extends RequestCallBack<String>
	{
		@Override
		public void onStart()
		{
			super.onStart();
			dialogLoading.show();
		}
		@Override
		public void onFailure(HttpException error, String msg)
		{
			LogUtils.e("onFailure = "+XUtil.getErrorInfo(error));
			ViewUtil.setText2TextView(shellView.findViewById(R.id.shell_tv_empty), XUtil.getErrorInfo(error));
			shellView.setVisibility(View.VISIBLE);
			dialogLoading.dismiss();
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			dialogLoading.dismiss();
			String result = info.result;
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(getActivity(), sessionErrorInfo);
				((IndexActivity)getActivity()).startLoginActvitiy(getActivity(), false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ViewUtil.setText2TextView(shellView.findViewById(R.id.shell_tv_empty), JsonCommonUtil.getCommonErrorInfo(result));
				shellView.setVisibility(View.VISIBLE);
				return;
			}
			//LogUtils.e("result = "+result);
			JSONObject jsonObject = JsonSmartUtil.getJsonObject(result, "data");
			JSONArray jsonArray_goods_list = JsonSmartUtil.getJsonArray(jsonObject, "goods_list");
			List<ProductBean> listProductBean = new ArrayList<ProductBean>();
			for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray_goods_list) && (i<jsonArray_goods_list.size())); i++)
			{
				JSONObject jsonObject_goods = JsonSmartUtil.getJsonObject(jsonArray_goods_list, i);
				ProductBean bean = new ProductBean();
				bean.goods_id = JsonSmartUtil.getString(jsonObject_goods, "goods_id");
				bean.rec_id = JsonSmartUtil.getString(jsonObject_goods, "rec_id");
				bean.goods_name = JsonSmartUtil.getString(jsonObject_goods, "goods_name");
				bean.market_price = JsonSmartUtil.getString(jsonObject_goods, "market_price");
				bean.shop_price = JsonSmartUtil.getString(jsonObject_goods, "shop_price");
				bean.countIncart = JsonSmartUtil.getString(jsonObject_goods, "goods_number");
				bean.subtotal = JsonSmartUtil.getString(jsonObject_goods, "subtotal");
				bean.thumb = JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(jsonObject_goods, "img"), "subtotal");
				bean.isSelected = true;
				listProductBean.add(bean);
			}
			adapter.refreshItem(listProductBean);
			JSONObject jsonObject_total = JsonSmartUtil.getJsonObject(jsonObject, "total");
			tvAmount.setText(JsonSmartUtil.getString(jsonObject_total, "goods_price"));
			toggleFootNavi();
		}
	}
	private void toggleFootNavi()
	{
		if(ObjUtil.isListEmpty(adapter.getList())){
			rootView.findViewById(R.id.layout_foot_navi).setVisibility(View.GONE);
			rootView.findViewById(R.id.ctv_edit).setVisibility(View.GONE);
			ViewUtil.setText2TextView(shellView.findViewById(R.id.shell_tv_empty), "购物车空空哒");
			shellView.setVisibility(View.VISIBLE);
		}else{
			rootView.findViewById(R.id.layout_foot_navi).setVisibility(View.VISIBLE);
			rootView.findViewById(R.id.ctv_edit).setVisibility(View.VISIBLE);
			shellView.setVisibility(View.GONE);
		}
	}
}
