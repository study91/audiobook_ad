package com.study91.audiobook.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.BookMediaClient;
import com.study91.audiobook.book.BookMediaService;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookContent;
import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;

import java.util.List;

/**
 * 内容图片视图页
 */
public class ContentTitleViewPager extends ViewPager {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public ContentTitleViewPager(Context context) {
        super(context);
        init(); //初始化
    }

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param attrs 属性集合
     */
    public ContentTitleViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) { return; }
        init(); //初始化
    }

    @Override
    protected void onDetachedFromWindow() {
        //注销媒体客户端
        if (m.mediaClient != null) {
            getMediaClient().unregister();
        }

        super.onDetachedFromWindow();
    }

    /**
     * 设置单击事件监听器
     * @param listener 单击事件监听器
     */
    public void setOnSingleTapListener(OnSingleTapListener listener) {
        m.onSingleTapListener = listener;
    }

    /**
     * 单击
     */
    private void onSingleTap() {
        if (m.onSingleTapListener != null) {
            m.onSingleTapListener.onSingleTap();
        }
    }

    /**
     * 初始化
     */
    private void init() {
        setAdapter(new ContentViewPagerAdapter()); //设置适配器
        addOnPageChangeListener(new OnContentPageChangeListener()); //添加内容页改变事件监听器
        getMediaClient().register(); //注册媒体客户端
        getMediaClient().refresh(); //获取状态
    }

    /**
     * 获取内容列表
     * @return 内容列表
     */
    private List<IBookContent> getContentList() {
        if (m.contentList == null) {
            m.contentList = getBook().getContentList();
        }

        return m.contentList;
    }

    /**
     * 设置显示内容
     * @param content 内容
     */
    private void setDisplayContent(IBookContent content) {
        m.displayContent = content;
    }

    /**
     * 获取内容
     * @return 内容
     */
    private IBookContent getDisplayContent() {
        return m.displayContent;
    }

    /**
     * 显示同步语音内容
     * @param intent 意图
     */
    private void showSynchronizationAudioContent(Intent intent) {
        int position = intent.getExtras().getInt(BookMediaService.VALUE_AUDIO_POSITION); //获取语音位置
        IBookContent audioContent = getBook().getCurrentAudioCatalog().getAudioContent(position); //获取当前语音内容

        //如果当前语音内容有效且不是当前正在显示的内容时，遍历设置并显示当前正在复读的内容
        if (audioContent != null && (getDisplayContent() == null ||
                getDisplayContent().getContentID() != audioContent.getContentID())) {
            setDisplayContent(audioContent); //重置语音内容

            //遍历查询页号对应的记录号，并设置内容视图页为查询到的记录号
            List<IBookContent> contentList = getContentList();
            for (int i = 0; i < contentList.size(); i++) {
                IBookContent content = contentList.get(i); //获取内容
                if (content.getContentID() == getDisplayContent().getContentID()) {
                    setCurrentItem(i); //设置内容视图页
                    getBook().setCurrentContent(content); //设置当前内容
                    break;
                }
            }

            Log.e("Test", "ContentViewPager.showSynchronizationAudioContent:" +
                    "当前显示页=" + getDisplayContent().getPage() + "," +
                    "当前内容页=" + getBook().getCurrentContent().getPage());
        }
    }

    /**
     * 显示当前内容
     */
    private void showCurrentContent() {
        //如果当前内容不是正在显示的内容时，遍历设置并显示当前内容
        if (getDisplayContent() == null ||
                getDisplayContent().getContentID() != getBook().getCurrentContent().getContentID()) {
            setDisplayContent(getBook().getCurrentContent()); //重置当前内容

            //遍历查询页号对应的记录号，并设置内容视图页为查询到的记录号
            List<IBookContent> contentList = getContentList();
            for (int i = 0; i < contentList.size(); i++) {
                IBookContent content = contentList.get(i); //获取内容
                if (content.getContentID() == getDisplayContent().getContentID()) {
                    setCurrentItem(i); //设置内容视图页
                    break;
                }
            }

            Log.e("Test", "ContentViewPager.showCurrentContent():" +
                    "当前显示页=" + getDisplayContent().getPage() + "," +
                    "当前内容页=" + getBook().getCurrentContent().getPage());
        }
    }

    /**
     * 获取全局书
     * @return 全局书
     */
    private IBook getBook() {
        return BookManager.getBook(getContext());
    }

    /**
     * 获取全局配置
     * @return 全局配置
     */
    private IOption getOption() {
        return OptionManager.getOption(getContext());
    }

    /**
     * 获取媒体客户端
     * @return 媒体客户端
     */
    private BookMediaClient getMediaClient() {
        if (m.mediaClient == null) {
            m.mediaClient = new MediaClient(getContext());
        }

        return m.mediaClient;
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 内容列表
         */
        List<IBookContent> contentList;

        /**
         * 显示内容
         */
        IBookContent displayContent;

        /**
         * 媒体客户端
         */
        BookMediaClient mediaClient;

        boolean hasMeasured = false;

        /**
         * 单击事件监听器
         */
        OnSingleTapListener onSingleTapListener;
    }

    /**
     * 媒体客户端
     */
    private class MediaClient extends BookMediaClient {
        /**
         * 构造器
         * @param context 应用程序上下文
         */
        public MediaClient(Context context) {
            super(context);
        }

        @Override
        public void setOnReceive(Intent intent) {
            //充许同步时执行
//            if (getBook().allowSynchronization()) {
//                //如果是同步复读状态时同步显示复读内容
//                if (getBook().synchronizationEnable()) {
//                    showSynchronizationAudioContent(intent); //显示同步语音内容
//                } else {
//                    showCurrentContent(); //显示当前内容
//                }
//            }
        }
    }

    /**
     * 内容视图页适配器
     */
    private class ContentViewPagerAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            String title = getBook().getCurrentAudioCatalog().getTitle();

            TextView textView = new TextView(getContext());
            textView.setText(title);

            return textView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return getContentList().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    /**
     * 内容页改变事件监听器
     */
    private class OnContentPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            List<IBookContent> contentList = getContentList(); //获取内容列表
            IBookContent content = contentList.get(position); //获取显示的内容

            //当显示的内容和全局书的当前内容不相同时重置全局书的当前内容（更新数据库记录）
            if (content.getContentID() != getDisplayContent().getContentID()) {
                //如果是同步复读状态且当前内容与语音内容不相同时，取消同步复读状态
                if (getBook().synchronizationEnable()) {
                    getBook().setSynchronizationEnable(false); //关闭同步复读状态
                    getMediaClient().refresh(); //刷新
                }

                getBook().setCurrentContent(content); //重置当前内容
                setDisplayContent(content); //重置显示内容

                getMediaClient().refresh(); //刷新客户端

                Log.e("Test", "ContentViewPager.OnContentPageChangeListener:" +
                        "当前显示页=" + getDisplayContent().getPage() + "," +
                        "当前内容页=" + getBook().getCurrentContent().getPage());
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
