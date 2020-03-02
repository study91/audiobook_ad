package com.study91.audiobook.media;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.study91.audiobook.book.BookMediaService;
import com.study91.audiobook.file.FileFactory;
import com.study91.audiobook.file.IFile;
import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * 语音媒体播放器
 */
class AudioMediaPlayer implements IMediaPlayer {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param filename 媒体文件名
     */
    public AudioMediaPlayer(Context context, String filename) {
        m.context = context;
        setFilename(filename); //设置媒体文件名
    }

    @Override
    public void setFilename(String filename) {
        m.filename = filename;
        prepare(); //准备媒体
    }

    @Override
    public String getFilename() {
        return m.filename;
    }

    @Override
    public void play() {
        getMediaPlayer().start();
    }

    @Override
    public void pause() {
        if(isPlaying()) {
            getMediaPlayer().pause();
        }
    }

    @Override
    public boolean isPlaying() {
        return getMediaPlayer().isPlaying();
    }

    @Override
    public void seekTo(int position) {
        getMediaPlayer().seekTo(position);
    }

    @Override
    public int getLength() {
        return getMediaPlayer().getDuration();
    }

    @Override
    public int getPosition() {
        return getMediaPlayer().getCurrentPosition();
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        getMediaPlayer().setVolume(leftVolume, rightVolume); //设置媒体播放器音量
    }

    @Override
    public void release() {
        getMediaPlayer().release(); //释放媒体播放器
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    private Context getContext() {
        return m.context;
    }

    /**
     * 获取全局选项
     * @return 选项
     */
    private IOption getOption() {
        return OptionManager.getOption(getContext());
    }

    /**
     * 获取媒体播放器
     * @return 媒体播放器
     */
    private MediaPlayer getMediaPlayer() {
        if (m.mediaPlayer == null) {
            m.mediaPlayer = new MediaPlayer(); //实例化媒体播放器
            m.mediaPlayer.setOnErrorListener(new OnMediaErrorListener()); //设置媒体播放器错误事件监听器

            IOption option = getOption(); //获取全局选项
            m.mediaPlayer.setVolume(option.getAudioLeftVolume(), option.getAudioRightVolume()); //设置音量
            m.mediaPlayer.setOnPreparedListener(new OnMediaPreparedListener()); //设置媒体准备完成事件监听器
            m.mediaPlayer.setOnCompletionListener(new OnMediaCompletionListener()); //设置媒体播放结束事件监听器
        }

        return m.mediaPlayer;
    }

    /**
     * 准备媒体
     */
    private void prepare() {
        getMediaPlayer().reset(); //重置媒体播放器

        IOption option = getOption(); //获取全局配置

        try {
            switch (option.getStorageType()) {
                case IFile.STORAGE_TYPE_ASSETS: //获取Assets资源
                    AssetManager assetManager = getContext().getAssets();
                    AssetFileDescriptor assetFileDescriptor = assetManager.openFd(getFilename());
                    getMediaPlayer().setDataSource(
                            assetFileDescriptor.getFileDescriptor(),
                            assetFileDescriptor.getStartOffset(),
                            assetFileDescriptor.getLength());
                    break;
                default: //默认获取资源方式
                    IFile file = FileFactory.createFile(
                            getContext(),
                            option.getStorageType(),
                            getFilename());
                    FileDescriptor fileDescriptor = file.getFileDescriptor();
                    getMediaPlayer().setDataSource(fileDescriptor); //设置媒体播放器数据源
                    break;
            }

            getMediaPlayer().prepare(); //准备媒体
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 媒体准备完成事件监听器
     */
    private class OnMediaPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            Intent intent = new Intent(getOption().getServiceAction()); //获取媒体服务端Intent
            intent.putExtra(BookMediaService.MESSAGE, BookMediaService.EVENT_AUDIO_PREPARED); //设置消息
            getContext().sendBroadcast(intent); //发送广播
        }
    }

    /**
     * 媒体播放结束事件监听器
     */
    private class OnMediaCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Intent intent = new Intent(getOption().getServiceAction()); //获取媒体服务端Intent
            intent.putExtra(BookMediaService.MESSAGE, BookMediaService.EVENT_AUDIO_COMPLETION); //设置消息
            getContext().sendBroadcast(intent); //发送广播
        }
    }

    /**
     * 媒体播放器错误事件监听器
     */
    private class OnMediaErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.e("Test", "语音媒体播放器错误：what=" + what + "，extra=" + extra);
            return false;
        }
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 应用程序上下文
         */
        Context context;

        /**
         * 媒体文件名
         */
        String filename;

        /**
         * 媒体播放器
         */
        MediaPlayer mediaPlayer;
    }
}
