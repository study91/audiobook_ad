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

import java.util.ArrayList;
import java.util.List;

/**
 * 目录
 */
class BookCatalog implements IBookCatalog {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param catalogID 目录ID
     */
    public BookCatalog(Context context, int catalogID) {
        m.context = context; //应用程序上下文
        load(catalogID); //载入
    }

    @Override
    public int getCatalogID() {
        return m.catalogID;
    }

    @Override
    public int getBookID() {
        return m.bookID;
    }

    @Override
    public IBook getBook() {
        if (m.book == null) {
            m.book = BookManager.createBook(getContext(), getBookID());
        }

        return m.book;
    }

    @Override
    public int getIndex() {
        return m.index;
    }

    @Override
    public int getPage() {
        return m.page;
    }

    @Override
    public String getTitle() {
        return m.title;
    }

    @Override
    public boolean displayCatalog() {
        return m.displayCatalog;
    }

    @Override
    public boolean displayContent() {
        return m.displayContent;
    }

    @Override
    public boolean displayExplain() {
        return m.displayExplain;
    }

    @Override
    public boolean displayPage() {
        return m.displayPage;
    }

    @Override
    public boolean displayIcon() {
        return m.displayIcon;
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

    @Override
    public boolean isKnowledgePoint() {
        return m.isKnowledgePoint;
    }

    @Override
    public int getFamiliarLevel() {
        return m.familiarLevel;
    }

    @Override
    public boolean hasAudio() {
        return m.hasAudio;
    }

    @Override
    public void setAllowPlayAudio(boolean value) {
        m.allowPlayAudio = value;
    }

    @Override
    public void updateAllowPlayAudio(boolean value) {
        IData data = null;

        try {
            data = DataFactory.createData(getContext()); //创建数据对象

            //将播放开关值转换为数据库中的实际存储值
            int allowPlayAudio = 0;
            if (value) allowPlayAudio = 1;

            //更新字符串
            String sql = "UPDATE [BookCatalog] " +
                    "SET [AllowPlayAudio] = " + allowPlayAudio + " " +
                    "WHERE [CatalogID] = " + getCatalogID();

            data.execute(sql); //执行更新
        }  finally {
            if(data != null) data.close(); //关闭数据对象
        }
    }

    @Override
    public boolean allowPlayAudio() {
        return m.allowPlayAudio;
    }

    @Override
    public String getAudioFilename() {
        return m.audioFilename;
    }

    @Override
    public String getOriginal() {
        return m.original;
    }

    @Override
    public String getExplain() {
        return m.explain;
    }

    @Override
    public List<IBookContent> getContentList() {
        if (m.contentList == null && getCatalogID() > 0) {
            IData data = null;
            Cursor cursor = null;

            try {
                data = DataFactory.createData(getContext()); //创建数据对象

                //查询字符串
                String sql = "SELECT [ContentID] FROM [BookContent] " +
                        "WHERE [CatalogID] = " + getCatalogID() + " " +
                        "ORDER BY [Page]";

                cursor = data.query(sql); //查询数据

                if(cursor.getCount() > 0) {
                    m.contentList = new ArrayList<>(); //实例化目录列表
                    for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        int contentID = cursor.getInt(cursor.getColumnIndex("ContentID")); //内容ID
                        IBookContent content = BookManager.createBookContent(getContext(), contentID); //创建内容
                        m.contentList.add(content); //添加到集合
                    }
                }
            } finally {
                if(cursor != null) cursor.close(); //关闭数据指针
                if(data != null) data.close(); //关闭数据对象
            }
        }

        return m.contentList;
    }

    @Override
    public IBookContent getAudioContent(long position) {
        IBookContent audioContent = null;

        List<IBookContent> contentList = getAudioContentList(); //获取语音内容列表

        if (contentList != null) {
            //如果有语音内容时，遍历查询当前时间点的内容
            for (IBookContent content : contentList) {
                if (content.getAudioStartTime() <= position) { //找到最后一个小于时间参数的内容
                    audioContent = content;
                } else { //如果开始时间大于时间参数，退出循环
                    break;
                }
            }
        }

        return audioContent;
    }

    @Override
    public List<IBookContent> getAudioContentList() {
        if (m.audioContentList == null && getCatalogID() > 0) {
            IData data = null;
            Cursor cursor = null;

            try {
                data = DataFactory.createData(getContext()); //创建数据对象

                //查询字符串
                String sql = "SELECT [ContentID] FROM [BookContent] " +
                        "WHERE " +
                        "[CatalogID] = " + getCatalogID() + " AND " +
                        "[HasAudio] = 1 " +
                        "ORDER BY [Page]";

                cursor = data.query(sql); //查询数据

                if(cursor.getCount() > 0) {
                    m.audioContentList = new ArrayList<>(); //实例化目录列表
                    for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        int contentID = cursor.getInt(cursor.getColumnIndex("ContentID")); //内容ID
                        IBookContent content = BookManager.createBookContent(getContext(), contentID); //创建内容
                        m.audioContentList.add(content); //添加到集合
                    }
                }
            } finally {
                if(cursor != null) cursor.close(); //关闭数据指针
                if(data != null) data.close(); //关闭数据对象
            }
        }

