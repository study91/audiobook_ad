package com.study91.audiobook.ad;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.study91.audiobook.config.ConfigFactory;
import com.study91.audiobook.config.IConfig;
import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;

import java.util.Date;

/**
 * 广告抽象类
 */

abstract class AAd implements IAd {
    private final String TAG = "Test"; //测试标识
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public AAd(Context context) {
        m.context = context;
    }

    @Override
    abstract public View getBannerView();

    @Override
    abstract public void release();

    protected void setBannerViewLayoutParams(View view) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        view.setLayoutParams(layoutParams);

        //根据全局配置判断是否显示广告
        Date firstRunTime = getOption().getFirstRunTime();
        Date thisTime = new Date();
        int daysBetween = (int)((thisTime.getTime() - firstRunTime.getTime()) / 86400000);

        if (getConfig().isTest()) { //测试版
            //测试版状态，无条件显示广告
            view.setVisibility(View.VISIBLE);
            Log.e(TAG, "测试版，显示广告，安装天数：第" + daysBetween + "天，广告显示：第" + getOption().getAdStartDay() + "天。");
        } else {
            //正式版状态，根据配置信息显示广告
            if (daysBetween < getOption().getAdStartDay()) {
                //不显示广告
                view.setVisibility(View.GONE);
                Log.e(TAG, "正式版，不显示广告，安装天数：第" + daysBetween + "天，广告显示：第" + getOption().getAdStartDay() + "天。");
            } else {
                //显示广告
                view.setVisibility(View.VISIBLE);
                Log.e(TAG, "正式版，显示广告，安装天数：第" + daysBetween + "天，广告显示：第" + getOption().getAdStartDay() + "天。");
            }
        }
    }

    /**
     * 获取全局选项
     */
    private IOption getOption() {
        return OptionManager.getOption(getContext());
    }

    /**
     * 获取全局配置
     */
    private IConfig getConfig() {
        return ConfigFactory.getConfig(getContext());
    }

    /**
     * 获取应用程序上下文
     */
    protected Context getContext() {
        return m.context;
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 应用程序上下文
         */
        Context context;
    }
}
