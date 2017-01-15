package com.subzero.maternalshop.fragment;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.IndexActivity;
import com.subzero.maternalshop.adapter.baseadapter.PopWindowAdapter;
import com.subzero.maternalshop.bean.LocationBean;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.util.JsonCommonUtil;
public class PopFragment extends BaseFragment
{
	public PopupWindow popupWindow;
	public TextView tvLocation;
	private PopWindowAdapter adapter;
	protected OnLocationClickListener onLocationClickListener;
	@Override
	public void initView(){		}
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		loadCityJson();
	}
	private void loadCityJson()
	{
		//currLatLng(天通苑北三区) = 40.075224 116.431224
		//http://muyin.cooltou.cn/zapi/?url=/home/data&format=json&action=area
		httpHandler = httpUtils.send(HttpMethod.GET, Url.cityApi , new CityCallBack());
	}
	/**商店列表的 回调
	 * */
	private final class CityCallBack extends RequestCallBack<String>
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
			if(JsonCommonUtil.getSessionErrorInfo(result) != null){
				((IndexActivity)getActivity()).startLoginActvitiy(getActivity(), false);
				return;
			}
			if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ToastUtil.shortAtCenterInThread(getActivity(), JsonCommonUtil.getCommonErrorInfo(result));
				return;
			}
			JSONArray jsonArray = JsonSmartUtil.getJsonArray(result, "data");
			List<LocationBean> list = new ArrayList<LocationBean>();
			for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray)) && (i<jsonArray.size()); i++)
			{
				JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonArray, i);
				LocationBean bean = new LocationBean();
				bean.id = JsonSmartUtil.getString(jsonObject, "region_id");
				bean.name =  JsonSmartUtil.getString(jsonObject, "region_name");
				list.add(bean);
			}
			adapter.addItem(list);
		}
	}
	@SuppressWarnings("deprecation")
	protected void initPopWindow()
	{
		View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_location, null);
		popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setOnDismissListener(new MyOnDismissListener());

		ListView listView = (ListView) popupView.findViewById(R.id.lv);
		adapter = new PopWindowAdapter(getActivity());
		listView.setOnItemClickListener(new MyOnItemClickListener());
		listView.setAdapter(adapter);
	}
	protected final class MyOnItemClickListener implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			popupWindow.dismiss();
			LocationBean bean = adapter.getList().get(position);
			tvLocation.setText(bean.name);
			User.setCityId(getActivity(), bean.id);
			User.setCityName(getActivity(), bean.name);
			if(onLocationClickListener!=null){
				Bundle bundle = new Bundle();
				bundle.putString("id", bean.id);
				bundle.putString("name", bean.name);
				onLocationClickListener.onLocationClick(bundle);
			}
		}
	}
	protected final class MyOnDismissListener implements OnDismissListener
	{
		@Override
		public void onDismiss()
		{
			Drawable drawableRight = getResources().getDrawable(R.drawable.ic_view_down_brow);
			drawableRight.setBounds(0, 0, drawableRight.getMinimumWidth(), drawableRight.getMinimumHeight());  
			tvLocation.setCompoundDrawables(null, null, drawableRight , null);
		}
	}
	public void setOnLocationClickListener(OnLocationClickListener onLocationClickListener){
		this.onLocationClickListener = onLocationClickListener;
	}
	public interface OnLocationClickListener{
		public void onLocationClick(Bundle bundle);
	}
}
