package com.study91.audiobook.book;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.study91.audiobook.R;
import com.study91.audiobook.data.DataFactory;
import com.study91.audiobook.data.IData;
import com.study91.audiobook.file.FileFactory;
import com.study91.audiobook.file.IFile;
import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 书
 */
class Book implements IBook {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param bookID 书ID
     */
    public Book(Context context, int bookID) {
        m.context = context; //应用程序上下文
        load(bookID); //载入
    }

    @Override
    public int getBookID() {
        return m.bookID;
    }

    @Override
    public String getBookName() {
        return m.bookName;
    }

    @Override
    public String getBookDepict() {
        return m.bookDepict;
    }

    @Override
    public int getBookStyle() {
        return m.bookStyle;
    }

    @Override
    public int getMediaType() {
        return m.mediaType;
    }

    @Override
    public int getMediaTitleLinkType() {
        return m.mediaTitleLinkType;
    }

    @Override
    public int getMediaIconLinkType() {
        return m.mediaIconLinkType;
    }

    @Override
    public boolean allowFullScreen() {
        return m.allowFullScreen;
    }

    @Override
    public int getFullScreenLinkType() {
        return m.fullScreenLinkType;
    }

    @Override
    public boolean allowSynchronization() {
        return m.allowSynchronization;
    }

    @Override
    public void setSynchronizationEnable(boolean synchronizationEnable) {
        IData data = null;

        try {
            data = DataFactory.createData(getContext()); //获取数据对象

            //将同步复读开关参数值转换为数据库中实际存储的整型值
            int synchronizationValue = 0;
            if (synchronizationEnable) synchronizationValue = 1;

            //更新字符串
            String sql = "UPDATE [Book] " +
                    "SET " +
                    "[SynchronizationEnable] = " + synchronizationValue + " " +
                    "WHERE " +
                    "[BookID] = " + getBookID();

            data.execute(sql); //执行更新
            m.synchronizationEnable = synchronizationEnable; //重置同步复读值

            //显示提示信息
            String msg;
            if (synchronizationEnable) { //同步复读打开
                msg = getContext().getResources().getString(R.string.msg_synchronization_enable);
            } else { //同步复读关闭
                msg = getContext().getResources().getString(R.string.msg_synchronization_disable);
            }

            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show(); //显示信息
        } finally {
            if(data != null) data.close(); //关闭数据对象
        }
    }

    @Override
    public boolean synchronizationEnable() {
        return m.synchronizationEnable;
    }

    @Override
    public boolean canHideToolbar() {
        return m.canHideToolbar;
    }

    @Override
    public void setCurrentContentID(int contentID) {
        if (contentID != getCurrentContentID()) {
            IData data = null;

            try {
                data = DataFactory.createData(getContext()); //创建数据对象

                //更新字符串
                String sql = "UPDATE [Book] " +
                        "SET [CurrentContentID] = " + contentID + " " +
                        "WHERE [BookID] = " + getBookID();
                data.execute(sql); //执行更新
            } finally {
                if (data != null) data.close(); //关闭数据对象
            }

            m.currentContentID = contentID; //重置当前内容ID
        }
    }

    @Override
    public int getCurrentContentID() {
        return m.currentContentID;
    }

    @Override
    public void setCurrentContent(IBookContent content) {
        if (content != null &&
                content.getContentID() != getCurrentContentID()) {
            setCurrentContentID(content.getContentID()); //设置当前内容ID
            m.currentContent = content; //重置当前内容
        }
    }

    @Override
    public IBookContent getCurrentContent() {
        if (m.currentContent == null ||
                m.currentContent.getContentID() != getCurrentContentID()) {
            List<IBookContent> contentList = getContentList(); //获取内容列表

            //遍历查找等于当前内容ID的语音
            for (int i = 0; i < contentList.size(); i++) {
                IBookContent content = contentList.get(i);
                if (content.getContentID() == getCurrentContentID()) {
                    m.currentContent = content;
                    break;
                }
            }
        }

        return m.currentContent;
    }

    @Override
    public void setCurrentAudioCatalogID(int catalogID) {
        if (catalogID != getCurrentAudioCatalogID()) {
            IData data = null;

            try {
                data = DataFactory.createData(getContext()); //创建数据对象

                //更新字符串
                String sql = "UPDATE [Book] " +
                        "SET [CurrentAudioCatalogID] = " + catalogID + " " +
                        "WHERE [BookID] = " + getBookID();
                data.execute(sql); //执行更新
            } finally {
                if (data != null) data.close(); //关闭数据对象
            }

            m.currentAudioCatalogID = catalogID; //重置当前语音目录ID
        }
    }

