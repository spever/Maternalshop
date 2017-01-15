package com.subzero.maternalshop.activity.order;
import java.util.ArrayList;
import java.util.List;

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
import com.socks.library.KLog;
import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.util.JsonCommonUtil;
/**修改签名
 * 包含：
 * 	昵称、性别、年龄、手机号、签名、评价
 * 启动者：
 * 	个人中心：UserManActivity
 * */
public class CommentActivity extends BaseActivity
{
	private EditText editText;
	private String goodsID;
	private List<Integer> listIVID;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		Intent intent = getIntent();
		goodsID = intent.getStringExtra("goodsID");
		moduleName = intent.getStringExtra(App.keyModuleName);
		listIVID = (listIVID==null)? new ArrayList<Integer> ():listIVID;
		initView();
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		findViewById(R.id.bt_submit).setOnClickListener(new MyOnClickListener());
		listIVID.add(R.id.iv_star_1);
		listIVID.add(R.id.iv_star_2);
		listIVID.add(R.id.iv_star_3);
		listIVID.add(R.id.iv_star_4);
		listIVID.add(R.id.iv_star_5);
		toggleImageViewList(1);
		findViewById(R.id.iv_star_1).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.iv_star_2).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.iv_star_3).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.iv_star_4).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.iv_star_5).setOnClickListener(new MyOnClickListener());
		editText = (EditText) findViewById(R.id.et_sign);
		editText.addTextChangedListener(new MyTextWatcher());
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
				RequestParams params = new RequestParams(XUtil.charset);
				params.addQueryStringParameter("uid", User.getUserId(context));
				params.addQueryStringParameter("sid", User.getSID(context));
				params.addQueryStringParameter("goods_id", goodsID);
				params.addBodyParameter("content", editText.getText().toString());
				params.addBodyParameter("rank", "2");
				LogUtils.e("goods_id = "+goodsID+" content = "+editText.getText().toString());
				httpHandler = httpUtils.send(HttpMethod.POST, Url.addCommentApi, params , new CommonCallBack());
			}else if(R.id.iv_star_1 == v.getId()){
				toggleImageViewList(1);
			}else if(R.id.iv_star_2 == v.getId()){
				toggleImageViewList(2);
			}else if(R.id.iv_star_3 == v.getId()){
				toggleImageViewList(3);
			}else if(R.id.iv_star_4 == v.getId()){
				toggleImageViewList(4);
			}else if(R.id.iv_star_5 == v.getId()){
				toggleImageViewList(5);
			}
		}
	}
	private void toggleImageViewList(int k){
		for (int i = 0; (listIVID!=null) && (i<listIVID.size()); i++){
			findViewById(listIVID.get(i)).setSelected(false);
			findViewById(listIVID.get(i)).setTag(false);
		}
		for (int i = 0; (listIVID!=null) && (i<k); i++){
			findViewById(listIVID.get(i)).setTag(true);
			findViewById(listIVID.get(i)).setSelected(true);
		}
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
			KLog.e("result = "+result);
			//Cache.saveTmpFile(result);
			/*Session 过期的异常*/
			String sessionErrorInfo = JsonCommonUtil.getSessionErrorInfo(result);
			/*普通 异常信息*/
			if(sessionErrorInfo!=null){
				ToastUtil.shortAtCenterInThread(context, sessionErrorInfo);
				startLoginActvitiy(activity, false);
				return ;
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ToastUtil.shortAtCenterInThread(context, JsonCommonUtil.getCommonErrorInfo(result));
				return;
			}
			App.orderListMustRefresh = true;
			Intent data = new Intent(context, OrderListActivity.class);
			data.putExtra("mustRefreshOederList", true);
			setResult(RESULT_OK, data);
			finish();
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
