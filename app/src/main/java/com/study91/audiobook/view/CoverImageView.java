package com.study91.audiobook.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBook;

/**
 * 封面图片
 */
public class CoverImageView extends ImageView{


    /**
     * 构造器
     * @param context 应用程序上下文
     * @param attrs 属性集合
     */
    public CoverImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //解决可视化编辑器无法识别自定义控件的问题
        if(isInEditMode()) return;

        IBook book = BookManager.getBook(getContext()); //获取全局书

        setScaleType(ImageView.ScaleType.FIT_XY); //设置图像全屏拉伸
        setImageDrawable(book.getCoverDrawable()); //设置图像Drawable
//        setImageDrawable(Drawable.createFromStream(file.getInputStream(), null)); //设置图像Drawable

    }
}