    @Override
    public int getCurrentAudioCatalogID() {
        return m.currentAudioCatalogID;
    }

    @Override
    public void setCurrentAudioCatalog(IBookCatalog catalog) {
        if (catalog != null &&
                catalog.getCatalogID() != getCurrentAudioCatalogID()) {
            if (!catalog.allowPlayAudio()) {
                resetAllowPlayAudio(catalog); //重置充许播放语音
                setCurrentAudioCatalogID(catalog.getCatalogID()); //设置当前语音目录ID
                m.currentAudioCatalog = catalog; //重置当前语音目录
                setFirstAudioCatalog(catalog); //设置为复读起点
                setLastAudioCatalog(catalog); //设置为复读终点
            } else {
                setCurrentAudioCatalogID(catalog.getCatalogID()); //设置当前语音目录ID
                m.currentAudioCatalog = catalog; //重置当前语音目录
            }
        }
    }

    @Override
    public IBookCatalog getCurrentAudioCatalog() {
        if (m.currentAudioCatalog == null ||
                m.currentAudioCatalog.getCatalogID() !=  getCurrentAudioCatalogID()) {
            List<IBookCatalog> catalogList = getCatalogList(); //获取目录列表

            //遍历查找等于当前目录ID的目录
            for (int i = 0; i < catalogList.size(); i++) {
                IBookCatalog catalog = catalogList.get(i);
                if (catalog.getCatalogID() == getCurrentAudioCatalogID()) {
                    m.currentAudioCatalog = catalog;
                    break;
                }
            }
        }

        return m.currentAudioCatalog;
    }

    @Override
    public boolean allowZoomContentImage() {
        return m.allowZoomContentImage;
    }

    @Override
    public boolean allowUpdateCurrentAudioPosition() {
        return m.allowUpdateCurrentAudioPosition;
    }

    @Override
    public void updateCurrentAudioPosition(int position) {
        IData data = null;

        try {
            data = DataFactory.createData(getContext()); //创建数据对象

            //更新字符串
            String sql = "UPDATE [Book] " +
                    "SET [CurrentAudioPosition] = " + position + " " +
                    "WHERE [BookID] = " + getBookID();
            data.execute(sql); //执行更新
            m.currentAudioPosition = position;
        } finally {
            if (data != null) data.close(); //关闭数据对象
        }
    }

    @Override
    public int getCurrentAudioPosition() {
        return m.currentAudioPosition;
    }

    @Override
    public void moveToPreviousContent() {

    }

    @Override
    public void moveToNextContent() {

    }

    @Override
    public void setFirstAudioCatalog(IBookCatalog catalog) {
        IBookCatalog oldFirstAudio = getFirstAudioCatalog(); //原复读起点

        if (catalog.getCatalogID() != oldFirstAudio.getCatalogID()) {
            IData data = null;

            try {
                data = DataFactory.createData(getContext()); //创建数据对象

                //更新字符串
                String sql = "UPDATE [BookCatalog] " +
                        "SET [AllowPlayAudio] = 0 " +
                        "WHERE " +
                        "[BookID] = " + getBookID() + " AND " +
                        "[HasAudio] = 1 AND " +
                        "[Index] < " + catalog.getIndex();
                data.execute(sql); //执行更新

                //如果新复读起点小于原复读起点的页号，打开新复读起点和原复读起点之间的播放开关
                if (catalog.getIndex() < oldFirstAudio.getIndex()) {
                    sql = "UPDATE [BookCatalog] " +
                            "SET [AllowPlayAudio] = 1 " +
                            "WHERE " +
                            "[BookID] = " + getBookID() + " AND " +
                            "[HasAudio] = 1 AND " +
                            "[Index] >= " + catalog.getIndex() + " AND " +
                            "[Index] < " + oldFirstAudio.getIndex();
                    data.execute(sql); //执行更新
                }
            } finally {
                if (data != null) data.close(); //关闭数据对象
            }

            //遍历重置目录中的语音播放开关
            List<IBookCatalog> catalogList = getCatalogList();
            for (IBookCatalog bookCatalog : catalogList) {
                if (bookCatalog.hasAudio()) {
                    if (bookCatalog.getIndex() >= catalog.getIndex() &&
                            bookCatalog.getIndex() < oldFirstAudio.getIndex()) {
                        bookCatalog.setAllowPlayAudio(true); //打开新复读起点和原复读起点之间的播放开关
                    } else if (bookCatalog.allowPlayAudio() &&
                            bookCatalog.getIndex() < catalog.getIndex()) {
                        bookCatalog.setAllowPlayAudio(false); //关闭复读起点以前的播放开关
                    }
                }
            }

            catalog.setAllowPlayAudio(true);
            m.firstAudioCatalog = catalog; //重置复读起点语音
        }
    }

