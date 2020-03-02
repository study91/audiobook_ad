package com.study91.audiobook.update;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.study91.audiobook.R;
import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.file.FileFactory;
import com.study91.audiobook.file.IXmlFile;
import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 远程更新
 */
class RemoteUpdate extends AUpdate {
    private Field m = new Field(); //私有字段
    private String tag = "RemoteUpdate"; //标识

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public RemoteUpdate(Context context) {
        super(context);
    }

    @Override
    public void update(boolean isStarted) {
        m.isStarted = isStarted;

        //有网络时启动一个新线程进行远程更新
        if(hasNetworkConnected()) {
            Thread updateThread = new Thread(new UpdateRunnable());
            updateThread.start(); //启动更新线程
        } else if (!isStarted()) {
            showNoNetDialog(); //显示无网络对话框
        }
    }

    /**
     * 是否充许更新
     */
    private boolean allowUpdate() {
        boolean allowUpdate = false;

        try {
            if(getUpdateXmlFile() != null) {
                allowUpdate = Boolean.valueOf(getUpdateXmlFile().getValue("update_allow"));
            }
        } catch (Exception e) {
            Log.e("Test", "allowUpdate()异常：" + e.getMessage());
        }

        return allowUpdate;
    }

    /**
     * 是否启动状态
     */
    private boolean isStarted() {
        return m.isStarted;
    }

    /**
     * 是否充许启动时更新
     */
    private boolean allowStartUpdate() {
        boolean allowAutoUpdate = false;

        if(getUpdateXmlFile() != null) {
            allowAutoUpdate = Boolean.valueOf(getUpdateXmlFile().getValue("update_start"));
        }

        return allowAutoUpdate;
    }

    /**
     * 获取远程版本
     */
    private String getRemoteVersion() {
        String remoteVersion = null;

        if(getUpdateXmlFile() != null) {
            remoteVersion = getUpdateXmlFile().getValue("update_version");
        }

        return remoteVersion;
    }

    /**
     * 获取远程版本号
     */
    private int getRemoteVersionCode() {
        int versionCode = -1;

        if(getUpdateXmlFile() != null) {
            versionCode = Integer.valueOf(getUpdateXmlFile().getValue("update_version_code"));
        }

        return versionCode;
    }

    /**
     * 获取远程包名
     */
    private String getRemotePackage() {
        String remotePackage = null;

        if(getUpdateXmlFile() != null) {
            remotePackage = getUpdateXmlFile().getValue("package");
        }

        return remotePackage;
    }

    /**
     * 获取更新标题
     */
    private String getUpdateTitle() {
        String updateTitle = null;

        if(getUpdateXmlFile() != null) {
            updateTitle = getUpdateXmlFile().getValue("update_title");
        }

        return updateTitle;
    }

    /**
     * 获取更新信息
     */
    private String getUpdateInfo() {
        String updateInfo = null;

        if(getUpdateXmlFile() != null) {
            updateInfo = getUpdateXmlFile().getValue("update_info");
        }

        return updateInfo;
    }

    /**
     * 获取授权信息
     */
    private String getEmpower() {
        String empower = null;

        if(getUpdateXmlFile() != null) {
            empower = getUpdateXmlFile().getValue("empower");
        }

        return empower;
    }

    /**
     * 获取版权信息
     */
    private String getCopyright() {
        String copyright = null;

        if(getUpdateXmlFile() != null) {
            copyright = getUpdateXmlFile().getValue("copyright");
        }

        return copyright;
    }

    /**
     * 获取更新地址Url
     */
    private String getUpdateUrl() {
        String url = null;

        if(getUpdateXmlFile() != null) {
            url = getUpdateXmlFile().getValue("update_url");
        }

        return url;
    }

    /**
     * 获取推荐地址Url
     */
    private String getRecommendUrl() {
        String url = null;

        if(getUpdateXmlFile() != null) {
            url = getUpdateXmlFile().getValue("recommend_url");
        }

        return url;
    }

    /**
     * 获取学习方法地址Url
     */
    private String getStudyUrl() {
        String url = null;

        if(getUpdateXmlFile() != null) {
            url = getUpdateXmlFile().getValue("study_url");
        }

        return url;
    }

