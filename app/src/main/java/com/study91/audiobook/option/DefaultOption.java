package com.study91.audiobook.option;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.util.Log;

import com.study91.audiobook.config.ConfigFactory;
import com.study91.audiobook.config.IConfig;
import com.study91.audiobook.data.DataFactory;
import com.study91.audiobook.data.IData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 默认选项
 */
class DefaultOption implements IOption {
    private final String SHARED_NAME = "AudioBook"; //存储文件名称
    private final String KEY_FIRST_RUN_TIME = "FirstRunTime"; //键（第一次启动时间）
    private final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"; //日期字符串格式

    private static IOption option; //全局选项
    private Field m = new Field(); //私有字段

    /**
     * 实例化选项
     * 注：使用单例模式保证只实例化一个全局选项
     * @param context 应用程序上下文
     * @return 选项
     */
    public static IOption instance(Context context) {
        if (option == null) {
            option = new DefaultOption(context);
        }

        return option;
    }

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    private DefaultOption(Context context) {
        m.context = context; //应用程序上下文
        IConfig config = ConfigFactory.getConfig(context); //获取配置
        load(config.getOptionID()); //载入数据
    }

    @Override
    public void init() {
        String KEY_IS_FIRST_RUN = "IsFirstRun"; //键（是否第一次启动）
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //读取是否第一次运行值
        m.isFirstRun = sharedPreferences.getBoolean(KEY_IS_FIRST_RUN, true);

        if (m.isFirstRun) { //第一次运行
            editor.putBoolean(KEY_IS_FIRST_RUN, false).apply(); //存储为不是第一次运行
            setFirstRunTime(); //设置第一次运行时间
        }
    }

    @Override
    public int getOptionID() {
        return m.optionID;
    }

    @Override
    public String getOptionName() {
        return m.optionName;
    }

    @Override
    public int getBookID() {
        return m.bookID;
    }

    @Override
    public int getStorageType() {
        return m.storageType;
    }

    @Override
    public String getResourcePath() {
        return m.resourcePath;
    }

    @Override
    public boolean canChooseBook() {
        return m.canChooseBook;
    }

    @Override
    public void setAudioVolume(float leftVolume, float rightVolume) {
        IData data = null;

        try {
            data = DataFactory.createData(getContext()); //获取数据对象

            //更新字符串
            String sql = "UPDATE [Option] " +
                    "SET " +
                    "[AudioLeftVolume] = '" + leftVolume + "'," +
                    "[AudioRightVolume] = '" + rightVolume + "' " +
                    "WHERE " +
                    "[OptionID] = " + getOptionID();

            data.execute(sql); //执行更新
            m.audioLeftVolume = leftVolume; //重置语音左声道
            m.audioRightVolume = rightVolume; //重置语音右声道
        } finally {
            if(data != null) data.close(); //关闭数据对象
        }
    }

    @Override
    public float getAudioLeftVolume() {
        return m.audioLeftVolume;
    }

    @Override
    public float getAudioRightVolume() {
        return m.audioRightVolume;
    }

    @Override
    public int getAudioLoopMode() {
        return m.audioLoopMode;
    }

    @Override
    public void setMusicVolume(float leftVolume, float rightVolume) {
        IData data = null;

        try {
            data = DataFactory.createData(getContext()); //获取数据对象

            //更新字符串
            String sql = "UPDATE [Option] " +
                    "SET " +
                    "[MusicLeftVolume] = '" + leftVolume + "'," +
                    "[MusicRightVolume] = '" + rightVolume + "' " +
                    "WHERE " +
                    "[OptionID] = " + getOptionID();

            data.execute(sql); //执行更新
            m.musicLeftVolume = leftVolume; //重置音乐左声道
            m.musicRightVolume = rightVolume; //重置音乐右声道
        } finally {
            if(data != null) data.close(); //关闭数据对象
        }
    }

    @Override
    public float getMusicLeftVolume() {
        return m.musicLeftVolume;
    }

    @Override
    public float getMusicRightVolume() {
        return m.musicRightVolume;
    }

    @Override
    public int getMusicLoopMode() {
        return m.musicLoopMode;
    }

    @Override
    public void setCatalogFontSize(int fontSize) {
        IData data = null;

        try {
            data = DataFactory.createData(getContext()); //获取数据对象

            //更新字符串
            String sql = "UPDATE [Option] " +
                    "SET " +
                    "[CatalogFontSize] = " + fontSize + " " +
                    "WHERE " +
                    "[OptionID] = " + getOptionID();

            data.execute(sql); //执行更新
            m.catalogFontSize = fontSize; //重置目录字体大小
        } finally {
            if(data != null) data.close(); //关闭数据对象
        }

        Log.e("Test", "目录字体大小：" + getCatalogFontSize());
    }

    @Override
    public int getCatalogFontSize() {
        return m.catalogFontSize;
    }

