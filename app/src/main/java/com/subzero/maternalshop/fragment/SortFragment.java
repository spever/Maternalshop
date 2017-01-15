package com.subzero.maternalshop.fragment;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import subzero.angeldevil.AutoScrollViewPager;
import subzero.angeldevil.AutoScrollViewPager.OnPageClickListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.squareup.picasso.Picasso;
import com.subzero.common.listener.OnFragmentSelectedListener;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.ScreenUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.common.view.CircleIndicator;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.IndexActivity;
import com.subzero.maternalshop.activity.WebViewActivity;
import com.subzero.maternalshop.activity.sort.SortDetailListActivity;
import com.subzero.maternalshop.adapter.baseadapter.SortFragmentAdapter;
import com.subzero.maternalshop.adapter.pageradapter.WelcomeAdapter;
import com.subzero.maternalshop.bean.AdBean;
import com.subzero.maternalshop.bean.SortListBean;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.Cache;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.util.JsonCommonUtil;
/**分类模块
 * 启动者：
 * 	默认首页：IndexActivity
 * 启动项：
 * 	分类二级列表：SortDetailListActivity
 * 	搜索商品：SortDetailListActivity
 * 适配器：
 * 	SortFragmentAdapter
 * */
public class SortFragment extends PopFragment
{
	private ListView listView;
	private SortFragmentAdapter adapter;
	private AutoScrollViewPager autoScrollViewPager;
	private static int imageHeight = 148;
	private List<AdBean> listAdBean;
	private LayoutParams imageParams;
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		((IndexActivity)activity).addOnFragmentSelectedListener(new MyOnFragmentSelectedListener());
		imageHeight = (int) ScreenUtil.dpToPx(activity, 165);
		initImageParams(getActivity());
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_sort, null);
			initPopWindow();
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
	public void initView()
	{
		rootView.findViewById(R.id.layout_search).setOnClickListener(new MyOnClickListener());
		tvLocation = (TextView) rootView.findViewById(R.id.tv_location);
		tvLocation.setOnClickListener(new MyOnClickListener());
		listView = (ListView) rootView.findViewById(R.id.lv);
		listView.setOnItemClickListener(new MyOnItemClickListener());
	}
	private final class MyOnItemClickListener implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			SortListBean bean = adapter.getList().get(position-1);
			startSortDetailListActivity(bean.cat_id, bean.cat_name);
		}
	}
	/**@category 加载广告Json数据*/
	private void loadJsonData(){
		httpHandler = httpUtils.send(HttpMethod.GET, Url.adApi+"&format=json&action=class",new ADCallBack());
	}
	/**请求广告的 回调
	 * */
	private final class ADCallBack extends RequestCallBack<String>
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
			ToastUtil.longAtCenterInThread(getActivity(), XUtil.getErrorInfo(error));
			RequestParams params = new RequestParams(XUtil.charset);
			params.addBodyParameter("format", "json");
			httpHandler = httpUtils.send(HttpMethod.POST, Url.sortApi, params , new SortListCallBack());
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			String result = info.result;
			JSONArray jsonArray = JsonSmartUtil.getJsonArray(result, "data");
			//LogUtils.e("result = "+result);
			listAdBean = new ArrayList<AdBean>();
			for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray) && (i < jsonArray.size())); i++)
			{
				JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonArray, i);
				AdBean bean = new AdBean();
				bean.ad_code = jsonObject.getAsString("ad_code");
				bean.ad_link = jsonObject.getAsString("ad_link");
				listAdBean.add(bean);
			}
			RequestParams params = new RequestParams(XUtil.charset);
			params.addBodyParameter("format", "json");
			/*TODO: 加载 分类列表 json 数据
			 * */
			//http://muyin.cooltou.cn/zapi/?url=/home/category&format=json
			httpHandler = httpUtils.send(HttpMethod.POST, Url.sortApi, params , new SortListCallBack());
		}
	}
	/**分类列表的 回调
	 * */
	private final class SortListCallBack extends RequestCallBack<String>
	{
		@Override
		public void onStart()
		{
			super.onStart();
			dialogShow(dialogLoading,getActivity());
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
			Cache.saveTmpFile(result);
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(getActivity(), sessionErrorInfo);
				((IndexActivity)getActivity()).startLoginActvitiy(getActivity(), false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "status"), "code"))){
				LogUtils.e("result = "+result);
				ToastUtil.longAtCenterInThread(getActivity(), JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "status"), "error_desc"));
				return ;
			}
			List<SortListBean> list = new ArrayList<SortListBean>();
			JSONArray jsonArray = JsonSmartUtil.getJsonArray(result, "data");
			for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray) && (i < jsonArray.size())); i++)
			{
				JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonArray, i);
				SortListBean bean = new SortListBean();
				bean.cat_id = JsonSmartUtil.getString(jsonObject, "cat_id");
				bean.cat_img = JsonSmartUtil.getString(jsonObject, "cat_img");
				bean.cat_name = JsonSmartUtil.getString(jsonObject, "cat_name");
				//cat_name
				list.add(bean);
			}
			if((list.size()>0) && list.size()<=6){
				setupHeadView(list.subList(0, list.size()));
				setupAdapter(null);
			}else if((list.size()>0) && list.size()>6){
				setupHeadView(list.subList(0, 6));
				setupAdapter(list.subList(6, list.size()));
			}
		}
	}
	private void setupHeadView(List<SortListBean> list)
	{
		if(getActivity() == null){
			return ;
		}
		View headView = LayoutInflater.from(getActivity()).inflate(R.layout.headview_fragment_sort, null);
		LinearLayout llHead1 = (LinearLayout) headView.findViewById(R.id.ll_head1);
		LinearLayout llHead2 = (LinearLayout) headView.findViewById(R.id.ll_head2);
		autoScrollViewPager = (autoScrollViewPager==null)?(AutoScrollViewPager) headView.findViewById(R.id.asvp) :autoScrollViewPager;
		CircleIndicator circleIndicator = (CircleIndicator) headView.findViewById(R.id.ci);
		List<ImageView> listTopImg = new ArrayList<ImageView>();
		for (int i = 0; (listAdBean!=null)&&(i < listAdBean.size()); i++)
		{
			ImageView imageView = new ImageView(getActivity());
			Picasso.with(getActivity()).load(listAdBean.get(i).ad_code).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(imageView);
			imageView.setScaleType(App.getScaleType());
			listTopImg.add(imageView);
			imageView.setLayoutParams(imageParams);
			//imageParams
		}
		/*顶部广告*/
		WelcomeAdapter adapter = new WelcomeAdapter(listTopImg);
		autoScrollViewPager.setAdapter(adapter);
		autoScrollViewPager.setInterval(1000 *3);
		autoScrollViewPager.startAutoScroll(1000 *3);
		autoScrollViewPager.setOnPageClickListener(new MyOnPageChangeListener());
		circleIndicator.setViewPager(autoScrollViewPager);
		/*前 6 条*/
		LayoutParams paramsWeight1 = new LayoutParams(0, imageHeight, 1);
		paramsWeight1.leftMargin = 20;
		LayoutParams paramsWeight2 = new LayoutParams(0, imageHeight, 2);
		paramsWeight2.leftMargin = 20;
		List<ImageView> listHeadImg = new ArrayList<ImageView>();
		for (int i = 0; i < list.size(); i++)
		{
			ImageView imageView = new ImageView(getActivity());
			imageView.setScaleType(ScaleType.CENTER_INSIDE);
			listHeadImg.add(imageView);
			//list.get(i).cat_img
			LogUtils.e("cat_img = "+list.get(i).cat_img);
			Picasso.with(getActivity()).load(list.get(i).cat_img).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(imageView);
			imageView.setOnClickListener(new HeadViewOnClickListener(list.get(i).cat_id, list.get(i).cat_name));
		}
		for (int i = 0; i < list.size(); i++){
			if(i == 0){
				listHeadImg.get(i).setLayoutParams(new LayoutParams(0, imageHeight, 2));
			}else if(i == 1){
				listHeadImg.get(i).setLayoutParams(paramsWeight2);
			}else if(i == 2){
				listHeadImg.get(i-1).setLayoutParams(paramsWeight1);
				listHeadImg.get(i).setLayoutParams(paramsWeight1);
			}else if(i == 3){
				listHeadImg.get(i).setLayoutParams(new LayoutParams(0, imageHeight, 2));
			}else if(i == 4){
				listHeadImg.get(i).setLayoutParams(paramsWeight2);
			}else if(i == 5){
				listHeadImg.get(i-1).setLayoutParams(paramsWeight1);
				listHeadImg.get(i).setLayoutParams(paramsWeight1);
			}
			/*选择 第几排*/
			if(i<=2){
				llHead1.addView(listHeadImg.get(i));
			}else{
				llHead2.addView(listHeadImg.get(i));
			}
		}
		listView.addHeaderView(headView);
	}
	private void setupAdapter(List<SortListBean> list)
	{
		adapter =(adapter==null)? new SortFragmentAdapter(getActivity(),list):adapter;
		listView.setAdapter(adapter);
	}
	private final class HeadViewOnClickListener implements OnClickListener
	{
		private String catID;
		private String catName;
		public HeadViewOnClickListener(String cat_id, String cat_name) {
			this.catID = cat_id;
			this.catName = cat_name;
		}
		@Override
		public void onClick(View v){
			startSortDetailListActivity(catID,catName);
		}
	}
	public void startSortDetailListActivity(String catID, String catName)
	{
		Intent intent = new Intent(getActivity(), SortDetailListActivity.class);
		intent.putExtra("catID", catID);
		intent.putExtra("catName", catName);
		intent.putExtra("functionType", App.functionTypeSortLevel2);
		startActivity(intent);
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(R.id.layout_search == v.getId()){
				Intent intent = new Intent(getActivity(), SortDetailListActivity.class);
				intent.putExtra("functionType", App.functionTypeSearch);
				startActivity(intent);
			}else if(R.id.tv_location == v.getId()){
				Drawable drawableRight = getResources().getDrawable(R.drawable.ic_view_up_brow);
				drawableRight.setBounds(0, 0, drawableRight.getMinimumWidth(), drawableRight.getMinimumHeight());  
				tvLocation.setCompoundDrawables(null, null, drawableRight , null);
				popupWindow.showAsDropDown(tvLocation, 0, 0);
			}
		}
	}
	private final class MyOnPageChangeListener implements OnPageClickListener
	{
		@Override
		public void onPageClick(AutoScrollViewPager pager, int position)
		{
			AdBean bean = listAdBean.get(position);
			Intent intent = new Intent(getActivity(), WebViewActivity.class);
			intent.putExtra("url", bean.ad_link);
			startActivity(intent);
		}
	}
	/**TODO: Fragment 响应的接口回调
	 * 相应 接口回调*/
	private final class MyOnFragmentSelectedListener implements OnFragmentSelectedListener
	{
		@Override
		public void onFragmentSelected(int indexSelected)
		{
			String cityName = User.getCityName(getActivity());
			if(!TextUtils.isEmpty(cityName)){
				tvLocation.setText(cityName);
			}else{
				tvLocation.setText("选择城市");
			}
			if((indexSelected == 1) && canLoading){
				canLoading = false;
				loadJsonData();
			}else{

			}
		}
	}
	@Override
	public void onStop()
	{
		super.onStop();
		if(autoScrollViewPager!=null){
			autoScrollViewPager.stopAutoScroll();
		}
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if(autoScrollViewPager!=null){
			autoScrollViewPager.stopAutoScroll();
		}
		autoScrollViewPager = null;
	}
	@Override
	public void onResume()
	{
		super.onResume();
		if(autoScrollViewPager!=null){
			autoScrollViewPager.startAutoScroll(1000 * 3);
		}
		String cityName = User.getCityName(getActivity());
		if(!TextUtils.isEmpty(cityName)){
			tvLocation.setText(cityName);
		}else{
			tvLocation.setText("选择城市");
		}
	}
	private void initImageParams(Context context)
	{
		int screenWidth = ScreenUtil.getScreenWidth(context);
		int imageHeight = 9*screenWidth / 16;
		imageParams = new LayoutParams(screenWidth,imageHeight);
	}
}
