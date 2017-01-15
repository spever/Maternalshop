package com.subzero.maternalshop.bean.multiplepay;
import java.io.Serializable;
/**所有 参数 必不可少*/
public class MultiplePayBean implements Serializable
{
	private static final long serialVersionUID = 4184299080223087397L;
	/**微信支付		无重复字符串*/
	public String weChatNoncestr;
	/**微信支付		商户id*/
	public String weChatPartnerid;
	/**微信支付		预支付订单id*/
	public String weChatPrepayid;
	/**微信支付		签名*/
	public String weChatSign;
	/**微信支付		时间戳*/
	public String weChatTimestamp;
	/**微信支付		微信支付AppID*/
	public String weChatAppID;
	/**支付宝    商户PID*/
	public String alipayPID;
	/**支付宝    商户收款账号 Seller*/
	public String alipaySeller;
	/**支付宝    商户私钥，pkcs8格式*/
	public String alipayRsaPrivate;
	/**支付宝    订单信息(调出支付宝客户端 只能看到这个标题)*/
	public String alipaySubject;
	/**支付宝    商品详情*/
	public String alipayBody;
	/**支付宝    商品金额 单位是元*/
	public String alipayPrice;
	/**支付宝 支付回调*/
	public String alipayUrl;
	/**银联支付		支付订单id*/
	public String unionTN;
	/**银联支付		银联后台环境标识，“00”将在银联正式环境发起交易, “01”将在 
	 * 银联测试环境发起交易 */
	public String unionMode;
}
