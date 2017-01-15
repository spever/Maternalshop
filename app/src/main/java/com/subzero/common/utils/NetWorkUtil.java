package com.subzero.common.utils;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
public class NetWorkUtil
{
	/**获取GPS状态*/
	public static boolean getGPSState(Context context)
	{
		LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return manager.isProviderEnabled(LocationManager.GPS_PROVIDER );
	}
	/**检测，当前网络是否可用*/
	public static boolean checkNetWorkAvaliable(Context context)
	{
		ConnectivityManager  manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if(networkInfo!=null){
			return networkInfo.isAvailable();/**当前网络，是否可用*/
		}
		return false;
	}
	/**检测，网络连接状态，是否可以传递数据*/
	public static boolean checkNetWorkConnected(Context context)
	{
		ConnectivityManager  manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if(networkInfo!=null){
			return networkInfo.isConnected();
		}
		return false;
	}
	/**检测 WiFi 是不是可以传递数据*/
	public static boolean checkWifiAvailable(Context context)
	{
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(networkInfo!=null  &&  networkInfo.isConnected()){
			return true;
		}
		return false;
	}
	/**检测 手机流量 是不是可以传递数据*/
	public static boolean checkMobileAvailable(Context context)
	{
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if(networkInfo!=null  &&  networkInfo.isConnected()){
			return true;
		}
		return false;
	}
	/**得到，当前网络的类型*/
	public static int getNetworkType(Context context)
	{
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if(networkInfo!=null){
			return networkInfo.getType();
		}
		return -1;
	}
}
