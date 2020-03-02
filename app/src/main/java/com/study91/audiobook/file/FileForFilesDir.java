package com.study91.audiobook.file;

import android.content.Context;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * FilesDir文件
 */
class FileForFilesDir extends AFile {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param filename 文件名
     */
    public FileForFilesDir(Context context, String filename) {
        m.context = context; //应用程序上下文
        setFilename(filename); //文件名
    }

    @Override
    public FileDescriptor getFileDescriptor() {
        FileDescriptor fileDescriptor;

        //获取文件输入流
        FileInputStream fileInputStream = AFile.getFileInputStream(getFilename());

        try {
            fileDescriptor = fileInputStream.getFD();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileDescriptor;
    }

    @Override
    public InputStream getInputStream() {
        return AFile.getFileInputStream(getFilename());
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    private Context getContext() {
        return m.context;
    }

    /**
     * 设置文件名
     * @param filename 文件名
     */
    private void setFilename(String filename) {
        m.filename =
                getContext().getFilesDir().getAbsolutePath() +
                File.separator +filename;
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
