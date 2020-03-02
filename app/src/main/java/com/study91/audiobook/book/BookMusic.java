package com.study91.audiobook.book;

import android.content.Context;
import android.database.Cursor;

import com.study91.audiobook.data.DataFactory;
import com.study91.audiobook.data.IData;
import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;

/**
 * 音乐
 */
class BookMusic implements IBookMusic {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param musicID 音乐ID
     */
    public BookMusic(Context context, int musicID) {
        m.context = context;
        load(musicID); //载入
    }

    @Override
    public int getMusicID() {
        return m.musicID;
    }

    @Override
    public int getBookID() {
        return m.bookID;
    }

    @Override
    public int getIndex() {
        return m.index;
    }

    @Override
    public String getMusicName() {
        return m.musicName;
    }

    @Override
    public String getMusicFilename() {
        return m.musicFilename;
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    private Context getContext() {
        return m.context;
    }

    /**
     * 载入数据
     * @param musicID 音乐ID
     */
    private void load(int musicID) {
        IData data = null;
        Cursor cursor = null;

        try{
            data = DataFactory.createData(getContext()); //创建数据对象
            String sql = "SELECT * FROM [BookMusic] WHERE [MusicID] = " + musicID; //查询字符串
            cursor = data.query(sql); //查询数据

            if(cursor.getCount() == 1) {
                cursor.moveToFirst(); //移动到首记录
                m.musicID = cursor.getInt(cursor.getColumnIndex("MusicID")); //音乐ID
                m.bookID = cursor.getInt(cursor.getColumnIndex("BookID")); //书ID
                m.index = cursor.getInt(cursor.getColumnIndex("Index")); //音乐索引
                m.musicName = cursor.getString(cursor.getColumnIndex("MusicName")); //音乐名称
                setMusicFilename(cursor.getString(cursor.getColumnIndex("MusicFilename"))); //音乐文件名
            }
        } finally {
            if(cursor != null) cursor.close(); //关闭数据指针
            if(data != null) data.close(); //关闭数据对象
        }
    }

    /**
     * 获取全局选项
     * @return 选项
     */
    private IOption getOption() {
        return OptionManager.getOption(getContext());
    }

    /**
     * 设置音乐文件名
     * @param filename 文件名
     */
    private void setMusicFilename(String filename) {
        if (filename != null) {
            m.musicFilename = getOption().getResourcePath() + filename;
        } else {
            m.musicFilename = null;
        }
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 应用程序上下文
         */
        Context context;

        /**
         * 音乐ID
         */
        int musicID;

        /**
         * 书ID
         */
        int bookID;

        /**
         * 音乐索引
         */
        int index;

        /**
         * 音乐名称
         */
        String musicName;

        /**
         * 音乐文件名
         */
        String musicFilename;
    }
}
