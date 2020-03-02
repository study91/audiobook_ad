package com.study91.audiobook.update;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.study91.audiobook.R;
import com.study91.audiobook.config.ConfigFactory;
import com.study91.audiobook.config.IConfig;
import com.study91.audiobook.file.FileFactory;
import com.study91.audiobook.file.IFile;
import com.study91.audiobook.option.OptionManager;

import java.io.File;

/**
 * 本地更新
 * @version 2
 */
class LocalUpdate extends AUpdate {
    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public LocalUpdate(Context context) {
        super(context);
    }

    @Override
    public void update(boolean isStarted) {
        int currentVersionCode = getCurrentVersionCode(); //获取当前版本号
        int oldVersionCode = getOldVersionCode(); //获取最后一次使用的版本号

        Log.e("Test", "LocalUpdate.update():" +
                "旧版本=" + oldVersionCode + "," +
                "当前版本=" + currentVersionCode);

        if(currentVersionCode != oldVersionCode) {
            copyConfigFile(); //复制配置文件
            copyDatabaseFile(); //复制数据库文件
            getConfig().refresh(); //刷新全局配置

            //存储最后使用版本为当前版本
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("VERSION_CODE", currentVersionCode);
            editor.apply();
        }

        Log.e("Test", "LocalUpdate.update():" +
                "选项名称=" + OptionManager.getOption(getContext()).getOptionName());
    }

    /**
     * 复制配置文件
     */
    private void copyConfigFile() {
        //Assets资源的配置文件名
        String assetsConfigFilename =
                getContext().getResources().getString(R.string.assets_config_filename);

        //目标配置文件名
        String configFilename =
                getContext().getFilesDir().getAbsolutePath() + File.separator + assetsConfigFilename;

        //创建Assets配置文件对象
        IFile assetsConfigFile = FileFactory.createFile(
                getContext(),
                IFile.STORAGE_TYPE_ASSETS,
                assetsConfigFilename);

        //复制Assets配置文件到目标配置文件
        assetsConfigFile.copyTo(configFilename);
    }

    /**
     * 复制数据库文件
     */
    private void copyDatabaseFile() {
        IConfig config = getConfig(); //获取全局配置

        //创建数据库源文件对象
        IFile databaseSourceFile = FileFactory.createFile(
                getContext(),
                IFile.STORAGE_TYPE_ASSETS,
                config.getDatabaseSource());

        //复制数据库源文件到数据库目标文件
        databaseSourceFile.copyTo(config.getDatabaseTarget());
    }

    /**
     * 获取全局配置
     * @return 全局配置
     */
    private IConfig getConfig() {
        return ConfigFactory.getConfig(getContext()); //获取全局配置
    }
}
