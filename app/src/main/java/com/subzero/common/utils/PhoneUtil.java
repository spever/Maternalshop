package com.subzero.common.utils;
import android.content.Context;
import android.telephony.TelephonyManager;
public class PhoneUtil
{
	/**获取手机IMEI参数*/
	public static String getIMEI(Context context)
	{
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);    
		return manager.getDeviceId();
	}
	/**获取手机生产商*/
	public static String getManufacturer(){
		return android.os.Build.MANUFACTURER;
	}
	/**获取手机号码，可能为 null*/
	public static String getPhoneNum(Context context)
	{
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);    
		return manager.getLine1Number();
	}
	/**获取手机生产商*/
	public static String getDevice(){
		return android.os.Build.DEVICE;
	}
}