    @Override
    public void setContentFontSize(int fontSize) {
        if (fontSize > 0) {
            IData data = null;

            try {
                data = DataFactory.createData(getContext()); //获取数据对象

                //更新字符串
                String sql = "UPDATE [Option] " +
                        "SET " +
                        "[ContentFontSize] = " + fontSize + " " +
                        "WHERE " +
                        "[OptionID] = " + getOptionID();

                data.execute(sql); //执行更新
                m.contentFontSize = fontSize; //重置目录字体大小
            } finally {
                if (data != null) data.close(); //关闭数据对象
            }
        }

        Log.e("Test", "内容字体大小：" + getContentFontSize());
    }

    @Override
    public int getContentFontSize() {
        return m.contentFontSize;
    }

    @Override
    public void setFullFontSize(int fontSize) {
        if (fontSize > 0) {
            IData data = null;

            try {
                data = DataFactory.createData(getContext()); //获取数据对象

                //更新字符串
                String sql = "UPDATE [Option] " +
                        "SET " +
                        "[FullFontSize] = " + fontSize + " " +
                        "WHERE " +
                        "[OptionID] = " + getOptionID();

                data.execute(sql); //执行更新
                m.fullFontSize = fontSize; //重置目录字体大小
            } finally {
                if (data != null) data.close(); //关闭数据对象
            }
        }

        Log.e("Test", "全屏字体大小：" + getFullFontSize());
    }

    @Override
    public int getFullFontSize() {
        return m.fullFontSize;
    }

    @Override
    public String getStudyUrl() {
        return m.studyUrl;
    }

    @Override
    public void updateStudyUrl(String url) {
        if (!url.equals(getStudyUrl())) {
            IData data = null;

            try {
                data = DataFactory.createData(getContext()); //创建数据对象

                //更新字符串
                String sql = "UPDATE [Option] " +
                        "SET [StudyUrl] = '" + url + "' " +
                        "WHERE [OptionID] = " + getOptionID();
                data.execute(sql); //执行更新
                m.studyUrl = url;
            } finally {
                if (data != null) data.close(); //关闭数据对象
            }
        }
    }

    @Override
    public String getQuestionUrl() {
        return m.questionUrl;
    }

    @Override
    public void updateQuestionUrl(String url) {
        if (!url.equals(getStudyUrl())) {
            IData data = null;

            try {
                data = DataFactory.createData(getContext()); //创建数据对象

                //更新字符串
                String sql = "UPDATE [Option] " +
                        "SET [QuestionUrl] = '" + url + "' " +
                        "WHERE [OptionID] = " + getOptionID();
                data.execute(sql); //执行更新
                m.questionUrl = url;
            } finally {
                if (data != null) data.close(); //关闭数据对象
            }
        }
    }

    @Override
    public String getVersion() {
        String version;

        try {
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
//            int versionCode = packageInfo.versionCode;
//            version = packageInfo.versionName + " code " + versionCode;
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        return version;
    }

    @Override
    public String getCopyright() {
        return m.copyright;
    }

    @Override
    public void updateCopyright(String copyright) {
        if (!copyright.equals(getCopyright())) {
            IData data = null;

            try {
                data = DataFactory.createData(getContext()); //创建数据对象

                //更新字符串
                String sql = "UPDATE [Option] " +
                        "SET [Copyright] = '" + copyright + "' " +
                        "WHERE [OptionID] = " + getOptionID();
                data.execute(sql); //执行更新
                m.copyright = copyright;
            } finally {
                if (data != null) data.close(); //关闭数据对象
            }
        }
    }

    @Override
    public boolean isFirstRun() {
        return m.isFirstRun;
    }

    @Override
    public Date getFirstRunTime() {
        if (m.firstRunTime == null) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_NAME, 0);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.CHINESE);

            //读取第一次运行时间
            String firstRunTimeString = sharedPreferences.getString(KEY_FIRST_RUN_TIME, null);
            try {
                m.firstRunTime = simpleDateFormat.parse(firstRunTimeString);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        return m.firstRunTime;
    }

    @Override
    public int getAdStartDay() {
        return m.adStartDay;
    }

    @Override
    public void updateAdStartDay(int startDay) {
        if (!getConfig().isTest()) {
            IData data = null;

            try {
                data = DataFactory.createData(getContext()); //获取数据对象

                //更新字符串
                String sql = "UPDATE [Option] " +
                        "SET " +
                        "[AdStartDay] = " + startDay + " " +
                        "WHERE " +
                        "[OptionID] = " + getOptionID();

                data.execute(sql); //执行更新
                m.adStartDay = startDay; //重置目录字体大小
            } finally {
                if (data != null) data.close(); //关闭数据对象
            }
        }
    }

    @Override
    public String getServiceAction() {
        return getContext().getPackageName() + ".action.book_media_service";
    }

