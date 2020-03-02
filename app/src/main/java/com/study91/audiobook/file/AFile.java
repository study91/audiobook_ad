package com.study91.audiobook.file;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件抽象类
 */
abstract class AFile implements IFile {
    @Override
    abstract public FileDescriptor getFileDescriptor();

    @Override
    abstract public InputStream getInputStream();

    @Override
    public void copyTo(String targetFilename) {
        //创建文件目录
        File file = new File(targetFilename);
        String path = file.getParent();

        //如果文件夹不存在，创建文件夹
        file = new File(path);
        if(!file.exists()) {
            file.mkdirs();
        }

        InputStream inputStream; //定义输入流
        OutputStream outputStream; //定义输出流

        try {
            inputStream = getInputStream(); //获取文件输入流
            outputStream = new FileOutputStream(targetFilename); //输出流

            byte[] buffer = new byte[8192];//定义缓冲区，缓冲区大小设置为8M
            int count;

            while((count = inputStream.read(buffer)) > 0){
                outputStream.write(buffer, 0, count);
            }

            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取文件输入流
     * @param filename 文件名（注：带完整路径的文件名）
     * @return 文件输入流
     */
    static protected FileInputStream getFileInputStream(String filename) {
        FileInputStream fileInputStream;

        File file = new File(filename);

        //获取文件输入流
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return fileInputStream;
    }
}
