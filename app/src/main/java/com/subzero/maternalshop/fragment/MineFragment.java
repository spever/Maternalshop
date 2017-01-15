package com.subzero.maternalshop.fragment;
import java.util.Random;

import net.minidev.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;
import com.subzero.common.listener.OnFragmentSelectedListener;
import com.subzero.common.utils.ActivityUtil;
import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.common.utils.XUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.IndexActivity;
import com.subzero.maternalshop.activity.order.OrderListActivity;
import com.subzero.maternalshop.activity.redpacket.RedPacketListActivity;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.Cache;
import com.subzero.maternalshop.config.Url;
import com.subzero.maternalshop.config.User;
import com.subzero.maternalshop.util.JsonCommonUtil;
import com.subzero.userman.UserManActivity;
import com.subzero.userman.alteruserinfo.AddrListActivity;
import com.subzero.userman.helper.HeplerListActivity;
/**我的模块
 * 启动者：
 * 		默认首页：IndexActivity
 * 启动项：
 * 		个人中心：UserManActivity
 * 		地址列表：AddrListActivity
 * 		帮助中心：HeplerListActivity
 * 		订单列表：OrderListActivity
 * 		红包列表：RedPacketListActivity
 * */
public class MineFragment extends BaseFragment
{
	public String userLogoUrl;
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		((IndexActivity)activity).addOnFragmentSelectedListener(new MyOnFragmentSelectedListener());
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_mine, null);
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
		rootView.findViewById(R.id.iv_user_logo).setOnClickListener(new MyOnClickListener());
		rootView.findViewById(R.id.tv_mine_helper).setOnClickListener(new MyOnClickListener());
		rootView.findViewById(R.id.tv_mine_order_list).setOnClickListener(new MyOnClickListener());
		rootView.findViewById(R.id.tv_mine_addr).setOnClickListener(new MyOnClickListener());
		rootView.findViewById(R.id.tv_mine_cart).setOnClickListener(new MyOnClickListener());
		rootView.findViewById(R.id.tv_red_packet).setOnClickListener(new MyOnClickListener());
	}
	@Override
	public void onResume()
	{
		super.onResume();
		if(App.mustrefreshUserInfo){
			App.mustrefreshUserInfo = false;
			loadJsonData();
		}
		String nickName = User.getNickName(getActivity());
		if(nickName!=null){
			((TextView)rootView.findViewById(R.id.tv_nick_name)).setText(nickName);
		}
	}
	/**相应 接口回调*/
	private final class MyOnFragmentSelectedListener implements OnFragmentSelectedListener
	{
		@Override
		public void onFragmentSelected(int indexSelected)
		{
			if((indexSelected == 3) && canLoading){
				canLoading = false;
				loadJsonData();
			}else{
				
			}
		}
	}
	/**获取个人信息详情*/
	private void loadJsonData()
	{
		RequestParams params = new RequestParams(XUtil.charset);
		params.addQueryStringParameter("format", "json");
		params.addQueryStringParameter("uid", User.getUserId(getActivity()));
		params.addQueryStringParameter("sid", User.getSID(getActivity()));
		httpHandler = httpUtils.send(HttpMethod.GET, Url.userInfoApi, params , new UserInfoCallBack());
	}
	/**@category 个人信息的 回调
	 * */
	private final class UserInfoCallBack extends RequestCallBack<String>
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
			dialogDismiss(dialogLoading);
			ToastUtil.longAtCenterInThread(getActivity(), XUtil.getErrorInfo(error));
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
			}else if(!"200".equalsIgnoreCase(JsonCommonUtil.getCode(result))){
				ToastUtil.shortAtCenterInThread(getActivity(), JsonCommonUtil.getCommonErrorInfo(result));
				return;
			}
			//LogUtils.e("result = "+result);
			JSONObject jsonObject = JsonSmartUtil.getJsonObject(result, "data");
			userLogoUrl = JsonSmartUtil.getString(jsonObject, "user_headimg");
			if(getActivity()!=null){
				Picasso.with(getActivity()).load(userLogoUrl+"?"+new Random().nextInt(1000)).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into((ImageView) rootView.findViewById(R.id.iv_user_logo));
			}
			/*昵称*/
			String nickName = JsonSmartUtil.getString(jsonObject, "user_nice_name");
			User.setNickName(getActivity(), nickName);
			ViewUtil.setText2TextView(rootView.findViewById(R.id.tv_nick_name), nickName);
			/*年龄*/
			String age = JsonSmartUtil.getString(jsonObject, "user_age");
			User.setAge(getActivity(), age);
			/*性别*/
			String genderID = JsonSmartUtil.getString(jsonObject, "user_sex");
			User.setGender(getActivity(), User.getGenderLabel(genderID));
			/*签名*/
			String user_autograph = JsonSmartUtil.getString(jsonObject, "user_autograph");
			User.setSign(getActivity(), user_autograph);
			/*积分*/
			ViewUtil.setText2TextView(rootView.findViewById(R.id.tv_points), JsonSmartUtil.getString(jsonObject, "user_pay_points")+"分");
			/*会员等级*/
			ViewUtil.setText2TextView(rootView.findViewById(R.id.tv_level), JsonSmartUtil.getString(jsonObject, "user_rank_name"));
		}
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			Intent intent  = null;
			if(R.id.iv_user_logo == v.getId()){
				intent = new Intent(getActivity(), UserManActivity.class);
				intent.putExtra("userLogoUrl", userLogoUrl);
				startActivity(intent);
				ActivityUtil.pushActivity(getActivity());
			}else if(R.id.tv_mine_helper == v.getId()){
				intent = new Intent(getActivity(), HeplerListActivity.class);
				startActivity(intent);
			}else if(R.id.tv_mine_order_list == v.getId()){
				intent = new Intent(getActivity(), OrderListActivity.class);
				startActivity(intent);
			}else if(R.id.tv_mine_addr == v.getId()){
				intent = new Intent(getActivity(), AddrListActivity.class);
				startActivity(intent);
			}else if(R.id.tv_mine_cart == v.getId()){
				((IndexActivity)getActivity()).switchFootNavi(2);
			}else if(R.id.tv_red_packet == v.getId()){
				intent = new Intent(getActivity(), RedPacketListActivity.class);
				startActivity(intent);
			}
		}
	}
}