    @Override
    public IBookCatalog getFirstAudioCatalog() {
        if (m.firstAudioCatalog == null) {
            List<IBookCatalog> catalogList = getCatalogList(); //获取目录列表

            //遍历找到第一个语音目录
            for (int i = 0; i < catalogList.size(); i++) {
                IBookCatalog catalog = catalogList.get(i);
                if (catalog.hasAudio() && catalog.allowPlayAudio()) {
                    m.firstAudioCatalog = catalog;
                    break;
                }
            }
        }

        return m.firstAudioCatalog;
    }

    @Override
    public void setLastAudioCatalog(IBookCatalog catalog) {
        IBookCatalog oldLastAudio = getLastAudioCatalog(); //原复读终点

        if (catalog.getCatalogID() != oldLastAudio.getCatalogID()) {
            IData data = null;

            try {
                data = DataFactory.createData(getContext()); //创建数据对象

                //更新字符串
                String sql = "UPDATE [BookCatalog] " +
                        "SET [AllowPlayAudio] = 0 " +
                        "WHERE " +
                        "[BookID] = " + getBookID() + " AND " +
                        "[HasAudio] = 1 AND " +
                        "[Index] > " + catalog.getIndex();

                data.execute(sql); //执行更新

                //如果新复读终点大于原复读终点的页号，打开新复读终点和原复读终点之间的播放开关
                if (catalog.getIndex() > oldLastAudio.getIndex()) {
                    sql = "UPDATE [BookCatalog] " +
                            "SET [AllowPlayAudio] = 1 " +
                            "WHERE " +
                            "[BookID] = " + getBookID() + " AND " +
                            "[HasAudio] = 1 AND " +
                            "[Index] <= " + catalog.getIndex() + " AND " +
                            "[Index] > " + oldLastAudio.getIndex();
                    data.execute(sql); //执行更新
                }

                //遍历重置目录中的语音播放开关
                List<IBookCatalog> catalogList = getCatalogList();
                for (IBookCatalog bookCatalog : catalogList) {
                    if (bookCatalog.hasAudio()) {
                        if (bookCatalog.getIndex() <= catalog.getIndex() &&
                                bookCatalog.getIndex() > oldLastAudio.getIndex()) {
                            bookCatalog.setAllowPlayAudio(true); //打开新复读终点和原复读终点之间的播放开关
                        } else if (bookCatalog.allowPlayAudio() &&
                                bookCatalog.getIndex() > catalog.getIndex()) {
                            bookCatalog.setAllowPlayAudio(false); //关闭复读终点以后的播放开关
                        }
                    }
                }

                catalog.setAllowPlayAudio(true);
                m.lastAudioCatalog = catalog; //重置复读终点语音
            } finally {
                if (data != null) data.close(); //关闭数据对象
            }
        }
    }

    @Override
    public IBookCatalog getLastAudioCatalog() {
        if (m.lastAudioCatalog == null) {
            List<IBookCatalog> catalogList = getCatalogList(); //获取目录列表

            //遍历找到第一个语音目录
            for (int i = catalogList.size() - 1; i >= 0; i--) {
                IBookCatalog catalog = catalogList.get(i);
                if (catalog.hasAudio() &&
                        catalog.allowPlayAudio()) {
                    m.lastAudioCatalog = catalog;
                    break;
                }
            }
        }

        return m.lastAudioCatalog;
    }

    @Override
    public void moveToPreviousAudioCatalog() {
        setCurrentAudioCatalog(getPreviousAudioCatalog(getCurrentAudioCatalog())); //重置当前语音目录
    }

    @Override
    public void moveToNextAudioCatalog() {
        setCurrentAudioCatalog(getNextAudioCatalog(getCurrentAudioCatalog())); //重置当前语音目录
    }