    @Override
    public String getClientAction() {
        return getContext().getPackageName() + ".action.book_media_client";
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    public Context getContext() {
        return m.context;
    }

    /**
     * 载入数据
     * @param optionID 选项ID
     */
    private void load(int optionID) {
        IData data = null;
        Cursor cursor = null;

        try {
            data = DataFactory.createData(getContext()); //创建数据对象
            String sql = "SELECT * FROM [Option] WHERE [OptionID] = " + optionID; //查询字符串
            cursor = data.query(sql); //查询数据

            if(cursor.getCount() == 1) {
                cursor.moveToFirst(); //移动到首记录
                m.optionID = cursor.getInt(cursor.getColumnIndex("OptionID")); //选项ID
                m.optionName = cursor.getString(cursor.getColumnIndex("OptionName")); //选项名称
                m.bookID = cursor.getInt(cursor.getColumnIndex("BookID")); //书ID
                m.resourcePath = cursor.getString(cursor.getColumnIndex("ResourcePath")); //资源路径
                m.canChooseBook = cursor.getInt(cursor.getColumnIndex("CanChooseBook")) != 0; //能选书
                m.audioLeftVolume = cursor.getFloat(cursor.getColumnIndex("AudioLeftVolume")); //语音左声道音量
                m.audioRightVolume = cursor.getFloat(cursor.getColumnIndex("AudioRightVolume")); //语音右声道音量
                m.audioLoopMode = cursor.getInt(cursor.getColumnIndex("AudioLoopMode")); //语音循环模式
                m.musicLeftVolume = cursor.getFloat(cursor.getColumnIndex("MusicLeftVolume")); //音乐左声道音量
                m.musicRightVolume = cursor.getFloat(cursor.getColumnIndex("MusicRightVolume")); //音乐右声道音量
                m.musicLoopMode = cursor.getInt(cursor.getColumnIndex("MusicLoopMode")); //音乐循环模式
                m.catalogFontSize = cursor.getInt(cursor.getColumnIndex("CatalogFontSize")); //目录字体大小
                m.contentFontSize = cursor.getInt(cursor.getColumnIndex("ContentFontSize")); //内容字体大小
                m.fullFontSize = cursor.getInt(cursor.getColumnIndex("FullFontSize")); //全屏字体大小
                m.studyUrl = cursor.getString(cursor.getColumnIndex("StudyUrl")); //学习方法Url地址
                m.questionUrl = cursor.getString(cursor.getColumnIndex("QuestionUrl")); //问题解签Url地址
                m.copyright = cursor.getString(cursor.getColumnIndex("Copyright")); //版权
                m.adStartDay = cursor.getInt(cursor.getColumnIndex("AdStartDay")); //广告显示日
                m.storageType = cursor.getInt(cursor.getColumnIndex("StorageType")); //存储类型

                //存储类型（如果是测试状态时，存储类型是SD卡，其它情况读取数据库中的存储值
//                if (getConfig().isTest()) {
//                    m.storageType = IFile.STORAGE_TYPE_SDCARD;
//                } else {
//                    m.storageType = cursor.getInt(cursor.getColumnIndex("StorageType"));
//                }
            }
        } finally {
            if(cursor != null) cursor.close(); //关闭数据指针
            if(data != null) data.close(); //关闭数据对象
        }
    }

    /**
     * 设置第一次运行时间
     */
    private void setFirstRunTime() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.CHINESE);

        Date firstRunTime = new Date(); //设置第一次运行时间
        String firstRunTimeString = simpleDateFormat.format(firstRunTime); //第一次运行时间字符串
        editor.putString(KEY_FIRST_RUN_TIME, firstRunTimeString).apply(); //存储第一次运行时间
    }

    /**
     * 获取全局配置
     */
    private IConfig getConfig() {
        return ConfigFactory.getConfig(getContext());
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 应用程序上下文
         */
        Context context;

        /**
         * 选项ID
         */
        int optionID;

        /**
         * 选项名称
         */
        String optionName;

        /**
         * 书ID
         */
        int bookID;

        /**
         * 存储类型
         */
        int storageType;

        /**
         * 资源路径
         */
        String resourcePath;

        /**
         * 能选书
         */
        boolean canChooseBook;

        /**
         * 语音左声道音量
         */
        float audioLeftVolume;

        /**
         * 语音右声道音量
         */
        float audioRightVolume;

        /**
         * 语音循环模式
         */
        int audioLoopMode;

        /**
         * 音乐左声道音量
         */
        float musicLeftVolume;

        /**
         * 音乐右声道音量
         */
        float musicRightVolume;

        /**
         * 音乐循环模式
         */
        int musicLoopMode;

        /**
         * 目录字体大小
         */
        int catalogFontSize;

        /**
         * 内容字体大小
         */
        int contentFontSize;

        /**
         * 全屏字体大小
         */
        int fullFontSize;

        /**
         * 学习方法Url地址
         */
        String studyUrl;

        /**
         * 问题解签Url地址
         */
        String questionUrl;

        /**
         * 版权
         */
        String copyright;

        /**
         * 是否第一次运行（默认是第一次运行）
         */
        boolean isFirstRun = true;

        /**
         * 第一次运行时间
         */
        Date firstRunTime;

        /**
         * 广告开始显示日（注：值为安装软件后第几天显示广告，值为0时，立即显示广告）
         */
        int adStartDay;
    }
}
