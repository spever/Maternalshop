package com.subzero.maternalshop.wxapi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.lidroid.xutils.util.LogUtils;
import com.socks.library.KLog;
import com.subzero.common.utils.ClassUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.multiplepay.AliPayUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.bean.multiplepay.MultiplePayBean;
import com.subzero.maternalshop.bean.multiplepay.PayResult;
import com.subzero.maternalshop.config.Cache;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.modelpay.PayResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;

public class WXPayEntryActivity extends Activity {
	private IWXAPI iwxapi;
	private AliPayHandler aliPayHandler;
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_CHECK_FLAG = 2;
	private Activity activity;
	private Context context;

	public static final String keyClassName = "keyClassName";
	public static final String keyPayResult = "keyPayResult";
	public static final String keyMultiplePayBean = "keyMutiplePayBean";
	public static final String keyPayType = "keyPayType";
	public static final String keyPayTitle = "keyPayTitle";

	public static final String payTypeAliPay = "payTypeAliPay";
	public static final String payTypeWeChatPay = "payTypeWeChatPay";
	public static final String payTypeUnionPay = "payTypeUnionPay";
	/** 取消支付 */
	public static final String payResultCancle = "payResultCancle";
	/** 支付失败 */
	public static final String payResultFail = "payResultFail";
	/** 支付成功 */
	public static final String payResultSuccess = "payResultSuccess";
	/** 支付处理中 */
	public static final String payResultDealing = "payResultDealing";
	/** 支付 匹配值 */
	private String payResultInfo = "";
	public String payType;
	public MultiplePayBean multiplePayBean;
	public Class<?> clazz;
	private String payTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multiple_pay_pay_result);
		Intent intent = getIntent();
		String className = intent.getStringExtra(keyClassName);
		if (TextUtils.isEmpty(className)) {
			finish();
		}
		clazz = ClassUtil.forName(className);
		payType = intent.getStringExtra(keyPayType);
		payTitle = intent.getStringExtra(keyPayTitle);
		multiplePayBean = (MultiplePayBean) intent
				.getSerializableExtra(keyMultiplePayBean);
		activity = this;
		context = this;
		if (payTitle != null) {
			((TextView) findViewById(R.id.tv)).setText(payTitle);
		}
		if (payTypeAliPay.equalsIgnoreCase(payType)) {
			startAliPay();
		} else if (payTypeWeChatPay.equalsIgnoreCase(payType)) {
			startWeChatPay();
		} else if (payTypeUnionPay.equalsIgnoreCase(payType)) {
			if (TextUtils.isEmpty(multiplePayBean.unionTN)) {
				finish();
			}
			startUnionPay();
		}
	}

	private void startWeChatPay() {
		iwxapi = WXAPIFactory.createWXAPI(this, multiplePayBean.weChatAppID);
		iwxapi.registerApp(multiplePayBean.weChatAppID);
		if (!iwxapi.isWXAppInstalled()) {
			ToastUtil.shortAtCenter(context, "没有安装微信客户端");
			finish();
		} else if (!iwxapi.isWXAppSupportAPI()) {
			ToastUtil.shortAtCenter(context, "您的微信客户端版本太低 不支持支付工能");
			finish();
		}
		// requestPayFinish();
		PayReq payReq = new PayReq();
		/* 在微信开放平台申请的应用id */
		payReq.appId = multiplePayBean.weChatAppID;
		/* 商户 id */
		payReq.partnerId = multiplePayBean.weChatPartnerid;
		/* 预支付订单 */
		payReq.prepayId = multiplePayBean.weChatPrepayid;
		/* 随机串，防重发 */
		payReq.nonceStr = multiplePayBean.weChatNoncestr;
		/* 时间戳，防重发 */
		payReq.timeStamp = multiplePayBean.weChatTimestamp;
		/* 根据文档填写的数据和签名 值为 Sign=WXPay 是固定的 */
		payReq.packageValue = "Sign=WXPay";
		/* 根据微信开放平台文档对数据做的签名 */
		payReq.sign = multiplePayBean.weChatSign;
		iwxapi.sendReq(payReq);
		iwxapi.handleIntent(getIntent(), new MyIWXAPIEventHandler());
	}

	/**
	 * TODO: 吊起银联 支付
	 * */
	private void startUnionPay()
	{
		/* activity  ——用于启动支付控件的活动对象
		 * spId  ——保留使用，这里输入null  
		 * sysProvider ——保留使用，这里输入null  
		 * orderInfo   ——订单信息为交易流水号，即TN。   
		 * mode   —— 银联后台环境标识，“00”将在银联正式环境发起交易, “01”将在 	*/
		LogUtils.e("TN = "+multiplePayBean.unionTN+"  mode = "+multiplePayBean.unionMode);
		final int ret = UPPayAssistEx.startPay(activity, null, null, multiplePayBean.unionTN, multiplePayBean.unionMode);
		//检测当前手机是否安装银联在线支付插件
		if(UPPayAssistEx.installUPPayPlugin(activity)){
			if (ret == 0) {
	        	UPPayAssistEx.installUPPayPlugin(activity);
	        } else {
	        	UPPayAssistEx.startPayByJAR(activity, WXPayEntryActivity.class, null, null,multiplePayBean.unionTN, multiplePayBean.unionMode);
	        }
		}else{
			new AlertDialog.Builder(context)
			.setMessage(
					"监测到你尚未安装银联在线支付插件,无法进行银联支付,请选择安装插件(已打包在本地,无流量消耗)还是使用其他支付")
			.setPositiveButton("安装",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface dialog,
								int which) {
							installUPPayPlugin("UPPayPluginExStd.apk");
							//安装插件后调起重新调起支付
							finish();
						}
					})
			.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(
								DialogInterface dialog,
								int which) {
							ToastUtil.shortAtCenter(context, "如果使用银联支付，请在其他地方下载安装“银联在线支付”");
							finish();
						}
					}).create().show();
		}
	}
	
	/**
	 * TODO: 安装银联支付插件
	 * */
	private void installUPPayPlugin(String fileName) {
		try {
			InputStream is = context.getAssets().open(fileName);
			File file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + fileName);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}
			fos.close();
			is.close();

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.parse("file://" + file),
					"application/vnd.android.package-archive");
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * TODO: 吊起支付宝 支付
	 * */
	private void startAliPay() {
		// 订单
		String orderInfo = AliPayUtil.getOrderInfo(
				multiplePayBean.alipaySubject, multiplePayBean.alipayBody,
				multiplePayBean.alipayPrice, multiplePayBean.alipayPID,
				multiplePayBean.alipaySeller, multiplePayBean.alipayUrl);
		// 对订单做RSA 签名
		String sign = AliPayUtil.sign(orderInfo,
				multiplePayBean.alipayRsaPrivate);
		// 仅需对sign 做URL编码
		sign = AliPayUtil.encode(sign);
		// 完整的符合支付宝参数规范的订单信息
		String payInfo = orderInfo + "&sign=\"" + sign + "\"&sign_type=\"RSA\"";
		aliPayHandler = (aliPayHandler == null) ? new AliPayHandler()
				: aliPayHandler;
		new AliPayPayRunnable(aliPayHandler, payInfo).start();
	}

	private final class AliPayPayRunnable extends Thread {
		private AliPayHandler aliPayHandler;
		private String payInfo;

		public AliPayPayRunnable(AliPayHandler aliPayHandler, String payInfo) {
			this.aliPayHandler = aliPayHandler;
			this.payInfo = payInfo;
		}

		@Override
		public void run() {
			// 构造PayTask 对象
			PayTask alipay = new PayTask(activity);
			// 调用支付接口，获取支付结果
			String result = alipay.pay(payInfo);
			Message msg = new Message();
			msg.what = SDK_PAY_FLAG;
			msg.obj = result;
			aliPayHandler.sendMessage(msg);
		}
	}

	@SuppressLint("HandlerLeak")
	private final class AliPayHandler extends Handler {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);
				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				@SuppressWarnings("unused")
				String resultInfo = payResult.getResult();
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					// Toast.makeText(context, "支付成功",
					// Toast.LENGTH_SHORT).show();
					payResultInfo = payResultSuccess;
				} else if (TextUtils.equals(resultStatus, "8000")) {
					// Toast.makeText(context, "支付结果确认中",
					// Toast.LENGTH_SHORT).show();
					payResultInfo = payResultDealing;
				} else if (TextUtils.equals(resultStatus, "6001")) {
					// Toast.makeText(context, "支付结果确认中",
					// Toast.LENGTH_SHORT).show();
					payResultInfo = payResultCancle;
					// anchoring
				} else {
					// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
					// Toast.makeText(context, "支付失败",
					// Toast.LENGTH_SHORT).show();
					payResultInfo = payResultFail;
				}
				setResultAndFinish();
			}
				break;
			case SDK_CHECK_FLAG: {
				Toast.makeText(context, "检查结果为：" + msg.obj, Toast.LENGTH_SHORT)
						.show();
				break;
			}
			default:
				break;
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		iwxapi.handleIntent(intent, new MyIWXAPIEventHandler());
	}

	private final class MyIWXAPIEventHandler implements IWXAPIEventHandler {
		@Override
		public void onReq(BaseReq req) {
			LogUtils.e("req.openId = " + req.openId);
			if (req instanceof PayReq) {
				PayReq payReq = (PayReq) req;
				LogUtils.e("预支付订单 = " + payReq.prepayId);
				LogUtils.e("商户 id = " + payReq.partnerId);
				LogUtils.e("第三方app自定义字符串 = " + payReq.extData);
			}
		}

		@Override
		public void onResp(BaseResp resp) {
			if (resp instanceof PayResp) {
				PayResp payResp = (PayResp) resp;
				Cache.saveTmpFile("预支付订单 = " + payResp.prepayId + "返回给商家的信息 = "
						+ payResp.returnKey + "第三方app自定义字符串 = "
						+ payResp.extData);
			}
			if (resp.errCode == 0) {
				payResultInfo = payResultSuccess;
			} else if (resp.errCode == -2) {
				payResultInfo = payResultCancle;
			} else if (resp.errCode == -1) {
				payResultInfo = payResultFail;
			}
			setResultAndFinish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		/*
		 * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
		 */
		String str = data.getExtras().getString("pay_result");
		KLog.e("2 " + data.getExtras().getString("merchantOrderId"));
		if (str.equalsIgnoreCase("success")) {
			payResultInfo = payResultSuccess;
		} else if (str.equalsIgnoreCase("fail")) {
			payResultInfo = payResultFail;
		} else if (str.equalsIgnoreCase("cancel")) {
			payResultInfo = payResultCancle;
		}
		// 支付完成,处理自己的业务逻辑!
		setResultAndFinish();
	}

	private void setResultAndFinish() {
		Intent data = new Intent(context, clazz);
		data.putExtra(keyPayResult, payResultInfo);
		setResult(RESULT_OK, data);
		finish();
	}
}