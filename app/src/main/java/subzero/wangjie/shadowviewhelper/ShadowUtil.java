package subzero.wangjie.shadowviewhelper;

import android.view.View;

public class ShadowUtil
{
	private final static int shadowColor = 0x77000000;
	public static void bindView(View view, int coverColor){
		ShadowProperty shadowProperty = new ShadowProperty();
		shadowProperty.setShadowColor(shadowColor);
		shadowProperty.setShadowRadius(10);
		shadowProperty.setShadowDx(0);
		shadowProperty.setShadowDy(0);
		ShadowViewHelper.bindShadowHelper(shadowProperty , view, coverColor, 10, 10);
	}

}
