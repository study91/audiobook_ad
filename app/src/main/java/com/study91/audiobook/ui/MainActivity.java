package com.study91.audiobook.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.study91.audiobook.R;
import com.study91.audiobook.ad.AdManager;
import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.BookMediaClient;
import com.study91.audiobook.book.BookMediaService;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookContent;
import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;
import com.study91.audiobook.permission.IPermissionManager;
import com.study91.audiobook.permission.PermissionManager;
import com.study91.audiobook.update.IUpdate;
import com.study91.audiobook.update.UpdateManager;
import com.study91.audiobook.view.ContentImageViewPager;
import com.study91.audiobook.view.MediaPlayerView;
import com.study91.audiobook.view.OnSingleTapListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

/**
 * 主窗口
 */
public class MainActivity extends Activity {
    private Field m = new Field(); //字段
    private UI ui = new UI(); //界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); //设置为竖屏显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //不关屏

        getPermissionManager().requestPermissions(this);

        //本地更新
        IUpdate localUpdate = UpdateManager.createUpdate(this, UpdateManager.LOCATION_LOCAL);
        if(localUpdate != null) localUpdate.update(true);

        //远程更新
        IUpdate remoteUpdate = UpdateManager.createUpdate(this, UpdateManager.LOCATION_REMOTE);
        if(remoteUpdate != null) remoteUpdate.update(true);

        //初始化全局选项（因为远程更新会有延迟，所以初始选项可能会暂时读取未更新的数据库中的选项内容）
        //此操作必须在更新操作之后，可以避免数据库有变动时的升级不能运行的问题
        getOption().init();

