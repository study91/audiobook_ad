package com.study91.audiobook.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.study91.audiobook.R;
import com.study91.audiobook.ad.AdManager;
import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;
import com.study91.audiobook.update.IUpdate;
import com.study91.audiobook.update.UpdateManager;

/**
 * 帮助窗口
 */
public class HelpActivity extends Activity {
    private UI ui = new UI(); //界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); //设置为竖屏显示

        //载入控件
        ui.noNetLayout = (LinearLayout) findViewById(R.id.noNetLayout); //无网络布局
        ui.helpRadioGroup = (RadioGroup) findViewById(R.id.helpRadioGroup); //单选按钮组
        ui.studyRadioButton = (RadioButton) findViewById(R.id.studyRadioButton); //学习方法单选按钮组
        ui.questionRadioButton = (RadioButton) findViewById(R.id.questionRadioButton); //问题解答单选按钮组

        ui.recommendRadioButton = (RadioButton) findViewById(R.id.recommendRadioButton); //相关推荐单选按钮组
        //书样式有内容图片时，不显示推荐按钮
        if (getBook().getBookStyle() == 1) {
            ui.recommendRadioButton.setVisibility(View.GONE);
        }

        ui.updateButton = (Button) findViewById(R.id.updateButton); //软件更新按钮
        ui.webView = (WebView) findViewById(R.id.webView); //Web浏览器
        ui.reloadButton = (Button) findViewById(R.id.reloadButton); //重新加载按钮
        ui.backButton = (Button) findViewById(R.id.backButton); //返回按钮

        //设置控件
        WebSettings webSettings = ui.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        ui.webView.clearCache(true); //清除Cache
        ui.webView.setBackgroundColor(0); //背景透明

        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        ui.webView.setWebViewClient(new HelpWebViewClient());

        //设置下载事件监听器
        ui.webView.setDownloadListener(new WebViewDownloadListener());

        //设置帮助多选按钮改变事件监听器
        ui.helpRadioGroup.setOnCheckedChangeListener(new OnHelpCheckedChangeListener());
        ui.studyRadioButton.setChecked(true); //默认为选择学习方法

        ui.reloadButton.setOnClickListener(new OnReloadButtonClickListener()); //重新加载按钮
        ui.updateButton.setOnClickListener(new OnUpdateButtonClickListener()); //更新按钮
        ui.backButton.setOnClickListener(new OnBackButtonClickListener()); //返回按钮

        //添加广告
//        ui.adLayout = (RelativeLayout) findViewById(R.id.adLayout); //广告布局
//        ui.adLayout.addView(AdManager.getAd(this).getBannerView()); //添加横幅广告
    }

    /**
     * 是否有网络
     * @return 如果有网络返回true，否则返回false
     */
    private boolean hasNetwork() {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivityManager != null) {
                // 获取网络连接管理的对象
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    /**
     * 获取全局书
     */
    private IBook getBook() {
        return BookManager.getBook(this);
    }

    /**
     * 获取全局选项
     */
    private IOption getOption() {
        return OptionManager.getOption(this);
    }

    //显示网页
    private void showWeb() {
        if (hasNetwork()) {
            ui.webView.setVisibility(View.VISIBLE);
            ui.noNetLayout.setVisibility(View.GONE);
            int checkedId = ui.helpRadioGroup.getCheckedRadioButtonId();

            String url = null;
            if (checkedId == ui.studyRadioButton.getId()) {
                url = getOption().getStudyUrl(); //学习方法Url
            } else if (checkedId == ui.questionRadioButton.getId()) {
                url = getOption().getQuestionUrl(); //问题解答Url
            } else if (checkedId == ui.recommendRadioButton.getId()) {
                url = getBook().getRecommendUrl(); //相关推荐Url
            }

            //载入网页
            if (url != null) {
                ui.webView.loadUrl(url);
            }
        } else {
            ui.webView.setVisibility(View.GONE);
            ui.noNetLayout.setVisibility(View.VISIBLE);
        }
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
         * 无网络布局
         */
        LinearLayout noNetLayout;

        /**
         * 帮助单选按钮组
         */
        RadioGroup helpRadioGroup;

        /**
         * 学习方法单选按钮
         */
        RadioButton studyRadioButton;

        /**
         * 问题解答单选按钮
         */
        RadioButton questionRadioButton;

        /**
         * 相关推荐单选按钮
         */
        RadioButton recommendRadioButton;

        /**
         * 软件更新按钮
         */
        Button updateButton;

        /**
         * 重新加载按钮
         */
        Button reloadButton;

        /**
         * 更多WebView
         */
        WebView webView;

        /**
         * 返回按钮
         */
        Button backButton;
    }

    /**
     * 返回按钮单击事件监听器
     */
    private class OnBackButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    /**
     * 更新按钮单击事件监听器
     */
    private class OnUpdateButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            IUpdate update = UpdateManager.createUpdate(
                    HelpActivity.this, UpdateManager.LOCATION_REMOTE);
            update.update(false);
        }
    }
    /**
     * 重新加载按钮单击事件监听器
     */
    private class OnReloadButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showWeb();
        }
    }

    /**
     * 帮助改变事件监听器
     */
    private class OnHelpCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            showWeb(); //显示网页
        }
    }

    /**
     * 帮助Web客户端私有类
     */
    private class HelpWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
            view.loadUrl(url);
            return true;
        }
    }

    /**
     * WebView下载监听器
     */
    private class WebViewDownloadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
}
