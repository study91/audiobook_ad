package com.study91.audiobook.book;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.study91.audiobook.R;
import com.study91.audiobook.media.IMediaPlayer;
import com.study91.audiobook.media.MediaPlayerFactory;
import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;
import com.study91.audiobook.ui.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 书媒体服务
 */
public class BookMediaService extends Service {
    public static final String MESSAGE = "MESSAGE"; //消息

    public static final int COMMAND_PLAY = 1; //命令（播放）
    public static final int COMMAND_PAUSE = 2; //命令（暂停播放）
    public static final int COMMAND_SEEK_TO = 3; //命令（定位播放位置）
    public static final int COMMAND_SET_AUDIO_VOLUME = 4; //命令（语音音量设置）
    public static final int COMMAND_SET_MUSIC_VOLUME = 5; //命令（音乐音量设置）
    public static final int EVENT_AUDIO_PREPARED = 6; //事件（语音准备就绪）
    public static final int EVENT_AUDIO_COMPLETION = 7; //事件（语音播放完成）
    public static final int EVENT_MUSIC_PREPARED = 8; //事件（音乐准备就绪）
    public static final int EVENT_MUSIC_COMPLETION = 9; //事件（音乐播放完成）
    public static final int COMMAND_REFRESH = 10; //命令（刷新状态）

    public static final String VALUE_AUDIO_LENGTH = "AUDIO_LENGTH"; //值（语音长度）
    public static final String VALUE_AUDIO_POSITION = "AUDIO_POSITION"; //值（语音位置）
    public static final String VALUE_AUDIO_LEFT_VOLUME = "AUDIO_LEFT_VOLUME"; //值（语音左声道音量）
    public static final String VALUE_AUDIO_RIGHT_VOLUME = "AUDIO_RIGHT_VOLUME"; //值（语音右声道音量）
    public static final String VALUE_MUSIC_LEFT_VOLUME = "MUSIC_LEFT_VOLUME"; //值（音乐左声道音量）
    public static final String VALUE_MUSIC_RIGHT_VOLUME = "MUSIC_RIGHT_VOLUME"; //值（音乐右声道音量）
    public static final String VALUE_IS_PLAYING = "IS_PLAYING"; //值（是否正在播放）

    private final int MEDIA_PLAYER_NOTIFICATION_ID = 0x160124; //媒体消息ID
    private final int TIMER_PERIOD = 250; //定时器周期时间（毫秒）

    private Field m = new Field(); //私有字段

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerPhoneStateListener(); //注册电话监听器
        registerServiceReceiver(); //注册媒体广播接收器
        registerHeadsetPlugReceiver(); //注册耳机广播接收器
        refresh(); //刷新

        Log.e("Test", "BookMediaService.onCreate():书媒体服务已启动！");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopTimer(); //停止定时器

        //更新当前播放位置
        if (getBook().allowUpdateCurrentAudioPosition()) {
            getBook().updateCurrentAudioPosition(getAudioMediaPlayer().getPosition());
        }

        //释放语音媒体播放器
        if (m.audioMediaPlayer != null) {
            getAudioMediaPlayer().release();
        }

        //释放音乐媒体播放器
        if (m.musicMediaPlayer != null) {
            getMusicMediaPlayer().release();
        }

        unregisterHeadsetPlugReceiver(); //注销耳机广播接收器

        //注销服务端广播接收器
        if (m.serviceReceiver != null) {
            unregisterReceiver(m.serviceReceiver);
        }

        releaseMediaNotification(); //释放媒体消息

