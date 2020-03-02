package com.study91.audiobook.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
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
import com.study91.audiobook.ui.ContentActivity1;
import com.study91.audiobook.ui.ContentActivity2;
import com.study91.audiobook.ui.MainActivity;

import java.util.List;

/**
 * 目录视图适配器
 * 注：客户端退出时需要调用unregister()方法注销目录适配器
 */
class CatalogViewAdapter extends BaseExpandableListAdapter{
    private Field m = new Field(); //字段
    private UI ui = new UI(); //界面

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public CatalogViewAdapter(Context context) {
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
        return 1;
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.catalog_group_view, parent, false);

        IBookCatalog catalog = getCatalogList().get(groupPosition); //获取书目录

        //加载控件
        ui.group.iconImageView = (ImageView) view.findViewById(R.id.iconImageView); //图标
        ui.group.pageTextView = (TextView) view.findViewById(R.id.pageTextView); //页码
        ui.group.titleTextView = (TextView) view.findViewById(R.id.titleTextView); //标题
        ui.group.playButton = (Button) view.findViewById(R.id.playButton); //播放按钮
        ui.group.loopImageView = (ImageView) view.findViewById(R.id.loopImageView); //循环图标

        //设置图标
        if (catalog.displayIcon()) {
            ui.group.iconImageView.setImageDrawable(catalog.getIconDrawable());
            ui.group.iconImageView.setOnClickListener(new OnDisplayButtonClickListener(catalog));
        } else {
            ui.group.iconImageView.setVisibility(View.INVISIBLE);
        }

        //目录页码
        if (catalog.displayPage()) {
            ui.group.pageTextView.setText(String.valueOf(catalog.getPage())); //显示页号
            ui.group.pageTextView.setTextSize(getOption().getCatalogFontSize()); //字体大小
            ui.group.pageTextView.setOnClickListener(new OnDisplayButtonClickListener(catalog));
            ui.group.iconImageView.setVisibility(View.VISIBLE); //显示默认的图标
        } else {
            ui.group.pageTextView.setVisibility(View.INVISIBLE); //不显示图标
        }

        //播放按钮
        ui.group.playButton.setFocusable(false);
        if (!catalog.hasAudio()) { //如果语音开关值为false时，不显示播放按钮
            ui.group.playButton.setVisibility(View.INVISIBLE);
        }

        //设置单击事件监听器
        ui.group.playButton.setOnClickListener(new OnPlayButtonClickListener(catalog));

        //设置当前项背景色
        if (catalog.getCatalogID() == getBook().getCurrentAudioCatalog().getCatalogID()) {
            view.setBackgroundResource(R.color.catalog_group_current);
            //设置是否正在播放的图标
            if (isPlaying()) { //正在播放时，设置为暂停图标
                ui.group.playButton.setBackgroundResource(R.drawable.catalog_group_pause);
            } else { //暂停播放时，设置为播放图标
                ui.group.playButton.setBackgroundResource(R.drawable.catalog_group_play);
            }
            ui.group.titleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        }

        //循环图标
        if (catalog.hasAudio() && catalog.allowPlayAudio()) {
//            if (catalog.getCatalogID() == getBook().getFirstAudioCatalog().getCatalogID()) {
//                ui.group.loopImageView.setBackgroundResource(R.mipmap.catalog_group_loop_first); //复读起点语音图标
//            } else if (catalog.getCatalogID() == getBook().getLastAudioCatalog().getCatalogID()) {
//                ui.group.loopImageView.setBackgroundResource(R.mipmap.catalog_group_loop_last); //复读终点语音图标
//            } else if (
//                    catalog.getPage() > getBook().getFirstAudioCatalog().getPage() &&
//                    catalog.getPage() < getBook().getLastAudioCatalog().getPage()) {
//                ui.group.loopImageView.setBackgroundResource(R.mipmap.catalog_group_loop_middle); //复读中间语音图标
//            }
            if (catalog.getCatalogID() == getBook().getFirstAudioCatalog().getCatalogID()) {
                ui.group.loopImageView.setBackgroundResource(R.mipmap.catalog_group_loop_first); //复读起点语音图标
            } else if (catalog.getCatalogID() == getBook().getLastAudioCatalog().getCatalogID()) {
                ui.group.loopImageView.setBackgroundResource(R.mipmap.catalog_group_loop_last); //复读终点语音图标
            } else if (catalog.getIndex() > getBook().getFirstAudioCatalog().getIndex() && catalog.getIndex() < getBook().getLastAudioCatalog().getIndex()) {
                ui.group.loopImageView.setBackgroundResource(R.mipmap.catalog_group_loop_middle); //复读中间语音图标
            }

            //复读起点和复读终点相同时，不显示循环图标
            if (getBook().getFirstAudioCatalog().getCatalogID() == getBook().getLastAudioCatalog().getCatalogID()) {
                ui.group.loopImageView.setVisibility(View.INVISIBLE); //不显示循环图标
            }
        }

