package com.subzero.maternalshop.bean;
public class AddrBean
{
	public String address_id;
	/**收货人姓名*/
	public String consignee;
	/**手机号*/
	public String mobile;
	/**小区详细地址*/
	public String address;
	/**1默认收货地址*/
	public String default_address;
	/**省份名称*/
	public String province_name;
	/**城市名称*/
	public String city_name;
	/**区县名称*/
	public String district_name;
	public void setDefaultAddress(String default_address){
		this.default_address = default_address;
	}
}
