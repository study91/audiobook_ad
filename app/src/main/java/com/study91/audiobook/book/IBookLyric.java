package com.study91.audiobook.book;

/**
 * 字幕接口
 */
public interface IBookLyric {
    /**
     * 获取字幕内容
     * @param time 时间
     */
    String getContent(long time);
}