        startMediaService(); //启动书媒体服务
        getMediaClient().register(); //注册媒体客户端
        loadContentView(); //载入视图
    }

    /**
     * 载入内容视图
     */
    private void loadContentView() {
        switch (getBook().getBookStyle()) {
            case 1: //样式1
                setContentView(R.layout.activity_style1_main);
                ui.playButton = (Button) findViewById(R.id.playButton); //播放按钮
                ui.playButton.setOnClickListener(new OnPlayButtonClickListener()); //播放按钮
                break;
            case 2: //样式2
                setContentView(R.layout.activity_style2_main);
                ui.titleImageView = (ImageView) findViewById(R.id.titleImageButton); //标题图片
                ui.titleImageView.setImageDrawable(getBook().getTitleDrawable()); //设置标题图片
                break;
        }

        //载入界面控件
        ui.fullLayout = (RelativeLayout) findViewById(R.id.fullLayout); //全屏布局
        ui.topLayout = (RelativeLayout) findViewById(R.id.topLayout); //顶部布局

        ui.bookButton = (Button) findViewById(R.id.bookButton); //选书按钮
        ui.shareButton = (Button) findViewById(R.id.shareButton); //分享按钮
        ui.recommendButton = (Button) findViewById(R.id.recommendButton); //推荐按钮

        ui.mediaPlayerView = (MediaPlayerView) findViewById(R.id.mediaPlayerView); //媒体播放器视图
        ui.synchronizationButton = (Button) ui.mediaPlayerView.findViewById(R.id.synchronizationButton); //同步按钮
        ui.empowerTextView = (TextView) findViewById(R.id.empowerTextView); //授权
        ui.versionTextView = (TextView) findViewById(R.id.versionTextView); //版本号
        ui.copyrightTextView = (TextView) findViewById(R.id.copyrightTextView); //版权
        ui.catalogButton = (Button) findViewById(R.id.catalogButton); //目录按钮
        ui.helpButton = (Button) findViewById(R.id.helpButton); //帮助按钮
        ui.exitButton = (Button) findViewById(R.id.exitButton); //退出按钮

        //能选书时才显示选书按钮
        if (getOption().canChooseBook()) { //能选书
            ui.bookButton.setVisibility(View.VISIBLE);
            ui.bookButton.setOnClickListener(new OnBookClickListener()); //选书按钮单击事件
        } else { //不能选书
            ui.bookButton.setVisibility(View.GONE);
        }

        ui.recommendButton.setOnClickListener(new OnRecommendButtonClickListener()); //推荐按钮

        ui.empowerTextView.setText(getBook().getEmpower()); //书版权
        String version = getResources().getString(R.string.version);
        version = version.replace("[version]", getOption().getVersion());
        ui.versionTextView.setText(version); //版本号
        ui.copyrightTextView.setText(getOption().getCopyright()); //软件版权
        ui.catalogButton.setOnClickListener(new OnCatalogClickListener()); //目录按钮
        ui.helpButton.setOnClickListener(new OnHelpButtonClickListener()); //帮助按钮
        ui.exitButton.setOnClickListener(new OnExitClickListener()); //退出按钮单击事件监听器

        if (!getBook().allowSynchronization()) {
            ui.synchronizationButton.setVisibility(View.GONE);
        } else {
            ui.synchronizationButton.setVisibility(View.VISIBLE);
        }

        ui.fullLayout.removeAllViews();
        ui.contentImageViewPager = new ContentImageViewPager(getApplicationContext());
        //ui.contentImageViewPager.setOnTouchListener(new OnContentViewTouchListener()); //内容视图页触屏事件
        ui.contentImageViewPager.setOnSingleTapListener(new OnContentViewSingleTapListener());
        ui.fullLayout.addView(ui.contentImageViewPager); //添加内容视图到全屏布局中

        //添加广告
//        ui.adLayout = (RelativeLayout) findViewById(R.id.adLayout); //广告布局
//        ui.adLayout.addView(AdManager.getAd(this).getBannerView()); //添加横幅广告

        //友盟统计开始
        String umeng_appkey = getBook().getStatisticsID(); //获取统计ID
        Log.d("Test", "友盟统计AppKey:" + umeng_appkey);
        UMConfigure.init(this, umeng_appkey, "Study91", UMConfigure.DEVICE_TYPE_PHONE, null);
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        //UMConfigure.setLogEnabled(true); //打开统计SDK调试模式，发布时需要注释或关闭
        //友盟统计结束

        setBookID(getBook().getBookID());
        setCurrentAudioContentID(0); //重置当前语音内容ID
    }

    @Override
    protected void onDestroy() {
        AdManager.release(); //释放广告资源

        //注销媒体客户端
        if (m.mediaClient != null) {
            m.mediaClient.unregister();
        }

        stopMediaService(); //停止书媒体服务

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: //按下手机的返回按钮
                if(!m.isPlaying) { //播放状态
                    showExitDialog(); //显示退出窗口
                } else { //暂停播放状态
                    showMinimizeDialog(); //显示最小化窗口
                }
                break;
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //引导用户手动授权必需权限
        boolean isAllGrant = getPermissionManager().guideRequestPermissions(this, permissions, grantResults);

        if (isAllGrant) {
            Log.i("Test", "所有权限已授权");
        } else {
            //有未授权的必须权限
            Log.i("Test", getString(R.string.permission_warning));
        }
    }

    /**
     * 获取全局书
     * @return 全局书
     */
    private IBook getBook() {
        return BookManager.getBook(this);
    }

    /**
     * 启动书媒体服务
     */
    private void startMediaService() {
        m.mediaServiceIntent = new Intent(this, BookMediaService.class);
        startService(m.mediaServiceIntent);
    }

    /**
     * 停止书媒体服务
     */
    private void stopMediaService() {
        if (m.mediaServiceIntent != null) {
            stopService(m.mediaServiceIntent);
        }
    }

    /**
     * 获取媒体客户端
     * @return 媒体客户端
     */
    private BookMediaClient getMediaClient() {
        if (m.mediaClient == null) {
            m.mediaClient = new MediaClient(this);
        }

        return m.mediaClient;
    }

    /**
     * 获取手势检测器
     * @return 手势检测器
     */
