package com.study91.audiobook.ad;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.sixth.adwoad.AdListener;
import com.sixth.adwoad.AdwoAdView;
import com.sixth.adwoad.ErrorCode;
import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.config.ConfigFactory;
import com.study91.audiobook.config.IConfig;

/**
 * 安沃广告
 */
class AdwoAd extends AAd {
    private final String TAG = "Test"; //测试标识

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public AdwoAd(Context context) {
        super(context);
    }

    @Override
    public View getBannerView() {
        try {
            //获取书广告
            AdwoAdView bannerView = new AdwoAdView(
                    getContext(),
                    getBook().getAdAppID(),
                    getConfig().isTest(), //是否测试状态（设置true 出现测试广告，false 为正式模式）
                    20); //以秒为单位，0 标示单次请求，最小不能为30 秒
            bannerView.setListener(new OnAdListener());
            setBannerViewLayoutParams(bannerView);
            return bannerView;
        } catch (Exception e) {
            Log.e("AdwoAd", "AdwoAd.getBannerView异常：" + e.getMessage());
            View view = new View(getContext());
            setBannerViewLayoutParams(view);
            return view;
        }
    }

    @Override
    public void release() {

    }

    /**
     * 获取全局书
     */
    private IBook getBook() {
        return BookManager.getBook(getContext());
    }

    /**
     * 获取全局配置
     */
    private IConfig getConfig() {
        return ConfigFactory.getConfig(getContext());
    }

    /**
     * 广告事件监听器
     */
    private class OnAdListener implements AdListener {
        @Override
        public void onReceiveAd(Object o) {
            Log.e(TAG, "安沃->请求广告成功！");
        }

        @Override
        public void onFailedToReceiveAd(View view, ErrorCode errorCode) {
            switch (errorCode.getErrorCode()) {
                case -27:
                    Log.e(TAG, "安沃->非法请求！");
                    break;
                case -29:
                    Log.e(TAG, "安沃->" + "Adwo_PID 不存在！");
                    break;
                case -28:
                    Log.e(TAG, "安沃->" + "Adwo_PID 未通过审核！");
                    break;
                case -23:
                    Log.e(TAG, "安沃->" + "程序评分低或存在作弊行为！");
                    break;
                case -25:
                    Log.e(TAG, "安沃->" + "使用代理服务器请求广告！");
                    break;
                case -22:
                    Log.e(TAG, "安沃->" + "未知错误，一般为PID 和包名不一致或为网络超时！");
                    break;
                case -24:
                    Log.e(TAG, "安沃->" + "该地区没有广告！");
                    break;
                case -31:
                    Log.e(TAG, "安沃->" + "广告均已到达消费上限！");
                    break;
                case -32:
                    Log.e(TAG, "安沃->" + "服务器忙！");
                    break;
                case -26:
                    Log.e(TAG, "安沃->" + "服务器无响应！");
                    break;
                case -30:
                    Log.e(TAG, "安沃->" + "其他未知错误！");
                    break;
                case 101:
                    Log.e(TAG, "安沃->" + "广告对象初始化失败！");
                    break;
                case 30:
                    Log.e(TAG, "安沃->" + "广告请求太频繁！");
                    break;
                case 31:
                    Log.e(TAG, "安沃->" + "网络连接失败！");
                    break;
                case 32:
                    Log.e(TAG, "安沃->" + "外部存储卡不存在！");
                    break;
                case 35:
                case 15:
                    Log.e(TAG, "安沃->" + "网络连接失败！");
                    break;
                case 37:
                case 38:
                    Log.e(TAG, "安沃->" + "下载广告资源失败！");
                    break;
                case 39:
                    Log.e(TAG, "安沃->" + "外部存储卡中没有广告资源！");
                    break;
                case 50:
                    Log.e(TAG, "安沃->" + "外部存储卡没有开屏广告！");
                    break;
                case 11:
                    Log.e(TAG, "安沃->" + "网络连接失败！");
                    break;
            }
        }

        @Override
        public void onPresentScreen() {
            Log.e(TAG, "安沃->onPresentScreen");
        }

        @Override
        public void onDismissScreen() {
            Log.e(TAG, "安沃->onDismissScreen");
        }
    }
}
