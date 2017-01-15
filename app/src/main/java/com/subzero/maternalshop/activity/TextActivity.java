package com.subzero.maternalshop.activity;
import subzero.maxwin.XListView;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.order.OrderListActivity;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.util.JsonCommonUtil;
import com.subzero.userman.UserManActivity;
/**修改签名
 * 包含：
 * 	昵称、性别、年龄、手机号、签名、评价
 * 启动者：
 * 	个人中心：UserManActivity
 * */
public class TextActivity extends BaseActivity
{
	private EditText editText;
	@SuppressWarnings("unused")
	private String goodsID;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text);
		Intent intent = getIntent();
		goodsID = intent.getStringExtra("goodsID");
		moduleName = intent.getStringExtra(App.keyModuleName);
		initView();
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		findViewById(R.id.bt_submit).setOnClickListener(new MyOnClickListener());
		editText = (EditText) findViewById(R.id.et_sign);
		editText.addTextChangedListener(new MyTextWatcher());
		if(User.getSign(context)!=null){
			editText.setText(User.getSign(context));
		}
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			String sign = editText.getText().toString();
			if(R.id.bt_submit == v.getId()){
				if(TextUtils.isEmpty(sign)){
					ToastUtil.longAtCenter(context, "请输入内容");
					return ;
				}
				if(OrderListActivity.class.getSimpleName().equalsIgnoreCase(moduleName)){
					updateComment();
					return;
				}
				User.setSign(context, sign);
				Intent data = new Intent(context, UserManActivity.class);
				data.putExtra("sign", sign);
				setResult(RESULT_OK, data);
				finish();
			}
		}
	}
	private void updateComment()
	{
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("uid", User.getUserId(context));
		params.addQueryStringParameter("sid", User.getSID(context));
		params.addQueryStringParameter("page", pageindex+"");
		params.addQueryStringParameter("count", Url.rows+"");
		LogUtils.e("uid = "+User.getUserId(context)+" sid = "+User.getSID(context));
		//http://muyin.cooltou.cn/zapi/?url=/order/list&format=json&uid=41&sid=a9ac8ecfed182c25ebae40cc342a1990d7aa75fb&page=1&count=10
		httpHandler = httpUtils.send(HttpMethod.GET, Url.orderListApi, params , new CommonCallBack());
	}
	/**订单列表的 回调S
	 * */
	private final class CommonCallBack extends RequestCallBack<String>
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
		}
		@Override
		public void onSuccess(ResponseInfo<String> info)
		{
			dialogDismiss(dialogLoading);
			String result = info.result;
			//KLog.e("result = "+result);
			//Cache.saveTmpFile(result);
			/*Session 过期的异常*/
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				((XListView)findViewById(R.id.xlv)).stopXListView();
				startLoginActvitiy(activity, false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ToastUtil.shortAtCenterInThread(context, JsonCommonUtil.getCommonErrorInfo(result));
				((XListView)findViewById(R.id.xlv)).stopXListView();
				return;
			}
		}
	}
	private final class MyTextWatcher implements TextWatcher
	{
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after){		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)		{		}
		@Override
		public void afterTextChanged(Editable s)		{

		}
	}
}
