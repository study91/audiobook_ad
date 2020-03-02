package com.study91.audiobook.book;

import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * 目录接口
 */
public interface IBookCatalog {
    /**
     * 获取目录ID
     * @return 目录ID
     */
    int getCatalogID();

    /**
     * 获取书ID
     * @return 书ID
     */
    int getBookID();

    /**
     * 获取书
     */
    IBook getBook();

    /**
     * 获取索引
     */
    int getIndex();

    /**
     * 获取页码
     */
    int getPage();

    /**
     * 获取目录标题
     */
    String getTitle();

    /**
     * 显示目录
     * 确定是否在目录列表显示
     * @return true=显示，false=不显示
     */
    boolean displayCatalog();

    /**
     * 显示内容
     * 确定在目录子项中是否显示“显示”按钮
     * @return true=显示，false=不显示
     */
    boolean displayContent();

    /**
     * 显示解释
     * 确定在目录子项中是否显示“解释”按钮
     * @return true=显示，false=不显示
     */
    boolean displayExplain();

    /**
     * 显示页号
     * 确定是否在目录列表中显示页号
     * @return true=显示，false=不显示
     */
    boolean displayPage();

    /**
     * 显示图标
     * 确定是否在目录列表中显示图标
     * @return true=显示，false=不显示
     */
    boolean displayIcon();

    /**
     * 获取图标文件名
     * @return 图标文件名
     */
    String getIconFilename();

    /**
     * 获取图标Drawable
     * 注：图标Drawable每次都根据getIconFilename()的图标文件名单独创建
     * @return 图标Drawable
     */
    Drawable getIconDrawable();


    /**
     * 是否知识点
     * @return true=是，false=不是
     */
    boolean isKnowledgePoint();

    /**
     * 获取熟悉级别
     * @return 熟悉级别（1=陌生 2=比较熟悉 3=非常熟悉）
     */
    int getFamiliarLevel();

    /**
     * 有语音
     * @return true=有语音，false=没有语音
     */
    boolean hasAudio();

    /**
     * 设置播放开关
     * @param value 值（true=充许播放，false=不充许播放)
     */
    void setAllowPlayAudio(boolean value);

    /**
     * 更新充许播放的语音
     * @param value 值（true=充许播放，false=不充许播放)
     */
    void updateAllowPlayAudio(boolean value);

    /**
     * 充许播放语音
     * @return true=充许播放，false=不充许播放
     */
    boolean allowPlayAudio();

    /**
     * 获取语音文件名
     * @return 语音文件名
     */
    String getAudioFilename();

    /**
     * 获取原文
     * @return 原文
     */
    String getOriginal();

    /**
     * 获取解释
     * @return 解释
     */
    String getExplain();

    /**
     * 获取内容列表
     * @return 内容列表
     */
    List<IBookContent> getContentList();

    /**
     * 获取语音内容
     * @param position 语音位置
     * @return 语音内容
     */
    IBookContent getAudioContent(long position);

    /**
     * 获取语音内容列表
     * @return 语音内容列表
     */
    List<IBookContent> getAudioContentList();
}
