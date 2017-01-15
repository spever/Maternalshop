package com.subzero.maternalshop.adapter.baseadapter;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.order.OrderListActivity;
import com.subzero.maternalshop.activity.order.OrderSubmitActivity;
import com.subzero.maternalshop.bean.AddrBean;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.CallBackType;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.util.JsonAddrList;
import com.subzero.maternalshop.util.JsonCommonUtil;
import com.subzero.userman.alteruserinfo.AddrDetailActivity;
import com.subzero.userman.alteruserinfo.AddrListActivity;
/**使用介绍，适配器
 * 宿主：
 * 	收货地列表：AddrListActivity
 * 启动项：
 * 	收货地址：AddrDetailActivity
 * */
public class AddrListAdapter extends BaseHttpAdapter
{
	protected List<AddrBean> list;
	private String moduleName;
	private int position;
	public AddrListAdapter(Context context) {
		this.list = new ArrayList<AddrBean>();
		this.context = context;
		initDialogLoading();
		initHttputils();
	}
	public String getModuleName(){
		return moduleName;
	}
	public void setModuleName(String moduleName)	{
		this.moduleName = moduleName;
	}
	/**更新适配器*/
	public void updateItem(AddrBean obj,int position){
		if((position<0) || (position > list.size()-1)){
			return;
		}
		list.set(position, obj);
		notifyDataSetChanged();
	}
	public void updateItem(List<AddrBean> list){
		for (int i = 0; (list!=null)&&(i < list.size()); i++){
			AddrBean bean = list.get(i);
			this.list.set(i, bean);
		}
		notifyDataSetChanged();
	}
	public void addItem(AddrBean obj){
		list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<AddrBean> list){
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void refreshItem(List<AddrBean> list){
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void removeItem(int position){
		if(list == null || list.size()<=0 || position >= list.size() || position<0){
			return;
		}
		this.list.remove(position);
		notifyDataSetChanged();
	}
	public List<AddrBean> getList(){
		return list;
	}
	public void clearAll(){
		this.list.clear();
		notifyDataSetChanged();
	}
	@Override
	public int getCount(){
		return (list==null) ? 0:list.size();
	}
	@Override
	public Object getItem(int position){
		return (list==null) ? null:list.get(position);
	}
	@Override
	public long getItemId(int position){
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(list == null || list.size()<=0){
			return convertView;
		}
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_addr_list, parent, false);
			holder.tvReceiver =(TextView) convertView.findViewById(R.id.tv_receiver);
			holder.tvPhoneNnum =(TextView) convertView.findViewById(R.id.tv_phone_num);
			holder.tvAddress =(TextView) convertView.findViewById(R.id.tv_addr);
			holder.lineLast = convertView.findViewById(R.id.line_last);
			holder.ivDefault = (ImageView) convertView.findViewById(R.id.iv_default);
			holder.layoutRoot = convertView.findViewById(R.id.layout_root);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		/*最后一条线*/
		if(position == (list.size()-1)){
			holder.lineLast.setVisibility(View.VISIBLE);
		}else{
			holder.lineLast.setVisibility(View.GONE);
		}
		/*解决错位问题*/
		holder.ivDefault.setTag(list.get(position).address_id);
		String idTag = (String) holder.ivDefault.getTag();
		LogUtils.e("position = "+position+" default_address = "+list.get(position).default_address);
		if(idTag.equalsIgnoreCase(list.get(position).address_id)){
			String default_address = list.get(position).default_address;
			if("1".equalsIgnoreCase(default_address)){
				holder.ivDefault.setSelected(true);
			}else{
				holder.ivDefault.setSelected(false);
			}
		}
		ViewUtil.setText2TextView(holder.tvReceiver, list.get(position).consignee);
		ViewUtil.setText2TextView(holder.tvPhoneNnum, list.get(position).mobile);
		ViewUtil.setText2TextView(holder.tvAddress, list.get(position).address);
		holder.ivDefault.setOnClickListener(new MyOnClickListener(position, App.clickEditSelected));
		holder.layoutRoot.setOnClickListener(new MyOnClickListener(position, App.clickBody));
		return convertView;
	}
	private final class MyOnClickListener implements OnClickListener
	{
		private int position;
		private String clickType;
		public MyOnClickListener(int position,String clickType) {
			this.position = position;
			this.clickType = clickType;
		}
		@Override
		public void onClick(View v)
		{
			AddrListAdapter.this.position = position;
			if(App.clickBody.equalsIgnoreCase(clickType)){
				Intent intent = new Intent(context, AddrDetailActivity.class);
				intent.putExtra("addressID", list.get(position).address_id);
				intent.putExtra("address", list.get(position).address);
				intent.putExtra("receiver", list.get(position).consignee);
				intent.putExtra("phoneNum", list.get(position).mobile);
				intent.putExtra("proviceName", list.get(position).province_name);
				intent.putExtra("cityName", list.get(position).city_name);
				intent.putExtra("countyName", list.get(position).district_name);
				context.startActivity(intent);
			}else if(App.clickEditSelected.equalsIgnoreCase(clickType)){
				RequestParams params = new RequestParams(XUtil.charset);
				params.addBodyParameter("address_id", list.get(position).address_id);
				//http://muyin.cooltou.cn/zapi/?url=/address/setDefault&format=json&address_id=49
				httpUtils.send(HttpMethod.POST, Url.addrSetDefaultApi, params , new UpdateAddressCallBack(CallBackType.setDefault));
			}
		}
	}
	/**常用地址列表的 回调
	 * */
	private final class UpdateAddressCallBack extends RequestCallBack<String>
	{
		private CallBackType callBackType;
		public UpdateAddressCallBack(CallBackType callBackType) {
			this.callBackType = callBackType;
		}
		@Override
		public void onStart()
		{
			super.onStart();
			dialogLoading.show();
		}
		@Override
		public void onFailure(HttpException error, String msg)
		{
			dialogLoading.dismiss();
			ToastUtil.longAtCenterInThread(context, XUtil.getErrorInfo(error));
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			//dialogLoading.dismiss();
			String result = info.result;
			LogUtils.e("result = "+result);
			//{"status":{"code":200},"data":[]}
			String code = JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "status"), "code");
			if("200".equalsIgnoreCase(code)){
				if(CallBackType.setDefault == callBackType){
					loadAddrList();
				}else{

				}
			}else if("400".equalsIgnoreCase(JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "status"), "code"))){
				dialogLoading.dismiss();
				ToastUtil.longAtCenterInThread(context, JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "status"), "error_desc"));
				return;
			}else{
				dialogLoading.dismiss();
				ToastUtil.shortAtCenterInThread(context, "操作失败");
			}
		}
	}
	private void loadAddrList()
	{
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("uid", User.getUserId(context));
		params.addQueryStringParameter("sid", User.getSID(context));
		LogUtils.e("uid  = "+User.getUserId(context));
		LogUtils.e("sid  = "+User.getSID(context));
		httpUtils.send(HttpMethod.GET, Url.addrListApi, params , new AddrListCallBack());
	}
	/**常用地址列表的 回调
	 * */
	private final class AddrListCallBack extends RequestCallBack<String>
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
			dialogLoading.dismiss();
			ToastUtil.longAtCenterInThread(context, XUtil.getErrorInfo(error));
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			dialogLoading.dismiss();
			String result = info.result;
			//LogUtils.e("result = "+result);
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				((OrderListActivity)context).startLoginActvitiy((Activity)context, false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ToastUtil.shortAtCenterInThread(context, JsonCommonUtil.getCommonErrorInfo(result));
				return;
			}
			JSONArray jsonArray = JsonSmartUtil.getJsonArray(result, "data");
			if(JsonSmartUtil.isJsonArrayEmpty(jsonArray)){
				ToastUtil.shortAtCenterInThread(context, "暂无数据，请新增");
			}
			List<AddrBean> list = JsonAddrList.getAddrList(jsonArray);
			refreshItem(list);
			//TODO 如果是 从订单确认 进入 收货地址选择  点击确认默认地址后 finish
			if(OrderSubmitActivity.class.getSimpleName().equalsIgnoreCase(moduleName)){
				if(position > (list.size() - 1)){
					return ;
				}
				AddrBean bean = list.get(position);
				Intent data = new Intent(context, OrderSubmitActivity.class);
				data.putExtra("address", bean.address);
				data.putExtra("consignee", bean.consignee);
				data.putExtra("province_name", bean.province_name);
				data.putExtra("city_name", bean.city_name);
				data.putExtra("district_name", bean.district_name);
				data.putExtra("mobile", bean.mobile);
				((AddrListActivity)context).setResult(Activity.RESULT_OK, data);
				((AddrListActivity)context).finish();
			}
		}
	}
	protected final class ViewHolder{
		/**收货人*/
		public TextView tvReceiver;
		/**做分隔线*/
		public View lineLast;
		/**具体收货地址*/
		public TextView tvAddress;
		/**手机号码*/
		public TextView tvPhoneNnum;
		/**默认地址*/
		public ImageView ivDefault;
		public View layoutRoot;
	}
}
