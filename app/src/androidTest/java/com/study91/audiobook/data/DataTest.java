package com.study91.audiobook.data;

import android.app.Application;
import android.database.Cursor;
import android.test.ApplicationTestCase;
import android.util.Log;

/**
 * 数据测试
 */
public class DataTest extends ApplicationTestCase<Application> {
    /**
     * 构造器
     */
    public DataTest() {
        super(Application.class);
    }

    /**
     * 测试SQLite数据
     */
    public void testSQLiteData() {
        IData data = DataFactory.createData(getContext());
        String sql = "SELECT * FROM [BookContent] WHERE [ContentID] = 20";
        Cursor cursor = data.query(sql);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            Log.e("Test", "内容ID：" + cursor.getInt(cursor.getColumnIndex("ContentID")));
            Log.e("Test", "语音文件名：" + cursor.getString(cursor.getColumnIndex("AudioFilename")));
        }
    }
}
