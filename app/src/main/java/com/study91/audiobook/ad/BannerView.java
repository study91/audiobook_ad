package com.study91.audiobook.ad;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;

import java.util.Date;

/**
 * 横幅广告视图
 */
public class BannerView extends RelativeLayout {
    private IAd mAd; //广告

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param attrs 参数集合
     */
    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if(isInEditMode()) return; //解决可视化编辑器无法识别自定义控件的问题

        IBook book = BookManager.getBook(context); //全局书
        mAd =   AdManager.getAd(context); //获取广告对象
        addView(mAd.getBannerView()); //添加横幅广告到布局中
    }

    /**
     * 获取全局选项
     */
    private IOption getOption() {
        return OptionManager.getOption(getContext());
    }
}
