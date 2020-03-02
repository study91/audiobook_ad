package com.study91.audiobook.config;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

/**
 * 配置测试
 */
public class ConfigTest extends ApplicationTestCase<Application> {
    /**
     * 构造器
     */
    public ConfigTest() {
        super(Application.class);
    }

    /**
     * 测试默认配置
     * @throws Exception 异常
     */
    public void testGetConfig() throws Exception {
        IConfig config = ConfigFactory.getConfig(getContext()); //获取全局配置
        Log.e("Test", "书ID：" + config.getBookID());
        Log.e("Test", "配置ID：" + config.getOptionID());
        Log.e("Test", "数据库源文件名：" + config.getDatabaseSource());
        Log.e("Test", "数据库目标文件名：" + config.getDatabaseTarget());
        Log.e("Test", "测试状态：" + config.isTest());
    }
}
