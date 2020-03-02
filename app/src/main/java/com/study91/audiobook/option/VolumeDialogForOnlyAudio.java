package com.study91.audiobook.option;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.study91.audiobook.R;

/**
 * 音量对话框（只有语音）
 */
class VolumeDialogForOnlyAudio extends AVolumeDialog{
    /**
     * 构造器
     *
     * @param context    应用程序上下文
     * @param themeResId 主题ID
     */
    public VolumeDialogForOnlyAudio(Context context, int themeResId) {
        super(context, themeResId);

        //语音音量拖动条
        ui.audioVolumeSeekBar = (SeekBar)findViewById(R.id.audioVolumeSeekBar);
        ui.audioVolumeSeekBar.setProgress((int) (getOption().getAudioLeftVolume() * 100));
        ui.audioVolumeSeekBar.setOnSeekBarChangeListener(new OnAudioVolumeSeekBarChangeListener());

        //背景音乐布局
        ui.musicLayout = (LinearLayout)findViewById(R.id.musicLayout);
        ui.musicLayout.setVisibility(View.GONE); //不显示背景音乐布局
    }

    @Override
    protected void updateVolume() {
        //更新语音音量
        float audioVolume = ((float)ui.audioVolumeSeekBar.getProgress()) / 100;
        getOption().setAudioVolume(audioVolume, audioVolume);
    }

    /**
     * 设置语音音量
     */
    private void setAudioVolume() {
        float volume = ((float)ui.audioVolumeSeekBar.getProgress()) / 100;
        getMediaClient().setAudioVolume(volume, volume);
    }

    /**
     * 界面
     */
    private UI ui = new UI();

    /**
     * 界面类
     */
    private class UI {
        /**
         * 语音音量
         */
        SeekBar audioVolumeSeekBar;

        /**
         * 背景音乐布局
         */
        LinearLayout musicLayout;
    }

    /**
     * 语音音量拖动条改变事件监听器
     */
    private class OnAudioVolumeSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            setAudioVolume(); //设置语音音量
            setChanged(true); //设置音量有变化
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }
}