//    private GestureDetector getGestureDetector() {
//        if (m.gestureDetector == null) {
//            m.gestureDetector = new GestureDetector(this, new OnContentViewGestureListener());
//        }
//
//        return m.gestureDetector;
//    }

    /**
     * 设置工具条
     * @param hasToolbar ture=有工具条，false=没有工具条
     */
    private void setToolbar(boolean hasToolbar) {
        if (hasToolbar) { //设置为有工具条
            ui.topLayout.setVisibility(View.VISIBLE); //显示工具条
            ui.mediaPlayerView.setVisibility(View.VISIBLE); //显示工具条
        } else { //设置为没有工具条
            ui.topLayout.setVisibility(View.GONE); //隐藏顶部工具条
            ui.mediaPlayerView.setVisibility(View.GONE); //隐藏底部工具条
        }

        setHasToolbar(hasToolbar); //设置是否有工具条
    }

    /**
     * 设置是否有工具条
     * @param hasToolbar 布尔值（true=有工具条，false=没有工具条）
     */
    private void setHasToolbar(boolean hasToolbar) {
        m.hasToolbar = hasToolbar;
    }

    /**
     * 是否有工具条
     * @return true=有工具条，false=没有工具条
     */
    private boolean hasToolbar() {
        return m.hasToolbar;
    }

    /**
     * 显示退出对话框
     */
    private void showExitDialog() {
        //构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_exit_title); //对话框标题
        builder.setMessage(R.string.dialog_exit_message); //对话框信息

        //设置退出按钮单击事件监听器
        builder.setPositiveButton(R.string.button_exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish(); //退出
            }
        });

        //设置取消按钮单击事件监听器
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //取消
            }
        });

        Dialog dialog = builder.create(); //创建对话框
        dialog.show(); //显示对话框
    }

    /**
     * 显示最小化对话框
     */
    private void showMinimizeDialog() {
        //构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_minimize_title); //对话框标题
        builder.setMessage(R.string.dialog_minimize_message); //对话框信息

        //设置确定按钮单击事件监听器
        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //最小化窗口
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
        });

        //设置取消按钮单击事件监听器
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //取消
            }
        });

        Dialog dialog = builder.create(); //创建对话框
        dialog.show(); //显示对话框
    }

    /**
     * 设置书ID
     * @param bookID 书ID
     */
    private void setBookID(int bookID) {
        m.bookID = bookID;
    }

    /**
     * 获取书ID
     */
    private int getBookID() {
        return m.bookID;
    }

    /**
     * 获取全局选项
     */
    private IOption getOption() {
        return OptionManager.getOption(this);
    }

    /**
     * 设置当前语音内容ID
     * @param contentID 内容ID
     */
    private void setCurrentAudioContentID(int contentID) {
        m.currentAudioContentID = contentID;
    }

    /**
     * 获取当前语音内容ID
     */
    private int getCurrentAudioContentID() {
        return m.currentAudioContentID;
    }

    /**
     * 获取权限管理器
     * @return 权限管理器
     */
    private IPermissionManager getPermissionManager() {
        if (m.permissionManager == null) {
            m.permissionManager = new PermissionManager();
        }

        return m.permissionManager;
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 权限管理器
         */
        IPermissionManager permissionManager;

        /**
         * 媒体服务Intent
         */
        Intent mediaServiceIntent;

        /**
         * 媒体客户端
         */
        BookMediaClient mediaClient;

        /**
         * 是否正在播放
         */
        boolean isPlaying;

        /**
         * 是否有工具条
         */
        boolean hasToolbar = true;

        /**
         * 手势监听器
         */
//        GestureDetector gestureDetector;

        /**
         * 书ID
         */
        int bookID;

        /**
         * 当前语音内容ID
         */
        int currentAudioContentID;
    }

    /**
     * 私有界面类
     */
    private class UI {
        /**
         * 顶部布局
         */
        RelativeLayout topLayout;

        /**
         * 全屏布局
         */
        RelativeLayout fullLayout;

        /**
         * 广告布局
         */
        RelativeLayout adLayout;

        /**
         * 内容视图页
         */
        ContentImageViewPager contentImageViewPager;

        /**
         * 书图片
         */
        ImageView titleImageView;

        /**
         * 选书按钮
         */
        Button bookButton;

        /**
         * 目录按钮
         */
        Button catalogButton;

        /**
         * 同步按钮
         */
        Button synchronizationButton;

        /**
         * 分享按钮
         */
        Button shareButton;

        /**
         * 更多按钮
         */
        Button recommendButton;

        /**
         * 帮助按钮
         */
        Button helpButton;

        /**
         * 播放按钮
         */
        Button playButton;

        /**
         * 退出按钮
         */
        Button exitButton;

        /**
         * 授权文本框
         */
        TextView empowerTextView;

        /**
         * 版本号文本框
         */
        TextView versionTextView;

        /**
         * 版权文本框
         */
        TextView copyrightTextView;

        /**
         * 媒体播放器视图
         */
        MediaPlayerView mediaPlayerView;
    }

    /**
     * 媒体客户端
     */
    private class MediaClient extends BookMediaClient {
        /**
         * 构造器
         * @param context 应用程序上下文
         */
        public MediaClient(Context context) {
            super(context);
        }

        @Override
        public void setOnReceive(Intent intent) {
            if (getBook().getBookID() != getBookID()) {
                loadContentView(); //载入视图
            }

            m.isPlaying = intent.getExtras().getBoolean(BookMediaService.VALUE_IS_PLAYING);

            IBookContent currentContent = getBook().getCurrentContent();

            int audioPosition = intent.getExtras().getInt(BookMediaService.VALUE_AUDIO_POSITION); //获取语音位置
            IBookContent audioContent = getBook().getCurrentAudioCatalog().getAudioContent(audioPosition); //获取当前语音内容
            if (audioContent != null) {
                setCurrentAudioContentID(audioContent.getContentID());
            }

            if (ui.playButton != null) {
                if (currentContent != null && currentContent.hasAudio()) {
                    ui.playButton.setVisibility(View.VISIBLE);
                    ui.playButton.setBackgroundResource(R.drawable.button_play);

                    if (audioContent != null &&
                            currentContent.getContentID() == audioContent.getContentID() &&
                            m.isPlaying) {
                        ui.playButton.setBackgroundResource(R.drawable.button_pause);
                    }
                } else {
                    ui.playButton.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 获取状态栏高度
     * @return 状态栏高度
     */
//    private int getStatusBarHeight() {
//        int height = 0;
//        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            height = getResources().getDimensionPixelSize(resourceId);
//        }
//        return height;
//    }

    /**
     * 获取屏幕高度
     * @return 屏幕高度
     */
//    private int getScreenHeight() {
//        int height;
//
//        //获取屏幕高度
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        height = displayMetrics.heightPixels;
//
//        return height;
//    }

    /**
     * 选书按钮单击事件监听器
     */
    private class OnBookClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), BookActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 目录单击事件监听器
     */
    private class OnCatalogClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), CatalogActivity.class);
            startActivity(intent);
            //showCatalogPopupWindow(); //显示目录弹出窗口
        }
    }

    /**
     * 显示目录弹出窗口
     */
