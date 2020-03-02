package com.study91.audiobook.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.study91.audiobook.R;
import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.BookMediaClient;
import com.study91.audiobook.book.BookMediaService;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookCatalog;
import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;

import java.util.List;

/**
 * 目录视图适配器
 * 注：客户端退出时需要调用unregister()方法注销目录适配器
 */
class AudioCatalogViewAdapter extends BaseExpandableListAdapter{
    private Field m = new Field(); //字段
    private UI ui = new UI(); //界面

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public AudioCatalogViewAdapter(Context context) {
        m.context = context; //应用程序上下文
        setCurrentCatalogID(getBook().getCurrentAudioCatalog().getCatalogID()); //设置当前目录ID
        getMediaClient().register(); //注册媒体客户端
    }

    /**
     * 注销目录视图适配器
     */
    public void unregister() {
        //注销媒体客户端
        if (m.mediaClient != null) {
            getMediaClient().unregister();
        }
    }

    @Override
    public int getGroupCount() {
        return getCatalogList().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return getCatalogList().get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return getCatalogList().get(groupPosition).getCatalogID();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //载入列表组布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.audio_catalog_group_view, parent, false);

        IBookCatalog catalog = getCatalogList().get(groupPosition); //获取书目录

        //加载控件
        ui.group.iconImageView = (ImageView) view.findViewById(R.id.iconImageView); //图标
        ui.group.pageTextView = (TextView) view.findViewById(R.id.pageTextView); //页码
        ui.group.titleTextView = (TextView) view.findViewById(R.id.titleTextView); //标题
        ui.group.choiceButton = (Button) view.findViewById(R.id.choiceButton); //选择按钮

        //设置图标
        if (catalog.displayIcon()) {
            ui.group.iconImageView.setImageDrawable(catalog.getIconDrawable());
        } else {
            ui.group.iconImageView.setVisibility(View.INVISIBLE);
        }

        //目录页码
        if (catalog.displayPage()) {
            ui.group.pageTextView.setText(String.valueOf(catalog.getPage())); //显示页号
            ui.group.pageTextView.setTextSize(getOption().getCatalogFontSize()); //字体大小
            ui.group.iconImageView.setVisibility(View.VISIBLE); //显示默认的图标
        } else {
            ui.group.pageTextView.setVisibility(View.INVISIBLE); //不显示图标
        }

        //选择按钮
        ui.group.choiceButton.setFocusable(false);
        if (catalog.allowPlayAudio()) {
            ui.group.choiceButton.setBackgroundResource(R.mipmap.catalog_group_choice_enable);
        } else {
            ui.group.choiceButton.setBackgroundResource(R.mipmap.catalog_group_choice_disable);
        }

        //设置单击事件监听器
        ui.group.choiceButton.setOnClickListener(new OnChoiceButtonClickListener(catalog));

        //设置当前项背景色
        if (catalog.getCatalogID() == getBook().getCurrentAudioCatalog().getCatalogID()) {
            view.setBackgroundResource(R.color.catalog_group_current);
            //设置是否正在播放的图标
            if (isPlaying()) { //正在播放时，设置为暂停图标
                ui.group.choiceButton.setBackgroundResource(R.drawable.catalog_group_pause);
            } else { //暂停播放时，设置为播放图标
                ui.group.choiceButton.setBackgroundResource(R.drawable.catalog_group_play);
            }
        }

        //标题
        ui.group.titleTextView.setText(catalog.getTitle());
        ui.group.titleTextView.setTextSize(getOption().getCatalogFontSize()); //标题字体大小

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * 获取应用程序上下文
     */
    private Context getContext() {
        return m.context;
    }

    /**
     * 获取全局书
     */
    private IBook getBook() {
        return BookManager.getBook(getContext());
    }

    /**
     * 获取目录列表
     */
    private List<IBookCatalog> getCatalogList() {
        return getBook().getAudioCatalogList();
    }

    /**
     * 获取全局选项
     */
    private IOption getOption() {
        return OptionManager.getOption(getContext());
    }

    /**
     * 设置当前目录ID
     */
    private void setCurrentCatalogID(int catalogID) {
        if (catalogID != getCurrentCatalogID()) {
            m.currentCatalogID = catalogID;
        }
    }

    /**
     * 获取当前目录ID
     */
    private int getCurrentCatalogID() {
        return m.currentCatalogID;
    }

    /**
     * 设置是否正在播放
     * @param isPlaying true=正在播放，false=没有播放
     */
    private void setIsPlaying(boolean isPlaying) {
        m.isPlaying = isPlaying;
    }

    /**
     * 是否正在播放
     * @return true=正在播放，false=没有播放
     */
    private boolean isPlaying() {
        return m.isPlaying;
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
         * 应用程序上下文
         */
        private Context context;

        /**
         * 媒体客户端
         */
        private BookMediaClient mediaClient;

        /**
         * 当前目录ID
         */
        private int currentCatalogID;

        /**
         * 是否正在播放
         */
        private boolean isPlaying;
    }

    /**
     * 私有界面类
     */
    private class UI {
        /**
         * 目录组项
         */
        CatalogGroup group = new CatalogGroup();

        /**
         * 目录组项类
         */
        private class CatalogGroup {
            /**
             * 图标
             */
            ImageView iconImageView;

            /**
             * 索引
             */
            TextView pageTextView;

            /**
             * 标题
             */
            TextView titleTextView;

            /**
             * 选择按钮
             */
            Button choiceButton;
        }
    }

    /**
     * 媒体客户端
     */
    private class MediaClient extends BookMediaClient {
        /**
         * 构造器
         *
         * @param context 应用程序上下文
         */
        public MediaClient(Context context) {
            super(context);
        }

        @Override
        public void setOnReceive(Intent intent) {
            boolean isRefresh = false; //是否刷新变量

            //有声书索引和原索引不相同时，更新列表变量设置为true
            if (getCurrentCatalogID() != getBook().getCurrentAudioCatalog().getCatalogID()) {
                setCurrentCatalogID(getBook().getCurrentAudioCatalog().getCatalogID()); //重置当前目录ID
                isRefresh = true; //充许刷新列表
            }

            //如果播放状态有变化，更新列表变量设置为true
            boolean isPlaying = intent.getExtras().getBoolean(BookMediaService.VALUE_IS_PLAYING); //获取是否正在播放
            if (isPlaying != isPlaying()) {
                setIsPlaying(isPlaying); //重置是否正在播放值
                isRefresh = true; //充许刷新列表
            }

            //刷新列表
            if (isRefresh) {
                notifyDataSetChanged();
            }
        }
    }

    /**
     * 选择按钮单击事件监听器
     */
    private class OnChoiceButtonClickListener implements View.OnClickListener {
        private Field m = new Field(); //私有字段

        /**
         * 构造器
         * @param catalog 目录
         */
        public OnChoiceButtonClickListener(IBookCatalog catalog) {
            m.catalog = catalog;
        }

        @Override
        public void onClick(View v) {
            if (getCurrentCatalogID() == getCatalog().getCatalogID()) {
                //点击的是当前目录
                if (isPlaying()) {
                    getMediaClient().pause(); //如果正在播放，就暂停播放
                } else {
                    getMediaClient().play(); //如果暂停播放，就开始播放
                }
            } else {
                //点击的不是当前目录
                getBook().resetAllowPlayAudio(getCatalog()); //重置播放开关
                notifyDataSetChanged();
            }
        }

        /**
         * 获取目录
         * @return 目录
         */
        private IBookCatalog getCatalog() {
            return m.catalog;
        }

        /**
         * 私有字段类
         */
        private class Field {
            /**
             * 书目录
             */
            IBookCatalog catalog;
        }
    }
}