    /**
     * 获取问题解答地址Url
     */
    private String getQuestionUrl() {
        String url = null;

        if(getUpdateXmlFile() != null) {
            url = getUpdateXmlFile().getValue("question_url");
        }

        return url;
    }

    /**
     * 获取开始广告显示日（注：安装软件后第几天显示广告）
     */
    private int getAdStartDay() {
        int startAdDay = -1;

        if(getUpdateXmlFile() != null) {
            startAdDay = Integer.valueOf(getUpdateXmlFile().getValue("ad_start_day"));
        }

        return startAdDay;
    }

    /**
     * 获取广告平台
     */
    private int getAdPlatform() {
        int platform = -1;

        if(getUpdateXmlFile() != null) {
            platform = Integer.valueOf(getUpdateXmlFile().getValue("ad_platform"));
        }

        return platform;
    }

    /**
     * 获取广告的应用ID
     */
    private String getAdAppID() {
        String appID = null;

        if(getUpdateXmlFile() != null) {
            appID = getUpdateXmlFile().getValue("ad_app_id");
        }

        return appID;
    }

    /**
     * 获取广告的广告位ID
     */
    private String getAdPlaceID() {
        String placeID = null;

        if(getUpdateXmlFile() != null) {
            placeID = getUpdateXmlFile().getValue("ad_place_id");
        }

        return placeID;
    }

    /**
     * 获取统计ID
     * @return 统计ID
     */
    private String getStatisticsID() {
        String statisticsID = null;

        if(getUpdateXmlFile() != null) {
            statisticsID = getUpdateXmlFile().getValue("statistics_id");
        }

        return statisticsID;
    }

    /**
     * 检查是否有网络连接
     * @return true=有网络连接 false=没有网络连接
     */
    private boolean hasNetworkConnected() {
        //获取手机所有连接管理对象（包括Wi-Fi,Net等连接的管理
        ConnectivityManager connectivityManager =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

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

        return false;
    }

    /**
     * 获取Apk文件名
     */
    private String getApkFilename() {
        String filename = null;

        String downloadUrl = getUpdateUrl();
        int start = downloadUrl.lastIndexOf("/");
        if(start != -1){
            filename = downloadUrl.substring(start + 1);
        }

        return filename;
    }

    /**
     * 显示无网络对话框
     */
    private void showNoNetDialog() {
        // 构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.dialog_update_title);
        builder.setMessage(R.string.msg_no_net);

        // 确定按扭
        builder.setNegativeButton(R.string.button_ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }

    /**
     * 显示软件更新对话框
     */
    private void showUpdateDialog() {
        // 构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.dialog_update_title);
        String msg = getContext().getResources().getString(R.string.dialog_update_message);
        msg = msg.replace("[current_version]", getOption().getVersion());
        msg = msg.replace("[remote_version]", getRemoteVersion());
        msg = msg + "\n\n" + getUpdateInfo();
        builder.setMessage(msg);

        // 更新
        builder.setPositiveButton(R.string.dialog_update_at_once, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog(); //显示下载对话框
            }
        });

        // 稍后更新
        builder.setNegativeButton(R.string.dialog_update_later, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }

