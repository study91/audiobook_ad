package com.study91.audiobook.data;

import android.content.Context;

import com.study91.audiobook.config.ConfigFactory;
import com.study91.audiobook.config.IConfig;

/**
 * 数据工厂
 */
public class DataFactory {
    /**
     * 创建数据
     * @param context 应用程序上下文
     * @return 数据
     */
    public static IData createData(Context context) {
        IConfig config = ConfigFactory.getConfig(context); //获取全局配置
        return new SQLiteData(context, config.getDatabaseTarget()); //返回SQLite数据
    }
}