    @Override
    public void resetAllowPlayAudio(IBookCatalog catalog) {
        if (catalog.allowPlayAudio()) { //如果语音当前的播放状态是开，那么执行取消复读操作
            if (catalog.getCatalogID() == getFirstAudioCatalog().getCatalogID()) {
                //取消播放的是复读起点语音时，将复读起点后的第一个目录设置为复读起点
                setFirstAudioCatalog(getNextAudioCatalog(catalog));
            } else if (catalog.getCatalogID() == getLastAudioCatalog().getCatalogID()) {
                //取消播放的是复读终点语音时，将复读终点前的第一个目录设置为复读终点
                setLastAudioCatalog(getPreviousAudioCatalog(catalog));
            } else if (catalog.getIndex() > getFirstAudioCatalog().getIndex() &&
                    catalog.getIndex() < getLastAudioCatalog().getIndex()) {
                //取消播放的是复读起点和复读终点之间的语音时，更新这个语音的播放状态为关闭播放
                catalog.updateAllowPlayAudio(false);
                catalog.setAllowPlayAudio(false);
            }
        } else { //如果语音当前的播放状态是关，那么执行添加复读操作
            catalog.updateAllowPlayAudio(true);
            catalog.setAllowPlayAudio(true);
            if (catalog.getIndex() < getFirstAudioCatalog().getIndex()) {
                //添加播放的是复读起点前的目录时，更新语音状态为打开播放状态，并且只将这个语音设置为复读起点
                m.firstAudioCatalog = catalog;
            } else if (catalog.getIndex() > getLastAudioCatalog().getIndex()) {
                //添加播放的是复读起点后的目录时，更新语音状态为打开播放状态，并且只将这个语音设置为复读终点
                m.lastAudioCatalog = catalog;
            }
        }
    }

    @Override
    public IBookMusic getCurrentMusic() {
        if (getMediaType() == IBook.MEDIA_TYPE_AUDIO_AND_MUSIC &&
                m.currentMusic == null &&
                getMusicList() != null &&
                getMusicList().size() > 0) {
            m.currentMusic = getMusicList().get(0); //默认获取第一首音乐
        }

        return m.currentMusic;
    }

    @Override
    public void moveToNextMusic() {
        if (getMediaType() == IBook.MEDIA_TYPE_AUDIO_AND_MUSIC &&
                getCurrentMusic() != null &&
                getMusicList() != null &&
                getMusicList().size() > 1) {
            IBookMusic lastMusic = getMusicList().get(getMusicList().size() - 1); //获取最后一首音乐

            if(getCurrentMusic().getMusicID() == lastMusic.getMusicID()){
                setCurrentMusic(getMusicList().get(0)); //如果当前音乐是最后一首音乐，重置当前音乐为第一首音乐
            } else{
                //遍历查找下一首音乐
                for (IBookMusic music : getMusicList()) {
                    if (music.getIndex() > getCurrentMusic().getIndex()) {
                        setCurrentMusic(music); //找到下一首音乐，重置当前音乐
                        break;
                    }
                }
            }
        }
    }

    @Override
    public String getCoverFilename() {
        return m.coverFilename;
    }

    @Override
    public Drawable getCoverDrawable() {
        Drawable drawable = null;

        if (getCoverFilename() != null) {
            IFile imageFile = FileFactory.createFile(
                    getContext(),
                    getOption().getStorageType(),
                    getCoverFilename()); //创建文件对象

            drawable = Drawable.createFromStream(imageFile.getInputStream(), null);
        }

        return drawable;
    }

    @Override
    public String getTitleFilename() {
        return m.titleFilename;
    }

    @Override
    public Drawable getTitleDrawable() {
        Drawable drawable = null;

        if (getTitleFilename() != null) {
            IFile imageFile = FileFactory.createFile(
                    getContext(),
                    getOption().getStorageType(),
                    getTitleFilename()); //创建文件对象

            drawable = Drawable.createFromStream(imageFile.getInputStream(), null);
        }

        return drawable;
    }

    @Override
    public String getEmpower() {
        return m.empower;
    }

    @Override
    public void updateEmpower(String empower) {
        if (!empower.equals(getEmpower())) {
            IData data = null;

            try {
                data = DataFactory.createData(getContext()); //创建数据对象

                //更新字符串
                String sql = "UPDATE [Book] " +
                        "SET [Empower] = '" + empower + "' " +
                        "WHERE [BookID] = " + getBookID();
                data.execute(sql); //执行更新
                m.empower = empower;
            } finally {
                if (data != null) data.close(); //关闭数据对象
            }
        }
    }

