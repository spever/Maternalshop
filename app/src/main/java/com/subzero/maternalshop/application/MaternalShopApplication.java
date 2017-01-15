package com.subzero.maternalshop.application;

import subzero.ereza.CustomActivityOnCrash;
import subzero.ereza.ErrorActivity;
import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import cn.smssdk.SMSSDK;

import com.subzero.maternalshop.activity.MainActivity;
import com.subzero.maternalshop.config.SDK;

public class MaternalShopApplication extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		initCrash();
		initSMSSDK();
	}
	/**版本要求 Android 4.0    api 14*/
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void initCrash()
	{
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		{
			CustomActivityOnCrash.install(this);
			CustomActivityOnCrash.setEnableAppRestart(true);
			CustomActivityOnCrash.setLaunchErrorActivityWhenInBackground(true);
			CustomActivityOnCrash.setShowErrorDetails(true);
			CustomActivityOnCrash.setErrorActivityClass(ErrorActivity.class);
			CustomActivityOnCrash.setRestartActivityClass(MainActivity.class);
		}
	}
	/**初始化ShareSDK短信验证的SDK*/
	private void initSMSSDK(){
		SMSSDK.initSDK(this, SDK.SMSSDKAppKey, SDK.SMSSDKAppSecret);
	}
	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
		System.exit(0);
	}
}
