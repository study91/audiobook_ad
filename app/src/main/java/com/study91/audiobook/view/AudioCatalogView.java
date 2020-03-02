package com.study91.audiobook.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import com.study91.audiobook.R;
import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookCatalog;

import java.util.List;

/**
 * 目录视图
 */
public class AudioCatalogView extends RelativeLayout {
    private Field m = new Field(); //私有字段
    private UI ui = new UI(); //界面

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param attrs 属性集合
     */
    public AudioCatalogView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //从布局文件中获取Layout
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.catalog_view, this);

        ui.listView = (ExpandableListView) findViewById(R.id.catalogExpandableListView); //获取列表视图

        //解决可视化编辑器无法识别自定义控件的问题
        if(isInEditMode()) return;

        ui.listView.setGroupIndicator(null); //去掉默认的下拉箭头图标
        ui.listView.setAdapter(getAudioCatalogViewAdapter()); //设置列表适配器
        ui.listView.setOnGroupExpandListener(new OnListViewGroupExpandListener()); //设置列表视图组项展开事件监听器
        ui.listView.setSelection(getCurrentCatalogPosition()); //设置列表选择项
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        //注销目录视图适配器
        if (m.audioCatalogViewAdapter != null) {
            getAudioCatalogViewAdapter().unregister();
        }
    }

    /**
     * 通知数据变化
     */
    public void notifyDataSetChanged() {
        getAudioCatalogViewAdapter().notifyDataSetChanged();
    }

    /**
     * 获取全局书
     * @return 全局书
     */
    private IBook getBook() {
        return BookManager.getBook(getContext());
    }

    /**
     * 获取当前目录位置
     * @return 目录位置
     */
    private int getCurrentCatalogPosition() {
        int position = 0;

        int currentCatalogID = getBook().getCurrentAudioCatalog().getCatalogID(); //获取当前语音目录ID
        List<IBookCatalog> catalogList = getBook().getAudioCatalogList(); //获取书目录列表

        //遍历查询当前语音索引在内容列表中的位置
        for(int i=0; i<catalogList.size(); i++) {
            IBookCatalog catalog = catalogList.get(i);
            if(catalog.getCatalogID() == currentCatalogID) {
                position = i;
                break;
            }
        }

        return position;
    }

    /**
     * 获取列表视图适配器
     * @return 列表视图适配器
     */
    private AudioCatalogViewAdapter getAudioCatalogViewAdapter() {
        if (m.audioCatalogViewAdapter == null) {
            m.audioCatalogViewAdapter = new AudioCatalogViewAdapter(getContext());
        }

        return m.audioCatalogViewAdapter;
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 目录视图适配器
         */
        AudioCatalogViewAdapter audioCatalogViewAdapter;
    }

    /**
     * 私有界面类
     */
    private class UI {
        /**
         * 列表视图
         */
        ExpandableListView listView;
    }

    /**
     * 列表视图组项展开事件监听器
     */
    private class OnListViewGroupExpandListener implements ExpandableListView.OnGroupExpandListener {
        @Override
        public void onGroupExpand(int groupPosition) {
            //遍历所有主题项
            for(int i = 0; i < getAudioCatalogViewAdapter().getGroupCount(); i++) {
                //如果不是当前选择项，并且子项为展开状态的关闭子项
                if (groupPosition != i && ui.listView.isGroupExpanded(groupPosition)) {
                    ui.listView.collapseGroup(i);
                }
            }
        }
    }
}