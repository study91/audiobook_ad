package com.study91.audiobook.data;

import android.database.Cursor;

/**
 * 数据接口
 */
public interface IData {
    /**
     * 查询数据
     * @param sql SQL语句
     * @return 数据指针
     */
    Cursor query(String sql);

    /**
     * 执行
     * @param sql SQL语句
     */
    void execute(String sql);

    /**
     * 关闭数据
     */
    void close();
}
