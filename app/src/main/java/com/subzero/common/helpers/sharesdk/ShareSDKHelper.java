package com.subzero.common.helpers.sharesdk;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.lidroid.xutils.util.LogUtils;
import com.subzero.common.utils.ToastUtil;
public class ShareSDKHelper
{
	private Context context;
	public String titleUrl;
	public String text;
	public String site;
	public String shareUrl;
	public String shareImgUrl;
	public String title;
	/**授权取消*/
	private static final int MSG_AUTH_CANCEL = 2;
	/**授权失败*/
	private static final int MSG_AUTH_ERROR= 3;
	/**授权完成*/
	private static final int MSG_AUTH_COMPLETE = 4;
	private Handler handler;
	private OnOneKeyShareListener onOneKeyShareListener;
	private OnOAuthLoginListener onOAuthLoginListener;
	public ShareSDKHelper(Context context, OnOneKeyShareListener onShareFinishListener) {
		this.context = context;
		this.onOneKeyShareListener = onShareFinishListener;
	}
	public ShareSDKHelper(Context context, OnOAuthLoginListener onOAuthLoginListener){
		this.context = context;
		this.onOAuthLoginListener = onOAuthLoginListener;	
		ShareSDK.initSDK(context);
		handler = new Handler(new OAuthLoginCallback());
	}
	/**@category 开始第三方登录
	 * @param platformName Wechat.NAME  QQ.NAME  SinaWeibo.NAME*/
	public void startOAuthLogin(String platformName) 
	{
		if(onOAuthLoginListener != null){
			onOAuthLoginListener.onStart();
		}
		Platform platform = ShareSDK.getPlatform(platformName);
		platform.setPlatformActionListener(new MyPlatformActionListener());
		//关闭SSO授权
		platform.SSOSetting(true);
		platform.showUser(null);
	}
	/**@category 第三方登录完成  回调
	 * */
	private final class OAuthLoginCallback implements Handler.Callback
	{
		@SuppressWarnings("unchecked")
		@Override
		public boolean handleMessage(Message msg)
		{
			if(msg.what == MSG_AUTH_CANCEL){
				/*取消授权*/
				if(onOAuthLoginListener != null){
					ToastUtil.longAtCenterInThread(context, "取消授权");
					onOAuthLoginListener.onFinish(OnListenerType.cancle,null, null);
				}
			} else if(msg.what == MSG_AUTH_ERROR){
				/*取消出错*/
				if(onOAuthLoginListener != null){
					onOAuthLoginListener.onFinish(OnListenerType.fail,null, null);
				}
			} else if(msg.what == MSG_AUTH_COMPLETE){
				LogUtils.e("授权成功");
				Object[] objs = (Object[]) msg.obj;
				Platform platform = (Platform) objs[0];
				HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
				Set<Entry<String,Object>> entrySet = res.entrySet();
				OAuthUserBean bean = new OAuthUserBean();
				if(QQ.NAME.equals(platform.getName())){
					bean = platformIsQQ(entrySet, bean);
				}else if(SinaWeibo.NAME.equals(platform.getName())){
					bean = platformIsSinaWeibo(entrySet, bean);
				}else if(Wechat.NAME.equals(platform.getName())){
					bean = platformIsWeChat(entrySet, bean);
				}
				LogUtils.e("用户信息："+bean);
				bean.oAuthid = platform.getDb().getUserId();
				bean.platformName = platform.getName();
				//ToastUtil.longAtCenterInThread(context, "欢迎你！"+bean.nickName+" id = "+bean.gender);
				if(onOAuthLoginListener != null){
					onOAuthLoginListener.onFinish(OnListenerType.success, null, bean);
				}
				ShareSDK.stopSDK(context);
			}
			return false;
		}
	}
	/**@category 调起 一键分享*/
	public  void startOneKeyShare()
	{
		ShareSDK.initSDK(context);
		OnekeyShare oks = new OnekeyShare();
		//关闭sso授权
		oks.disableSSOWhenAuthorize(); 
		oks.setTitle(title);
		oks.setTitleUrl(titleUrl);
		oks.setText(text);
		oks.setUrl(shareUrl);
		oks.setComment("我是测试评论文本");
		oks.setSite(site);
		oks.setImageUrl(shareImgUrl);
		oks.setSiteUrl(shareUrl);
		oks.setCallback(new MyPlatformActionListener());
		oks.show(context);
		// 启动分享GUI
	}
	private final class MyPlatformActionListener implements PlatformActionListener
	{
		@Override
		public void onCancel(Platform platform, int action)
		{
			if((action == Platform.ACTION_SHARE) && (onOneKeyShareListener!=null)){
				LogUtils.e("取消分享");
				onOneKeyShareListener.onFinish(OnListenerType.cancle);
			}else if ((action == Platform.ACTION_USER_INFOR) && (onOAuthLoginListener!=null)) {
				onOAuthLoginListener.onFinish(OnListenerType.cancle, null, null);
			}
		}
		@Override
		public void onComplete(Platform platform, int action, HashMap<String,Object> res)
		{
			if((action == Platform.ACTION_SHARE) && (onOneKeyShareListener!=null)){
				LogUtils.e("分享完成");
				onOneKeyShareListener.onFinish(OnListenerType.success);
			}else if (action == Platform.ACTION_USER_INFOR) {
				Message msg = new Message();
				msg.what = MSG_AUTH_COMPLETE;
				msg.obj = new Object[] {platform, res};
				handler.sendMessage(msg);
			}
		}
		@Override
		public void onError(Platform platform, int action, Throwable t){
			if((action == Platform.ACTION_SHARE) && (onOneKeyShareListener!=null)){
				LogUtils.e("分享出错: action = "+action+" t = "+t);
				onOneKeyShareListener.onFinish(OnListenerType.fail);
			}else if ((action == Platform.ACTION_USER_INFOR) && (onOAuthLoginListener!=null)) {
				ToastUtil.longAtCenterInThread(context, "登录 出错  action = "+action+" t = "+t.getLocalizedMessage());
				onOAuthLoginListener.onFinish(OnListenerType.fail, "", null);
			}
		}
	}
	private OAuthUserBean platformIsWeChat(Set<Entry<String, Object>> entrySet, OAuthUserBean bean)
	{
		for(Iterator<Entry<String, Object>> ir = entrySet.iterator(); ir.hasNext();)
		{
			Entry<String,Object> mapEntry = ir.next();
			String key = mapEntry.getKey();
			Object value = mapEntry.getValue();
			if("nickname".equalsIgnoreCase(key)){
				bean.nickName = (String)value;
			}else if("sex".equalsIgnoreCase(key)){
				bean.gender = value+"";
				if(bean.gender.equalsIgnoreCase("1")){
					bean.gender = "男";
				}else{
					bean.gender = "女";
				}
			}else if("province".equalsIgnoreCase(key)){
				bean.province = (String)value;
			}else if("city".equalsIgnoreCase(key)){
				bean.city = (String)value;
			}else if("headimgurl".equalsIgnoreCase(key)){
				bean.portraitUrl = (String)value;
			}
		}
		return bean;
	}
	private OAuthUserBean platformIsSinaWeibo(Set<Entry<String, Object>> entrySet, OAuthUserBean bean)
	{
		for(Iterator<Entry<String, Object>> ir = entrySet.iterator(); ir.hasNext();)
		{
			Entry<String,Object> mapEntry = ir.next();
			String key = mapEntry.getKey();
			Object value = mapEntry.getValue();
			if("name".equalsIgnoreCase(key)){
				bean.nickName = (String)value;
			}
			else if("gender".equalsIgnoreCase(key))
			{
				String gender = (String)value;
				if("m".equals(gender)){
					bean.gender = "男";
				}else{
					bean.gender = "女";
				}
			}
			else if("location".equalsIgnoreCase(key)){
				String location = (String)value;
				bean.province = location.split(" ")[0];
				bean.city = location.split(" ")[1];
			}
			else if("avatar_large".equalsIgnoreCase(key)){
				bean.portraitUrl = (String)value;
			}
		}
		return bean;
	}
	private OAuthUserBean platformIsQQ(Set<Entry<String, Object>> entrySet, OAuthUserBean bean)
	{
		for(Iterator<Entry<String, Object>> ir = entrySet.iterator(); ir.hasNext();)
		{
			Entry<String,Object> mapEntry = ir.next();
			String key = mapEntry.getKey();
			Object value = mapEntry.getValue();
			if("nickname".equalsIgnoreCase(key)){
				bean.nickName = (String)value;
			}
			else if("gender".equalsIgnoreCase(key)){
				bean.gender = (String)value;
			}
			else if("province".equalsIgnoreCase(key)){
				bean.province = (String)value;
			}
			else if("city".equalsIgnoreCase(key)){
				bean.city = (String)value;
			}
			else if("figureurl_qq_1".equalsIgnoreCase(key)){
				bean.portraitUrl = (String)value;
			}
		}
		return bean;
	}
	public interface OnOneKeyShareListener{
		public void onFinish(OnListenerType onListenerType);
	}
	public interface OnOAuthLoginListener{
		public void onStart();
		public void onFinish(OnListenerType onListenerType, String error, OAuthUserBean oAuthUserBean);
	}
	public enum OnListenerType{
		/**取消*/
		cancle,
		/**成功*/
		success,
		/**失败*/
		fail
	}
}
