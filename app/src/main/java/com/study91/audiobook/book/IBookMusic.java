package com.study91.audiobook.book;

/**
 * 音乐接口
 */
public interface IBookMusic {
    /**
     * 获取音乐ID
     * @return 音乐ID
     */
    int getMusicID();

    /**
     * 获取书ID
     * @return 书ID
     */
    int getBookID();

    /**
     * 获取音乐索引
     * @return 音乐索引
     */
    int getIndex();

    /**
     * 获取音乐名称
     * @return 音乐名称
     */
    String getMusicName();

    /**
     * 获取音乐文件名
     * @return 音乐文件名
     */
    String getMusicFilename();
}
