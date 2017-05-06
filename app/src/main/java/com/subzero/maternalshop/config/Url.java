package com.subzero.maternalshop.config;

import java.net.URLEncoder;

import com.lidroid.xutils.util.LogUtils;

public class Url
{
	//http://muyin.cooltou.cn
	public static final int rows = 10;
//	public static final String doMain = "http://muyin.cooltou.cn/";
	public static final String doMain = "http://muyin.fjguang.com/";
	public static final String doMainApi = "http://muyin.fjguang.com/zapi/?url=/";
	/**4  (/user/signup)注册*/
	public static final String registerApi = doMainApi+"user/signup";
	/**17  忘记密码*/
	public static final String forgetPwdApi = doMainApi+"user/forget_password&format=json";
	/**5  (/user/signin)登录*/
	public static final String loginApi = doMainApi+"user/signin&format=json";
	/**(/user/signin)登录   第三的方 登录*/
	public static final String OAuthloginApi = doMainApi+"user/auth_signin&format=json&action=login";
	/**9  (/user/info)会员资料*/
	public static final String logoutApi = doMainApi+"user/info&format=json&action=logout";
	/**9  (/user/info)会员资料*/
	public static final String userInfoApi = doMainApi+"user/info";
	/**10  (/user/upinfo)修改资料*/
	public static final String userInfoAlterApi = doMainApi+"user/upinfo";
	public static final String redPacketList = doMainApi+"user/bonus&format=json";
	/**1  ( /home/data ) 首页*/
	public static final String shopListApi = doMainApi+"home/data&format=json";
	/**20  (/supplier/data)店铺*/
	public static final String shopDetailApi = doMainApi+"supplier/data&format=json";
	/**8  (/supplier/data)分类商品*/
	public static final String sortInOneShopApi = doMainApi+"supplier/data&format=json&action=category";
	/**3   (/home/category)分类*/
	public static final String sortApi = doMainApi+"home/category";
	/**2  （/home/ads） 广告*/
	public static final String adApi = doMainApi+"home/ads";
	/**1  ( /home/data ) 首页*/
	public static final String cityApi = doMainApi+"home/data&format=json&action=area";
	/**6   (/search)产品列表
	 * 搜索列表：列表*/
	public static final String searchProductApi = doMainApi+"search&format=json";
	/**1  ( /home/data ) 首页
	 * 搜索店铺：列表*/
	public static final String searchShopApi = doMainApi+"search_shop&format=json";
	/**17  (/search)产品列表
	 * 分类的二级列表：列表*/
	public static final String sortLevel2Api = doMainApi+"search&format=json";
	/**19 (/goods)商品详情
	 * 商品详情*/
	public static final String productDetailApi = doMainApi+"goods&format=json";
	/**19 (/goods)商品详情
	 * 商品详情*/
	public static final String productDetailHtml5Api = doMainApi+"goods&format=json&action=desc";
	/**商品详情：评论列表*/
	public static final String productCommentListApi = doMainApi+"comments";
	/**购物车：加入购物车*/
	public static final String cartAddApi = doMainApi+"cart&format=json&action=create";
	/**购物车：加入购物车*/
	public static final String cartListApi = doMainApi+"cart&format=json&action=list";
	/**购物车：更新 修改 购物车*/
	public static final String cartUpdateApi = doMainApi+"cart&format=json&action=update";
	/**购物车：移除 购物车*/
	public static final String cartRemoveApi = doMainApi+"cart&format=json&action=delete";
	/**购物车：立即购买*/
	public static final String cartBuyApi = doMainApi+"cart&format=json&action=one_create";
	/**12    (/flow)订单
	 * 购物车：确认购买 */
	//订单确认：（?url=/flow&format=json&action=checkOrder&rec_id=&uid=&sid=）（多个：rec_id=62|63|64） 
	public static final String cartSubmitApi = doMainApi+"flow&format=json&action=checkOrder";
	/**14  (/order)用户订单
	 * 订单列表：MineFragment*/
	public static final String orderListApi = doMainApi+"order/list&format=json";
	/**12   (/flow)订单
	 * 订单：确认提交：OrderPayActivity*/
	public static final String orderSubmitApi = doMainApi+"flow&format=json&action=done";
	/**29   微信预支付单
	 * */
	public static final String weChatPayOrderApi = "http://muyin.fjguang.com/zapi/?payment=wxpay/beforepay&format=json";
	/**30   银联支付支付单
	 * */
	public static final String UnionPayApi = "http://muyin.fjguang.com/zapi/index.php?payment=unionpay/apppay&format=json";
	/**14  (/order)用户订单
	 * 取消订单：OrderPayActivity*/
	public static final String orderCancelApi = doMainApi+"order/cancel&format=json";
	/**18    支付完成
	 * 订单：支付完成：OrderPayActivity*/
	public static final String payFinishApi = doMainApi+"order/pay_notify&format=json";
	/**14  (/order)用户订单
	 * 去收货：OrderPayActivity*/
	public static final String orderAffirmReceived = doMainApi+"order/affirmReceived&format=json";
	/**区域：列表*/
	public static final String areaListApi = doMainApi+"/region&format=json";
	/**收货地管理：列表*/
	public static final String addrListApi = doMainApi+"address/list&format=json";
	/**收货地管理：新增*/
	public static final String addrAddApi = doMainApi+"address/add&format=json";
	/**收货地管理：删除*/
	public static final String addrDeleteApi = doMainApi+"address/delete&format=json";
	/**收货地管理：修改*/
	public static final String addrUpdateApi = doMainApi+"address/update&format=json";
	/**收货地管理：设置默认*/
	public static final String addrSetDefaultApi = doMainApi+"address/setDefault&format=json";
	/**帮助中心：一级列表*/
	public static final String helperListApi = doMainApi+"help&format=json";
	/**帮助中心：一级列表*/
	public static final String aboutUsApi = doMainApi+"help&action=about&format=json";
	/**4   (/comments)商品评论
	 * 添加评论
	 * */
	public static final String addCommentApi = doMainApi+"comments&format=json&action=add";
	public static final String orderDetail = doMainApi+"order/info&format=json";
	public static final String orderRefundGetOrderInfo = doMainApi+"order/back_order&format=json&acton=back";
	public static final String orderRefund = doMainApi+"order/back_order&format=json&action=back_act";
	/**编码*/
	public static String encode(String strUnCode) 
	{
		try
		{
			return URLEncoder.encode(strUnCode, "UTF-8");
		} catch (Exception e){
			LogUtils.e("有异常："+e);
		}
		return strUnCode;	
	}
}
