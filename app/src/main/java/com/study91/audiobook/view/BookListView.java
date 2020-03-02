package com.study91.audiobook.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.study91.audiobook.R;
import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.BookMediaClient;
import com.study91.audiobook.book.BookMediaService;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.ILibrary;
import com.study91.audiobook.ui.MainActivity;

import java.util.List;

/**
 * 书列表视图
 */
public class BookListView extends RelativeLayout {
    private ExpandableListView mListView; //列表视图
    private BookListViewAdapter mListViewAdapter; //列表视图适配器
    private BookMediaClient mMediaClient; //媒体客户端
    private int mAudioPosition; //当前语音播放位置
    private boolean mIsPlaying; //是否正在播放

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param attrs 属性集合
     */
    public BookListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //从布局文件中获取Layout
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.book_list_view, this);

        if(isInEditMode()) return; //解决可视化编辑器无法识别自定义控件的问题

        //读取控件
        mListView = (ExpandableListView) findViewById(R.id.bookListView); //列表视图

        //设置控件
        mListView.setGroupIndicator(null); //去掉默认的下拉箭头图标
        mListViewAdapter = new BookListViewAdapter(); //实例化列表视图适配器
        mListView.setAdapter(mListViewAdapter); //设置列表视图适配器

        //实例化媒体客户端
        mMediaClient = new MediaClient(getContext());
        mMediaClient.register(); //注册媒体客户端
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mMediaClient != null) {
            mMediaClient.unregister();
        }

        super.onDetachedFromWindow();
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
            Bundle bundle = intent.getExtras();
            mAudioPosition = bundle.getInt(BookMediaService.VALUE_AUDIO_POSITION); //获取语音位置
            mIsPlaying = bundle.getBoolean(BookMediaService.VALUE_IS_PLAYING); //获取是否正在播放
        }
    }

    /**
     * 书列表视图适配器
     */
    private class BookListViewAdapter extends BaseExpandableListAdapter {
        private List<IBook> mBookList; //书列表
        private TextView mBookIDTextView; //书ID
        private TextView mBookNameTextView; //书名称

        @Override
        public int getGroupCount() {
            return getBookList().size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return getBookList().get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return getBookList().get(groupPosition).getBookID();
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
            View view = LayoutInflater.from(getContext()).inflate(R.layout.book_list_group_view, parent, false);

            //载入控件
            mBookIDTextView = (TextView) view.findViewById(R.id.bookIDTextView); //书ID
            mBookNameTextView = (TextView) view.findViewById(R.id.bookNameTextView); //书名称

            IBook book = getBookList().get(groupPosition); //获取书对象

            //设置控件
            mBookIDTextView.setText(String.valueOf(book.getBookID())); //书ID
            mBookNameTextView.setText(book.getBookName()); //书名称
            mBookNameTextView.setOnClickListener(new OnBookClickListener(book.getBookID()));

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
         * 获取全局书
         */
        private IBook getBook() {
            return BookManager.getBook(getContext());
        }

        /**
         * 获取书列表
         */
        private List<IBook> getBookList() {
            if (mBookList == null) {
                ILibrary library = BookManager.createLibrary(getContext());
                mBookList = library.getBookListWithout(getBook().getBookID());
            }

            return mBookList;
        }

        /**
         * 书单击事件监听器
         */
        private class OnBookClickListener implements View.OnClickListener {
            private int bookID; //书ID

            /**
             * 构造器
             * @param bookID 书ID
             */
            public OnBookClickListener(int bookID) {
                this.bookID = bookID;
            }

            @Override
            public void onClick(View v) {
                //如果正在播放，先暂停播放
                if (mIsPlaying) {
                    mMediaClient.pause();
                }

                if (getBook().allowUpdateCurrentAudioPosition()) {
                    getBook().updateCurrentAudioPosition(mAudioPosition); //更新语音位置
                }

                BookManager.setBook(getContext(), bookID); //重置书

                //打开主窗口
                Intent intent = new Intent(getContext(), MainActivity.class);
                getContext().startActivity(intent);

                mMediaClient.refresh(); //刷新媒体服务
            }
        }
    }
}
