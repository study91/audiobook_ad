package com.study91.audiobook.book;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;

import com.study91.audiobook.data.DataFactory;
import com.study91.audiobook.data.IData;
import com.study91.audiobook.file.FileFactory;
import com.study91.audiobook.file.IFile;
import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;

/**
 * 内容
 */
class BookContent implements IBookContent {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     *
     * @param context   应用程序上下文
     * @param contentID 内容ID
     */
    public BookContent(Context context, int contentID) {
        m.context = context;
        load(contentID); //载入
    }

    @Override
    public int getContentID() {
        return m.contentID;
    }

    @Override
    public int getCatalogID() {
        return m.catalogID;
    }

    @Override
    public IBookCatalog getCatalog() {
        if (m.catalog == null) {
            m.catalog = BookManager.createBookCatalog(getContext(), getCatalogID());
        }

        return m.catalog;
    }

    @Override
    public int getPage() {
        return m.page;
    }

    @Override
    public boolean hasAudio() {
        return m.hasAudio;
    }

    @Override
    public long getAudioStartTime() {
        return m.audioStartTime;
    }

    @Override
    public String getImageFilename() {
        return m.imageFilename;
    }

    @Override
    public Drawable getImageDrawable() {
        Drawable drawable = null;

        if (getImageFilename() != null) {
            IFile imageFile = FileFactory.createFile(
                    getContext(),
                    getOption().getStorageType(),
                    getImageFilename()); //创建文件对象

            drawable = Drawable.createFromStream(imageFile.getInputStream(), null);
        }

        return drawable;
    }

    @Override
    public String getIconFilename() {
        return m.iconFilename;
    }

    @Override
    public Drawable getIconDrawable() {
        Drawable drawable = null;

        if (getIconFilename() != null) {
            //创建图标文件
            IFile file = FileFactory.createFile(
                    getContext(),
                    getOption().getStorageType(),
                    getIconFilename());

            drawable = Drawable.createFromStream(file.getInputStream(), null); //创建Drawable
        }

        return drawable;
    }

    /**
     * 载入数据
     *
     * @param contentID 内容ID
     */
    private void load(int contentID) {
        IData data = null;
        Cursor cursor = null;

        try {
            data = DataFactory.createData(getContext()); //创建数据对象
            String sql = "SELECT * FROM [BookContent] WHERE [ContentID] = " + contentID; //查询字符串
            cursor = data.query(sql); //查询数据
            load(cursor); //载入数据
        } finally {
            if (cursor != null) cursor.close(); //关闭数据指针
            if (data != null) data.close(); //关闭数据对象
        }
    }

    /**
     * 载入数据
     *
     * @param cursor 数据指针
     */
    private void load(Cursor cursor) {
        if (cursor.getCount() == 1) {
            cursor.moveToFirst(); //移动到首记录
            m.contentID = cursor.getInt(cursor.getColumnIndex("ContentID")); //内容ID
            m.catalogID = cursor.getInt(cursor.getColumnIndex("CatalogID")); //目录ID
            m.page = cursor.getInt(cursor.getColumnIndex("Page")); //页号
            m.hasAudio = cursor.getInt(cursor.getColumnIndex("HasAudio")) != 0; //有语音
            setAudioStartTime(cursor.getString(cursor.getColumnIndex("AudioStartTime"))); //语音开始时间
            setImageFilename(cursor.getString(cursor.getColumnIndex("ImageFilename"))); //图片文件名
            setIconFilename(cursor.getString(cursor.getColumnIndex("IconFilename"))); //图标文件名
        }
    }

    /**
     * 设置语音开始时间
     *
     * @param audioStartTime 语音开始时间字符串
     */
    private void setAudioStartTime(String audioStartTime) {
        //有语音，并且语音开始时间不为null且有效时执行
        //（注：如果时间格式不正确，会引发异常，需要进行处理）
        if (hasAudio() && audioStartTime != null) {
            m.audioStartTime = parseTime(audioStartTime);
        }
    }

    /**
     * 解析时间
     * @param time 时间字符串
     * @return 时间
     */
    private long parseTime(String time) {
        long result = -1;

        String timeString = time.replace(".", ":"); //将时间字符串中的“.”替换成“:”，以便于进行字符串分隔
        String[] timeArray = timeString.split(":"); //用字符“:”将时间字符串分隔为时间数组

        if (timeArray.length == 3) {
            long min = Long.parseLong(timeArray[0]); //提取分钟值
            long sec = Long.parseLong(timeArray[1]); //提取秒值
            long ms = Long.parseLong(timeArray[2]); //提取毫秒值

            // 时间格式：00:17.04的计算方式
            if (timeArray[2].length() == 2) {
                result = (min * 60 + sec) * 1000 + ms * 10;
            }

            // 时间格式：00:04.415的计算方式
            if (timeArray[2].length() == 3) {
                result = (min * 60 + sec) * 1000 + ms;
            }
        }

        return result;
    }

    /**
     * 设置图片文件名
     * @param filename 文件名
     */
    private void setImageFilename(String filename) {
        if (filename != null) {
            m.imageFilename = getOption().getResourcePath() + filename;
        } else {
            m.imageFilename = null;
        }
    }

    /**
     * 设置图标文件名
     * @param filename 文件名
     */
    private void setIconFilename(String filename) {
        if (filename != null) {
            m.iconFilename = getOption().getResourcePath() + filename;
        } else {
            m.iconFilename = null;
        }
    }

    /**
     * 获取应用程序上下文
     *
     * @return 应用程序上下文
     */
    private Context getContext() {
        return m.context;
    }

    /**
     * 获取全局选贡
     * @return 选贡
     */
    private IOption getOption() {
        return OptionManager.getOption(getContext());
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
         * 内容ID
         */
        int contentID;

        /**
         * 目录ID
         */
        int catalogID;

        /**
         * 目录
         */
        IBookCatalog catalog;

        /**
         * 页码
         */
        int page;

        /**
         * 有语音
         */
        boolean hasAudio;

        /**
         * 语音开始时间
         */
        long audioStartTime;

        /**
         * 内容图片文件名
         */
        String imageFilename;

        /**
         * 图标文件名
         */
        String iconFilename;
    }
}
