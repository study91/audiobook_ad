package com.study91.audiobook.option;

import android.content.Context;
import android.widget.SeekBar;

import com.study91.audiobook.R;

/**
 * 音量对话框（有语音和音乐）
 */
class VolumeDialogForAudioAndMusic extends AVolumeDialog {
    /**
     * 构造器
     * @param context    应用程序上下文
     * @param themeResId 主题ID
     */
    public VolumeDialogForAudioAndMusic(Context context, int themeResId) {
        super(context, themeResId);

        //语音音量拖动条
        ui.audioVolumeSeekBar = (SeekBar)findViewById(R.id.audioVolumeSeekBar);
        ui.audioVolumeSeekBar.setProgress((int) (getOption().getAudioLeftVolume() * 100));
        ui.audioVolumeSeekBar.setOnSeekBarChangeListener(new OnAudioVolumeSeekBarChangeListener());

        //背景音乐音量拖动条
        ui.musicVolumeSeekBar = (SeekBar)findViewById(R.id.musicVolumeSeekBar);
        ui.musicVolumeSeekBar.setProgress((int) (getOption().getMusicLeftVolume() * 100));
        ui.musicVolumeSeekBar.setOnSeekBarChangeListener(new OnMusicVolumeSeekBarChangeListener());
    }

    @Override
    protected void updateVolume() {
        //更新语音音量
        float audioVolume = ((float)ui.audioVolumeSeekBar.getProgress()) / 100;
        getOption().setAudioVolume(audioVolume, audioVolume);

        float musicVolume = ((float)ui.musicVolumeSeekBar.getProgress()) / 100;
        getOption().setMusicVolume(musicVolume, musicVolume);
    }

    /**
     * 设置语音音量
     */
    private void setAudioVolume() {
        float volume = ((float)ui.audioVolumeSeekBar.getProgress()) / 100;
        getMediaClient().setAudioVolume(volume, volume);
    }

    /**
     * 设置背景音乐音量
     */
    private void setMusicVolume() {
        float volume = ((float)ui.musicVolumeSeekBar.getProgress()) / 100;
        getMediaClient().setMusicVolume(volume, volume);
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

    /**
     * 背景音乐音量拖动条改变事件监听器
     */
    private class OnMusicVolumeSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            setMusicVolume(); //设置背景音乐音量
            setChanged(true); //设置音量有变化
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }
}