    /**
     * 显示不需要更新对话框
     */
    private void showNoUpdateDialog() {
        // 构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.dialog_update_title);

        String msg = getContext().getResources().getString(R.string.dialog_no_update);
        msg = msg.replace("[current_version]", getOption().getVersion());
        msg = msg.replace("[remote_version]", getRemoteVersion());
        builder.setMessage(msg);

        // 确定按钮
        builder.setNegativeButton(R.string.button_ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }

    /**
     * 获取全局选项
     */
    private IOption getOption() {
        return OptionManager.getOption(getContext());
    }

    /**
     * 显示下载对话框
     */
    private void showDownloadDialog()
    {
        // 构造软件下载对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.dialog_updating);

        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.update_progress, null);

        //设置下载进度条
        ProgressBar downloadProgressBar = (ProgressBar) view.findViewById(R.id.updateProgressBar);
        setDownloadProgressBar(downloadProgressBar);

        builder.setView(view);

        // 取消更新
        builder.setNegativeButton(R.string.dialog_update_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                setCancelUpdate(true); //设置为取消更新状态
            }
        });

        //设置下载对话框
        Dialog downloadDialog = builder.create();
        setDownloadDialog(downloadDialog);

        getDownloadDialog().show(); //显示下载对话框

        //启动下载线程
        Thread downloadApkThread = new DownloadThread();
        downloadApkThread.start();
    }

    /**
     * 安装APK文件
     */
    private void installApk()
    {
        File apkfile = new File(getDownloadPath(), getApkFilename());
        if (!apkfile.exists())
        {
            return;
        }

        // 通过Intent安装APK文件
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
                Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        getContext().startActivity(intent);
    }

    /**
     * 获取更新处理器
     * @return 更新处理器
     */
    private Handler getUpdateHandler() {
        return m.updateHandler;
    }

    /**
     * 设置下载进度条
     * @param progressBar 进度条
     */
    private void setDownloadProgressBar(ProgressBar progressBar) {
        m.downloadProgressBar = progressBar;
    }

    /**
     * 设置下载进度
     * @param value 下载进度值
     */
    private void setDownloadProgress(int value) {
        m.downloadProgress = value;
    }

    /**
     * 获取更新进度
     * @return 更新进度
     */
    private int getDownloadProgress() {
        return m.downloadProgress;
    }

    /**
     * 获取下载进度条
     * @return 进度条
     */
    private ProgressBar getDownloadProgressBar() {
        return m.downloadProgressBar;
    }

    /**
     * 设置下载对话框
     * @param dialog 对话框
     */
    private void setDownloadDialog(Dialog dialog) {
        m.downloadDialog = dialog;
    }

    /**
     * 获取下载对话框
     * @return 下载对话框
     */
    private Dialog getDownloadDialog() {
        return m.downloadDialog;
    }

    /**
     * 设置取消更新
     * @param value 值
     */
    private void setCancelUpdate(boolean value) {
        m.cencelUpdate = value;
    }

    /**
     * 是否取消更新
     * @return true=取消更新 false=不取消更新
     */
    private boolean isCancelUpdate() {
        return m.cencelUpdate;
    }

    /**
     * 获取下载路径
     * @return 下载路径
     */
    private String getDownloadPath() {
        if(m.savePath == null) {
            m.savePath = Environment.getExternalStorageDirectory() + "/study91/download";
        }

        return m.savePath;
    }

    /**
     * 获取更新Xml文件
     */
    private IXmlFile getUpdateXmlFile() {
        try {
            if (m.updateXmlFile == null) {
                Log.e("Test", "更新Url:" + getBook().getUpdateUrl());
                m.updateXmlFile = FileFactory.createXmlFile(getBook().getUpdateUrl());
            }
        } catch (Exception e) {
            Log.e("Test", "getUpdateXmlFile()异常：" + e.getMessage());
        }

        return m.updateXmlFile;
    }

    /**
     * 获取全局书
     */
    private IBook getBook() {
        return BookManager.getBook(getContext());
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 是否启动状态
         */
        boolean isStarted;

        /**
         * 更新处理器
         */
        Handler updateHandler = new UpdateHandler();

        /**
         * 下载进度条
         */
        ProgressBar downloadProgressBar;

        /**
         * 更新进度
         */
        int downloadProgress;

        /**
         * 下载对话框
         */
        Dialog downloadDialog;

        /**
         * 是否取消更新
         */
        boolean cencelUpdate;

        /**
         * 存储路径
         */
        String savePath;

        /**
         * 更新Xml文件
         */
        IXmlFile updateXmlFile;
    }

    /**
     * 更新Runnable类
     */
    private class UpdateRunnable implements Runnable {
        @Override
        public void run() {
            try {
                if (allowUpdate()) {
                    getBook().updateRecommendUrl(getRecommendUrl()); //更新推荐Url地址
                    getBook().updateEmpower(getEmpower()); //更新授权信息
                    getBook().updateAdPlatform(getAdPlatform()); //更新广告平台
                    getBook().updateAdAppID(getAdAppID()); //更新广告的应用ID
                    getBook().updateAdPlaceID(getAdPlaceID()); //更新广告的广告位ID
                    getBook().updateStatisticsID(getStatisticsID()); //更新统计ID
                    getOption().updateStudyUrl(getStudyUrl()); //更新学习方法Url地址
                    getOption().updateQuestionUrl(getQuestionUrl()); //更新问题解答Url地址
                    getOption().updateAdStartDay(getAdStartDay()); //更新广告显示日
                    getOption().updateCopyright(getCopyright()); //更新版权信息

                    Log.d(tag, "更新推荐Url地址：" + getRecommendUrl());
                    Log.d(tag, "更新授权信息：" + getEmpower());
                    Log.d(tag, "更新版权信息：" + getCopyright());
                    Log.d(tag, "更新学习方法Url地址：" + getStudyUrl());
                    Log.d(tag, "更新问题解答Url地址：" + getQuestionUrl());
                    Log.d(tag, "更新广告显示日：" + getAdStartDay());
                    Log.d(tag, "更新广告平台：" + getAdPlatform());
                    Log.d(tag, "更新广告的应用ID：" + getAdAppID());
                    Log.d(tag, "更新广告的广告位ID：" + getAdPlaceID());
                    Log.d(tag, "更新统计ID：" + getStatisticsID());

                    if ((isStarted() && allowStartUpdate()) || !isStarted()) {
                        //更新数据
                        int remoteVersionCode = getRemoteVersionCode(); //获取远程版本号
                        int currentVersionCode = getCurrentVersionCode(); //获取当前应用程序版本号

                        Log.e("Test", "RemoteUpdate.UpdateRunnable.run():" +
                                "远程版本=" + remoteVersionCode + "," +
                                "当前版本=" + currentVersionCode);

                        Handler updateHandler = getUpdateHandler(); //获取更新处理器

                        //如果更新版本号大于当前应用程序版本号，进行远程更新
                        if (remoteVersionCode > currentVersionCode) {
                            updateHandler.sendEmptyMessage(UpdateHandler.MSG_UPDATE); //发送更新信息
                        } else if (!isStarted()) {
                            updateHandler.sendEmptyMessage(UpdateHandler.MSG_NO_UPDATE); //发送没有更新的信息
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("Test", "更新异常：" + e.getMessage());
            }
        }
    }

    /**
     * 更新处理器
     */
    private class UpdateHandler extends Handler {
        public static final int MSG_UPDATE = 1; //更新消息
        public static final int MSG_DOWNLOAD = 2; //下载消息
        public static final int MSG_DOWNLOAD_FINISH = 3; //下载完成消息
        public static final int MSG_NO_UPDATE = 4; //没有更新消息

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case MSG_NO_UPDATE: //没有更新
                    showNoUpdateDialog(); //显示不需要更新对话框
                    break;
                case MSG_UPDATE: //更新
                    showUpdateDialog(); //显示更新对话框
                    break;
                case MSG_DOWNLOAD: //下载
                    getDownloadProgressBar().setProgress(getDownloadProgress());
                    break;
                case MSG_DOWNLOAD_FINISH: //下载完成
                    installApk();
                    break;
            }
        }
    }

    /**
     * 下载文件线程
     */
    private class DownloadThread extends Thread {
        @Override
        public void run() {
            try	{
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    File file = new File(getDownloadPath()); //实例化存储目录的文件对象
                    if (!file.exists()) {
                        file.mkdirs(); //存储目录不存在时，创建目录
                    }

                    URL url = new URL(getUpdateUrl()); //实例化URL对象

                    // 创建连接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    int length = connection.getContentLength(); //获取文件大小
                    InputStream inputStream = connection.getInputStream(); //创建输入流

                    File apkFile = new File(getDownloadPath(), getApkFilename());
                    OutputStream outputStream = new FileOutputStream(apkFile);

                    int count = 0;
                    byte[] buffer = new byte[1024]; //缓存

                    // 写入到文件中
                    do {
                        int readLength = inputStream.read(buffer);
                        count += readLength;

                        int progress = (int) (((float) count / length) * 100); //计算进度条位置
                        setDownloadProgress(progress); //设置更新进度
                        getUpdateHandler().sendEmptyMessage(UpdateHandler.MSG_DOWNLOAD); // 更新进度

                        if (readLength <= 0)
                        {
                            getUpdateHandler().sendEmptyMessage(UpdateHandler.MSG_DOWNLOAD_FINISH); //下载完成
                            break;
                        }

                        outputStream.write(buffer, 0, readLength); //写入文件
                    } while (!isCancelUpdate());// 点击取消就停止下载.

                    outputStream.close();
                    inputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            getDownloadDialog().dismiss(); //取消下载对话框显示
        }
    };
}