package com.subzero.maternalshop.util;

import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import com.subzero.common.utils.JsonSmartUtil;
import com.subzero.maternalshop.bean.AddrBean;

public class JsonAddrList
{
	public static List<AddrBean> getAddrList(JSONArray jsonArray){
		List<AddrBean> list = new ArrayList<AddrBean>();
		for (int i = 0; (!JsonSmartUtil.isJsonArrayEmpty(jsonArray)) && (i<jsonArray.size()); i++)
		{
			JSONObject jsonObject = JsonSmartUtil.getJsonObject(jsonArray, i);
			AddrBean bean = new AddrBean();
			bean.address_id = JsonSmartUtil.getString(jsonObject, "address_id");
			bean.address = JsonSmartUtil.getString(jsonObject, "address");
			bean.consignee = JsonSmartUtil.getString(jsonObject, "consignee");
			bean.default_address = JsonSmartUtil.getString(jsonObject, "default_address");
			bean.mobile = JsonSmartUtil.getString(jsonObject, "mobile");
			bean.province_name = JsonSmartUtil.getString(jsonObject, "province_name");
			bean.city_name = JsonSmartUtil.getString(jsonObject, "city_name");
			bean.district_name = JsonSmartUtil.getString(jsonObject, "district_name");
			list.add(bean);
		}
		return list;
	}
}