        Log.e("Test", "BookMediaService.onDestroy():书媒体服务已停止！");
    }

    /**
     * 是否电话状态暂停播放
     */
    private boolean isPhoneStatePause() {
        return m.isPhoneStatePause;
    }

    /**
     * 设置是否电话状态暂停播放
     */
    private void setIsPhoneStatePause(boolean value) {
        m.isPhoneStatePause = value;
    }

    /**
     * 获取全局书
     */
    private IBook getBook() {
        return BookManager.getBook(this);
    }

    /**
     * 获取全局选项
     */
    private IOption getOption() {
        return OptionManager.getOption(this);
    }

    /**
     * 获取语音媒体播放器
     */
    private IMediaPlayer getAudioMediaPlayer() {
        if (m.audioMediaPlayer == null) {
            IBookCatalog catalog = getBook().getCurrentAudioCatalog(); //获取当前语音内容

            //创建语音媒体播放器
            m.audioMediaPlayer = MediaPlayerFactory.createAudioMediaPlayer(
                    this, catalog.getAudioFilename());

            //移动到上次播放的位置
            m.audioMediaPlayer.seekTo(getBook().getCurrentAudioPosition());
        }

        return m.audioMediaPlayer;
    }

    /**
     * 获取音乐媒体播放器
     */
    private IMediaPlayer getMusicMediaPlayer() {
        if (getBook().getMediaType() == IBook.MEDIA_TYPE_AUDIO_AND_MUSIC &&
                m.musicMediaPlayer == null) {
            IBookMusic music = getBook().getCurrentMusic(); //获取当前音乐

            //创建音乐媒体播放器
            m.musicMediaPlayer =
                    MediaPlayerFactory.createMusicMediaPlayer(this, music.getMusicFilename());
        }

        return m.musicMediaPlayer;
    }

    /**
     * 播放
     */
    private void play() {
        getAudioMediaPlayer().play(); //播放语音

        //播放音乐
        if (getBook().getMediaType() == IBook.MEDIA_TYPE_AUDIO_AND_MUSIC &&
                !getMusicMediaPlayer().isPlaying()) {
            getMusicMediaPlayer().play();
        }

        showMediaNotification(); //显示媒体消息
        startTimer(); //启动定时器
        Log.e("Test", "BookMediaService.play():" + getBook().getCurrentAudioCatalog().getTitle() + "播放中...");
    }

    /**
     * 暂停播放
     */
    private void pause() {
        if (isPlaying()) {
            getAudioMediaPlayer().pause(); //暂停播放语音

            //暂停播放音乐
            if (getMusicMediaPlayer() != null &&
                    getMusicMediaPlayer().isPlaying()) {
                getMusicMediaPlayer().pause();
            }

            showMediaNotification(); //显示媒体消息
            stopTimer(); //停止定时器
            Log.e("Test", "暂停播放");
        }
    }

    /**
     * 是否正在播放
     * @return true=正在播放，false=没有播放
     */
    private boolean isPlaying() {
        return getAudioMediaPlayer().isPlaying();
    }

    /**
     * 定位播放位置
     * @param position 位置
     */
    private void seekTo(int position) {
        getAudioMediaPlayer().seekTo(position);
        Log.e("Test", "定位播放位置->" + position);
    }

    /**
     * 获取媒体长度
     * @return 媒体长度
     */
    private int getLength() {
        return getAudioMediaPlayer().getLength();
    }

    /**
     * 获取媒体当前位置
     * @return 媒体当前位置
     */
    private int getPosition() {
        return getAudioMediaPlayer().getPosition();
    }

    /**
     * 设置语音音量
     * @param leftVolume 左声道音量
     * @param rightVolume 右声道音量
     */
    private void setAudioVolume(float leftVolume, float rightVolume) {
        getAudioMediaPlayer().setVolume(leftVolume, rightVolume);
    }

    /**
     * 设置音乐音量
     * @param leftVolume 左声道音量
     * @param rightVolume 右声道音量
     */
    private void setMusicVolume(float leftVolume, float rightVolume) {
        if (getBook().getMediaType() == IBook.MEDIA_TYPE_AUDIO_AND_MUSIC) {
            getMusicMediaPlayer().setVolume(leftVolume, rightVolume);
        }
    }

    /**
     * 发送状态
     */
    private void refresh() {
        Intent intent = new Intent(getOption().getClientAction()); //实例化媒体服务端Intent

        //设置需要传递的状态信息参数
        intent.putExtra(BookMediaService.VALUE_IS_PLAYING, isPlaying()); //是否播放中
        intent.putExtra(BookMediaService.VALUE_AUDIO_LENGTH, getLength()); //语音长度
        intent.putExtra(BookMediaService.VALUE_AUDIO_POSITION, getPosition()); //语音位置

        sendBroadcast(intent); //发送广播

        if (m.audioMediaPlayer != null && isPlaying()) {
            showMediaNotification(); //显示通知消息
        }
    }

    /**
     * 启动定时器
     */
    private void startTimer() {
        if (m.timer == null) {
            m.timer = new Timer();
        }

        if (m.timerTask == null) {
            m.timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (m.audioMediaPlayer != null) {
                        refresh(); //发送状态
                    }
                }
            };
        }

        m.timer.schedule(m.timerTask, 0, TIMER_PERIOD);
    }

    /**
     * 停止定时器
     */
    private void stopTimer() {
        //停止定时器任务
        if (m.timerTask != null) {
            m.timerTask.cancel();
            m.timerTask = null;
        }

        //停止定时器
        if (m.timer != null) {
            m.timer.cancel();
            m.timer = null;
        }
    }

    /**
     * 显示媒体消息
     */
    public void showMediaNotification() {
        String CHANNEL_ID = getPackageName();
        String CHANNEL_NAME = "Channel " + getPackageName();
        NotificationChannel notificationChannel = null;

        //SDK >= 26 时才创建 NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setSound(null, null);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }

        //创建Intent
        Intent intent = new Intent(this, MainActivity.class);

        //创建PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //创建Builder
        NotificationCompat.Builder builder =new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher); //设置通知小图标
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)); //设置通知大图标
        builder.setContentIntent(pendingIntent); //设置通知内容Intent

        IBook book = getBook(); //获取有声书
        builder.setContentTitle(book.getCurrentAudioCatalog().getTitle()); //设置通知标题

        int position = getPosition(); //媒体播放位置
        if (position >= 0) {
            int length = getLength(); //媒体长度
            builder.setContentText(parseTime(position) + "/" + parseTime(length)); //设置通知内容
            builder.setProgress(length, position, false); //设置通知进度条
        }

        //显示为前台通知，避免服务被清除
        startForeground(MEDIA_PLAYER_NOTIFICATION_ID, builder.build());
    }

    /**
     * 解释为时间字符串
     * @param time 时间
     * @return 时间字符串
     */
    private String parseTime(long time) {
        //初始化Formatter的转换格式。
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.CHINA);
        return dateFormat.format(time);
    }

    /**
     * 释放媒体消息
     */
    public void releaseMediaNotification() {
        android.app.NotificationManager manager =
                (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(MEDIA_PLAYER_NOTIFICATION_ID);
    }

    /**
     * 注册电话状态监听器
     */
    private void registerPhoneStateListener() {
        if (m.telephonyManager == null) {
            m.telephonyManager = (TelephonyManager)getSystemService(Service.TELEPHONY_SERVICE); //获取电话管理器
            m.phoneStateListener = new CustomPhoneStateListener();
            m.telephonyManager.listen(m.phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    /**
     * 客户电话状态监听器
     */
    private class CustomPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: //电话挂断
                    if(isPhoneStatePause()) {
                        play(); //播放
                        setIsPhoneStatePause(false); //设置挂机后不播放
                        refresh();
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING: //电话响铃
                    if(isPlaying()) {
                        pause(); //暂停播放
                        setIsPhoneStatePause(true); //设置挂机后继续播放
                        refresh();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: //来电接通 或者 去电，去电接通  但是没法区分
                    if (isPlaying()) {
                        pause();
                        setIsPhoneStatePause(true); //设置挂机后继续播放
                        refresh();
                    }
                    break;
            }
        }
    }

    /**
     * 注册耳机广播接收器
     */
    private void registerHeadsetPlugReceiver() {
        m.headsetPlugReceiver = new HeadSetPlugReceiver();
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(m.headsetPlugReceiver, intentFilter);
    }

    /**
     * 注销耳机广播接收器
     */
    private void unregisterHeadsetPlugReceiver() {
        if (m.headsetPlugReceiver != null) {
            unregisterReceiver(m.headsetPlugReceiver);
        }
    }

    /**
     * 耳机广播接收器
     */
    private class HeadSetPlugReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
                if (isPlaying()) {
                    pause();
                    refresh();
                }
            }
        }
    }

    /**
     * 注册服务端广播接收器
     */
    private void registerServiceReceiver() {
        m.serviceReceiver = new ServiceBroadcastReceiver(); //实例化服务端广播接收器
        IntentFilter filter = new IntentFilter(getOption().getServiceAction()); //实例化InterFilter
        registerReceiver(m.serviceReceiver, filter); //注册广播接收器
    }

    /**
     * 媒体服务端广播接收器
     */
    private class ServiceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收广播
            Bundle bundle = intent.getExtras();
            int message = bundle.getInt(MESSAGE); //读取接收的消息
            int position; //语音位置
            float leftVolume; //左声道音量
            float rightVolume; //右声道音量

            switch(message) {
                case COMMAND_PLAY: //播放
                    //如果语音播放器正在播放的语音与书的当前语音不相同时，重置语音播放器的语音文件
                    if (getAudioMediaPlayer().getFilename() !=
                            getBook().getCurrentAudioCatalog().getAudioFilename()) {
                        stopTimer();
                        getAudioMediaPlayer().setFilename(getBook().getCurrentAudioCatalog().getAudioFilename()); //重置语音文件
                    }
                    play();
                    refresh();
                    break;
                case COMMAND_PAUSE: //暂停播放
                    pause();
                    refresh();
                    break;
                case COMMAND_SEEK_TO: //定位播放位置
                    position = bundle.getInt(VALUE_AUDIO_POSITION);
                    seekTo(position);
                    refresh();
                    break;
                case COMMAND_SET_AUDIO_VOLUME: //设置语音音量
                    leftVolume = bundle.getFloat(VALUE_AUDIO_LEFT_VOLUME);
                    rightVolume = bundle.getFloat(VALUE_AUDIO_RIGHT_VOLUME);
                    setAudioVolume(leftVolume, rightVolume);
                    break;
                case COMMAND_SET_MUSIC_VOLUME:
                    leftVolume = bundle.getFloat(VALUE_MUSIC_LEFT_VOLUME);
                    rightVolume = bundle.getFloat(VALUE_MUSIC_RIGHT_VOLUME);
                    setMusicVolume(leftVolume, rightVolume);
                    break;
                case COMMAND_REFRESH: //状态信息
                    //如果语音播放器正在播放的语音与书的当前语音不相同时，重置语音播放器的语音文件
                    if (getAudioMediaPlayer().getFilename() !=
                            getBook().getCurrentAudioCatalog().getAudioFilename()) {
                        getAudioMediaPlayer().setFilename(getBook().getCurrentAudioCatalog().getAudioFilename()); //重置语音文件
                        //如果充许更新当前语音位置时，设置初始的语音位置
                        if (getBook().allowUpdateCurrentAudioPosition()) {
                            seekTo(getBook().getCurrentAudioPosition()); //设置语音位置
                        }
                    }
                    refresh();
                    break;
                case EVENT_AUDIO_PREPARED: //语音准备就绪
                    refresh();
                    break;
                case EVENT_AUDIO_COMPLETION: //语音播放完成
                    stopTimer(); //停止定时器
                    getBook().moveToNextAudioCatalog(); //移动到一下条语音
                    getAudioMediaPlayer().setFilename(getBook().getCurrentAudioCatalog().getAudioFilename());
                    play();
                    break;
                case EVENT_MUSIC_PREPARED: //音乐准备就绪
                    refresh();
                    break;
                case EVENT_MUSIC_COMPLETION: //音乐播放完成
                    getBook().moveToNextMusic();
                    getMusicMediaPlayer().setFilename(getBook().getCurrentMusic().getMusicFilename());
                    getMusicMediaPlayer().play();
                    break;
            }
        }
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 电话管理器
         */
        TelephonyManager telephonyManager;

        /**
         * 电话状态监听器
         */
        PhoneStateListener phoneStateListener;

        /**
         * 挂机后是否继续播放
         */
        boolean isPhoneStatePause;

        /**
         * 耳机广播接收器
         */
        BroadcastReceiver headsetPlugReceiver;

        /**
         * 语音媒体播放器
         */
        IMediaPlayer audioMediaPlayer;

        /**
         * 音乐媒体播放器
         */
        IMediaPlayer musicMediaPlayer;

        /**
         * 服务端广播接收器
         */
        BroadcastReceiver serviceReceiver;

        /**
         * 定时器
         */
        Timer timer;

        /**
         * 定时器任务
         */
        TimerTask timerTask;
    }
}