    @Override
    public String getUpdateUrl() {
        return m.updateUrl;
    }

    @Override
    public String getRecommendUrl() {
        return m.recommendUrl;
    }

    @Override
    public void updateRecommendUrl(String url) {
        if (!url.equals(getRecommendUrl())) {
            IData data = null;

            try {
                data = DataFactory.createData(getContext()); //创建数据对象

                //更新字符串
                String sql = "UPDATE [Book] " +
                        "SET [RecommendUrl] = '" + url + "' " +
                        "WHERE [BookID] = " + getBookID();
                data.execute(sql); //执行更新
                m.recommendUrl = url;
            } finally {
                if (data != null) data.close(); //关闭数据对象
            }
        }
    }

    @Override
    public int getAdPlatform() {
        return m.adPlatform;
    }

    @Override
    public void updateAdPlatform(int platform) {
        if (platform > 0) {
            IData data = null;

            try {
                data = DataFactory.createData(getContext()); //创建数据对象

                //更新字符串
                String sql = "UPDATE [Book] " +
                        "SET [AdPlatform] = " + platform + " " +
                        "WHERE [BookID] = " + getBookID();
                data.execute(sql); //执行更新
                m.adPlatform = platform;
            } finally {
                if (data != null) data.close(); //关闭数据对象
            }
        }
    }

    @Override
    public String getAdAppID() {
        return m.adAppID;
    }

    @Override
    public void updateAdAppID(String appID) {
        if (!appID.equals(getAdAppID())) {
            IData data = null;

            try {
                data = DataFactory.createData(getContext()); //创建数据对象

                //更新字符串
                String sql = "UPDATE [Book] " +
                        "SET [AdAppID] = '" + appID + "' " +
                        "WHERE [BookID] = " + getBookID();
                data.execute(sql); //执行更新
                m.adAppID = appID;
            } finally {
                if (data != null) data.close(); //关闭数据对象
            }
        }
    }

    @Override
    public String getAdPlaceID() {
        return m.adPlaceID;
    }

    @Override
    public void updateAdPlaceID(String placeID) {
        if (!placeID.equals(getAdPlaceID())) {
            IData data = null;

            try {
                data = DataFactory.createData(getContext()); //创建数据对象

                //更新字符串
                String sql = "UPDATE [Book] " +
                        "SET [AdPlaceID] = '" + placeID + "' " +
                        "WHERE [BookID] = " + getBookID();
                data.execute(sql); //执行更新
                m.adPlaceID = placeID;
            } finally {
                if (data != null) data.close(); //关闭数据对象
            }
        }
    }

    @Override
    public String getStatisticsID() {
        return m.statisticsID;
    }

    @Override
    public void updateStatisticsID(String statisticsID) {
        if (!statisticsID.equals(getStatisticsID())) {
            IData data = null;

            try {
                data = DataFactory.createData(getContext()); //创建数据对象

                //更新字符串
                String sql = "UPDATE [Book] " +
                        "SET [StatisticsID] = '" + statisticsID + "' " +
                        "WHERE [BookID] = " + getBookID();
                data.execute(sql); //执行更新
                m.statisticsID = statisticsID;
            } finally {
                if (data != null) data.close(); //关闭数据对象
            }
        }
    }

    @Override
    public List<IBookCatalog> getCatalogList() {
        if (m.catalogList == null) {
            IData data = null;
            Cursor cursor = null;

            try {
                data = DataFactory.createData(getContext()); //创建数据对象

                //查询字符串
                String sql = "SELECT [CatalogID] FROM [BookCatalog] " +
                        "WHERE " +
                        "[BookID] = " + getBookID() + " AND " +
                        "[DisplayCatalog] = 1 " +
                        "ORDER BY [Index]";

                cursor = data.query(sql); //查询数据

                if(cursor.getCount() > 0) {
                    m.catalogList = new ArrayList<>(); //实例化书目录列表
                    for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        int catalogID = cursor.getInt(cursor.getColumnIndex("CatalogID")); //目录ID
                        IBookCatalog catalog = BookManager.createBookCatalog(getContext(), catalogID); //创建目录
                        m.catalogList.add(catalog); //添加到列表
                    }
                }
            } finally {
                if(cursor != null) cursor.close(); //关闭数据指针
                if(data != null) data.close(); //关闭数据对象
            }
        }

