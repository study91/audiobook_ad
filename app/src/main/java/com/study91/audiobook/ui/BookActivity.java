package com.study91.audiobook.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.study91.audiobook.R;

/**
 * 选书窗口
 */
public class BookActivity extends Activity {
    private UI ui = new UI(); //界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); //设置为竖屏显示

        ui.adLayout = (RelativeLayout) findViewById(R.id.adLayout); //广告布局
        ui.backButton = (Button) findViewById(R.id.backButton);

        ui.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
    }
}
