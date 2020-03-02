package com.study91.audiobook.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.study91.audiobook.R;
import com.study91.audiobook.ad.AdManager;
import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;
import com.study91.audiobook.view.CatalogView;

/**
 * 目录窗口
 */
public class CatalogActivity extends Activity {
    private UI ui = new UI(); //界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); //设置为竖屏显示

        //载入控件
        ui.backButton = (Button) findViewById(R.id.backButton); //返回按钮
        ui.smallFontButton = (Button) findViewById(R.id.smallFontButton); //缩小字体按钮
        ui.largeFontButton = (Button) findViewById(R.id.largeFontButton); //放大字体按钮
        ui.catalogView = (CatalogView) findViewById(R.id.catalogView); //目录视图

        //设置控件
        ui.smallFontButton.setOnClickListener(new OnSmallFontClickListener());
        ui.largeFontButton.setOnClickListener(new OnLargeFontClickListener());
        ui.backButton.setOnClickListener(new OnBackClickListener()); //返回按钮单击事件

        //添加广告
//        ui.adLayout = (RelativeLayout) findViewById(R.id.adLayout); //广告布局
//        ui.adLayout.addView(AdManager.getAd(this).getBannerView()); //添加横幅广告
    }

    /**
     * 获取全局选项
     */
    private IOption getOption() {
        return OptionManager.getOption(this);
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
         * 缩小字体按钮
         */
        Button smallFontButton;

        /**
         * 放大字体按钮
         */
        Button largeFontButton;

        /**
         * 目录视图
         */
        CatalogView catalogView;
    }

    /**
     * 获取全局书
     * @return 全局书
     */
    private IBook getBook() {
        return BookManager.getBook(this);
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
     * 缩小字体按钮单击事件监听器
     */
    private class OnSmallFontClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int fontSize = getOption().getCatalogFontSize() - 2;
            if (fontSize > 0) {
                getOption().setCatalogFontSize(fontSize);
                ui.catalogView.notifyDataSetChanged();
            }
        }
    }

    /**
     * 放大字体按钮单击事件监听器
     */
    private class OnLargeFontClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int fontSize = getOption().getCatalogFontSize() + 2;
            if (fontSize > 0) {
                getOption().setCatalogFontSize(fontSize);
                ui.catalogView.notifyDataSetChanged();
            }
        }
    }
}
