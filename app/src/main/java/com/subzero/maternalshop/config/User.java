package com.subzero.maternalshop.config;
import java.util.List;

import android.content.Context;

import com.subzero.common.utils.ClassUtil;
import com.subzero.common.utils.SPUtil;
public class User
{
	public static final String loginType = "loginType";
	/**当前 用户在线*/
	public static final String isOnline = "isOnline";
	/**用户名*/
	public static final String userName = "userName";
	/**手机号*/
	public static final String phoneNum = "phoneNum";
	/**userID*/
	public static final String userID = "userID";
	/**昵称*/
	public static final String nickName = "nickName";
	/**密码*/
	public static final String pwd = "pwd";
	/**userToken*/
	public static final String userToken = "userToken";
	/**用户 签名*/
	public static final String sign = "sign";
	/**年龄*/
	public static final String age = "age";
	/**性别*/
	public static final String gender = "gender";
	/**sessionID*/
	public static final String sessionID = "sessionID";
	public static int pwdLengthMin = 6;
	public static int pwdLengthMax = 12;
	/**userID*/
	public static final String cityID = "cityID";
	public static final String cityName = "cityName";
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	public static final String platformName = "platformName";
	public static final String oAuthid = "oAuthid";
	public static final String portraitUrl = "portraitUrl";
	//
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	/**清空所有 共享存储数据*/
	public static void logout(Context context){
		List<String> list = ClassUtil.getFieldsList(User.class);
		for (int i = 0; (list!=null)&&(i < list.size()); i++){
			SPUtil.remove(context, list.get(i));
		}
	}
	/**当前用户在线*/
	public static String getPortraitUrl(Context context){
		return SPUtil.getString(context, User.portraitUrl);
	}
	/**当前用户在线*/
	public static boolean setPortraitUrl(Context context, String portraitUrl){
		return SPUtil.putString(context, User.portraitUrl,portraitUrl);
	}
	/**当前用户在线*/
	public static String getOAuthid(Context context){
		return SPUtil.getString(context, User.oAuthid);
	}
	/**当前用户在线*/
	public static boolean setOAuthid(Context context, String oAuthid){
		return SPUtil.putString(context, User.oAuthid,oAuthid);
	}
	/**当前用户在线*/
	public static String getPlatformName(Context context){
		return SPUtil.getString(context, User.platformName);
	}
	/**当前用户在线*/
	public static boolean setPlatformName(Context context, String platformName){
		return SPUtil.putString(context, User.platformName,platformName);
	}
	/**当前用户在线*/
	public static String getLoginType(Context context){
		return SPUtil.getString(context, User.loginType);
	}
	/**当前用户在线*/
	public static boolean setLoginType(Context context, String loginType){
		return SPUtil.putString(context, User.loginType,loginType);
	}
	/**当前用户在线*/
	public static boolean getIsOnline(Context context){
		return SPUtil.getBoolean(context, User.isOnline,false);
	}
	/**当前用户在线*/
	public static boolean setIsOnline(Context context, boolean isOnline){
		return SPUtil.putBoolean(context, User.isOnline,isOnline);
	}
	/**用户id*/
	public static String getUserId(Context context){
		return SPUtil.getString(context, User.userID,null);
	}
	/**用户id*/
	public static void setUserId(Context context, String userId){
		SPUtil.putString(context, User.userID, userId);
	}
	/**用户id*/
	public static String getCityId(Context context){
		return SPUtil.getString(context, User.cityID,"2");
	}
	/**用户id*/
	public static void setCityId(Context context, String cityId){
		SPUtil.putString(context, User.cityID, cityId);
	}
	/**用户id*/
	public static String getCityName(Context context){
		return SPUtil.getString(context, User.cityName,"北京");
	}
	/**用户id*/
	public static void setCityName(Context context, String cityName){
		SPUtil.putString(context, User.cityName, cityName);
	}
	/**SID*/
	public static String getSID(Context context){
		return SPUtil.getString(context, User.sessionID,null);
	}
	/**SID*/
	public static void setSID(Context context, String sessionID){
		SPUtil.putString(context, User.sessionID, sessionID);
	}
	/**用户 密码*/
	public static String getPwd(Context context){
		return SPUtil.getString(context, User.pwd,null);
	}
	/**用户 密码*/
	public static void setPwd(Context context, String pwd){
		SPUtil.putString(context, User.pwd, pwd);
	}
	/**手机号*/
	public static String getPhoneNum(Context context){
		return SPUtil.getString(context, User.phoneNum,null);
	}
	/**手机号*/
	public static void setPhoneNum(Context context, String phoneNum){
		SPUtil.putString(context, User.phoneNum, phoneNum);
	}
	/**用户名*/
	public static String getUserName(Context context){
		return SPUtil.getString(context, User.userName,null);
	}
	/**用户名*/
	public static void setUserName(Context context, String userName){
		SPUtil.putString(context, User.userName, userName);
	}
	/**用户 token */
	public static String getUserToken(Context context){
		return SPUtil.getString(context, User.userToken,null);
	}
	/**用户 token */
	public static void setUserToken(Context context, String userToken){
		SPUtil.putString(context, User.userToken, userToken);
	}
	/**用户昵称*/
	public static String getNickName(Context context){
		return SPUtil.getString(context, User.nickName,null);
	}
	/**用户昵称*/
	public static void setNickName(Context context, String nickName){
		SPUtil.putString(context, User.nickName, nickName);
	}
	/**用户签名*/
	public static String getSign(Context context){
		return SPUtil.getString(context, User.sign,null);
	}
	/**用户签名*/
	public static void setSign(Context context, String sign){
		SPUtil.putString(context, User.sign, sign);
	}
	/**年龄*/
	public static String getAge(Context context){
		return SPUtil.getString(context, User.age,null);
	}
	/**年龄*/
	public static void setAge(Context context, String age){
		SPUtil.putString(context, User.age, age);
	}
	/**性别*/
	public static String getGenderLabel(String genderID){
		if("1".equals(genderID)){
			return "男";
		}else{
			return "女";
		}
	}
	/**性别*/
	public static String getGender(Context context){
		return SPUtil.getString(context, User.gender,null);
	}
	/**性别*/
	public static void setGender(Context context, String gender){
		SPUtil.putString(context, User.gender, gender);
	}

}
