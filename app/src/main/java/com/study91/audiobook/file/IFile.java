package com.study91.audiobook.file;

import java.io.FileDescriptor;
import java.io.InputStream;

/**
 * 文件接口
 */
public interface IFile {

    /**
     * 存储类型（assets资源）
     */
    public static final int STORAGE_TYPE_ASSETS = 1;

    /**
     * 存储类型（FilesDir目录资源）
     */
    public static final int STORAGE_TYPE_FILESDIR = 2;

    /**
     * 存储类型（SDCard资源）
     */
    public static final int STORAGE_TYPE_SDCARD = 3;

    /**
     * 获取文件描述
     * @return 文件描述
     */
    FileDescriptor getFileDescriptor();

    /**
     * 获取输入流
     * @return 输入流
     */
    InputStream getInputStream();

    /**
     * 复制文件到目标文件
     * @param targetFilename 目标文件名
     */
    void copyTo(String targetFilename);
}
