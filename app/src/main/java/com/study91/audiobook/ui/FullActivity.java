package com.study91.audiobook.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.study91.audiobook.R;
import com.study91.audiobook.ad.AdManager;
import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.BookMediaClient;
import com.study91.audiobook.book.BookMediaService;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;
import com.study91.audiobook.view.MediaPlayerView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 全屏窗口
 */
public class FullActivity extends Activity {
    private final int HIDE_TOOLBAR_MSG = 0x10052227; //隐藏工具条消息
    private final int HIDE_TOOLBAR_DELAY_TIME = 3000; //隐藏工具条时间（毫秒）
    private final int TIMER_PERIOD = 250; //定时器周期（毫秒）

    private Field m = new Field(); //私有字段
    private UI ui = new UI(); //界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); //设置为横屏显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //不关屏

        //载入界面控件
        ui.topLayout = (RelativeLayout) findViewById(R.id.topLayout); //顶部布局
        ui.fullLayout = (RelativeLayout) findViewById(R.id.fullLayout); //全屏布局
        ui.mediaPlayerView = (MediaPlayerView) findViewById(R.id.mediaPlayerView); //媒体播放器视图
        ui.backButton = (Button) findViewById(R.id.backButton); //返回按钮
        ui.smallFontButton = (Button) findViewById(R.id.smallFontButton); //缩小字体按钮
        ui.largeFontButton = (Button) findViewById(R.id.largeFontButton); //放大字体按钮

        //向全屏布局中添加内容视图页
        ui.originalTextView = (TextView) findViewById(R.id.originalTextView);
        ui.originalTextView.setTextSize(getOption().getFullFontSize());

        ui.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ui.smallFontButton.setOnClickListener(new OnSmallFontClickListener());
        ui.largeFontButton.setOnClickListener(new OnLargeFontClickListener());

        //添加广告
//        ui.adLayout = (RelativeLayout) findViewById(R.id.adLayout); //广告布局
//        ui.adLayout.addView(AdManager.getAd(this).getBannerView()); //添加横幅广告

        //隐藏媒体播放器全屏按钮
        Button fullScreenButton = (Button)ui.mediaPlayerView.findViewById(R.id.fullScreenButton);
        fullScreenButton.setVisibility(View.GONE);

