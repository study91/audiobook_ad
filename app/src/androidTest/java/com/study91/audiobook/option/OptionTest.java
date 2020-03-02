package com.study91.audiobook.option;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

/**
 * 选项测试
 */
public class OptionTest extends ApplicationTestCase<Application> {
    /**
     * 构造器
     */
    public OptionTest() {
        super(Application.class);
    }

    /**
     * 测试全局选项
     */
    public void testGetOption() {
        IOption option = OptionManager.getOption(getContext());

        Log.e("Test", "选项ID：" + option.getOptionID());
        Log.e("Test", "选项名称：" + option.getOptionName());
        Log.e("Test", "书ID：" + option.getBookID());
        Log.e("Test", "存储类型：" + option.getStorageType());
        Log.e("Test", "资源路径：" + option.getResourcePath());
        Log.e("Test", "能选书：" + option.canChooseBook());
        Log.e("Test", "语音左声道音量：" + option.getAudioLeftVolume());
        Log.e("Test", "语音右声道音量：" + option.getAudioRightVolume());
        Log.e("Test", "语音循环模式：" + option.getAudioLoopMode());
        Log.e("Test", "音乐左声道音量：" + option.getMusicLeftVolume());
        Log.e("Test", "音乐右声道音量：" + option.getMusicRightVolume());
        Log.e("Test", "音乐循环模式：" + option.getMusicLoopMode());
        Log.e("Test", "目录字体大小：" + option.getCatalogFontSize());
        Log.e("Test", "内容字体大小：" + option.getContentFontSize());
        Log.e("Test", "全屏字体大小：" + option.getFullFontSize());
        Log.e("Test", "学习方法地址：" + option.getStudyUrl());
        Log.e("Test", "问题解答地址：" + option.getQuestionUrl());
        Log.e("Test", "版权：" + option.getCopyright());
        Log.e("Test", "广告显示日：" + option.getAdStartDay());
        Log.e("Test", "媒体服务端Action：" + option.getServiceAction());
        Log.e("Test", "媒体客户端Action：" + option.getClientAction());
        Log.e("Test", "是否第一次运行：" + option.isFirstRun());

        Log.e("Test", "第二次读是否第一次运行：" + option.isFirstRun());
    }
}
