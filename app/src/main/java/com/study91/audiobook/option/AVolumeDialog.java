package com.study91.audiobook.option;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.WindowManager;

import com.study91.audiobook.R;
import com.study91.audiobook.book.BookMediaClient;

/**
 * 音量对话框抽象类
 */
abstract class AVolumeDialog extends Dialog{
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param themeResId 主题ID
     */
    public AVolumeDialog(Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.volume_dialog); //加载对话框布局
        setAttributes(); //设置对话框属性
        getMediaClient().register(); //注册媒体客户端
    }

    @Override
    protected void onStop() {
        super.onStop();

        //如果音量有变化，更新音量设置
        if(hasChanged()) {
            updateVolume(); //更新音量设置
        }

        if (m.mediaClient != null) {
            getMediaClient().unregister(); //注销媒体客户端
        }
    }

    /**
     * 更新音量设置
     */
    protected abstract void updateVolume();

    /**
     * 设置对话框属性
     */
    private void setAttributes() {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes(); //获取窗口属性

        DisplayMetrics displayMetrics = new DisplayMetrics(); //获取显示
        WindowManager windowManager = getWindow().getWindowManager(); //获取窗口管理器
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int rotation = windowManager.getDefaultDisplay().getRotation(); //获取屏幕方向
        switch(rotation) {
            case Surface.ROTATION_0: //竖屏
                layoutParams.width = (int)(displayMetrics.widthPixels * 0.8);
                break;
            case Surface.ROTATION_90: //横屏
                layoutParams.width = (int)(displayMetrics.heightPixels * 0.8);
                break;
            case Surface.ROTATION_180: //竖屏
                layoutParams.width = (int)(displayMetrics.widthPixels * 0.8);
                break;
            case Surface.ROTATION_270: //横屏
                layoutParams.width = (int)(displayMetrics.heightPixels * 0.8);
                break;
        }

        getWindow().setAttributes(layoutParams);
    }

    /**
     * 获取媒体客户端
     * @return 媒体客户端
     */
    protected BookMediaClient getMediaClient() {
        if (m.mediaClient == null) {
            m.mediaClient = new BookMediaClient(getContext()) {
                @Override
                public void setOnReceive(Intent intent) {

                }
            };
        }

        return m.mediaClient;
    }

    /**
     * 获取全局选项
     * @return 全局选项
     */
    protected IOption getOption() {
        return OptionManager.getOption(getContext());
    }

    /**
     * 设置音量是否改变值
     * @param value 改变值
     */
    protected void setChanged(boolean value) {
        m.changed = value;
    }

    /**
     * 音量是否有变化
     * @return true=有变化 false=无变化
     */
    protected boolean hasChanged() {
        return m.changed;
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 媒体客户端
         */
        BookMediaClient mediaClient;

        /**
         * 是否改变
         */
        boolean changed = false;
    }
}