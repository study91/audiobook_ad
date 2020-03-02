package com.study91.audiobook.book;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;

/**
 * 媒体客户端
 */
public abstract class BookMediaClient {
    private Context context; //应用程序上下文
    private BroadcastReceiver clientReceiver; //客户端广播接收器

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public BookMediaClient(Context context) {
        this.context = context;
    }

    /**
     * 设置客户端广播接收器
     * @param intent 客户端广播接收器Intent
     */
    public abstract void setOnReceive(Intent intent);

    /**
     * 播放
     */
    public void play() {
        Intent intent = getMediaServiceIntent(); //获取媒体服务端Intent
        intent.putExtra(BookMediaService.MESSAGE, BookMediaService.COMMAND_PLAY); //设置消息
        getContext().sendBroadcast(intent); //发送广播
    }

    /**
     * 暂停播放
     */
    public void pause() {
        Intent intent = getMediaServiceIntent(); //获取媒体服务端Intent
        intent.putExtra(BookMediaService.MESSAGE, BookMediaService.COMMAND_PAUSE); //设置消息
        getContext().sendBroadcast(intent); //发送广播
    }

    /**
     * 定位播放位置
     * @param position 播放位值
     */
    public void seekTo(int position) {
        Intent intent = getMediaServiceIntent(); //获取媒体服务端Intent
        intent.putExtra(BookMediaService.MESSAGE, BookMediaService.COMMAND_SEEK_TO); //设置消息
        intent.putExtra(BookMediaService.VALUE_AUDIO_POSITION, position); //设置播放位置参数
        getContext().sendBroadcast(intent); //发送广播
    }

    /**
     * 设置语音音量
     * @param leftVolume 左声道音量
     * @param rightVolume 右声道音量
     */
    public void setAudioVolume(float leftVolume, float rightVolume) {
        Intent intent = getMediaServiceIntent(); //获取媒体服务端Intent
        intent.putExtra(BookMediaService.MESSAGE, BookMediaService.COMMAND_SET_AUDIO_VOLUME); //设置消息
        intent.putExtra(BookMediaService.VALUE_AUDIO_LEFT_VOLUME, leftVolume); //设置语音左声道参数
        intent.putExtra(BookMediaService.VALUE_AUDIO_RIGHT_VOLUME, rightVolume); //设置语音右声道参数
        getContext().sendBroadcast(intent); //发送广播
    }

    /**
     * 设置音乐音量
     * @param leftVolume 左声道音量
     * @param rightVolume 右声道音量
     */
    public void setMusicVolume(float leftVolume, float rightVolume) {
        Intent intent = getMediaServiceIntent(); //获取媒体服务端Intent
        intent.putExtra(BookMediaService.MESSAGE, BookMediaService.COMMAND_SET_MUSIC_VOLUME); //设置消息
        intent.putExtra(BookMediaService.VALUE_MUSIC_LEFT_VOLUME, leftVolume); //设置音乐左声道参数
        intent.putExtra(BookMediaService.VALUE_MUSIC_RIGHT_VOLUME, rightVolume); //设置音乐右声道参数
        getContext().sendBroadcast(intent); //发送广播
    }

    /**
     * 刷新
     */
    public void refresh() {
        Intent intent = getMediaServiceIntent(); //获取媒体服务端Intent
        intent.putExtra(BookMediaService.MESSAGE, BookMediaService.COMMAND_REFRESH); //设置消息
        getContext().sendBroadcast(intent); //发送广播
    }

    /**
     * 注册客户端
     */
    public void register() {
        clientReceiver = new ClientBroadcastReceiver(); //实例化客户端广播接收器
        IntentFilter filter = new IntentFilter(getOption().getClientAction()); //实例化InterFilter
        getContext().registerReceiver(clientReceiver, filter); //注册广播接收器
    }

    /**
     * 注销客户端
     */
    public void unregister() {
        //注销客户端广播接收器
        if (clientReceiver != null) {
            getContext().unregisterReceiver(clientReceiver);
        }
    }

    /**
     * 获取应用程序上下文
     */
    private Context getContext() {
        return context;
    }

    /**
     * 获取全局选项
     */
    private IOption getOption() {
        return OptionManager.getOption(getContext());
    }

    /**
     * 获取书媒体服务端Intent
     * @return 书媒体服务端Intent
     */
    private Intent getMediaServiceIntent() {
        return new Intent(getOption().getServiceAction()); //实例化有声书服务端Intent
    }

    /**
     * 媒体客户端广播接收器
     */
    private class ClientBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            setOnReceive(intent); //设置接收器
        }
    }
}
