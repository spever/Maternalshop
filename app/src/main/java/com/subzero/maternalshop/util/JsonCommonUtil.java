package com.subzero.maternalshop.util;
import net.minidev.json.JSONObject;

import com.subzero.common.utils.JsonSmartUtil;
public class JsonCommonUtil
{
	/**
	 * 如果 code = 400 
	 * 同时 error_code = 100
	 * @return 异常信息
	 * */
	public static String getSessionErrorInfo(String jsonStr)
	{
		JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonStr, "status");
		String code = JsonSmartUtil.getString(jsonObject, "code");
		String error_code = JsonSmartUtil.getString(jsonObject, "error_code");
		String error_desc = JsonSmartUtil.getString(jsonObject, "error_desc");
		if("400".equalsIgnoreCase(code) && "100".equalsIgnoreCase(error_code)){
			return error_desc;
		}
		return null;
	}
	/**
	 * 如果 code = 400 
	 * @return 异常信息
	 * */
	public static String getCommonErrorInfo(String jsonStr)
	{
		JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonStr, "status");
		String error_desc = JsonSmartUtil.getString(jsonObject, "error_desc");
		return error_desc;
	}
	public static String getCode(String jsonStr){
		JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonStr, "status");
		return JsonSmartUtil.getString(jsonObject, "code");
	}
}
