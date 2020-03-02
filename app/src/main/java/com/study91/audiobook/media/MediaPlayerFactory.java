package com.study91.audiobook.media;

import android.content.Context;

/**
 * 媒体播放器工厂
 */
public class MediaPlayerFactory {
    /**
     * 创建语音媒体播放器
     * @param context 应用程序上下文
     * @param filename 媒体文件名
     * @return 语音媒体播放器
     */
    public static IMediaPlayer createAudioMediaPlayer(Context context, String filename) {
        return new AudioMediaPlayer(context, filename);
    }

    /**
     * 创建音乐媒体播放器
     * @param context 应用程序上下文
     * @param filename 媒体文件名
     * @return 音乐媒体播放器
     */
    public static IMediaPlayer createMusicMediaPlayer(Context context, String filename) {
        return new MusicMediaPlayer(context, filename);
    }
}
