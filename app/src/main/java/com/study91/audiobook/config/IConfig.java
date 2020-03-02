package com.study91.audiobook.config;

/**
 * 配置接口
 */
public interface IConfig {
    /**
     * 获取书ID
     */
    int getBookID();

    /**
     * 获取配置ID
     */
    int getOptionID();

    /**
     * 获取源数据库
     */
    String getDatabaseSource();

    /**
     * 获取目标数据库
     */
    String getDatabaseTarget();

    /**
     * 是否测试软件
     */
    boolean isTest();

    /**
     * 更新
     */
    void refresh();
}
