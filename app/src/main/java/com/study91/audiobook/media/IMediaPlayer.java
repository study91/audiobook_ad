package com.study91.audiobook.media;

/**
 * 媒体播放器接口
 */
public interface IMediaPlayer {
    /**
     * 设置媒体文件名
     * @param filename 媒体文件名
     */
    void setFilename(String filename);

    /**
     * 获取媒体文件名
     * @return 媒体文件名
     */
    String getFilename();

    /**
     * 播放
     */
    void play();

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 是否正在播放
     * @return true=正在播放，false=没有播放
     */
    boolean isPlaying();

    /**
     * 定位媒体播放位置
     * @param position 媒体位置
     */
    void seekTo(int position);

    /**
     * 获取媒体长度
     * @return 媒体长度
     */
    int getLength();

    /**
     * 获取媒体当前位置
     * @return 媒体当前位置
     */
    int getPosition();

    /**
     * 设置音量
     * @param leftVolume 左声道音量
     * @param rightVolume 右声道音量
     */
    void setVolume(float leftVolume, float rightVolume);

    /**
     * 释放媒体播放器
     */
    void release();
}
