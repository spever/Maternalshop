package com.subzero.maternalshop.bean;
import android.os.Parcel;
import android.os.Parcelable;
/**产品*/
public class ProductBean implements Parcelable
{
	/**商品ID*/
	public String goods_id;
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
	/**商品小图*/
	public String small;
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
	/**被选中*/
	public boolean isSelected;
	@Override
	public int describeContents(){
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(goods_id);  
		dest.writeString(goods_name);  
		dest.writeString(goods_number);
		dest.writeString(url);
		dest.writeString(thumb);
		dest.writeString(shop_price);
		dest.writeString(salesnum);
	}
	public static final Creator<ProductBean> CREATOR  = new Creator<ProductBean>() {
		//实现从source中创建出类的实例的功能  
		@Override  
		public ProductBean createFromParcel(Parcel source) {  
			ProductBean bean  = new ProductBean();  
			bean.goods_id = source.readString();  
			bean.goods_name= source.readString();  
			bean.goods_number = source.readString();  
			bean.url = source.readString();
			bean.thumb = source.readString();
			bean.shop_price = source.readString();
			bean.salesnum = source.readString();
			return bean;  
		}  
		//创建一个类型为T，长度为size的数组  
		@Override  
		public ProductBean[] newArray(int size) {  
			return new ProductBean[size];  
		}  
	};
	@Override
	public String toString()
	{
		return "ProductBean [goods_id=" + goods_id + ", goods_name=" + goods_name + ", goods_number=" + goods_number + ", goods_comment=" + goods_comment + ", rec_id=" + rec_id + ", thumb=" + thumb
				+ ", url=" + url + ", small=" + small + ", salesnum=" + salesnum + ", market_price=" + market_price + ", shop_price=" + shop_price + ", promote_price=" + promote_price + ", total="
				+ total + ", subtotal=" + subtotal + ", countIncart=" + countIncart + ", isSelected=" + isSelected + "]";
	}




}
