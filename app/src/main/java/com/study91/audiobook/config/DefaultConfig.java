package com.study91.audiobook.config;

import android.content.Context;

import com.study91.audiobook.R;
import com.study91.audiobook.file.FileFactory;
import com.study91.audiobook.file.IFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 默认配置
 */
class DefaultConfig implements IConfig {
    private static IConfig config; //全局配置
    private Field m = new Field(); //私有字段

    /**
     * 实例化配置
     * 注：使用单例模式保证只实例化一个全局配置
     * @param context 应用程序上下文
     * @return 配置
     */
    public static IConfig instance(Context context) {
        if (config == null) {
            config = new DefaultConfig(context);
        }

        return config;
    }

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    private DefaultConfig(Context context) {
        m.context = context;
    }

    @Override
    public int getBookID() {
        return Integer.parseInt(getConfigValue("BookID"));
    }

    @Override
    public int getOptionID() {
        return Integer.parseInt(getConfigValue("OptionID"));
    }

    @Override
    public String getDatabaseSource() {
        return getConfigValue("DatabaseSource");
    }

    @Override
    public String getDatabaseTarget() {
        String databaseFilename = getConfigValue("DatabaseTarget"); //从配置文件读取数据库文件名
        return getContext().getDatabasePath(databaseFilename).getAbsolutePath(); //带完整路径的数据库文件名
    }

    @Override
    public boolean isTest() {
        return Boolean.parseBoolean(getConfigValue("Test"));
    }

    @Override
    public void refresh() {
        m.properties = null;
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    private Context getContext() {
        return m.context;
    }

    /**
     * 获取存储在Assets资源中的原始配置文件名
     * @return 配置文件名
     */
    private String getConfigFilename() {
        String path = getContext().getFilesDir().getAbsolutePath() + File.separator;
        return path + getAssetsConfigFilename();
    }

    /**
     * 获取Assets资源配置文件名
     * @return Assets资源配置文件名
     */
    private String getAssetsConfigFilename() {
        return getContext().getResources().getString(R.string.assets_config_filename);
    }

    /**
     * 获取属性
     * @return 属性
     */
    private Properties getProperties() {
        if (m.properties == null) {
            File configFile = new File(getConfigFilename()); //实例化原始配置文件

            //如果配置文件不存在，将Assets资源中的配置文件复制到Files目录中
            if (!configFile.exists()) {
                copyConfigFileFromAssets(); //从Assets复制配置文件
            }

            //创建配置文件
            IFile filesConfigFile = FileFactory.createFile(
                    getContext(),
                    IFile.STORAGE_TYPE_FILESDIR,
                    getAssetsConfigFilename());

            InputStream inputStream = filesConfigFile.getInputStream(); //获取输入流
            m.properties = new Properties();

            try {
                m.properties.load(inputStream); //载入配置
                inputStream.close(); //关闭输入流
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return m.properties;
    }

    /**
     * 从Assets复制配置文件
     */
    private void copyConfigFileFromAssets() {
        //实例化配置文件
        IFile assetsConfigFile = FileFactory.createFile(
                getContext(),
                IFile.STORAGE_TYPE_ASSETS,
                getAssetsConfigFilename());

        //复制Assets资源中的配置文件到实际配置文件目录中
        assetsConfigFile.copyTo(getConfigFilename());
    }

    /**
     * 获取配置文件中的字符串值
     * @param key 键
     * @return 字符串
     */
    private String getConfigValue(String key) {
        return getProperties().getProperty(key).trim();
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
         * 属性
         */
        private Properties properties;
    }
}
