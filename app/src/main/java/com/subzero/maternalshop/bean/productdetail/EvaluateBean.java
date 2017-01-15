package com.subzero.maternalshop.bean.productdetail;

import java.util.List;

public class EvaluateBean
{
	public String id;
	public String title;
	public String content;
	public String create;
	/**积分*/
	public String give_integral;
	/**商品名称*/
	public String goods_name;
	/**商品 在订单列表中 展示的 被购买的 数量*/
	public String goods_number;
	/**商品 被评论的数量*/
	public String goods_comment;
	/**商品 在 购物车的 ID，删除商品需要这个 id*/
	public String rec_id;
	/**商品主图*/
	public String thumb;
	/**图片链接地址*/
	public String url;
	public List<String> listImgUrl;
	/**快递费用*/
	public String shipping_fee;
	public String shipping_city;
	public String shipping_address;
	public String shipping_fuwu;
	/**销量*/
	public String salesnum;
	/**市场售价*/
	public String market_price;
	/**本店售价  <br/>有促销价不显示本店价*/
	public String shop_price;
	/**促销价格 <br/>有促销价不显示本店价*/
	public String promote_price;
	/**总数量*/
	public String total;
	/**产品 在购物车的 数量*单价*/
	public String subtotal;
	/**被选购的数量*/
	public String countIncart;
	/**OrderListBeanType
	 * 一种商品 多种商品*/
	public int listBeanType;
	public String shopName;
	public String promotion;
}
