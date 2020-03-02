package com.study91.audiobook.file;

import android.content.Context;

/**
 * 文件工厂
 */
public class FileFactory {
    /**
     * 创建文件
     * @param context 应用程序上下文
     * @param storageType 存储类型（1=assets资源,2=应用文件目录,3=SD卡）
     * @param filename 文件名
     * @return 文件
     */
    public static IFile createFile(Context context, int storageType, String filename) {
        IFile file = null;

        switch (storageType) {
            case IFile.STORAGE_TYPE_ASSETS: //assets资源文件
                file = new FileForAssets(context, filename);
                break;
            case IFile.STORAGE_TYPE_FILESDIR: //FilesDir目录中的文件
                file = new FileForFilesDir(context, filename);
                break;
            case IFile.STORAGE_TYPE_SDCARD: //SDCard中的文件
                file = new FileForSDCard(filename);
                break;
        }
        return file;
    }

    /**
     * 创建Xml文件
     * @param url Xml文件Url地址
     */
    public static IXmlFile createXmlFile(String url) {
        return new XmlFile(url);
    }
}
