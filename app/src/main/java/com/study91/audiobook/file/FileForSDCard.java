package com.study91.audiobook.file;

import android.os.Environment;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * SD卡文件
 */
class FileForSDCard extends AFile{
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param filename 文件名
     */
    public FileForSDCard(String filename) {
        setFilename(filename); //设置文件名
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
     * 设置文件名
     * @param filename 文件名
     */
    public void setFilename(String filename) {
        m.filename = Environment.getExternalStorageDirectory().getPath() +
                File.separator +
                filename;
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
         * 文件名
         */
        private String filename;
    }
}
