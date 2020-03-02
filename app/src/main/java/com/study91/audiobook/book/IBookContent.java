package com.study91.audiobook.book;

import android.graphics.drawable.Drawable;

/**
 * 内容接口
 */
public interface IBookContent {
    /**
     * 获取内容ID
     * @return 内容ID
     */
    int getContentID();

    /**
     * 获取目录ID
     * @return 目录ID
     */
    int getCatalogID();

    /**
     * 获取目录
     * @return 目录
     */
    IBookCatalog getCatalog();

    /**
     * 获取页码
     * @return 页码
     */
    int getPage();

    /**
     * 有语音
     * @return true=有语音，false=没有语音
     */
    boolean hasAudio();

    /**
     * 获取语音开始时间
     */
    long getAudioStartTime();

    /**
     * 获取内容图片文件名
     */
    String getImageFilename();

    /**
     * 获取内容图片Drawable
     */
    Drawable getImageDrawable();

    /**
     * 获取图标文件名
     */
    String getIconFilename();

    /**
     * 获取图标Drawable
     */
    Drawable getIconDrawable();
}