//    private void showCatalogPopupWindow() {
//        //载入布局文件
//        final View view = LayoutInflater.from(
//                getApplicationContext()).inflate(R.layout.catalog_popwindow,
//                null);
//
//        //计算弹出窗口高度
//        int height = getScreenHeight() -
//                ui.topLayout.getHeight() -
//                ui.mediaPlayerView.getHeight() -
//                ui.bannerView.getHeight() -
//                getStatusBarHeight();
//
//        //实例化弹出窗口
//        BookManager.CatalogPopupWindow = new PopupWindow(
//                view,
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                height,
//                true);
//
//        //设置弹出窗口背景图片，只有设置了这个属性才能在点击弹出窗口外部或点击返回按钮时关闭弹出窗口
//        BookManager.CatalogPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
//        BookManager.CatalogPopupWindow.setOutsideTouchable(false); //设置是否允许在外点击使其消失，好象上条语句已实现？
//        BookManager.CatalogPopupWindow.showAsDropDown(ui.topLayout); //显示弹出窗口
//    }

    /**
     * 推荐按钮单击事件监听器
     */
    private class OnRecommendButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), RecommendActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 帮助按钮单击事件监听器
     */
    private class OnHelpButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 播放按钮单击事件监听器
     */
    private class OnPlayButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.e("Test", "是否正在播放：" + m.isPlaying);

            IBookContent currentContent = getBook().getCurrentContent();

            if (currentContent.getCatalogID() != getBook().getCurrentAudioCatalogID()) {
                //当前内容不是语音内容（以下执行顺序非常重要）
                getBook().setCurrentAudioCatalog(currentContent.getCatalog());
//                getMediaClient().pause(); //先暂停
//                getMediaClient().refresh(); //刷新
                getMediaClient().play();
                getMediaClient().seekTo((int) currentContent.getAudioStartTime());
            } else {
                //当前内容是语音内容
                if (currentContent.getContentID() == getCurrentAudioContentID()) {
                    //当前页是语音内容
                    if (m.isPlaying) { //当前页正在播放
                        getMediaClient().pause();
                    } else { //当前页暂停播放
                        getMediaClient().play();
                    }
                } else {
                    //当前页不是语音内容
                    if (m.isPlaying) {
                        getMediaClient().seekTo((int) currentContent.getAudioStartTime());
                    } else {
                        getMediaClient().play();
                        getMediaClient().seekTo((int) currentContent.getAudioStartTime());
                    }
                }
            }

            getMediaClient().refresh();

            Log.e("Test", "当前内容ID：" + currentContent.getContentID() + "," +
                    "语音内容ID：" + getCurrentAudioContentID());
        }
    }

    /**
     * 退出单击事件监听器
     */
    private class OnExitClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showExitDialog();
        }
    }

    /**
     * 内容视图触屏事件监听器
     */
