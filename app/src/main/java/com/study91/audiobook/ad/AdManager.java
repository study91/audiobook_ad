package com.study91.audiobook.ad;

import android.content.Context;
import android.view.View;

import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBook;

/**
 * 广告管理器
 */
public class AdManager {
    private static final int PLATFORM_ADWO = 1; //安沃广告平台
    private static final int PLATFROM_GDT = 2; //广点通广告平台

    /**
     * 获取广告
     * @param context 应用程序上下文
     * @return 广告
     */
    public static IAd getAd(Context context) {
        if (M.ad == null) {
            IBook book = BookManager.getBook(context); //获取全局书

            //广告平台处理
            switch (book.getAdPlatform()) {
                case PLATFORM_ADWO: //安沃
                    M.ad = new DefaultAd(context); //暂时显示默认广告子类
                    break;
                case PLATFROM_GDT: //广点通
                    break;
            }
        }

        return M.ad;
    }

    /**
     * 释放资源
     */
    public static void release() {
        if (M.ad != null) {
            M.ad.release();
            M.ad = null;
        }
    }

    /**
     * 私有全局类
     */
    private static class M {
        /**
         * 广告
         */
        static IAd ad;
    }
}