        //标题
        ui.group.titleTextView.setText(catalog.getTitle());
        ui.group.titleTextView.setTextSize(getOption().getCatalogFontSize()); //标题字体大小


        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //载入列表子视图布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.catalog_child_view, parent, false);

        final IBookCatalog catalog = getCatalogList().get(groupPosition); //获取书目录

        //载入控件
        ui.child.firstButton = (Button) view.findViewById(R.id.firstButton); //复读起点按钮
        ui.child.lastButton = (Button) view.findViewById(R.id.lastButton); //复读终点按钮
        ui.child.displayButton = (Button) view.findViewById(R.id.displayButton); //显示按钮
        ui.child.explainButton = (Button) view.findViewById(R.id.explainButton); //详解按钮
        ui.child.allowPlayAudioButton = (Button) view.findViewById(R.id.allowPlayAudioButton); //播放开关按钮

        //设置事件监听器
        ui.child.firstButton.setOnClickListener(new OnFirstButtonClickListener(catalog));
        ui.child.lastButton.setOnClickListener(new OnLastButtonClickListener(catalog));
        ui.child.allowPlayAudioButton.setOnClickListener(new OnAllowPlayAudioButtonClickListener(catalog));
        ui.child.displayButton.setOnClickListener(new OnDisplayButtonClickListener(catalog));
        ui.child.explainButton.setOnClickListener(new OnExplainButtonClickListener(groupPosition));

        //设置需要隐藏的按钮
        if (!catalog.displayContent()) ui.child.displayButton.setVisibility(View.GONE);
        if (!catalog.displayExplain()) ui.child.explainButton.setVisibility(View.GONE);

        if (catalog.hasAudio()) {
            //有语音时执行
            if (catalog.getPage() < getBook().getCurrentAudioCatalog().getPage()) { //页号小于当前目录页号
                ui.child.lastButton.setEnabled(false); //复读终点按钮禁用
                ui.child.firstButton.setEnabled(true); //复读起点按钮可用
            } else if (catalog.getPage() > getBook().getCurrentAudioCatalog().getPage()) { //页号大于当前目录页号
                ui.child.lastButton.setEnabled(true); //复读终点按钮可用
                ui.child.firstButton.setEnabled(false); //复读起点按钮禁用
            } else { //页号等于当前目录页号
                ui.child.allowPlayAudioButton.setEnabled(false); //播放开关按钮禁用
            }

            if (catalog.getCatalogID() == getBook().getFirstAudioCatalog().getCatalogID()) {
                ui.child.firstButton.setEnabled(false); //复读起点按钮禁用
            }

            if (catalog.getCatalogID() == getBook().getLastAudioCatalog().getCatalogID()) {
                ui.child.lastButton.setEnabled(false); //复读终点按钮禁用
            }

            if(catalog.allowPlayAudio()) { //播放开关打开
                ui.child.allowPlayAudioButton.setBackgroundResource(R.drawable.catalog_child_cancel);
            } else { //播放开关关闭
                ui.child.allowPlayAudioButton.setBackgroundResource(R.drawable.catalog_child_add);
            }
        } else {
            //没有语音的目录，关闭复读起点、复读终点、播放开关按钮
            ui.child.firstButton.setVisibility(View.GONE); //禁用复读起点按钮
            ui.child.lastButton.setVisibility(View.GONE); //禁用复读终点按钮
            ui.child.allowPlayAudioButton.setVisibility(View.GONE); //禁用播放开关按钮
        }

