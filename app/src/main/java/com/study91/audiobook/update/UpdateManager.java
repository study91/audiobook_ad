package com.study91.audiobook.update;

import android.content.Context;
import android.util.Log;

/**
 * 更新管理器
 * @author 周传昕
 */
public class UpdateManager {
    /**
     * 本地
     */
    public static final int LOCATION_LOCAL = 1;

    /**
     * 远程
     */
    public static final int LOCATION_REMOTE = 2;

    /**
     * 创建更新管理器
     * @param context 应用程序上下文
     * @param location 位置
     * @return 更新管理器
     */
    public static IUpdate createUpdate(Context context, int location) {
        IUpdate updateManager = null;

        switch (location) {
            case LOCATION_LOCAL: //创建本地更新管理器
                updateManager = new LocalUpdate(context);
                break;
            case LOCATION_REMOTE: //创建远程更新管理器
                updateManager = new RemoteUpdate(context);
                break;
        }

        return updateManager;
    }
}