package com.subzero.common.utils;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.lidroid.xutils.util.LogUtils;
//跟App相关的辅助类
//跟App相关的辅助类
public class AppUtil
{
	/**获取应用程序名称*/
	public static String getAppName(Context context)
	{
		try
		{
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * [获取应用程序版本名称信息]
	 * @param context
	 * @return 当前应用的版本名称
	 */
	public static String getVersionName(Context context)
	{
		try{
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e){
			LogUtils.e("有异常："+e);
		}
		return null;
	}
	/**
	 * 判断某个应用当前是否正在运行
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isAppRunning(Context context, String packageName) 
	{
		if (packageName == null)
			return false;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 判断服务是否运行
	 * @param mContext
	 * @param serviceName
	 * @return true为运行，false为不在运行
	 */
	public boolean isServiceRunning(Context mContext, String serviceName)
	{
		ActivityManager myManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(30);
		for (int i = 0; i < runningService.size(); i++)
		{
			String serName = runningService.get(i).service.getClassName().toString();
			if (serName.equals(serviceName)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 判断 用户是否安装微信客户端
	 */
	public static boolean isWeixinAvilible(Context context) {
		final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equals("com.tencent.mm")) {
					return true;
				}
			}
		}

		return false;
	}
	/**
	 * 判断 用户是否安装QQ客户端
	 */
	public static boolean isQQClientAvailable(Context context) {
		final PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				LogUtils.e("pn = "+pn);
				if (pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
					return true;
				}
			}
		}
		return false;
	}
}
