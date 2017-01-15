package com.subzero.common.utils.multiplepay;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import com.socks.library.KLog;
import com.tencent.mm.sdk.modelpay.PayReq;
@SuppressLint("DefaultLocale")
public class WeChatSignUtil
{
	/**@param key  SDK.weChatPayKey
	 * */
	public static String getSign(PayReq payReq, String key){
		if(payReq==null){
			return null;
		}
		String stringA = "appid="+payReq.appId
				+"&noncestr="+payReq.nonceStr
				+"&package="+payReq.packageValue
				+"&partnerid="+payReq.partnerId
				+"&prepayid"+payReq.prepayId
				+"&timestamp="+payReq.timeStamp
				+"&key="+key;
		/*LogUtils.e("nonceStr = "+payReq.nonceStr);
		LogUtils.e("packageValue = "+payReq.packageValue);
		LogUtils.e("partnerId = "+payReq.partnerId);
		LogUtils.e("prepayId = "+payReq.prepayId);
		LogUtils.e("timeStamp = "+payReq.timeStamp);
		LogUtils.e("key = "+key);*/
		String singMd5 = getMessageDigest(stringA.getBytes()).toUpperCase();
		KLog.e("singMd5 = "+singMd5);
		return singMd5;
	}
	public static String getNonceStr(){
		final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String random = getRandom(LETTERS.toCharArray(), 32);
		String digest = getMessageDigest(random.getBytes());
		return digest;
	}
	/**获得一个String, 长度为length, 由sourceChar[]元素随机组成
	 * <br/>失败, 返回null*/
	public static String getRandom(char[] sourceChar, int length) 
	{
		if (sourceChar == null || sourceChar.length == 0 || length < 0) {
			return null;
		}
		StringBuilder str = new StringBuilder(length);
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			str.append(sourceChar[random.nextInt(sourceChar.length)]);
		}
		return str.toString();
	}
	private final static String getMessageDigest(byte[] buffer) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(buffer);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}
	public static String getPhoneIp() { 
		try { 
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) { 
				NetworkInterface intf = en.nextElement(); 
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) { 
					InetAddress inetAddress = enumIpAddr.nextElement(); 
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) { 
						return inetAddress.getHostAddress().toString(); 
					} 
				} 
			} 
		} catch (Exception e) { 
		} 
		return "127.0.0.1"; 
	}
	public static String toXml(List<NameValuePair> params, String key) {
		StringBuilder sb = new StringBuilder();  
		StringBuilder sb2 = new StringBuilder();  
		sb2.append("<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><xml>");  
		for (int i = 0; i < params.size(); i++) {  
			// sb是用来计算签名的  
			sb.append(params.get(i).getName());  
			sb.append('=');  
			sb.append(params.get(i).getValue());  
			sb.append('&');  
			// sb2是用来做请求的xml参数  
			sb2.append("<" + params.get(i).getName() + ">");  
			sb2.append(params.get(i).getValue());  
			sb2.append("</" + params.get(i).getName() + ">");  
		}  
		sb.append("key=");  
		sb.append(key);  
		String packageSign = null;  
		// 生成签名  
		packageSign = getMessageDigest(sb.toString().getBytes()).toUpperCase();
		sb2.append("<sign><![CDATA[");  
		sb2.append(packageSign);  
		sb2.append("]]></sign>");  
		sb2.append("</xml>");  

		// 这一步最关键 我们把字符转为 字节后,再使用“ISO8859-1”进行编码，得到“ISO8859-1”的字符串  
		try {  
			return new String(sb2.toString().getBytes(), "ISO8859-1");  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
		return "";  
	}
}
