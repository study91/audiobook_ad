package com.study91.audiobook.config;

import android.content.Context;

/**
 * 配置工厂
 */
public class ConfigFactory {
    /**
     * 获取全局配置
     * @param context 应用程序上下文
     * @return 全局配置
     */
    public static IConfig getConfig(Context context) {
        return DefaultConfig.instance(context); //返回默认配置
    }
}