        getMediaClient().register(); //注册媒体客户端
        startToolbarHandler(); //启动工具条处理器
    }

    @Override
    protected void onDestroy() {
        //注销媒体客户端
        if (m.mediaClient != null) {
            m.mediaClient.unregister();
        }

        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        setHideToolbarTime(HIDE_TOOLBAR_DELAY_TIME); //当发生任何触屏操作时，重置隐藏工具条时间为初始延迟时间
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return getGestureDetector().onTouchEvent(event);
    }

    /**
     * 设置是否正在播放
     * @param isPlaying 布尔值（true=正在播放，false=没有播放）
     */
    private void setIsPlaying(boolean isPlaying) {
        m.isPlaying = isPlaying;
    }

    /**
     * 是否正在播放
     * @return true=正在播放，false=没有播放
     */
    private boolean isPlaying() {
        return m.isPlaying;
    }

    /**
     * 设置是否启动定时器
     * @param isStartTimer 布尔值（true=已启动定时器，false=没有启动定时器）
     */
    private void setIsStartTimer(boolean isStartTimer) {
        m.isStartTimer = isStartTimer;
    }

    /**
     * 是否启动定时器
     * @return true=已启动定时器，false=没有启动定时器
     */
    private boolean isStartTimer() {
        return m.isStartTimer;
    }

    /**
     * 设置是否有工具条
     * @param isHasToolbar 布尔值（true=有工具条，false=没有工具条）
     */
    private void setIsHasToolbar(boolean isHasToolbar) {
        m.isHasToolbar = isHasToolbar;
    }

    /**
     * 是否有工具条
     * @return true=有工具条，false=没有工具条
     */
    private boolean isHasToolbar() {
        return m.isHasToolbar;
    }

    /**
     * 设置隐藏工具条时间（以毫秒为单位）
     * @param time 时间（以毫秒为单位）
     */
    private void setHideToolbarTime(int time) {
        m.hideToolbarTime = time;
    }

    /**
     * 隐藏工具条时间（以毫秒为单位）
     * @return 时间（以毫秒为单位）
     */
    private int hideToolbarTime() {
        return m.hideToolbarTime;
    }

    /**
     * 启动工具条处理器
     */
    private void startToolbarHandler() {
        m.toolbarHandler = new ToolbarHandler();
    }

    /**
     * 获取工具条处理器
     * @return 工具条处理器
     */
    private Handler getToolbarHandler() {
        return m.toolbarHandler;
    }

    /**
     * 启动定时器
     */
    private void startToolbarTimer() {
        if (!isStartTimer()) {
            if (m.toolbarTimer == null) {
                m.toolbarTimer = new Timer();
            }

            if (m.toolbarTimerTask == null) {
                m.toolbarTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (hideToolbarTime() <= 0) {
                            getToolbarHandler().sendEmptyMessage(HIDE_TOOLBAR_MSG); //发送隐藏工具条消息
                        }

                        setHideToolbarTime(hideToolbarTime() - TIMER_PERIOD); //重置隐藏工具条时间
                    }
                };
            }

            setHideToolbarTime(HIDE_TOOLBAR_DELAY_TIME); //初始化隐藏工具条时间
            m.toolbarTimer.schedule(m.toolbarTimerTask, 0, TIMER_PERIOD);

            setIsStartTimer(true); //设置为已启动定时器
        }
    }

    /**
     * 停止定时器
     */
    private void stopToolbarTimer() {
        if (isStartTimer()) {
            //停止定时器任务
            if (m.toolbarTimerTask != null) {
                m.toolbarTimerTask.cancel();
                m.toolbarTimerTask = null;
            }

            //停止定时器
            if (m.toolbarTimer != null) {
                m.toolbarTimer.cancel();
                m.toolbarTimer = null;
            }

            setIsStartTimer(false); //设置为没有启动定时器
        }
    }

    /**
     * 设置工具条
     * @param value ture=有工具条，false=没有工具条
     */
    private void setToolbar(boolean value) {
        if (value) { //设置为有工具条
            ui.topLayout.setVisibility(View.VISIBLE); //显示工具条
            ui.mediaPlayerView.setVisibility(View.VISIBLE); //显示工具条
        } else { //设置为没有工具条
            ui.topLayout.setVisibility(View.GONE); //隐藏顶部工具条
            ui.mediaPlayerView.setVisibility(View.GONE); //隐藏底部工具条
        }

        setIsHasToolbar(value); //设置是否有工具条
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
     * 获取手势监听器
     * @return 手势监听器
     */
    private GestureDetector getGestureDetector() {
        if (m.gestureDetector == null) {
            m.gestureDetector = new GestureDetector(this, new OnContentViewGestureListener());
        }

        return m.gestureDetector;
    }

    /**
     * 获取媒体客户端
     * @return 媒体客户端
     */
    private BookMediaClient getMediaClient() {
        if (m.mediaClient == null) {
            m.mediaClient = new MediaClient(this);
        }

        return m.mediaClient;
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 是否有工具条
         */
        boolean isHasToolbar = true;

        /**
         * 是否启动定时器
         */
        boolean isStartTimer;

        /**
         * 是否正在播放
         */
        boolean isPlaying;

        /**
         * 工具条处理器
         */
        Handler toolbarHandler;

        /**
         * 工具条定时器
         */
        Timer toolbarTimer;

        /**
         * 工具条定时器任务
         */
        TimerTask toolbarTimerTask;

        /**
         * 隐藏工具条时间
         */
        int hideToolbarTime;

        /**
         * 媒体客户端
         */
        BookMediaClient mediaClient;

        /**
         * 手势检测器
         */
        GestureDetector gestureDetector;
    }

    /**
     * 私有界面类
     */
    private class UI {
        /**
         * 顶部布局
         */
        RelativeLayout topLayout;

        /**
         * 原文文本框
         */
        TextView originalTextView;

        /**
         * 全屏布局
         */
        RelativeLayout fullLayout;

        /**
         * 广告布局
         */
        RelativeLayout adLayout;

        /**
         * 返回按钮
         */
        Button backButton;

        /**
         * 缩小字体按钮
         */
        Button smallFontButton;

        /**
         * 放大字体按钮
         */
        Button largeFontButton;

        /**
         * 媒体播放器视图
         */
        MediaPlayerView mediaPlayerView;
    }

    /**
     * 媒体客户端
     */
    private class MediaClient extends BookMediaClient {
        /**
         * 构造器
         * @param context 应用程序上下文
         */
        public MediaClient(Context context) {
            super(context);
        }

        @Override
        public void setOnReceive(Intent intent) {
            String original = getBook().getCurrentAudioCatalog().getOriginal();
            ui.originalTextView.setText(original);
            ui.originalTextView.setTextSize(getOption().getFullFontSize());

            boolean isPlaying = intent.getExtras().getBoolean(BookMediaService.VALUE_IS_PLAYING); //获取是否正在播放
            setIsPlaying(isPlaying); //重置是否正在播放值

            if (ui.mediaPlayerView.catalogShowing()) { //目录正在显示
                stopToolbarTimer(); //停止工具条定时器
            } else { //目录没有显示
                if (isHasToolbar()) { //有工具条
                    if (isPlaying) { //正在播放
                        startToolbarTimer(); //启动工具条定时器
                    } else { //没有播放
                        stopToolbarTimer(); //停止工具条定时器
                    }
                } else { //没有工具条
                    stopToolbarTimer(); //停止工具条定时器
                }
            }
        }
    }

    /**
     * 隐藏工具条处理器
     */
    private class ToolbarHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HIDE_TOOLBAR_MSG: //隐藏工具条
                    setToolbar(false); //隐藏工具条
                    break;
            }
        }
    }

    /**
     * 内容视图手势监听器
     */
    private class OnContentViewGestureListener implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            setToolbar(!isHasToolbar()); //设置工具条
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            IBook book = getBook(); //获取全局书
            boolean isPlaying = isPlaying(); //获取是否正在播放值

            //手势左右滑动时，切换正面闪卡
            if(e1.getX() - e2.getX() > 50) { //手势从右向左滑动
                book.moveToNextAudioCatalog(); //移动到下一个语音
                getMediaClient().play(); //播放语音
                if (!isPlaying) getMediaClient().pause(); //如果不是播放状态，暂停播放
            } else if (e2.getX() - e1.getX() > 50) { //手势从左向右滑动
                book.moveToPreviousAudioCatalog(); //移动到上一个语音
                getMediaClient().play(); //播放语音
                if (!isPlaying) getMediaClient().pause(); //如果不是播放状态，暂停播放
            } else if (e1.getY() - e2.getY() > 50) { //手势从上向下滑动
                Log.e("Test", "手势从下向上滑动");
            } else if (e2.getY() - e1.getY() > 50) { //手势从下往上滑动
                Log.e("Test", "手势从上向下滑动");
            }

            return false;
        }
    }

    /**
     * 内容触屏事件监听器
     */
    private class OnContentTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return getGestureDetector().onTouchEvent(event);
        }
    }

    /**
     * 缩小字体按钮单击事件监听器
     */
    private class OnSmallFontClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int fontSize = getOption().getFullFontSize() - 2;
            if (fontSize > 0) {
                getOption().setFullFontSize(fontSize);
                ui.originalTextView.setTextSize(fontSize);
            }
        }
    }

    /**
     * 放大字体按钮单击事件监听器
     */
    private class OnLargeFontClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int fontSize = getOption().getFullFontSize() + 2;
            if (fontSize > 0) {
                getOption().setFullFontSize(fontSize);
                ui.originalTextView.setTextSize(fontSize);
            }
        }
    }
}
