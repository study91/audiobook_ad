package com.study91.audiobook.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.study91.audiobook.R;
import com.study91.audiobook.ad.AdManager;
import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.BookMediaClient;
import com.study91.audiobook.book.BookMediaService;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookCatalog;
import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;
import com.study91.audiobook.view.ContentViewPager1;

/**
 * 详解窗口
 */
public class ContentActivity1 extends Activity {
    private final int SHOW_TYPE_ORIGINAL = 0; //显示原文
    private final int SHOW_TYPE_EXPLAIN = 1; //显示详解

    private Field m = new Field(); //私有字段
    private UI ui = new UI(); //界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content1);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); //设置为竖屏显示

        //载入控件
        ui.backButton = (Button) findViewById(R.id.backButton); //返回按钮
        ui.contentRadioGroup = (RadioGroup) findViewById(R.id.contentRadioGroup); //内容单选接钮组
        ui.originalRadioButton = (RadioButton) findViewById(R.id.originalRadioButton); //原文单选接钮
        ui.explainRadioButton = (RadioButton) findViewById(R.id.explainRadioButton); //详解单选接钮
        ui.smallFontButton = (Button) findViewById(R.id.smallFontButton); //缩小字体按钮
        ui.largeFontButton = (Button) findViewById(R.id.largeFontButton); //放大字体按钮
        ui.playButton = (Button) findViewById(R.id.playButton); //播放按钮
        ui.contentViewPager = (ContentViewPager1) findViewById(R.id.contentViewPager); //内容视图页

        //设置控件
        ui.contentViewPager.setCurrentItem(getPosition()); //设置内容视图页的当前位置
        ui.contentViewPager.addOnPageChangeListener(new OnContentPageChangeListener()); //内容视图页变化监听器
        ui.smallFontButton.setOnClickListener(new OnSmallFontClickListener()); //缩小字体按钮单击事件监听器
        ui.largeFontButton.setOnClickListener(new OnLargeFontClickListener()); //放大字体按钮单击事件监听器
        ui.playButton.setOnClickListener(new OnPlayClickListener()); //播放按钮单击事件监听器
        ui.backButton.setOnClickListener(new OnBackClickListener()); //返回按钮单击事件监听器

        //设置内容多选按钮改变事件监听器
        ui.contentRadioGroup.setOnCheckedChangeListener(new OnContentCheckedChangeListener());
        ui.originalRadioButton.setChecked(true); //默认为选择原文按钮

        //添加广告
//        ui.adLayout = (RelativeLayout) findViewById(R.id.adLayout); //广告布局
//        ui.adLayout.addView(AdManager.getAd(this).getBannerView()); //添加横幅广告