//    private class OnContentViewTouchListener implements View.OnTouchListener {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            return getGestureDetector().onTouchEvent(event);
//        }
//    }

    /**
     * 内容视图单击事件监听器
     */
    private class OnContentViewSingleTapListener implements OnSingleTapListener {
        @Override
        public void onSingleTap() {
            Log.e("Test", "MainActivity.OnContentViewSingleTapListener.onSingleTap:单击事件");
            if (getBook().canHideToolbar()) {
                //如果能隐藏工具条时，重置工具条状态
                setToolbar(!hasToolbar()); //设置工具条
            }
        }
    }

    /**
     * 内容视图手势监听器
     */
//    private class OnContentViewGestureListener implements GestureDetector.OnGestureListener {
//        @Override
//        public boolean onDown(MotionEvent e) {
//            return false;
//        }
//
//        @Override
//        public void onShowPress(MotionEvent e) {
//
//        }
//
//        @Override
//        public boolean onSingleTapUp(MotionEvent e) {
//            Log.e("Test", "MainActivity.OnContentViewGestureListener.onSigleTapUp:单击事件");
//            if (getBook().canHideToolbar()) {
//                //如果能隐藏工具条时，重置工具条状态
//                setToolbar(!hasToolbar()); //设置工具条
//            }
//
//            return false;
//        }
//
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            return false;
//        }
//
//        @Override
//        public void onLongPress(MotionEvent e) {
//
//        }
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            //手势左右滑动时，切换正面闪卡
////            if (e1.getX() - e2.getX() > 50) { //手势从右向左滑动
////                Log.e("Test", "手势从右向左滑动");
////            } else if (e2.getX() - e1.getX() > 50) { //手势从左向右滑动
////                Log.e("Test", "手势从左向右滑动");
////            } else if (e1.getY() - e2.getY() > 50) { //手势从上向下滑动
////                Log.e("Test", "手势从上向下滑动");
////            } else if (e2.getY() - e1.getY() > 50) { //手势从下向上滑动
////                Log.e("Test", "手势从下向上滑动");
////            }
//            return false;
//        }
//    }
}
