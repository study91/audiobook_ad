package com.study91.audiobook.update;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

/**
 * 更新抽象类
 */
abstract class AUpdate implements IUpdate{
    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public AUpdate(Context context) {
        m.context = context; //应用程序上下文
    }

    @Override
    abstract public void update(boolean isStarted);

    /**
     * 获取当前版本号
     * @return 当前版本号
     */
    protected int getCurrentVersionCode() {
        int versionCode;

        try {
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        return versionCode;
    }

    /**
     * 获取旧版本号
     * @return 旧版本号
     */
    protected int getOldVersionCode() {
        int lastVersionCode;

        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            lastVersionCode = preferences.getInt("VERSION_CODE", 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return  lastVersionCode;
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    protected Context getContext() {
        return m.context; //应用程序上下文
    }

    /**
     * 私有字段
     */
    private Field m = new Field();

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 应用程序上下文
         */
        Context context;
    }
}
