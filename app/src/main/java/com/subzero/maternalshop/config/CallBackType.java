package com.subzero.maternalshop.config;
public enum CallBackType{
	/**添加 地址信息*/
	addAddr,
	/**删除 地址信息*/
	deleteAddr,
	/**更新 地址信息*/
	updateAddr,
	/**设置为 默认*/
	setDefault,
	/**取消订单*/
	cancelOrder,
	/**确认收货*/
	affirmReceived,
	/**修改用户资料*/
	alterUserInfo,
	/**退出登录*/
	logout,
	productText,
	productHtml,
	/**添加购物车*/
	cartAdd,
	/**一键购物*/
	cartOneKeyBuy,
	/**加载json数据*/
	loadJsonData,
	/**申请退款*/
	orderRefund
}
