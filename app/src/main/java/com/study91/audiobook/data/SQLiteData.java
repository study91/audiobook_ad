package com.study91.audiobook.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.study91.audiobook.config.ConfigFactory;
import com.study91.audiobook.config.IConfig;
import com.study91.audiobook.file.FileFactory;
import com.study91.audiobook.file.IFile;

import java.io.File;

/**
 * SQLite数据
 */
class SQLiteData implements IData {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param databaseFilename 数据库文件名
     */
    public SQLiteData(Context context, String databaseFilename) {
        m.context = context; //应用程序上下文
        m.databaseFilename = databaseFilename; //数据库文件名
    }

    @Override
    public Cursor query(String sql) {
        return getSqliteDatabase().rawQuery(sql, null);
    }

    @Override
    public void execute(String sql) {
        getSqliteDatabase().execSQL(sql);
    }

    @Override
    public void close() {
        //如果数据库不等于null，关闭数据库
        if(m.sqliteDatabase != null) {
            m.sqliteDatabase.close();
        }
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    private Context getContext() {
        return m.context;
    }

    /**
     * 获取数据库文件名
     * @return 数据库文件名
     */
    private String getDatabaseFilename() {
        return m.databaseFilename;
    }

    /**
     * 获取SQlite数据库
     * @return SQlite数据库
     */
    private SQLiteDatabase getSqliteDatabase() {
        if (m.sqliteDatabase == null) {
            File file = new File(getDatabaseFilename()); //实例化数据库文件对象

            //如果数据库文件不存在，从Assets资源复制数据库
            if (!file.exists()) {
                copyDatabaseFileFromAssets(); //从Assets资源复制数据库文件
            }

            //打开数据库
            m.sqliteDatabase = SQLiteDatabase.openDatabase(
                    getDatabaseFilename(),
                    null,
                    SQLiteDatabase.OPEN_READWRITE);
        }

        return m.sqliteDatabase;
    }

    /**
     * 从Assets资源复制数据库文件
     */
    private void copyDatabaseFileFromAssets() {
        //获取配置文件
        IConfig config = ConfigFactory.getConfig(getContext());

        //获取存储在assets资源中的原始配置文件
        IFile assetsDatabaseFile = FileFactory.createFile(
                getContext(),
                IFile.STORAGE_TYPE_ASSETS,
                config.getDatabaseSource());

        //复制原始配置文件到目标资源文件
        assetsDatabaseFile.copyTo(config.getDatabaseTarget());
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
         * 数据库文件名
         */
        private String databaseFilename;

        /**
         * SQLite数据库
         */
        private SQLiteDatabase sqliteDatabase;
    }
}
