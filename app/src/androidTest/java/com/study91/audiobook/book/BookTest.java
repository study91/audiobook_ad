package com.study91.audiobook.book;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.util.List;

/**
 * 书测试
 */
public class BookTest extends ApplicationTestCase<Application> {
    /**
     * 构造器
     */
    public BookTest() {
        super(Application.class);
    }

    /**
     * 测试全局书
     */
    public void testGetBook() {
        IBook book = BookManager.getBook(getContext());
        showBook(book);
    }

    /**
     * 测试图书馆
     */
    public void testLibrary() {
        ILibrary library = BookManager.createLibrary(getContext());
        List<IBook> bookList = library.getBookList();
        for (IBook book : bookList) {
            Log.e("Test", book.getBookID() + "." + book.getBookName());
        }
    }

    /**
     * 测试书
     */
    public void testBook() {
        IBook book = BookManager.createBook(getContext(), 20);
        showBook(book);
    }

    /**
     * 显示书
     * @param book 书
     */
    private void showBook(IBook book) {
        Log.e("Test", "****** 书测试 ******");
        Log.e("Test", "书ID：" + book.getBookID());
        Log.e("Test", "书名称：" + book.getBookName());
        Log.e("Test", "书样式：" + book.getBookStyle());
        Log.e("Test", "媒体类型：" + book.getMediaType());
        Log.e("Test", "媒体标题链接类型：" + book.getMediaTitleLinkType());
        Log.e("Test", "媒体图标链接类型：" + book.getMediaIconLinkType());
        Log.e("Test", "充许全屏：" + book.allowFullScreen());
        Log.e("Test", "全屏链接类型：" + book.getFullScreenLinkType());
        Log.e("Test", "充许同步复读：" + book.allowSynchronization());
        Log.e("Test", "同步复读开关：" + book.synchronizationEnable());
        Log.e("Test", "是否能隐藏工具条：" + book.canHideToolbar());
        Log.e("Test", "当前内容ID：" + book.getCurrentContentID());
        Log.e("Test", "当前语音目录ID：" + book.getCurrentAudioCatalogID());
        Log.e("Test", "当前语音目录：" + book.getCurrentAudioCatalog().getTitle());
        Log.e("Test", "当前语音文件：" + book.getCurrentAudioCatalog().getAudioFilename());
        Log.e("Test", "充许缩放内容图片：" + book.allowZoomContentImage());
        Log.e("Test", "充许更新当前语音位置：" + book.allowUpdateCurrentAudioPosition());
        Log.e("Test", "当前语音位置：" + book.getCurrentAudioPosition());
        Log.e("Test", "封面图片文件名：" + book.getCoverFilename());
        Log.e("Test", "标题图片文件名：" + book.getTitleFilename());
        Log.e("Test", "授权：" + book.getEmpower());
        Log.e("Test", "更新Url地址：" + book.getUpdateUrl());
        Log.e("Test", "推荐Url地址：" + book.getRecommendUrl());
        Log.e("Test", "广告平台：" + book.getAdPlatform());
        Log.e("Test", "广告应用ID：" + book.getAdAppID());
        Log.e("Test", "广告位ID：" + book.getAdPlaceID());
        Log.e("Test", "书描述：" + book.getBookDepict());

        if (book.getCurrentMusic() != null) {
            Log.e("Test", "当前音乐文件：" + book.getCurrentMusic().getMusicFilename());
            book.moveToNextMusic();
            Log.e("Test", "下一首音乐：" + book.getCurrentMusic().getMusicFilename());
            book.moveToNextMusic();
            Log.e("Test", "下一首音乐：" + book.getCurrentMusic().getMusicFilename());
            book.moveToNextMusic();
            Log.e("Test", "下一首音乐：" + book.getCurrentMusic().getMusicFilename());
            book.moveToNextMusic();
            Log.e("Test", "下一首音乐：" + book.getCurrentMusic().getMusicFilename());
        }

        List<IBookCatalog> catalogList = book.getCatalogList();
        if (catalogList != null) {
            Log.e("Test", "*** 目录 ***");
            for (int i = 0; i < catalogList.size(); i++) {
                IBookCatalog catalog = catalogList.get(i);
                String msg = "【";
                if (catalog.allowPlayAudio()) msg += "-可播放-";
                if (catalog.displayIcon()) msg += "-显示图标-";
                if (catalog.displayPage()) msg += "-显示页码-";
                msg += "】";
                Log.e("Test", "    " +
                        catalog.getIndex() + "." +
                        catalog.getPage() + "." +
                        catalog.getTitle() + "-" +
                        catalog.getIconFilename() + msg);
            }
        } else {
            Log.e("Test", "*** 没有目录 ***");
        }

        List<IBookCatalog> audioCatalogList = book.getAudioCatalogList();
        if (audioCatalogList != null) {
            Log.e("Test", "*** 语音目录 ***");
            for (int i = 0; i < audioCatalogList.size(); i++) {
                IBookCatalog catalog = audioCatalogList.get(i);
                String msg = "【";
                if (catalog.allowPlayAudio()) msg += "-可播放-";
                if (catalog.displayIcon()) msg += "-显示图标-";
                if (catalog.displayPage()) msg += "-显示页码-";
                msg += "】";
                Log.e("Test", "    " +
                        catalog.getIndex() + "." +
                        catalog.getPage() + "." +
                        catalog.getTitle() + "-" +
                        catalog.getIconFilename() + msg);
            }
        } else {
            Log.e("Test", "*** 没有语音目录 ***");
        }

        List<IBookContent> contentList = book.getContentList();
        if (contentList != null) {
            Log.e("Test", "*** 内容 ***");
            for (int i = 0; i < contentList.size(); i++) {
                IBookContent content = contentList.get(i);
                Log.e("Test", "    " + content.getPage() + "." + content.getImageFilename());
            }
        } else {
            Log.e("Test", "*** 没有内容 ***");
        }

        List<IBookMusic> musicList = book.getMusicList();
        if (musicList != null) {
            Log.e("Test", "*** 音乐 ***");
            for (int i = 0; i < musicList.size(); i++) {
                IBookMusic music = musicList.get(i);
                Log.e("Test", "    " + music.getIndex() + "." + music.getMusicName());
            }
        } else {
            Log.e("Test", "*** 没有音乐 ***");
        }
    }

