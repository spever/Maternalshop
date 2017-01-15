package com.subzero.maternalshop.config;
import java.util.List;

import android.text.TextUtils;
import android.widget.ImageView.ScaleType;

import com.subzero.common.utils.ObjUtil;
import com.subzero.maternalshop.bean.ProductBean;
public class App
{
	public static final String keyModuleName = "keyModuleName";
	/**首次加载*/
	public static final String loadFirst = "loadFirst";
	/**上拉加载*/
	public static final String loadMore = "loadUpMore";
	/**下拉刷新*/
	public static final String loadRefresh = "loadDownRefresh";
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	public static final String loginTypeNormal = "loginTypeNormal";
	public static final String loginTypeOAuth = "loginTypeOAuth";
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	public static final String functionTypeSortLevel2 = "functionTypeSortLevel2";
	public static final String functionTypeSearch = "functionTypeSearch";
	public static final String functionTypeSortInOneShop = "functionTypeSortInOneShop";
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	public static final String orderPayTypeNOSubmit = "orderPayTypeNOSubmit";
	public static final String orderPayTypeNeedSubmit = "orderPayTypeNeedSubmit";
	public static boolean orderListMustRefresh = false;
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	/**综合 递减*/
	public static final String sortTypeMultiple = "sortTypeSalesmultipleDesc";
	/**综合 递增*/
	public static final String sortTypeMultipleAsc = "sortTypeSalesmultipleAsc";
	/**销量 递减*/
	public static final String sortTypeSalesVolume = "sortTypeSalesVolumeDesc";
	/**销量 递增*/
	public static final String sortTypeSalesVolumeAsc = "sortTypeSalesVolumeAsc";
	/**价格递减*/
	public static final String sortTypePriceDesc = "sortTypePriceDesc";
	/**价格递增*/
	public static final String sortTypePriceAsc = "sortTypePriceAsc";
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	/**修改 昵称*/
	public static final String alterNickName = "alterNickName";
	/**修改 年龄*/
	public static final String alterAge = "alterAge";
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	/**布局的主体*/
	public static final String clickBody = "clickBody";	
	public static final String clickOrder = "clickOrder";
	/**选中*/
	public static final String clickEditSelected = "clickEditSelected";	
	public static final String clickLeft= "clickLeft";	
	public static final String clickRight= "clickRight";	
	public static boolean mustrefreshUserInfo;

	/**获取省份列表*/
	public static final String areaProvince = "areaProvince";
	/**获取城市列表*/
	public static final String areaCity = "areaCity";
	/**获取区县列表*/
	public static final String areaCounty = "areaCounty";
	public static final String payStatusun = "未付款";
	public static final String payStatusing = "付款中";
	public static final String payStatusied = "已付款";
	public static final String shippingStatusun = "未发货";
	public static final String shippingStatusing = "配货中";
	public static final String shippingStatused = "已发货";
	public static final String shippingStatusFinished = "收货确认";
	public static final String orderStatusun = "未确认";
	public static final String orderStatused = "已确认";
	public static final String orderStatusCanceled= "已取消";
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	public static final String actionGotoPay= "去付款";
	public static final String actionGotoPayCancle= "去取消付款";
	public static final String actionGotoRefundOrder= "去申请退款";
	public static final String actionGotoComment= "去评价";
	public static final String actionGotoReceive= "去收货";
	public static final String actionNoCanceled= "已取消";
	public static final String actionNoPayed= "已付款";
	public static final String actionNoCommented= "已评价";
	public static final String actionNoRefunding= "退款中";
	public static final String actionNoRefunded= "已退款";
	/**购物车id*/
	public static final String rec_id = "rec_id";
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	/**CartFragment 页面 是从详情页回来的*/
	public static boolean isCartFragmentBackFromeDetail = false;
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	/**获取 月销量123笔*/
	public static String getSalesVolumeLable(String salesVolume){
		if(!TextUtils.isEmpty(salesVolume)){
			return "月销量"+salesVolume+"笔";
		}
		return "月销量"+0+"笔";
	}
	/**获取￥123*/
	public static String getPriceLable(String price){
		return "￥"+price;
	}
	/**评论: 123*/
	public static String getCommentLable(String count){
		return "评论: "+count;
	}
	/**评论: 123*/
	public static String getCommentInProductDetailLable(String count){
		return "商品评价（"+count+"）";
	}
	/**主营: 奶粉*/
	public static String getSalseTypeLable(String price){
		return "主营: "+price;
	}
	public static String getActionTypeLabel(String actionType){
		if(App.actionGotoRefundOrder.equalsIgnoreCase(actionType)){
			return "申请退款";
		}else if(App.actionGotoComment.equalsIgnoreCase(actionType)){
			return "去评价";
		}
		return "申请退款";
	}
	/**  123|12|833*/
	public static String recIDList2String(List<ProductBean> list){
		if(ObjUtil.isListEmpty(list)){
			return null;
		}
		String rec_id = "";
		for (int i = 0; i < list.size(); i++)
		{
			ProductBean bean = list.get(i);
			rec_id += bean.rec_id+"|";
		}
		String subString = rec_id.substring(0, rec_id.length()-1);
		return subString;
	}
	public static String getShopPromotePrice(ProductBean productBean){
		if((!TextUtils.isEmpty(productBean.promote_price)) && (!"0".equalsIgnoreCase(productBean.promote_price))){
			return "￥"+productBean.promote_price;	
		}
		return "￥"+productBean.shop_price;
	}
	public static String getPayLable(String payStatus){
		if("0".equalsIgnoreCase(payStatus)){
			return "未付款";
		}else if("2".equalsIgnoreCase(payStatus)){
			return "已付款";
		}
		return "未确定";
	}
	//ScaleType.FIT_XY
	public static ScaleType getScaleType(){
		return ScaleType.FIT_XY;
	}
}
