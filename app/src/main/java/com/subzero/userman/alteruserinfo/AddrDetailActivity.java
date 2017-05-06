package com.subzero.userman.alteruserinfo;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.StringUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.config.CallBackType;
import com.subzero.maternalshop.config.Url;
/**收货地址
 * 启动者：
 * 	收货地址列表： AddrListActivity(AddrListAdapter)
 * 启动项：
 * 	区域列表：AreaListActivity*/
public class AddrDetailActivity extends BaseActivity
{
	public static int requestCode = 100;
	private String phoneNum;
	/**收货人*/
	private String receiver;
	private String proviceID;
	private String cityID;
	private String countyID;
	private String proviceName;
	private String cityName;
	private String countyName;
	private String addressID;
	/**村镇 小区 门牌号 详细地址*/
	private String address;
	/**收货地址列表为空*/
	private boolean isAddrListEmpty;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addr_detail);
		addressID = getIntent().getStringExtra("addressID");
		address = getIntent().getStringExtra("address");
		receiver = getIntent().getStringExtra("receiver");
		proviceName = getIntent().getStringExtra("proviceName");
		cityName = getIntent().getStringExtra("cityName");
		countyName = getIntent().getStringExtra("countyName");
		phoneNum = getIntent().getStringExtra("phoneNum");
		isAddrListEmpty = getIntent().getBooleanExtra("isAddrListEmpty", false);
		initView();
	}
	@Override
	public void initView()
	{
		if(!TextUtils.isEmpty(addressID)){
			ViewUtil.setText2TextView(findViewById(R.id.tv_area), proviceName+"、"+cityName+"、"+countyName);
			ViewUtil.setText2EditText(findViewById(R.id.et_receiver), receiver);
			ViewUtil.setText2EditText(findViewById(R.id.et_phone_num), phoneNum);
			ViewUtil.setText2EditText(findViewById(R.id.et_addr), address);
		}else{
			findViewById(R.id.layout_delete).setVisibility(View.GONE);
		}
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		findViewById(R.id.bt_submit).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_area).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_delete).setOnClickListener(new MyOnClickListener());
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(data == null || data.getExtras() == null){
			return ;
		}
		proviceID = data.getStringExtra("proviceID");
		proviceName = data.getStringExtra("proviceName");
		cityID = data.getStringExtra("cityID");
		cityName = data.getStringExtra("cityName");
		countyID = data.getStringExtra("countyID");
		countyName = data.getStringExtra("countyName");
		ViewUtil.setText2TextView(findViewById(R.id.tv_area), proviceName+"、"+cityName+"、"+countyName);
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(R.id.tv_area == v.getId()){
				/*区域列表*/
				startActivityForResult((new Intent(context, AreaListActivity.class)), requestCode);
			}
			else if(R.id.bt_submit == v.getId()){
				receiver = ((EditText)findViewById(R.id.et_receiver)).getText().toString();
				if(TextUtils.isEmpty(receiver)){
					ToastUtil.shortAtCenter(context, "收货人为空");
					return;
				} 
				phoneNum = ((EditText)findViewById(R.id.et_phone_num)).getText().toString();
				if(!StringUtil.isPhoneNum(phoneNum)){
					ToastUtil.shortAtCenter(context, "手机号非法");
					return;
				} 
				address = ((EditText)findViewById(R.id.et_addr)).getText().toString();
				if(TextUtils.isEmpty(address)){
					ToastUtil.shortAtCenter(context, "收货地址为空");
					return;
				} 
				if(TextUtils.isEmpty(addressID)){
					addAddr();
				}else{
					updateAddr();
				}
			}
			else if(R.id.tv_delete == v.getId()){
				deleteAddr();
			}
		}
	}
	/**收货地址新增 
	 * @param
	 * @param
	 * @param  */
	private void addAddr()
	{
		RequestParams params = new RequestParams(XUtil.charset);
		params.addBodyParameter("province", proviceID);
		params.addBodyParameter("city", cityID);
		params.addBodyParameter("district", countyID);
		params.addBodyParameter("mobile", phoneNum);
		params.addBodyParameter("consignee", receiver);
		params.addBodyParameter("address", address);
		if(isAddrListEmpty){
			LogUtils.e("default_address = 1");
			params.addBodyParameter("default_address", 1+"");
		}else{
			params.addBodyParameter("default_address", 0+"");
			LogUtils.e("default_address = 0");
		}
		LogUtils.e(XUtil.params2String(params));
		httpUtils.send(HttpMethod.POST, Url.addrAddApi, params , new AddressCallBack(CallBackType.addAddr));
	}
	/**删除 收货地址  */
	public void deleteAddr()
	{
		RequestParams params = new RequestParams(XUtil.charset);
		params.addBodyParameter("address_id", addressID);
		httpUtils.send(HttpMethod.POST, Url.addrDeleteApi, params , new AddressCallBack(CallBackType.deleteAddr));
	}
	/**更新 收货地址  */
	public void updateAddr()
	{
		RequestParams params = new RequestParams(XUtil.charset);
		params.addBodyParameter("province", proviceID);
		params.addBodyParameter("city", cityID);
		params.addBodyParameter("district", countyID);
		params.addBodyParameter("mobile", phoneNum);
		params.addBodyParameter("consignee", receiver);
		params.addBodyParameter("address", address);
		params.addBodyParameter("address_id", addressID);
		httpUtils.send(HttpMethod.POST, Url.addrUpdateApi, params , new AddressCallBack(CallBackType.updateAddr));
	}
	/**常用地址列表的 回调
	 * */
	private final class AddressCallBack extends RequestCallBack<String>
	{
		private CallBackType callBackType;
		public AddressCallBack(CallBackType callBackType) {
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
			dialogDismiss(dialogLoading);
			String result = info.result;
			//LogUtils.e("result = "+result);
			//{"status":{"code":200},"data":[]}
			String code = JsonSmartUtil.getString(JsonSmartUtil.getJsonObject(result, "status"), "code");
			if("200".equalsIgnoreCase(code)){
				if(CallBackType.addAddr == callBackType){
					ToastUtil.shortAtCenterInThread(context, "提交成功");
					finish();
				}else if(CallBackType.deleteAddr == callBackType){
					ToastUtil.shortAtCenterInThread(context, "删除成功");
					finish();
				}else if(CallBackType.updateAddr == callBackType){
					ToastUtil.shortAtCenterInThread(context, "修改成功");
					finish();
				}
			}
		}
	}
}