        return m.audioContentList;
    }

    /**
     * 载入
     * @param catalogID 目录ID
     */
    private void load(int catalogID) {
        IData data = null;
        Cursor cursor = null;

        try{
            data = DataFactory.createData(getContext()); //创建数据对象
            String sql = "SELECT * FROM [BookCatalog] WHERE [CatalogID] = " + catalogID; //查询字符串
            cursor = data.query(sql); //查询数据

            if(cursor.getCount() == 1) {
                cursor.moveToFirst(); //移动到首记录
                m.catalogID = cursor.getInt(cursor.getColumnIndex("CatalogID")); //目录ID
                m.bookID = cursor.getInt(cursor.getColumnIndex("BookID")); //书ID
                m.index = cursor.getInt(cursor.getColumnIndex("Index")); //索引
                m.page = cursor.getInt(cursor.getColumnIndex("Page")); //页码
                m.title = cursor.getString(cursor.getColumnIndex("Title")); //目录标题
                m.title = m.title.replace("\r\n", "\n");
                m.displayCatalog = cursor.getInt(cursor.getColumnIndex("DisplayCatalog")) != 0; //显示目录
                m.displayContent = cursor.getInt(cursor.getColumnIndex("DisplayContent")) != 0; //显示内容
                m.displayExplain = cursor.getInt(cursor.getColumnIndex("DisplayExplain")) != 0; //显示解释
                m.displayPage = cursor.getInt(cursor.getColumnIndex("DisplayPage")) != 0; //显示页号
                m.displayIcon = cursor.getInt(cursor.getColumnIndex("DisplayIcon")) != 0; //显示图标
                m.isKnowledgePoint = cursor.getInt(cursor.getColumnIndex("IsKnowledgePoint")) != 0; //是否知识点
                m.familiarLevel = cursor.getInt(cursor.getColumnIndex("FamiliarLevel")); //熟悉级别
                m.hasAudio = cursor.getInt(cursor.getColumnIndex("HasAudio")) != 0; //有语音
                m.allowPlayAudio = cursor.getInt(cursor.getColumnIndex("AllowPlayAudio")) != 0; //充许播放语音
                m.original = cursor.getString(cursor.getColumnIndex("Original")); //原文
                if (m.original != null) {
                    m.original = m.original.replace("\r\n", "\n");
                }
                m.explain = cursor.getString(cursor.getColumnIndex("Explain")); //详解
                setIconFilename(cursor.getString(cursor.getColumnIndex("IconFilename"))); //图标文件名
                setAudioFilename(cursor.getString(cursor.getColumnIndex("AudioFilename"))); //语音文件名
            }
        } finally {
            if(cursor != null) cursor.close(); //关闭数据指针
            if(data != null) data.close(); //关闭数据对象
        }
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    private Context getContext() {
        return m.context;
    }

    /**
     * 获取全局选项
     * @return 选项
     */
    private IOption getOption() {
        return OptionManager.getOption(getContext());
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
     * 设置语音文件名
     * @param filename 文件名
     */
    private void setAudioFilename(String filename) {
        if (filename != null) {
            m.audioFilename = getOption().getResourcePath() + filename;
        } else {
            m.audioFilename = null;
        }
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
         * 目录ID
         */
        int catalogID;

        /**
         * 书ID
         */
        int bookID;

        /**
         * 书
         */
        IBook book;

        /**
         * 索引
         */
        int index;

        /**
         * 页号
         */
        int page;

        /**
         * 标题
         */
        String title;

        /**
         * 显示目录
         */
        boolean displayCatalog;

        /**
         * 显示内容
         */
        boolean displayContent;

        /**
         * 显示详解
         */
        boolean displayExplain;

        /**
         * 显示页号
         */
        boolean displayPage;

        /**
         * 显示图标
         */
        boolean displayIcon;

        /**
         * 图标文件名
         */
        String iconFilename;

        /**
         * 是否知识点
         */
        boolean isKnowledgePoint;

        /**
         * 熟悉级别
         */
        int familiarLevel;

        /**
         * 有语音
         */
        boolean hasAudio;

        /**
         * 充许播放语音
         */
        boolean allowPlayAudio;

        /**
         * 语音文件名
         */
        String audioFilename;

        /**
         * 原文
         */
        String original;

        /**
         * 详解
         */
        String explain;

        /**
         * 内容列表
         */
        List<IBookContent> contentList;

        /**
         * 语音内容列表
         */
        List<IBookContent> audioContentList;
    }
}