        m.mediaClient = new MediaClient(this); //媒体客户端
        m.mediaClient.register(); //注册媒体客户端
    }

    @Override
    protected void onDestroy() {
        //注销媒体客户端
        if (m.mediaClient != null) {
            m.mediaClient.unregister();
        }

        //释放广告资源
        AdManager.release();

        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        m.position = getIntent().getExtras().getInt("Position"); //读取位置
        ui.contentViewPager.setCurrentItem(getPosition()); //设置内容视图页的当前位置
        super.onNewIntent(intent);
    }

    /**
     * 设置位置
     * @param position 位置
     */
    private void setPosition(int position) {
        m.position = position;
    }

    /**
     * 获取位置
     * @return 位置
     */
    private int getPosition() {
        if (m.position < 0) {
            m.position = getIntent().getExtras().getInt("Position"); //读取位置
        }

        return m.position;
    }

    /**
     * 设置显示类型
     * @param showType 类型（0=显示原文 1=显示详解）
     */
    private void setShowType(int showType) {
        m.showType = showType;
    }

    /**
     * 显示类型
     * @return 类型（0=显示原文 1=显示详解）
     */
    private int getShowType() {
        return m.showType;
    }

    /**
     * 全局书
     */
    private IBook getBook() {
        return BookManager.getBook(this);
    }

    /**
     * 获取全局选项
     */
    private IOption getOption() {
        return OptionManager.getOption(this);
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 目录位置
         */
        int position = -1;

        /**
         * 显示类型
         */
        int showType;

        /**
         * 是否正在播放
         */
        boolean isPlaying;

        /**
         * 媒体客户端
         */
        BookMediaClient mediaClient;
    }

    /**
     * 私有界面类
     */
    private class UI {
        /**
         * 广告布局
         */
        RelativeLayout adLayout;

        /**
         * 返回按钮
         */
        Button backButton;

        /**
         * 内容单选按钮组
         */
        RadioGroup contentRadioGroup;

        /**
         * 原文单选接钮
         */
        RadioButton originalRadioButton;

        /**
         * 详解单选按钮
         */
        RadioButton explainRadioButton;

        /**
         * 原文按钮
         */
//        Button originalButton;

        /**
         * 详解按钮
         */
//        Button explainButton;

        /**
         * 缩小字体按钮
         */
        Button smallFontButton;

        /**
         * 放大字体按钮
         */
        Button largeFontButton;

        /**
         * 播放按钮
         */
        Button playButton;

        /**
         * 内容视图页
         */
        ContentViewPager1 contentViewPager;
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
            Bundle bundle = intent.getExtras();
            m.isPlaying = bundle.getBoolean(BookMediaService.VALUE_IS_PLAYING); //获取是否正在播放

            //设置播放图标
            ui.playButton.setBackgroundResource(R.drawable.button_play);
            IBookCatalog catalog = getBook().getCatalogList().get(getPosition()); //获取当前目录
            if (catalog.getCatalogID() == getBook().getCurrentAudioCatalogID() && m.isPlaying) {
                //如果是当前语音目录并且正在播放时，设置为暂停播放图标
                ui.playButton.setBackgroundResource(R.drawable.button_pause);
            }
        }
    }

    /**
     * 返回按钮单击事件监听器
     */
    private class OnBackClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    /**
     * 内容改变事件监听器
     */
    private class OnContentCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == ui.originalRadioButton.getId()) {
                setShowType(SHOW_TYPE_ORIGINAL);
                refreshContentViewPager();
            } else if (checkedId == ui.explainRadioButton.getId()) {
                setShowType(SHOW_TYPE_EXPLAIN);
                refreshContentViewPager();
            }
        }
    }

    /**
     * 缩小字体按钮单击事件监听骂
     */
    private class OnSmallFontClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int fontSize = getOption().getContentFontSize() - 2;

            if (fontSize > 0) {
                setFontSize(getPosition(), fontSize);

                if(getPosition() > 0) {
                    setFontSize(getPosition() - 1, fontSize);
                }

                if (getPosition() < getBook().getCatalogList().size() - 1) {
                    setFontSize(getPosition() + 1, fontSize);
                }

                getOption().setContentFontSize(fontSize); //更新内容字体大小
            }
        }
    }

    /**
     * 放大字体按钮单击事件监听器
     */
    private class OnLargeFontClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int fontSize = getOption().getContentFontSize() + 2;

            if (fontSize > 0) {
                setFontSize(getPosition(), fontSize);

                if(getPosition() > 0) {
                    setFontSize(getPosition() - 1, fontSize);
                }

                if (getPosition() < getBook().getCatalogList().size() - 1) {
                    setFontSize(getPosition() + 1, fontSize);
                }

                getOption().setContentFontSize(fontSize); //更新内容字体大小
            }

        }
    }

    /**
     * 播放按钮单击事件监听器
     */
    private class OnPlayClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            IBookCatalog catalog = getBook().getCatalogList().get(getPosition());
            if (catalog.getCatalogID() == getBook().getCurrentAudioCatalogID()) {
                if (m.isPlaying) {
                    m.mediaClient.pause();
                } else {
                    m.mediaClient.play();
                }
            } else {
                getBook().setCurrentAudioCatalog(catalog);
                m.mediaClient.play();
            }
        }
    }

    /**
     * 刷新视图页
     */
    private void refreshContentViewPager() {
        refreshContentViewPager(getPosition());
        refreshContentViewPager(getPosition() - 1);
        refreshContentViewPager(getPosition() + 1);
        refreshPlayButton();
    }

    /**
     * 刷新播放按钮
     */
    private void refreshPlayButton() {
        //判断是否显示播放按钮
        IBookCatalog catalog = getBook().getCatalogList().get(getPosition()); //获取当前目录
        if (!catalog.hasAudio() || !catalog.allowPlayAudio()) {
            ui.playButton.setVisibility(View.GONE);
        } else {
            ui.playButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 刷新内容视图页
     * @param position 位置
     */
    private void refreshContentViewPager(int position) {
        if (position >= 0) {
            //原文滚动条
            ScrollView originalScrollView =
                    (ScrollView) ui.contentViewPager.findViewWithTag("OriginalScrollView" + position);

            //详解滚动条
            ScrollView explainScrollView =
                    (ScrollView) ui.contentViewPager.findViewWithTag("ExplainScrollView" + position);

            //显示内容
            switch (getShowType()) {
                case SHOW_TYPE_ORIGINAL: //显示原文
                    if (originalScrollView != null) originalScrollView.setVisibility(View.VISIBLE);
                    if (explainScrollView != null) explainScrollView.setVisibility(View.GONE);
                    break;
                case SHOW_TYPE_EXPLAIN: //显示详解
                    if (originalScrollView != null) originalScrollView.setVisibility(View.GONE);
                    if (explainScrollView != null) explainScrollView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }


    /**
     * 设置字体大小
     * @param position 位置
     * @param fontSize 字体大小
     */
    private void setFontSize(int position, int fontSize) {
        if (position >= 0 && fontSize > 0) {
            TextView pageTextView = (TextView) ui.contentViewPager.findViewWithTag("PageTextView" + position);
            TextView titleTextView = (TextView) ui.contentViewPager.findViewWithTag("TitleTextView" + position);
            TextView originalTextView = (TextView) ui.contentViewPager.findViewWithTag("OriginalTextView" + position);
            TextView explainTextView = (TextView) ui.contentViewPager.findViewWithTag("ExplainTextView" + position);

            pageTextView.setTextSize(fontSize);
            titleTextView.setTextSize(fontSize);
            originalTextView.setTextSize(fontSize);
            explainTextView.setTextSize(fontSize);
        }
    }

    /**
     * 内容页改变事件监听器
     */
    private class OnContentPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setPosition(position);
            refreshContentViewPager();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
