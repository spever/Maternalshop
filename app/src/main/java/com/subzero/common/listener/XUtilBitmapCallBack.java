package com.subzero.common.listener;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.util.LogUtils;
import com.subzero.common.utils.ObjUtil;
/**继承图片下载的回调*/
public class XUtilBitmapCallBack extends BitmapLoadCallBack<ImageView>
{
	/**加载成功  */
	@Override
	public void onLoadCompleted(ImageView imageView, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from){
		if(ObjUtil.isBitmapEmpty(bitmap)){
			LogUtils.e("uri = "+uri);
			return;
		}
		imageView.setImageBitmap(bitmap);
	}
	@Override
	public void onLoadFailed(ImageView imageView, String uri, Drawable drawable)
	{
		if(ObjUtil.isDrawableEmpty(drawable)){
			return;
		}
		imageView.setImageDrawable(drawable);
	}
}