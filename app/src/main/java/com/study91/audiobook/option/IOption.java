package com.study91.audiobook.option;

import java.util.Date;

/**
 * 选项接口
 */
public interface IOption {
//    /**
//     * 循环模式（不循环）
//     */
//    static final int LOOP_MODE_NONE = 0;
//
//    /**
//     * 循环模式（列表顺序循环）
//     */
//    static final int LOOP_MODE_LIST = 1;
//
//    /**
//     * 循环模式（随机循环）
//     */
//    static final int LOOP_MODE_RANDOM = 2;

    /**
     * 初始化
     */
    void init();

    /**
     * 获取选项ID
     * @return 选项ID
     */
    int getOptionID();

    /**
     * 获取选项名称
     * @return 选项名称
     */
    String getOptionName();

    /**
     * 获取书ID
     * @return 书ID
     */
    int getBookID();

    /**
     * 获取存储类型
     * @return 存储类型
     */
    int getStorageType();

    /**
     * 获取资源路径
     * @return 资源路径
     */
    String getResourcePath();

    /**
     * 是否能选书
     * @return true=能选书，false=不能选书
     */
    boolean canChooseBook();

    /**
     * 设置语音音量
     * @param leftVolume 左声道音量
     * @param rightVolume 右声道音量
     */
    void setAudioVolume(float leftVolume, float rightVolume);

    /**
     * 获取语音左声道音量
     * @return 语音左声道音量
     */
    float getAudioLeftVolume();

    /**
     * 获取语音右声道音量
     * @return 语音右声道音量
     */
    float getAudioRightVolume();

    /**
     * 获取语音循环模式
     * @return 语音循环模式
     */
    int getAudioLoopMode();

    /**
     * 设置音乐音量
     * @param leftVolume 左声道音量
     * @param rightVolume 右声道音量
     */
    void setMusicVolume(float leftVolume, float rightVolume);

    /**
     * 获取音乐左声道音量
     * @return 音乐左声道音量
     */
    float getMusicLeftVolume();

    /**
     * 获取音乐右声道音量
     * @return 音乐右声道音量
     */
    float getMusicRightVolume();

    /**
     * 获取音乐循环模式
     * @return 音乐循环模式
     */
    int getMusicLoopMode();

    /**
     * 设置目录字体大小
     * @param fontSize 字体大小
     */
    void setCatalogFontSize(int fontSize);

    /**
     * 获取目录字体大小
     * @return 字体大小
     */
    int getCatalogFontSize();

    /**
     * 设置内容字体大小
     * @param fontSize 字体大小
     */
    void setContentFontSize(int fontSize);

    /**
     * 获取内容字体大小
     */
    int getContentFontSize();

    /**
     * 设置全屏字体大小
     * @param fontSize 字体大小
     */
    void setFullFontSize(int fontSize);

    /**
     * 获取全屏字体大小
     */
    int getFullFontSize();

    /**
     * 获取学习方法Url地址
     */
    String getStudyUrl();

    /**
     * 更新学习方法地址
     * @param url Url地址
     */
    void updateStudyUrl(String url);

    /**
     * 获取问题解签Url地址
     */
    String getQuestionUrl();

    /**
     * 更新问题解答地址
     * @param url Url地址
     */
    void updateQuestionUrl(String url);

    /**
     * 获取版本号
     */
    String getVersion();

    /**
     * 获取版权信息
     */
    String getCopyright();

    /**
     * 更新版权信息
     * @param copyright 版权信息
     */
    void updateCopyright(String copyright);

    /**
     * 是否第一次启动
     */
    boolean isFirstRun();

    /**
     * 获取第一次启动时间
     */
    Date getFirstRunTime();

    /**
     * 获取广告显示日
     */
    int getAdStartDay();

    /**
     * 更新广告显示日
     * @param startDay 广告显示日
     */
    void updateAdStartDay(int startDay);

    /**
     * 获取媒体服务端Action
     */
    String getServiceAction();

    /**
     * 获取媒体客户端Action
     */
    String getClientAction();
}
