package com.study91.audiobook.book;

import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * 书接口
 */
public interface IBook {
    /**
     * 媒体类型（有语音和音乐）
     */
    static final int MEDIA_TYPE_AUDIO_AND_MUSIC = 1;

    /**
     * 媒体类型（只有语音）
     */
    static final int MEDIA_TYPE_ONLY_AUDIO = 2;

    /**
     * 媒体类型（语音左声道，音乐右声道）
     */
    static final int MEDIA_TYPE_AUDIO_LEFT = 3;

    /**
     * 媒体类型（语音右声道，音乐左声道）
     */
    static final int MEDIA_TYPE_AUDIO_RIGHT = 4;

    /**
     * 媒体链接类型（无链接）
     */
    static final int MEDIA_LINK_TYPE_NONE = 0;

    /**
     * 媒体链接类型（同步）
     */
    static final int MEDIA_LINK_TYPE_SYNCHRONIZATION = 1;

    /**
     * 媒体链接类型（标题+原文+详解，易经听读类型）
     */
    static final int MEDIA_LINK_TYPE_EXPLAIN1 = 2;

    /**
     * 媒体链接类型（原文+详解，三字经类型）
     */
    static final int MEDIA_LINK_TYPE_EXPLAIN2 = 3;

    /**
     * 媒体链接类型（原文全屏）
     */
    static final int MEDIA_LINK_TYPE_FULL_ORIGINAL = 4;

    /**
     * 获取书ID
     */
    int getBookID();

    /**
     * 获取书名称
     */
    String getBookName();

    /**
     * 获取书描述
     */
    String getBookDepict();

    /**
     * 获取书样式（1=有内容图片 2=无内容图片）
     */
    int getBookStyle();

    /**
     * 获取媒体类型（0=无声音 1=完整语音和音乐 2=只有语音 3=语音左声道 4=语音右声道）
     */
    int getMediaType();

    /**
     * 媒体标题链接类型（0=无链接 1=主窗口 2=内容窗口 3=全屏窗口）
     */
    int getMediaTitleLinkType();

    /**
     * 媒体图标链接类型（0=无链接 1=主窗口 2=内容窗口 3=全屏窗口）
     */
    int getMediaIconLinkType();

    /**
     * 充许全屏
     */
    boolean allowFullScreen();

    /**
     * 全屏链接类型（0=无链接 1=主窗口 2=内容窗口 3=全屏窗口）
     */
    int getFullScreenLinkType();

    /**
     * 充许同步复读（true=充许，false=不充许）
     */
    boolean allowSynchronization();

    /**
     * 设置同步复读开关
     * @param synchronizationEnable 开关值（true=打开，false=关闭）
     */
    void setSynchronizationEnable(boolean synchronizationEnable);

    /**
     * 同步复读开关
     * @return true=打开，false=关闭
     */
    boolean synchronizationEnable();

    /**
     * 是否能隐藏工具条（true=能隐藏 false=不能隐藏）
     */
    boolean canHideToolbar();

    /**
     * 设置当前内容ID
     */
    void setCurrentContentID(int contentID);

    /**
     * 获取当前内容ID
     */
    int getCurrentContentID();

    /**
     * 设置当前内容
     */
    void setCurrentContent(IBookContent content);

    /**
     * 获取当前内容
     */
    IBookContent getCurrentContent();

    /**
     * 设置当前语音目录ID
     */
    void setCurrentAudioCatalogID(int catalogID);

    /**
     * 获取当前语音目录ID
     */
    int getCurrentAudioCatalogID();

    /**
     * 设置当前语音目录
     */
    void setCurrentAudioCatalog(IBookCatalog catalog);

    /**
     * 获取当前语音目录
     */
    IBookCatalog getCurrentAudioCatalog();

    /**
     * 充许缩放内容图片
     */
    boolean allowZoomContentImage();

    /**
     * 充许更新当前语音位置
     */
    boolean allowUpdateCurrentAudioPosition();

    /**
     * 更新当前语音位置
     * @param position 语音位置
     */
    void updateCurrentAudioPosition(int position);

    /**
     * 获取当前语音位置
     */
    int getCurrentAudioPosition();

    /**
     * 移动到上条内容
     */
    void moveToPreviousContent();

    /**
     * 移动到下条内容
     */
    void moveToNextContent();

    /**
     * 设置复读起点目录
     */
    void setFirstAudioCatalog(IBookCatalog catalog);

    /**
     * 获取复读起点目录
     */
    IBookCatalog getFirstAudioCatalog();

    /**
     * 设置复读终点目录
     */
    void setLastAudioCatalog(IBookCatalog catalog);

    /**
     * 获取复读终点目录
     */
    IBookCatalog getLastAudioCatalog();

    /**
     * 移动到上一个语音目录
     */
    void moveToPreviousAudioCatalog();

    /**
     * 移动到下一个语音目录
     */
    void moveToNextAudioCatalog();

    /**
     * 重置充许播放语音
     * 对传入的目录参数的allowPlayAudio布尔值进行开关设置
     * 例如：如果allowPlayAudio=true，那么重置为false，如果allowPlayAudio=false，那么重置为true
     * @param catalog 目录
     */
    void resetAllowPlayAudio(IBookCatalog catalog);

    /**
     * 获取当前音乐
     */
    IBookMusic getCurrentMusic();

    /**
     * 移动到下一首音乐
     */
    void moveToNextMusic();

    /**
     * 获取封面图片文件名
     */
    String getCoverFilename();

    /**
     * 获取封面Drawable
     */
    Drawable getCoverDrawable();

    /**
     * 获取标题图片文件名
     */
    String getTitleFilename();

    /**
     * 获取标题Drawable
     */
    Drawable getTitleDrawable();

    /**
     * 获取授权信息
     */
    String getEmpower();

    /**
     * 更新授权信息
     * @param empower 授权信息
     */
    void updateEmpower(String empower);

    /**
     * 获取更新Url地址
     */
    String getUpdateUrl();

    /**
     * 获取推荐Url地址
     */
    String getRecommendUrl();

    /**
     * 更新推荐Url地址
     * @param url Url地址
     */
    void updateRecommendUrl(String url);

    /**
     * 获取广告平台（1=安沃）
     */
    int getAdPlatform();

    /**
     * 更新广告平台
     * @param platform 广告平台（1=安沃）
     */
    void updateAdPlatform(int platform);

    /**
     * 获取广告应用ID
     */
    String getAdAppID();

    /**
     * 更新广告的应用ID
     * @param appID 应用ID
     */
    void updateAdAppID(String appID);

    /**
     * 获取广告位ID
     */
    String getAdPlaceID();

    /**
     * 更新广告的广告位ID
     * @param placeID 广告位ID
     */
    void updateAdPlaceID(String placeID);

    /**
     * 获取统计ID
     * @return 统计ID
     */
    String getStatisticsID();

    /**
     * 更新统计ID
     * @param statisticsID 统计ID
     */
    void updateStatisticsID(String statisticsID);

    /**
     * 获取目录列表
     */
    List<IBookCatalog> getCatalogList();

    /**
     * 获取语音目录列表
     */
    List<IBookCatalog> getAudioCatalogList();

    /**
     * 获取内容列表
     */
    List<IBookContent> getContentList();

    /**
     * 获取音乐列表
     */
    List<IBookMusic> getMusicList();
}
