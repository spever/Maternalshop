package com.subzero.maternalshop.fragment;
import java.util.ArrayList;
import java.util.List;

import subzero.wangjie.shadowviewhelper.ShadowUtil;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import android.R.array;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMap.OnMarkerDragListener;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.subzero.common.listener.OnFragmentSelectedListener;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.ScreenUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.IndexActivity;
import com.subzero.maternalshop.activity.search.SearchShopActivity;
import com.subzero.maternalshop.activity.shop.ShopDetailActivity;
import com.subzero.maternalshop.bean.ShopLocationBean;
import com.subzero.maternalshop.config.Cache;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
/**首页模块
 * 启动者：
 * 	默认首页：IndexActivity
 * 启动项：
 * 	搜索店铺：SearchShopActivity
 * 	店铺详情：ShopDetailActivity
 * */
public class HomepageFragment extends PopFragment
{
	private MapView mapView;
	private AMap aMap;
	private LocationManagerProxy locationManagerProxy;
	private MyAMapLocationListener aMapLocationListener;
	protected AMapLocation aMapLocation;
	/**当前经纬度*/
	private LatLng currLatLng;
	/**目标经纬度*/
	protected LatLng destLatLon;
	protected float zoomXXXLarge = 19f;
	protected float zoomXXLarge = 15f;
	protected float zoomXLarge = 12f;
	/**位置变化通知距离，单位为米。*/
	private static final float minDistance = 100;
	/**位置变化的通知时间，单位为毫秒。如果为-1，定位只定位一次。*/
	//1000 * 10
	private static final long minTime = -1;
	/**目标地址的 经纬度*/
	private List<ShopLocationBean> listShopLocationBean;
	private Marker currMarker;
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		((IndexActivity)activity).addOnFragmentSelectedListener(new MyOnFragmentSelectedListener());
		setOnLocationClickListener(new MyOnLocationClickListener());
	}
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		listShopLocationBean = new ArrayList<ShopLocationBean>();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_homepage, null);
			initPopWindow();
			initView();
			initAMap(savedInstanceState);
			startLocation();
			loadShopListJson(User.getCityId(getActivity()));
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
		tvLocation = (TextView) rootView.findViewById(R.id.tv_location);
		rootView.findViewById(R.id.layout_search).setOnClickListener(new MyOnClickListener());
		rootView.findViewById(R.id.iv_start_location).setOnClickListener(new MyOnClickListener());
		ShadowUtil.bindView(rootView.findViewById(R.id.iv_start_location),0xEEFFFFFF);
		tvLocation.setOnClickListener(new MyOnClickListener());
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(R.id.layout_search == v.getId()){
				Intent intent = new Intent(getActivity(), SearchShopActivity.class);
				startActivity(intent);
			}else if(R.id.iv_start_location == v.getId()){
				startLocation();
			}else if(R.id.tv_location == v.getId()){
				Drawable drawableRight = getResources().getDrawable(R.drawable.ic_view_up_brow);
				drawableRight.setBounds(0, 0, drawableRight.getMinimumWidth(), drawableRight.getMinimumHeight());  
				tvLocation.setCompoundDrawables(null, null, drawableRight , null);
				popupWindow.showAsDropDown(tvLocation, 0, 0);
			}
		}
	}
	/**TODO: Fragment 响应的接口回调
	 * */
	private final class MyOnFragmentSelectedListener implements OnFragmentSelectedListener
	{
		@Override
		public void onFragmentSelected(int indexSelected)
		{
			if(indexSelected == 0){
				String cityName = User.getCityName(getActivity());
				if(!TextUtils.isEmpty(cityName)){
					tvLocation.setText(cityName);
				}else{
					tvLocation.setText("选择城市");
				}
				mapView.setVisibility(View.VISIBLE);
			}else{
				if(currMarker!=null){
					currMarker.hideInfoWindow();
				}
				mapView.setVisibility(View.GONE);
			}
		}
	}
	/**TODO: 加载店铺列表
	 * */
	private void loadShopListJson(String locationId)
	{
		//currLatLng(天通苑北三区) = 40.075224 116.431224
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("region_id", locationId);
		LogUtils.e("locationId = "+locationId);
		//http://muyin.cooltou.cn/zapi/?url=/home/data&format=json&region_id=2
		httpHandler = httpUtils.send(HttpMethod.GET, Url.shopListApi, params , new ShopListCallBack());
	}
	/**商店列表的 回调
	 * */
	private final class ShopListCallBack extends RequestCallBack<String>
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
			LogUtils.e("result =sssssssssssssss "+result);
			Cache.saveTmpFile(result);
			JSONArray jsonArray = JsonSmartUtil.getJsonArray(result, "data");
			LogUtils.e("result =sdfsdfsdfsd "+jsonArray.size());
			listShopLocationBean = (listShopLocationBean==null) ? new ArrayList<ShopLocationBean> ():listShopLocationBean;
			listShopLocationBean.clear();
			for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray)) && (i < jsonArray.size()); i++)
			{
				JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonArray, i);
				double lat = Double.parseDouble(JsonSmartUtil.getString(jsonObject, "company_latitude"));
				double lng = Double.parseDouble(JsonSmartUtil.getString(jsonObject, "company_longitude"));
				ShopLocationBean bean = new ShopLocationBean();
				bean.lat = lat;
				bean.lng = lng;
				bean.comment_count = JsonSmartUtil.getString(jsonObject, "comment_count");
				bean.company_intro = JsonSmartUtil.getString(jsonObject, "company_intro")+"";
				bean.order_count = JsonSmartUtil.getString(jsonObject, "order_count");
				bean.supplier_id = JsonSmartUtil.getString(jsonObject, "supplier_id");
				bean.supplier_name = JsonSmartUtil.getString(jsonObject, "supplier_name");
				listShopLocationBean.add(bean);
				
			}
			LogUtils.e("result =size "+listShopLocationBean.size());
			startLocation();
		}
	}
	/**AMap配置信息
	 * @time 2015-03-03    16:15*/
	private void initAMap(Bundle savedInstanceState)
	{
		mapView = (MapView) rootView.findViewById(R.id.mv);
		/*必须要写*/ 
		mapView.onCreate(savedInstanceState);
		aMap = mapView.getMap();
		aMap.setMapType(AMap.MAP_TYPE_NORMAL);
		aMap.setMyLocationEnabled(true);
		aMap.getUiSettings().setScrollGesturesEnabled(true);
		aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
		aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomXLarge));
		aMap.setLocationSource(new MyLocationSource());
		aMap.setOnMarkerDragListener(new MyOnMarkerDragListener());
		aMap.setOnMarkerClickListener(new MyOnMarkerClickListener());
		aMap.setOnMapLoadedListener(new MyOnMapLoadedListener());
		aMap.setOnInfoWindowClickListener(new MyOnInfoWindowClickListener());
		aMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
		aMap.setOnMapClickListener(new MyOnMapClickListener());
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location));
		myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));
		aMap.setMyLocationStyle(myLocationStyle);
	}
	private final class MyOnMapClickListener implements OnMapClickListener
	{
		@Override
		public void onMapClick(LatLng latLng)
		{
			LogUtils.e("点击地图 currMarker = "+(currMarker==null));
			if(currMarker!=null){
				currMarker.hideInfoWindow();
			}
		}
	}
	private final class MyOnInfoWindowClickListener implements OnInfoWindowClickListener
	{
		@Override
		public void onInfoWindowClick(Marker marker)
		{
			marker.hideInfoWindow();
		}
	}
	private final class MyInfoWindowAdapter implements InfoWindowAdapter
	{
		@Override
		public View getInfoContents(Marker marker)
		{
			return null;
		}
		@SuppressLint("InflateParams")
		@Override
		public View getInfoWindow(Marker marker)
		{
			View viewInfoWindow = LayoutInflater.from(getActivity()).inflate(R.layout.info_window_business_detail, null);
			TextView tvName = (TextView) viewInfoWindow.findViewById(R.id.tv_name);
			TextView tvDescribe = (TextView) viewInfoWindow.findViewById(R.id.tv_describe);
			TextView tvSaleCount = (TextView) viewInfoWindow.findViewById(R.id.tv_sale_count);
			TextView tvEvaluateCount = (TextView) viewInfoWindow.findViewById(R.id.tv_evaluate_count);
			//getScreenWidth
			viewInfoWindow.findViewById(R.id.iv_shop_detail).setOnClickListener(new InfoWindowClickListener(Integer.parseInt(marker.getSnippet())));
			viewInfoWindow.findViewById(R.id.layout_shop_detail).setOnClickListener(new InfoWindowClickListener(Integer.parseInt(marker.getSnippet())));
			LayoutParams params = new LayoutParams((int) (ScreenUtil.getScreenWidth(getActivity())*0.8), LayoutParams.WRAP_CONTENT);
			viewInfoWindow.setLayoutParams(params );
			if(!TextUtils.isEmpty(marker.getTitle())){
				tvName.setText(marker.getTitle());
			}
			int position = 0;
			if(!TextUtils.isEmpty(marker.getSnippet())){
				position = Integer.parseInt(marker.getSnippet());
			}
			ShopLocationBean bean = listShopLocationBean.get(position);
			ViewUtil.setText2TextView(tvSaleCount, "月销"+bean.order_count+"笔");
			ViewUtil.setText2TextView(tvEvaluateCount, "评论"+bean.comment_count);
			ViewUtil.setText2TextView(tvDescribe, bean.company_intro);
			return viewInfoWindow;
		}
	}
	private final class InfoWindowClickListener implements OnClickListener
	{
		private int position;
		public InfoWindowClickListener(int position) {
			this.position = position;
		}
		@Override
		public void onClick(View v)
		{
			if((R.id.iv_shop_detail == v.getId()) || (R.id.layout_shop_detail == v.getId())){
				Intent intent = new Intent(getActivity(), ShopDetailActivity.class);
				intent.putExtra("suppId", listShopLocationBean.get(position).supplier_id);
				startActivity(intent);
				currMarker.hideInfoWindow();
			}
		}
	}
	/**标注物的点击事件
	 * */
	private final class MyOnMarkerClickListener implements OnMarkerClickListener
	{
		/**@return 返回true不会 将InfoWindow移到手机中心， false会*/
		@Override
		public boolean onMarkerClick(Marker marker)
		{
			HomepageFragment.this.currMarker = marker;
			if (aMap != null) {
				//jumpMarker(marker);
			}
			return false;
		}
	}
	/**让 地图标注物，跳起来*/
	protected void jumpMarker(final Marker marker)
	{
		final LatLng latLng = marker.getPosition();
		final Handler handler = new Handler();
		final long timeStart = SystemClock.uptimeMillis();
		Projection proj = aMap.getProjection();
		Point pointTo = proj.toScreenLocation(latLng);
		pointTo.offset(0, -80);
		final LatLng latLngFrom = proj.fromScreenLocation(pointTo);
		final Interpolator interpolator = new BounceInterpolator();
		final long duration = 500;
		handler.post(new Runnable() {
			@Override
			public void run()
			{
				long timeElapsed = SystemClock.uptimeMillis() - timeStart;
				float t = interpolator.getInterpolation((float) timeElapsed / duration);
				double lng = t * latLng.longitude + (1 - t) * latLngFrom.longitude;
				double lat = t * latLng.latitude + (1 - t) * latLngFrom.latitude;
				marker.setPosition(new LatLng(lat, lng));
				if (t < 1.0) {
					handler.postDelayed(this, 50);
				}
			}
		});
	}
	/**地图标注物 拖拽完成的回调*/
	private final class MyOnMarkerDragListener implements OnMarkerDragListener
	{
		@Override
		public void onMarkerDrag(Marker marker)
		{

		}

		@Override
		public void onMarkerDragEnd(Marker marker)
		{
			LogUtils.e("标注物 拖拽 结束");
		}
		@Override
		public void onMarkerDragStart(Marker marker)
		{
			LogUtils.e("标注物 拖拽 开始");
		}
	}
	private final class MyLocationSource implements LocationSource
	{
		@Override
		public void activate(OnLocationChangedListener listener) {
			LogUtils.e("LocationSource.activate()。。。");
			startLocation();
		}
		@Override
		public void deactivate() {
			LogUtils.e("LocationSource.deactivate()。。。");
			stopLocation();
		}
	}
	/**AMap定位的回调接口
	 * @time 2015-03-04    12:31*/
	private final class MyAMapLocationListener implements AMapLocationListener
	{
		@Override
		public void onLocationChanged(Location location){	
			LogUtils.e("onLocationChanged");
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras){
			LogUtils.e("onStatusChanged");
		}
		@Override
		public void onProviderEnabled(String provider){
			LogUtils.e("onProviderEnabled");
		}
		@Override
		public void onProviderDisabled(String provider){
			LogUtils.e("onProviderDisabled");
		}
		@Override
		public void onLocationChanged(AMapLocation aMapLocation)
		{
			//TODO 定位完成
			dialogDismiss(dialogLoading);
			//LogUtils.e("onLocationChanged");
			if(aMapLocation==null || aMapLocation.getAMapException().getErrorCode()!=0){
				LogUtils.e("网络连接异常");
				ToastUtil.shortAtCenter(getActivity(), "网络连接异常");
				return;
			}
			HomepageFragment.this.aMapLocation = aMapLocation;
			currLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
			//LogUtils.e("currLatLng = "+currLatLng.latitude+" "+currLatLng.longitude);
			MarkerOptions	markerOptions = new MarkerOptions();
			markerOptions.draggable(true);
			//markerOptions.title("我的位置");
			BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_location));
			markerOptions.icon(descriptor);
			markerOptions.position(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
			aMap.clear();
			@SuppressWarnings("unused")
			Marker marker = aMap.addMarker(markerOptions);
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currLatLng.latitude, currLatLng.longitude), zoomXXLarge);
			aMap.moveCamera(cameraUpdate);
			addMarkers();
		}
	}
	private Bitmap imgMarker;  
	private int width,height;   //图片的高度和宽带  
    private Bitmap imgTemp;  //临时标记图  
	private Drawable createDrawable(int letter) {  
        imgTemp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);  
        Canvas canvas = new Canvas(imgTemp);  
        Paint paint = new Paint(); // 建立画笔  
        paint.setDither(true);  
        paint.setFilterBitmap(true);  
        Rect src = new Rect(0, 0, width, height);  
        Rect dst = new Rect(0, 0, width, height);  
        canvas.drawBitmap(imgMarker, src, dst, paint);  
  
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG  
                | Paint.DEV_KERN_TEXT_FLAG);  
        textPaint.setTextSize(20.0f);  
        textPaint.setTypeface(Typeface.DEFAULT_BOLD); // 采用默认的宽度  
        textPaint.setColor(Color.WHITE);  
  
        canvas.drawText(String.valueOf(letter), width /2-5, height/2+5,  
                textPaint);  
        canvas.save(Canvas.ALL_SAVE_FLAG);  
        canvas.restore();  
        return (Drawable) new BitmapDrawable(getResources(), imgTemp);  
  
    }  
	
	public static Bitmap drawableToBitmap(Drawable drawable) {

	       
        Bitmap bitmap = Bitmap.createBitmap(

                                        drawable.getIntrinsicWidth(),

                                        drawable.getIntrinsicHeight(),

                                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                                                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        //canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;

}
	/**@category 添加地图标注物*/
	private void addMarkers()
	{
//		int resId[] = new int[]{R.drawable.ic_marker1,R.drawable.ic_marker2,R.drawable.ic_marker3,R.drawable.ic_marker4,R.drawable.ic_marker5,R.drawable.ic_marker6,R.drawable.ic_marker7,R.drawable.ic_marker8,R.drawable.ic_marker9,R.drawable.ic_marker10};
//		int resId[] = new int[listShopLocationBean.size()];
		ArrayList<Drawable> drawables = new ArrayList<>();

		for(int i = 0; i < listShopLocationBean.size(); i++){
			drawables.add(createDrawable(i));
		}
		
		for (int i = 0; i < listShopLocationBean.size(); i++)
		{
			ShopLocationBean bean = listShopLocationBean.get(i);
			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.position(new LatLng(bean.lat, bean.lng));
			markerOptions.title(bean.supplier_name);
			markerOptions.snippet(i+"");
//			markerOptions.icon(BitmapDescriptorFactory.fromResource(resId[i]));
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(drawables.get(i))));

			LogUtils.e("result =size "+markerOptions+"-----------------------"+i);
			
			Marker marker = aMap.addMarker(markerOptions);
			marker.setDraggable(true);
		}
	}
	

	/**开始AMap定位； 显示对话框
	 * @time 2015-03-04    13:51*/
	private void startLocation() {
		//TODO 开始定位
		locationManagerProxy = (locationManagerProxy==null) ? LocationManagerProxy.getInstance(getActivity()) : locationManagerProxy;
		aMapLocationListener = (aMapLocationListener==null) ? new MyAMapLocationListener() : aMapLocationListener;
		locationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, minTime, minDistance, aMapLocationListener);
		locationManagerProxy.setGpsEnable(true);
		dialogShow(dialogLoading, getActivity());
	}
	/**停止AMap定位
	 * @time 2015-03-04    13:51*/
	private void stopLocation()
	{
		if(locationManagerProxy != null)
		{
			locationManagerProxy.removeUpdates(aMapLocationListener);
			locationManagerProxy.destroy();
		}
		locationManagerProxy = null;
		aMapLocationListener = null;
	}
	/**地图载入成功的 回调*/
	private final class MyOnMapLoadedListener implements OnMapLoadedListener
	{
		@Override
		public void onMapLoaded()
		{
			//LogUtils.e("地图载入成功--onMapLoaded");
		}
	}
	/**方法必须重写*/
	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
		String cityName = User.getCityName(getActivity());
		if(!TextUtils.isEmpty(cityName)){
			tvLocation.setText(cityName);
		}else{
			tvLocation.setText("选择城市");
		}
	}
	/**方法必须重写*/
	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
		stopLocation();
	}
	/**方法必须重写*/
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}
	/**方法必须重写*/
	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		stopLocation();
	}
	/**TODO: 切换城市
	 * */
	private final class MyOnLocationClickListener implements OnLocationClickListener
	{
		@Override
		public void onLocationClick(Bundle bundle)
		{
			LogUtils.e("id = "+bundle.getString("id")+" name = "+bundle.getString("name"));
			loadShopListJson(bundle.getString("id"));
		}
	}
}
