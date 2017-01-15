package com.subzero.maternalshop.activity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.subzero.common.utils.SPUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.User;
import com.subzero.userman.LoginActivity;
/**
 * 程序入口
 * 启动项：
 * 	欢迎页面：WelcomeActivity
 * 	注册登录：LoginActivity
 * */
public class MainActivity extends Activity
{
	private final static String appIsFirst = "appIsFirstRunning";
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		boolean isFirst = SPUtil.getBoolean(context, MainActivity.appIsFirst, true);
		if(isFirst){/*App是第一次运行*/
			SPUtil.putBoolean(context, MainActivity.appIsFirst, false);
			startActivity(new Intent(context, WelcomeActivity.class));
			finish();
		}
		else{/*App不是第一次运行*/
			boolean online = User.getIsOnline(context);
			Intent intent = new Intent(context, LoginActivity.class);
			intent.putExtra(App.keyModuleName, MainActivity.class.getSimpleName());
			if(online){/*有默认账户*/
				intent.putExtra("phoneNum", User.getPhoneNum(context));
				intent.putExtra("pwd", User.getPwd(context));
			}
			startActivity(intent);
			finish();
		} 
	}
}
