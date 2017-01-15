package com.subzero.maternalshop.bean.order;
import java.util.List;
import com.subzero.maternalshop.bean.ProductBean;
public class OrderListBean
{
	/**订单 id*/
	public String order_id;
	/**订单 编号*/
	public String order_sn;
	/**订单 生成 时间*/
	public String order_time;
	/**未付款/付款中/已付款*/
	public String pay_status;
	/**支付方式的 id*/
	public String pay_id;
	/**发货状态*/
	public String shipping_status;
	/**订单总金额*/
	public String total_fee;
	/**订单状态（未确认时可以取消订单）*/
	public String order_status;
	/**商品列表*/
	public List<ProductBean> listProduct;
	/**订单中，商品的总数量*/
	public String totalCommodity;
	/**OrderListBeanType
	 * 一种商品 多种商品*/
	public int orderListBeanType;
	public String is_back;
}
