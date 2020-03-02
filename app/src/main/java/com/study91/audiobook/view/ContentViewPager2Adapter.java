package com.study91.audiobook.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.study91.audiobook.R;
import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookCatalog;
import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;

import java.util.List;

/**
 * 内容视图页适配器
 */
class ContentViewPager2Adapter extends PagerAdapter{
    private Field m = new Field(); //私有字段
    private UI ui = new UI(); //界面

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public ContentViewPager2Adapter(Context context) {
        m.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        List<IBookCatalog> catalogList = getCatalogList(); //获取目录列表
        IBookCatalog catalog = catalogList.get(position); //获取当前目录

        //从布局文件中获取视图
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.content_view2, null);

        //载入控件
        ui.iconImageView = (ImageView) view.findViewById(R.id.iconImageView); //图标
        ui.pageTextView = (TextView) view.findViewById(R.id.pageTextView); //页码
        ui.originalTextView = (TextView) view.findViewById(R.id.originalTextView); //原文
        ui.explainTextView = (TextView) view.findViewById(R.id.explainTextView); //详解

        //设置控件
        if (catalog.displayIcon()) {
            ui.iconImageView.setImageDrawable(catalog.getIconDrawable());
            ui.iconImageView.setVisibility(View.VISIBLE);
            ui.pageTextView.setBackgroundColor(Color.TRANSPARENT);
        } else {
            ui.iconImageView.setVisibility(View.GONE);
        }

        if (catalog.displayPage()) {
            ui.pageTextView.setText("" + catalog.getPage());
            ui.pageTextView.setTextSize(getOption().getContentFontSize());
            ui.pageTextView.setVisibility(View.VISIBLE);
        } else {
            ui.pageTextView.setVisibility(View.GONE);
        }

        ui.originalTextView.setText(catalog.getOriginal()); //设置原文
        ui.originalTextView.setTextSize(getOption().getContentFontSize());

        ui.explainTextView.setText(catalog.getExplain()); //设置详解
        ui.explainTextView.setTextSize(getOption().getContentFontSize());

        //设置控件标识
        ui.pageTextView.setTag("PageTextView" + position); //页码文本框
        ui.originalTextView.setTag("OriginalTextView" + position); //原文文本框
        ui.explainTextView.setTag("ExplainTextView" + position); //详解文本框

        container.addView(view); //添加视图

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return getCatalogList().size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
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
     * 获取全局选项
     */
    private IOption getOption() {
        return OptionManager.getOption(getContext());
    }

    /**
     * 获取目录列表
     */
    private List<IBookCatalog> getCatalogList() {
        if (m.catalogList == null) {
            m.catalogList = getBook().getCatalogList();
        }

        return m.catalogList;
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
         * 目录列表
         */
        List<IBookCatalog> catalogList;
    }

    /**
     * 私有界面类
     */
    private class UI {
        /**
         * 图标
         */
        ImageView iconImageView;

        /**
         * 页码
         */
        TextView pageTextView;

        /**
         * 原文
         */
        TextView originalTextView;

        /**
         * 详解
         */
        TextView explainTextView;
    }
}