        return m.catalogList;
    }

    @Override
    public List<IBookCatalog> getAudioCatalogList() {
        if (m.audioCatalogList == null) {
            IData data = null;
            Cursor cursor = null;

            try {
                data = DataFactory.createData(getContext()); //创建数据对象

                //查询字符串
                String sql = "SELECT [CatalogID] FROM [BookCatalog] " +
                        "WHERE " +
                        "[BookID] = " + getBookID() + " AND " +
                        "[DisplayCatalog] = 1 AND " +
                        "[HasAudio] = 1 " +
                        "ORDER BY [Index]";

                cursor = data.query(sql); //查询数据

                if(cursor.getCount() > 0) {
                    m.audioCatalogList = new ArrayList<>(); //实例化书目录列表
                    for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        int catalogID = cursor.getInt(cursor.getColumnIndex("CatalogID")); //目录ID
                        IBookCatalog catalog = BookManager.createBookCatalog(getContext(), catalogID); //创建目录
                        m.audioCatalogList.add(catalog); //添加到列表
                    }
                }
            } finally {
                if(cursor != null) cursor.close(); //关闭数据指针
                if(data != null) data.close(); //关闭数据对象
            }
        }
        
        return m.audioCatalogList;
    }

//    @Override
//    public List<IBookCatalog> getAudioCatalogList() {
//        if (m.audioCatalogList == null) {
//            List<IBookCatalog> catalogList = getCatalogList(); //获取目录列表
//            if (catalogList != null) {
//                m.audioCatalogList = new ArrayList<>(); //实例化语音目录列表
//
//                //遍历加入有语音的目录到语音目录列表中
//                for (IBookCatalog catalog: catalogList) {
//                    if (catalog.hasAudio()) {
//                        m.audioCatalogList.add(catalog);
//                    }
//                }
//            }
//        }
//
//        return m.audioCatalogList;
//    }

    @Override
    public List<IBookContent> getContentList() {
        if (m.contentList == null) {
            IData data = null;
            Cursor cursor = null;

            try{
                data = DataFactory.createData(getContext()); //创建数据对象

                //查询字符串
                String sql = "SELECT [BookContent].[ContentID] " +
                        "FROM " +
                        "(" +
                        "[Book] INNER JOIN [BookCatalog] ON [Book].[BookID]=[BookCatalog].[BookID]" +
                        ") INNER JOIN " +
                        "[BookContent] ON [BookCatalog].[CatalogID]=[BookContent].[CatalogID] " +
                        "WHERE " +
                        "[Book].[BookID] = " + getBookID() + " " +
                        "ORDER BY " +
                        "[BookCatalog].[Index],[BookContent].[Page]";

                cursor = data.query(sql); //查询数据

                if(cursor.getCount() > 0) {
                    m.contentList = new ArrayList<>(); //实例化有声书内容列表
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        int contentID = cursor.getInt(cursor.getColumnIndex("ContentID")); //有声书内容ID
                        IBookContent content = BookManager.createBookContent(getContext(), contentID); //创建有声书内容
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
    public List<IBookMusic> getMusicList() {
        if (m.musicList == null) {
            IData data = null;
            Cursor cursor = null;

            try {
                data = DataFactory.createData(getContext()); //创建数据对象

                //查询字符串
                String sql = "SELECT [MusicID] FROM [BookMusic] " +
                        "WHERE [BookID] = " + getBookID() + " " +
                        "ORDER BY [Index]";

                cursor = data.query(sql); //查询数据

                if(cursor.getCount() > 0) {
                    m.musicList = new ArrayList<>(); //实例化书目录列表
                    for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        int musicID = cursor.getInt(cursor.getColumnIndex("MusicID")); //音乐ID
                        IBookMusic music = BookManager.createBookMusic(getContext(), musicID); //创建音乐
                        m.musicList.add(music); //添加到列表
                    }
                }
            } finally {
                if(cursor != null) cursor.close(); //关闭数据指针
                if(data != null) data.close(); //关闭数据对象
            }
        }

        return m.musicList;
    }

    /**
     * 载入
     * @param bookID 书ID
     */
    private void load(int bookID) {
        IData data = null; //数据对象
        Cursor cursor = null; //数据指针

        try {
            data = DataFactory.createData(getContext()); //创建数据对象
            String sql = "SELECT * FROM [Book] WHERE [BookID] = " + bookID; //查询字符串
            cursor = data.query(sql); //查询数据

            if(cursor.getCount() == 1) {
                cursor.moveToFirst(); //移动到首记录
                m.bookID = cursor.getInt(cursor.getColumnIndex("BookID")); //书ID
                m.bookName = cursor.getString(cursor.getColumnIndex("BookName")); //书名称
                m.bookDepict = cursor.getString(cursor.getColumnIndex("BookDepict")); //书描述
                m.bookStyle = cursor.getInt(cursor.getColumnIndex("BookStyle")); //书样式
                m.mediaType = cursor.getInt(cursor.getColumnIndex("MediaType")); //媒体类型
                m.mediaTitleLinkType = cursor.getInt(cursor.getColumnIndex("MediaTitleLinkType")); //媒体标题链接类型
                m.mediaIconLinkType = cursor.getInt(cursor.getColumnIndex("MediaIconLinkType")); //媒体图标链接类型
                m.allowFullScreen = cursor.getInt(cursor.getColumnIndex("AllowFullScreen")) != 0; //设置充许全屏
                m.fullScreenLinkType = cursor.getInt(cursor.getColumnIndex("FullScreenLinkType")); //全屏链接类型
                m.allowSynchronization = cursor.getInt(cursor.getColumnIndex("AllowSynchronization")) != 0; //设置充许同步复读
                m.synchronizationEnable = cursor.getInt(cursor.getColumnIndex("SynchronizationEnable")) != 0; //设置同步复读开关
                m.canHideToolbar = cursor.getInt(cursor.getColumnIndex("CanHideToolbar")) != 0; //是否能隐藏工具条
                m.currentContentID = cursor.getInt(cursor.getColumnIndex("CurrentContentID")); //当前内容ID
                m.currentAudioCatalogID = cursor.getInt(cursor.getColumnIndex("CurrentAudioCatalogID")); //当前语音目录ID
                m.allowZoomContentImage = cursor.getInt(cursor.getColumnIndex("AllowZoomContentImage")) != 0; //充许缩放内容图片
                m.allowUpdateCurrentAudioPosition = cursor.getInt(cursor.getColumnIndex("AllowUpdateCurrentAudioPosition")) != 0; //设置充许更新当前语音位置
                m.currentAudioPosition = cursor.getInt(cursor.getColumnIndex("CurrentAudioPosition")); //当前语音位置
                m.empower = cursor.getString(cursor.getColumnIndex("Empower")); //授权
                m.updateUrl = cursor.getString(cursor.getColumnIndex("UpdateUrl")); //更新Url地址
                m.recommendUrl = cursor.getString(cursor.getColumnIndex("RecommendUrl")); //推荐Url地址
                m.adPlatform = cursor.getInt(cursor.getColumnIndex("AdPlatform")); //广告平台
                m.adAppID = cursor.getString(cursor.getColumnIndex("AdAppID")); //广告应用ID
                m.adPlaceID = cursor.getString(cursor.getColumnIndex("AdPlaceID")); //广告位ID
                m.statisticsID = cursor.getString(cursor.getColumnIndex("StatisticsID")); //统计ID

                setCoverFilename(cursor.getString(cursor.getColumnIndex("CoverFilename"))); //封面图片文件名
                setTitleFilename(cursor.getString(cursor.getColumnIndex("TitleFilename"))); //标题图片文件名
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
     * 获取传入目录参数的上一条语音目录
     * @param catalog 目录
     * @return 目录
     */
    private IBookCatalog getPreviousAudioCatalog(IBookCatalog catalog) {
        IBookCatalog previousAudio = null;

        if (catalog.getCatalogID() == getFirstAudioCatalog().getCatalogID()) {
            //如果当前语音是复读起点语音，那么上一个语音就是复读终点语音
            previousAudio = getLastAudioCatalog();
        } else {
            //遍历查询上一个语音目录
            List<IBookCatalog> catalogList = getCatalogList(); //获取目录列表

            for (int i = catalogList.size() - 1; i >= 0 ; i--) {
                IBookCatalog bookCatalog = catalogList.get(i);
                if (bookCatalog.hasAudio() &&
                        bookCatalog.allowPlayAudio() &&
                        bookCatalog.getIndex() < catalog.getIndex()) {
                    previousAudio = bookCatalog;
                    break;
                }
            }
        }

        return previousAudio;
    }

    /**
     * 获取传入目录参数的下一条语音目录
     * @param catalog 目录
     * @return 目录
     */
    private IBookCatalog getNextAudioCatalog(IBookCatalog catalog) {
        IBookCatalog nextAudio = null;

        if (catalog.getCatalogID() == getLastAudioCatalog().getCatalogID()) {
            //如果当前语音是复读终点语音，那么下一个语音就是复读起点语音
            nextAudio = getFirstAudioCatalog();
        } else {
            //遍历查询下一个语音目录
            List<IBookCatalog> catalogList = getCatalogList(); //获取目录列表

            for (int i = 0; i < catalogList.size(); i++) {
                IBookCatalog bookCatalog = catalogList.get(i);
                if (bookCatalog.hasAudio() &&
                        bookCatalog.allowPlayAudio() &&
                        bookCatalog.getIndex() > catalog.getIndex()) {
                    nextAudio = bookCatalog;
                    break;
                }
            }
        }

        return nextAudio;
    }

    /**
     * 设置当前音乐
     * @param music 音乐
     */
    private void setCurrentMusic(IBookMusic music) {
        m.currentMusic = music;
    }

    /**
     * 设置封面图片文件名
     * @param filename 封面图片文件名
     */
    private void setCoverFilename(String filename) {
        if (filename != null && filename.length() > 0) {
            m.coverFilename = getOption().getResourcePath() + filename;
        } else {
            m.coverFilename = null;
        }
    }

    /**
     * 设置标题图片文件名
     * @param filename 标题图片文件名
     */
    private void setTitleFilename(String filename) {
        if (filename != null && filename.length() > 0) {
            m.titleFilename = getOption().getResourcePath() + filename;
        } else {
            m.titleFilename = null;
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
         * 书ID
         */
        int bookID;

        /**
         * 书名称
         */
        String bookName;

        /**
         * 书描述
         */
        String bookDepict;

        /**
         * 书样式
         */
        int bookStyle;

        /**
         * 媒体类型
         */
        int mediaType;

        /**
         * 媒体标题链接类型
         */
        int mediaTitleLinkType;

        /**
         * 媒体图标链接类型
         */
        int mediaIconLinkType;

        /**
         * 充许全屏
         */
        boolean allowFullScreen;

        /**
         * 全屏链接类型
         */
        int fullScreenLinkType;

        /**
         * 充许同步复读
         */
        boolean allowSynchronization;

        /**
         * 同步复读开关
         */
        boolean synchronizationEnable;

        /**
         * 是否能隐藏工具条
         */
        boolean canHideToolbar;

        /**
         * 当前书内容ID
         */
        int currentContentID;

        /**
         * 当前内容
         */
        IBookContent currentContent;

        /**
         * 当前语音目录ID
         */
        int currentAudioCatalogID;

        /**
         * 当前语音位置
         */
        int currentAudioPosition;

        /**
         * 充许缩放内容图片
         */
        boolean allowZoomContentImage;

        /**
         * 充许更新当前语音位置
         */
        boolean allowUpdateCurrentAudioPosition;

        /**
         * 封面图片文件名
         */
        String coverFilename;

        /**
         * 标题图片文件名
         */
        String titleFilename;

        /**
         * 授权
         */
        String empower;

        /**
         * 更新Url地址
         */
        String updateUrl;

        /**
         * 推荐Url地址
         */
        String recommendUrl;

        /**
         * 封面广告平台ID
         */
        int coverAdPid;

        /**
         * 列表广告平台ID
         */
        int listAdPid;

        /**
         * 帮助广告平台ID
         */
        int helpAdPid;

        /**
         * 全屏广告平台ID
         */
        int fullAdPid;

        /**
         * 广告平台
         */
        int adPlatform;

        /**
         * 广告应用ID
         */
        String adAppID;

        /**
         * 广告位ID
         */
        String adPlaceID;

        /**
         * 统计ID
         */
        String statisticsID;

        /**
         * 复读起点目录
         */
        IBookCatalog firstAudioCatalog;

        /**
         * 当前语音目录
         */
        IBookCatalog currentAudioCatalog;

        /**
         * 复读终点目录
         */
        IBookCatalog lastAudioCatalog;

        /**
         * 当前音乐
         */
        IBookMusic currentMusic;

        /**
         * 目录列表
         */
        List<IBookCatalog> catalogList;

        /**
         * 语音目录列表
         */
        List<IBookCatalog> audioCatalogList;

        /**
         * 内容列表
         */
        List<IBookContent> contentList;

        /**
         * 音乐列表
         */
        List<IBookMusic> musicList;
    }
}
