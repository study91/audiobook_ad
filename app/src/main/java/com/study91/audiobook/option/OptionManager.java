package com.study91.audiobook.option;

import android.app.Dialog;
import android.content.Context;

import com.study91.audiobook.R;
import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBook;

/**
 * 选项管理器
 */
public class OptionManager {
    /**
     * 获取选项
     * @param context 应用程序上下文
     * @return 选项
     */
    public static IOption getOption(Context context) {
        return DefaultOption.instance(context); //返回默认选项
    }

    /**
     * 显示音量对话框
     * @param context 应用程序上下文
     */
    public static void showVolumeDialog(Context context) {
        Dialog dialog = null;

        IBook book = BookManager.getBook(context); //获取有声书

        switch(book.getMediaType()) {
            case IBook.MEDIA_TYPE_AUDIO_AND_MUSIC: //有语音和背景音乐
                dialog = new VolumeDialogForAudioAndMusic(context, R.style.VolumeDialogTheme);
                break;
            case IBook.MEDIA_TYPE_ONLY_AUDIO: //只有语音
                dialog = new VolumeDialogForOnlyAudio(context, R.style.VolumeDialogTheme);
                break;
            case IBook.MEDIA_TYPE_AUDIO_LEFT: //语音左声道，音乐右声道
                dialog = new VolumeDialogForAudioLeft(context, R.style.VolumeDialogTheme);
                break;
            case IBook.MEDIA_TYPE_AUDIO_RIGHT: //语音右声道，音乐左声道
                dialog = new VolumeDialogForAudioRight(context, R.style.VolumeDialogTheme);
                break;
        }

        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }
    }
}