        return view;
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
        return getBook().getCatalogList();
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
     * 获取目录位置
     * @param catalog 目录
     * @return 目录位置
     */
    private int getCatalogPosition(IBookCatalog catalog) {
        int position = -1;

        List<IBookCatalog> catalogList = getBook().getCatalogList(); //获取目录列表

        for (int i=0; i<catalogList.size(); i++) {
            IBookCatalog bookCatalog = catalogList.get(i);
            if (bookCatalog.getCatalogID() == catalog.getCatalogID()) {
                position = i;
                break;
            }
        }

        return position;
    }

    /**
     * 显示详解1
     * @param position 目录位置
     */
    private void showExplain1(int position) {
        Intent intent = new Intent(getContext(), ContentActivity1.class);
        intent.putExtra("Position", position); //加入目录位置参数
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    /**
     * 显示详解2
     * @param position 目录位置
     */
    private void showExplain2(int position) {
        Intent intent = new Intent(getContext(), ContentActivity2.class);
        intent.putExtra("Position", position); //加入目录位置参数
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
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
         * 目录子项
         */
        CatalogChild child = new CatalogChild();

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
             * 循环图标
             */
            ImageView loopImageView;

            /**
             * 播放按钮
             */
            Button playButton;
        }

        /**
         * 目录子项类
         */
        private class CatalogChild {
            /**
             * 复读起点按钮
             */
            Button firstButton;

            /**
             * 复读终点按钮
             */
            Button lastButton;

            /**
             * 显示按钮
             */
            Button displayButton;

            /**
             * 解释按钮
             */
            Button explainButton;

            /**
             * 充许播放按钮
             */
            Button allowPlayAudioButton;
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
     * 播放按钮单击事件监听器
     */
    private class OnPlayButtonClickListener implements View.OnClickListener {
        private Field m = new Field(); //私有字段

        /**
         * 构造器
         * @param catalog 目录
         */
        public OnPlayButtonClickListener(IBookCatalog catalog) {
            m.catalog = catalog;
        }

        @Override
        public void onClick(View v) {
            if (getCurrentCatalogID() == getCatalog().getCatalogID()) {
                //点击的是当前目录的播放按钮
                if (isPlaying()) {
                    getMediaClient().pause(); //如果正在播放，就暂停播放
                } else {
                    getMediaClient().play(); //如果暂停播放，就开始播放
                }
            } else {
                //点击的不是当前目录的播放按钮
                getBook().setCurrentAudioCatalog(getCatalog());
                getMediaClient().play();
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

    /**
     * 复读起点按钮单击事件监听器
     */
    private class OnFirstButtonClickListener implements View.OnClickListener {
        private Field m = new Field(); //私有字段

        /**
         * 构造器
         * @param catalog 目录
         */
        public OnFirstButtonClickListener(IBookCatalog catalog) {
            m.catalog = catalog; //有声书内容
        }

        @Override
        public void onClick(View v) {
            getBook().setFirstAudioCatalog(getCatalog()); //设置复读起点
            notifyDataSetChanged();
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

    /**
     * 复读终点按钮单击事件监听器
     */
    private class OnLastButtonClickListener implements View.OnClickListener {
        private Field m = new Field(); //私有字段

        /**
         * 构造器
         * @param catalog 目录
         */
        public OnLastButtonClickListener(IBookCatalog catalog) {
            m.catalog = catalog; //有声书内容
        }

        @Override
        public void onClick(View v) {
            getBook().setLastAudioCatalog(getCatalog()); //设置复读终点
            notifyDataSetChanged();
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

    /**
     * 充许播放语音开关按钮单击事件监听器
     */
    private class OnAllowPlayAudioButtonClickListener implements View.OnClickListener {
        private Field m = new Field(); //私有字段

        /**
         * 构造器
         * @param catalog 目录
         */
        public OnAllowPlayAudioButtonClickListener(IBookCatalog catalog) {
            m.catalog = catalog; //目录
        }

        @Override
        public void onClick(View v) {
            getBook().resetAllowPlayAudio(getCatalog()); //重置播放开关
            notifyDataSetChanged();
        }

        /**
         * 获取目录
         *
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
             * 有声书内容
             */
            IBookCatalog catalog;
        }
    }

    /**
     * 显示按钮单击事件监听器
     */
    private class OnDisplayButtonClickListener implements View.OnClickListener {
        private Field m = new Field(); //私有字段

        /**
         * 构造器
         * @param catalog 目录
         */
        public OnDisplayButtonClickListener(IBookCatalog catalog) {
            m.catalog = catalog; //目录
        }

        @Override
        public void onClick(View v) {
            //如果充许同步且同步复读打开时，关闭同步复读开关
            if (getBook().allowSynchronization() &&
                    getBook().synchronizationEnable() &&
                    getBook().getCurrentContent().getPage() != getCatalog().getPage()) {
                getBook().setSynchronizationEnable(false); //关闭同步复读开关
            }

            //设置书的当前内容为选择的目录的首页内容
            switch (getBook().getBookStyle()) {
                case 1:  //样式1（显示内容图片）
                    if (getCatalog().displayContent()) {
                        showContentImage();
                    }
                    break;
                case 2: //样式2（有详解时显示详解）
                    if (getCatalog().displayExplain()) {
                        showExplain();
                    }
                    break;
            }
        }

        /**
         * 显示内容图片
         */
        private void showContentImage() {
            //设置书的当前内容为选择的目录的首页内容
            getBook().setCurrentContent(getCatalog().getContentList().get(0));
            getMediaClient().refresh(); //刷新

            //打开主窗口
            Intent intent = new Intent(getContext(), MainActivity.class);
            getContext().startActivity(intent);

            //启动内容窗口后马上关闭媒体播放视图的目录弹出窗口
            if (BookManager.CatalogPopupWindow != null) {
                BookManager.CatalogPopupWindow.dismiss(); //关闭目录弹出窗口
            }
        }

        /**
         * 显示详解
         */
        private void showExplain() {
            int position = getCatalogPosition(getCatalog()); //获取当前目录位置
            switch (getBook().getMediaIconLinkType()) {
                case IBook.MEDIA_LINK_TYPE_EXPLAIN1: //原文详解1
                    showExplain1(position);
                    break;
                case IBook.MEDIA_LINK_TYPE_EXPLAIN2: //原文详解2
                    showExplain2(position);
                    break;
            }
        }

        /**
         * 获取目录
         *
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
             * 有声书内容
             */
            IBookCatalog catalog;
        }
    }

    /**
     * 解释按钮单击事件监听器
     */
    private class OnExplainButtonClickListener implements View.OnClickListener {
        private Field m = new Field(); //私有字段

        /**
         * 构造器
         * @param position 位置
         */
        public OnExplainButtonClickListener(int position) {
            m.position = position; //位置
        }

        @Override
        public void onClick(View v) {
            int linkType = getBook().getMediaTitleLinkType();
            switch (linkType) {
                case IBook.MEDIA_LINK_TYPE_EXPLAIN1: //原文详解1
                    showExplain1(getPosition());
                    break;
                case IBook.MEDIA_LINK_TYPE_EXPLAIN2: //原文详解2
                    showExplain2(getPosition());
                    break;
            }
        }


        /**
         * 获取位置
         * @return 位置
         */
        private int getPosition() {
            return m.position;
        }

//        /**
//         * 显示详解1
//         */
//        private void showExplain1() {
//            Intent intent = new Intent(getContext(), ContentActivity1.class);
//            intent.putExtra("Position", getPosition()); //加入目录位置参数
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            getContext().startActivity(intent);
//        }
//
//        /**
//         * 显示详解2
//         */
//        private void showExplain2() {
//            Intent intent = new Intent(getContext(), ContentActivity2.class);
//            intent.putExtra("Position", getPosition()); //加入目录位置参数
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            getContext().startActivity(intent);
//        }

        /**
         * 私有字段类
         */
        private class Field {
            /**
             * 位置
             */
            int position;
        }
    }
}
