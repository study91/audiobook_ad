package com.study91.audiobook.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * 内容视图页
 */
public class ContentViewPager1 extends ViewPager{
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public ContentViewPager1(Context context) {
        super(context);
        setAdapter(getContentViewPagerAdapter());
    }

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param attrs 属性集合
     */
    public ContentViewPager1(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) { return; }
        setAdapter(getContentViewPagerAdapter());
    }

    /**
     * 获取内容视图页适配器
     * @return 内容视图页适配器
     */
    private ContentViewPager1Adapter getContentViewPagerAdapter() {
        if (m.contentViewPagerAdapter == null) {
            m.contentViewPagerAdapter = new ContentViewPager1Adapter(getContext());
        }

        return m.contentViewPagerAdapter;
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 内容视图页适配器
         */
        ContentViewPager1Adapter contentViewPagerAdapter;
    }
}
