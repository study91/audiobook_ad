package com.study91.audiobook.option;

import android.content.Context;
import android.widget.SeekBar;

import com.study91.audiobook.R;

/**
 * 音量对话框（语音左声道，音乐右声道）
 */
class VolumeDialogForAudioRight extends AVolumeDialog{
    /**
     * 构造器
     * @param context    应用程序上下文
     * @param themeResId 主题ID
     */
    public VolumeDialogForAudioRight(Context context, int themeResId) {
        super(context, themeResId);

        //语音音量拖动条
        ui.audioVolumeSeekBar = (SeekBar)findViewById(R.id.audioVolumeSeekBar);
        ui.audioVolumeSeekBar.setProgress((int)(getOption().getAudioRightVolume() * 100));
        ui.audioVolumeSeekBar.setOnSeekBarChangeListener(new AudioVolumeOnSeekBarChangeListener());

        //背景音乐音量拖动条
        ui.musicVolumeSeekBar = (SeekBar)findViewById(R.id.musicVolumeSeekBar);
        ui.musicVolumeSeekBar.setProgress((int)(getOption().getAudioLeftVolume() * 100));
        ui.musicVolumeSeekBar.setOnSeekBarChangeListener(new MusicVolumeOnSeekBarChangeListener());
    }

    @Override
    protected void updateVolume() {
        //更新语音音量
        float rightVolume = ((float)ui.audioVolumeSeekBar.getProgress()) / 100;
        float leftVolume = ((float)ui.musicVolumeSeekBar.getProgress()) / 100;
        getOption().setAudioVolume(leftVolume, rightVolume);
    }

    /**
     * 设置音量
     */
    private void setVolume() {
        float rightVolume = ((float)ui.audioVolumeSeekBar.getProgress()) / 100;
        float leftVolume = ((float)ui.musicVolumeSeekBar.getProgress()) / 100;
        getMediaClient().setAudioVolume(leftVolume, rightVolume);
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
         * 背景音乐音量
         */
        SeekBar musicVolumeSeekBar;
    }

    /**
     * 语音音量拖动条改变事件监听器
     */
    private class AudioVolumeOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            setVolume(); //设置音量
            setChanged(true); //设置音量有变化
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    /**
     * 背景音乐音量拖动条改变事件监听器
     */
    private class MusicVolumeOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            setVolume(); //设置音量
            setChanged(true); //设置音量有变化
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }
}