    /**
     * 测试目录
     */
    public void testBookCatalog() {
        IBookCatalog catalog = BookManager.createBookCatalog(getContext(), 2674);
        Log.e("Test", "****** 目录测试 ******");
        Log.e("Test", "目录ID：" + catalog.getCatalogID());
        Log.e("Test", "书ID：" + catalog.getBookID());
        Log.e("Test", "书名称：" + catalog.getBook().getBookName());
        Log.e("Test", "索引：" + catalog.getIndex());
        Log.e("Test", "页码：" + catalog.getPage());
        Log.e("Test", "目录标题：" + catalog.getTitle());
        Log.e("Test", "显示目录：" + catalog.displayCatalog());
        Log.e("Test", "显示内容：" + catalog.displayContent());
        Log.e("Test", "显示解释：" + catalog.displayExplain());
        Log.e("Test", "显示页码：" + catalog.displayPage());
        Log.e("Test", "显示图标：" + catalog.displayIcon());
        Log.e("Test", "图标文件名：" + catalog.getIconFilename());
        Log.e("Test", "是否知识点：" + catalog.isKnowledgePoint());
        Log.e("Test", "熟悉级别：" + catalog.getFamiliarLevel());
        Log.e("Test", "有语音：" + catalog.hasAudio());
        Log.e("Test", "充许播放语音：" + catalog.allowPlayAudio());
        Log.e("Test", "语音文件名：" + catalog.getAudioFilename());
//        Log.e("Test", "原文：" + catalog.getOriginal());
//        Log.e("Test", "详解：" + catalog.getExplain());

        List<IBookContent> contentList = catalog.getContentList();
        if (contentList != null) {
            Log.e("Test", "*** 内容 ***");
            for (int i = 0; i < contentList.size(); i++) {
                IBookContent content = contentList.get(i);
                Log.e("Test", "    " + content.getPage() + "." + content.getImageFilename());
            }
        } else {
            Log.e("Test", "*** 没有内容 ***");
        }

        List<IBookContent> audioContentList = catalog.getAudioContentList();
        if (audioContentList != null) {
            Log.e("Test", "*** 语音内容 ***");
            for (int i = 0; i < audioContentList.size(); i++) {
                IBookContent content = audioContentList.get(i);
                Log.e("Test", "    " + content.getPage() + "." + content.getImageFilename() +
                        "(" + content.getAudioStartTime() + ")");
            }

            Log.e("Test", "语音内容(80000)：" + catalog.getAudioContent(80000).getPage() + "页");
        }
    }

    /**
     * 测试内容
     */
    public void testBookContent() {
        IBookContent content = BookManager.createBookContent(getContext(), 21);

        Log.e("Test", "****** 内容测试 ******");
        Log.e("Test", "内容ID：" + content.getContentID());
        Log.e("Test", "目录ID：" + content.getCatalogID());
        Log.e("Test", "目录标题：" + content.getCatalog().getTitle());
        Log.e("Test", "页号：" + content.getPage());
        Log.e("Test", "有语音：" + content.hasAudio());
        Log.e("Test", "语音开始时间：" + content.getAudioStartTime());
        Log.e("Test", "图片文件名：" + content.getImageFilename());
    }

    /**
     * 测试音乐
     */
    public void testBookMusic() {
        IBookMusic music = BookManager.createBookMusic(getContext(), 2);
        Log.e("Test", "****** 音乐测试 ******");
        Log.e("Test", "音乐ID：" + music.getMusicID());
        Log.e("Test", "书ID：" + music.getBookID());
        Log.e("Test", "音乐索引：" + music.getIndex());
        Log.e("Test", "音乐名称：" + music.getMusicName());
        Log.e("Test", "音乐文件名：" + music.getMusicFilename());
    }
}
