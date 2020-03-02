package com.study91.audiobook.file;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

/**
 * Assets资源文件
 */
class FileForAssets extends AFile {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param filename 文件名
     */
    public FileForAssets(Context context, String filename) {
        m.context = context; //应用程序上下文
        m.filename = filename; //文件名
    }

    @Override
    public FileDescriptor getFileDescriptor() {
        FileDescriptor fileDescriptor;

        AssetManager assetManager = getContext().getAssets();
        AssetFileDescriptor assetFileDescriptor;

        try {
            assetFileDescriptor = assetManager.openFd(getFilename());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fileDescriptor = assetFileDescriptor.getFileDescriptor();

        return fileDescriptor;
    }

    @Override
    public InputStream getInputStream() {
        InputStream inputStream;

        try {
            inputStream = getContext().getAssets().open(getFilename());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return inputStream;
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    private Context getContext() {
        return m.context;
    }

    /**
     * 获取文件名
     * @return 文件名
     */
    private String getFilename() {
        return m.filename;
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 应用程序上下文
         */
        private Context context;

        /**
         * 文件名
         */
        private String filename;
    }
}
