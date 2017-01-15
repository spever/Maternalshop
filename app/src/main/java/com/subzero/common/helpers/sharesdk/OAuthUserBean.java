package com.subzero.common.helpers.sharesdk;

public class OAuthUserBean
{
	/**微信的userID   QQ的userID   新浪的userID*/
	public String oAuthid;
	/**昵称*/
	public String nickName;
	/**市级单位*/
	public String city;
	/**省级单位*/
	public String province;
	/**性别*/
	public String gender;
	/**肖像*/
	public String portraitUrl;
	/**平台名称   Wechat.NAME QQ.NAME SinaWeibo.NAME*/
	public String platformName;
	@Override
	public String toString(){
		return "用户信息 [平台名称="+platformName+", userId="+oAuthid+", 昵称=" + nickName + ", 市=" + city + ", 省=" + province + ", 性别=" + gender + ", 头像url=" + portraitUrl + "]";
	}
}
