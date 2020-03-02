package com.study91.audiobook.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.study91.audiobook.R;
import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.BookMediaClient;
import com.study91.audiobook.book.BookMediaService;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookCatalog;
import com.study91.audiobook.book.IBookContent;
import com.study91.audiobook.option.OptionManager;
import com.study91.audiobook.ui.ContentActivity2;
import com.study91.audiobook.ui.FullActivity;
import com.study91.audiobook.ui.ContentActivity1;
import com.study91.audiobook.ui.MainActivity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * 书媒体播放器视图
 */
public class MediaPlayerView extends RelativeLayout {
    private UI ui = new UI(); //界面
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param attrs 属性集合
     */
    public MediaPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //从布局文件中获取Layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.media_player_view, this);

        //载入界面控件
        ui.audioPositionTextView = (TextView) findViewById(R.id.mediaPositionTextView); //语音位置文本框
        ui.audioLengthTextView = (TextView) findViewById(R.id.mediaLengthTextView); //语音长度文本框
        ui.audioSeekBar = (SeekBar) findViewById(R.id.mediaSeekBar); //语音拖动条
        ui.iconImageView = (ImageView) findViewById(R.id.iconImageView); //图标按钮
        ui.pageTextView = (TextView) findViewById(R.id.pageTextView); //页号文本框
        ui.titleTextView = (TextView) findViewById(R.id.titleTextView); //标题文本框
        ui.playButton = (Button) findViewById(R.id.playButton); //播放按钮
        ui.synchronizationButton = (Button) findViewById(R.id.synchronizationButton); //同步按钮
        ui.fullScreenButton = (Button) findViewById(R.id.fullScreenButton); //全屏按钮
        ui.volumeButton = (Button) findViewById(R.id.volumeButton); //音量按钮
        ui.catalogButton = (Button) findViewById(R.id.catalogButton); //目录按钮

        //设置控件
        ui.audioSeekBar.setOnSeekBarChangeListener(new OnAudioSeekBarChangeListener()); //设置进度改变事件监听器
        ui.iconImageView.setOnClickListener(new OnIconClickListener());
        ui.titleTextView.setOnClickListener(new OnTitleClickListener());
        ui.playButton.setOnClickListener(new OnPlayClickListener());
        ui.synchronizationButton.setOnClickListener(new OnSynchronizationButtonClickListener());
        ui.volumeButton.setOnClickListener(new OnVolumeButtonClickListener());
        ui.catalogButton.setOnClickListener(new OnCatalogButtonClickListener());

        if(isInEditMode()) return; //解决可视化编辑器无法识别自定义控件的问题

        if (getBook().allowFullScreen()) {
            ui.fullScreenButton.setVisibility(VISIBLE); //全屏打开
            ui.fullScreenButton.setOnClickListener(new OnFullScreenButtonClickListener());
        } else { //全屏关闭
            ui.fullScreenButton.setVisibility(GONE);
        }

        getMediaClient().register(); //注册媒体客户端
        getMediaClient().refresh(); //刷新
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        //注销媒体客户端
        if (m.mediaClient != null) {
            m.mediaClient.unregister();
            Log.e("Test", "注销媒体客户端");
        }
    }

    /**
     * 是否正在显示目录
     * @return true=正在显示目录，false=没有显示目录
     */
    public boolean catalogShowing() {
        return m.catalogShowing;
    }

    /**
     * 获取媒体客户端
     * @return 媒体客户端
     */
    private MediaClient getMediaClient() {
        if (m.mediaClient == null) {
            m.mediaClient = new MediaClient(getContext());
        }

        return m.mediaClient;
    }

    /**
     * 解释为时间字符串
     * @param time 时间
     * @return 时间字符串
     */
    private String parseTime(long time) {
        //初始化Formatter的转换格式。
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.CHINA);
        return dateFormat.format(time);
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
     * @return 书ID
     */
    private int getBookID() {
        return m.bookID;
    }

    /**
     * 设置书目录ID
     * @param catalogID 目录ID
     */
    private void setCatalogID(int catalogID) {
        m.catalogID = catalogID;
    }

    /**
     * 获取书目录ID
     * @return 书目录ID
     */
    private int getCatalogID() {
        return m.catalogID;
    }

    /**
     * 设置是否正在播放
     * @param value 值（true=正在播放，false=没有播放）
     */
    private void setIsPlaying(boolean value) {
        m.isPlaying = value;
    }

    /**
     * 是否正在播放
     * @return true=正在播放，false=没有播放
     */
    private boolean isPlaying() {
        return m.isPlaying;
    }

    /**
     * 设置是否正在显示目录
     * @param catalogShowing 是否显示目录值（true=正在显示目录，false=没有显示目录）
     */
    private void setCatalogShowing(boolean catalogShowing) {
        m.catalogShowing = catalogShowing;
    }

    /**
     * 显示目录
     */
    private void showCatalog() {
        int[] location = new int[2];
        getLocationOnScreen(location); //将媒体播放器的座标位置读取到数组中
        int x = location[0]; //媒体播放器的x座标
        int y = location[1]; //媒体播放器的y座标

        //载入布局文件
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.catalog_popwindow, null);

        //实例化弹出窗口
        BookManager.CatalogPopupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                y - getHeight(),
                true);

        //设置弹出窗口背景图片，只有设置了这个属性才能在点击弹出窗口外部或点击返回按钮时关闭弹出窗口
        BookManager.CatalogPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        BookManager.CatalogPopupWindow.setOutsideTouchable(false); //设置是否允许在外点击使其消失，好象上条语句已实现？

        //显示弹出窗口（显示在媒体播放器上方）
        BookManager.CatalogPopupWindow.showAtLocation(
                view,
                Gravity.NO_GRAVITY,
                x,
                y - BookManager.CatalogPopupWindow.getHeight());

        //窗口消失事件监听器
        BookManager.CatalogPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setCatalogShowing(false); //设置为没有显示目录
            }
        });

        setCatalogShowing(true); //设置为显示目录
    }


    /**
     * 显示内容
     */
    private void showContent(int linkType) {
        Log.d("Test", "链接类型：" + linkType);

        switch (linkType) {
            case IBook.MEDIA_LINK_TYPE_SYNCHRONIZATION: //同步内容
                synchronizationContent();
                break;
            case IBook.MEDIA_LINK_TYPE_EXPLAIN1: //标题+原文+详解
                showExplain1();
                break;
            case IBook.MEDIA_LINK_TYPE_EXPLAIN2: //原文+详解
                showExplain2();
                break;
            case IBook.MEDIA_LINK_TYPE_FULL_ORIGINAL: //全屏原文
                showFull1();
                break;
        }
    }

    /**
     * 同步内容
     */
    private void synchronizationContent() {
        IBook book = getBook(); //获取全局书
        int position = ui.audioSeekBar.getProgress(); //获取拖动条位置
        IBookContent content = book.getCurrentAudioCatalog().getAudioContent(position); //获取语音内容
        if (content != null) {
            book.setCurrentContent(content); //设置当前内容
            getMediaClient().refresh(); //刷新媒体客户端
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
    }

    /**
     * 显示详解1
     */
    private void showExplain1() {
        List<IBookCatalog> catalogList = getBook().getCatalogList(); //获取目录列表

        int position = 0;
        int currentAudioCatalogID = getBook().getCurrentAudioCatalogID();
        for (int i=0; i<catalogList.size(); i++) {
            IBookCatalog catalog = catalogList.get(i);
            if (catalog.getCatalogID() == currentAudioCatalogID) {
                position = i;
                break;
            }
        }

        Intent intent = new Intent(getContext(), ContentActivity1.class);
        intent.putExtra("Position", position); //加入目录位置参数
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    /**
     * 显示详解2
     */
    private void showExplain2() {
        List<IBookCatalog> catalogList = getBook().getCatalogList(); //获取目录列表

        int position = 0;
        int currentAudioCatalogID = getBook().getCurrentAudioCatalogID();
        for (int i=0; i<catalogList.size(); i++) {
            IBookCatalog catalog = catalogList.get(i);
            if (catalog.getCatalogID() == currentAudioCatalogID) {
                position = i;
                break;
            }
        }

        Intent intent = new Intent(getContext(), ContentActivity2.class);
        intent.putExtra("Position", position); //加入目录位置参数
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    /**
     * 显示全屏窗口
     */
    private void showFull1() {
        Intent intent = new Intent(getContext(), FullActivity.class);
        getContext().startActivity(intent);
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
            Bundle bundle = intent.getExtras();
            //设置拖动条
            int audioLength = bundle.getInt(BookMediaService.VALUE_AUDIO_LENGTH); //获取语音长度
            int audioPosition = bundle.getInt(BookMediaService.VALUE_AUDIO_POSITION); //获取语音位置
            boolean isPlaying = bundle.getBoolean(BookMediaService.VALUE_IS_PLAYING); //获取是否正在播放

            ui.audioPositionTextView.setText(parseTime(audioPosition)); //设置语音位置文本
            ui.audioLengthTextView.setText(parseTime(audioLength)); //设置语音长度文本
            ui.audioSeekBar.setMax(audioLength); //设置语音进度条最大值
            ui.audioSeekBar.setProgress(audioPosition); //设置语音进度条播放位置

            if (isPlaying) {
                ui.playButton.setBackgroundResource(R.drawable.media_player_pause); //设置为暂停播放图标
            } else {
                ui.playButton.setBackgroundResource(R.drawable.media_player_play); //设置为播放图标
            }
            setIsPlaying(isPlaying); //设置是否正在播放

            IBookCatalog catalog = getBook().getCurrentAudioCatalog(); //获取当前语音目录
            IBookContent audioContent = catalog.getAudioContent(audioPosition); //获取语音内容

            if (getBook().synchronizationEnable()) { //同步复读打开
                ui.synchronizationButton.setBackgroundResource(R.drawable.media_player_synchronization_enable);
            } else { //同步复读取消
                ui.synchronizationButton.setBackgroundResource(R.drawable.media_player_synchronization_disable);
            }

            //当书或书目录有变化时，更新标题内容
            if (getBook().getBookID() != getBookID() ||
                    catalog.getCatalogID() != getCatalogID() ||
                    (audioContent != null && audioContent.getContentID() != m.contentID)) {
                //设置页号
                if (catalog.displayPage()) {
                    ui.pageTextView.setVisibility(View.VISIBLE);
                    if (audioContent != null) { //有内容页时，设置为详细页号
                        ui.pageTextView.setText(Integer.toString(catalog.getAudioContent(audioPosition).getPage())); //设置为内容页号
                    } else { //没有内容页时，设置为目录页号
                        ui.pageTextView.setText(Integer.toString(catalog.getPage())); //设置为目录页号
                    }
                } else {
                    ui.pageTextView.setVisibility(View.GONE);
                }

                //设置图标
                if (catalog.displayIcon()) { //显示图标
                    if (audioContent != null) { //有内容时，设置为内容图标
                        ui.iconImageView.setImageDrawable(audioContent.getIconDrawable());
                    } else { //没有内容时，设置为目录图标
                        ui.iconImageView.setImageDrawable(catalog.getIconDrawable());
                    }
                } else if (catalog.displayPage()) { //显示页码
                    ui.iconImageView.setImageResource(R.mipmap.media_player_icon);
                } else { //没有图标也没有页码
                    ui.iconImageView.setImageResource(R.mipmap.ic_launcher);
                }

                //设置标题内容
                String title = trimLeftString("　", catalog.getTitle().trim());
                ui.titleTextView.setText(title);

                setBookID(getBook().getBookID()); //重新设置书ID
                setCatalogID(catalog.getCatalogID()); //重新设置书目录ID

                if (audioContent != null) {
                    m.contentID = audioContent.getContentID(); //重置内容ID
                } else {
                    m.contentID = 0;
                }
            }
        }
    }

    /**
     * 清除左边字符串
     * @param target 目标字符串
     * @param replacement 字符串
     * @return 清除后的字符串
     */
    private String trimLeftString(String target, String replacement) {
        String result = replacement;

        int index = result.indexOf(target);

        while (index == 0) {
            result = result.replaceFirst(target, "");
            index = result.indexOf(target);
        }

        return result;
    }

//    private Bitmap addCenterImage(Bitmap SrcBmp, Bitmap bitmap) {
//
//        int width = SrcBmp.getWidth() / 5;
//
//        int offsetX = (SrcBmp.getWidth() - bitmap.getWidth()) / 2;
//        int offsetY = (SrcBmp.getHeight() - bitmap.getHeight()) / 2;
//
//        int bmpW = bitmap.getWidth();
//        int bmpH = bitmap.getHeight();
//
//        Canvas ca = new Canvas(bitmap);
//        Path path = new Path();
//
//        for (int i = 0; i < bmpW; i++) {
//            for (int j = 0; j < bmpH; j++) { // 这点不透明而且左右上下四点至少有一点是透明的，那这点就是边缘
//                if ( bitmap.getPixel(i, j) != Color.TRANSPARENT &&
//                        (i < 0 && bitmap.getPixel(i - 1, j) == Color.TRANSPARENT ||
//                        i < bmpW - 1 && bitmap.getPixel(i + 1, j) == Color.TRANSPARENT ||
//                        j < 0 && bitmap.getPixel(i, j - 1) == Color.TRANSPARENT ||
//                        j < bmpH - 1 && bitmap.getPixel(i, j + 1) == Color.TRANSPARENT))
//                path.addRect(i - 1, j - 1, i, j, Path.Direction.CCW); // 搜集边缘
//                Paint paint = new Paint();
//                paint.setAntiAlias(true);
//                paint.setColor(Color.WHITE);
//                paint.setStyle(Paint.Style.FILL_AND_STROKE);
//                paint.setStrokeWidth(4.0f);
//
//                ca.drawPath(path, paint); // 画出边缘
//                Canvas canvas = new Canvas(SrcBmp);
//
//                canvas.drawBitmap(bitmap, offsetX, offsetY, null);
//            }
//        }
//
//        return SrcBmp;
//    }
//
//    private Bitmap getStrokeBitmap(Bitmap bitmap) {
//        int bmpW = bitmap.getWidth();
//        int bmpH = bitmap.getHeight();
//
//        Bitmap strokeBitmap = Bitmap.createBitmap(bmpW + 2, bmpH + 2, Bitmap.Config.ARGB_8888);
//
//        Canvas ca = new Canvas(strokeBitmap);
//        Path path = new Path();
//        Paint paint = new Paint();
//
//        for (int i = 0; i < bmpW; i++) {
//            for (int j = 0; j < bmpH; j++) {
//                // 这点不透明而且上下左右四点至少有一点是透明的，那这点就是边缘
//                if (bitmap.getPixel(i, j) != Color.TRANSPARENT && (
//                        i < 0 && bitmap.getPixel(i - 1, j) == Color.TRANSPARENT ||
//                        i < bmpW - 1 && bitmap.getPixel(i + 1, j) == Color.TRANSPARENT ||
//                        j < 0 && bitmap.getPixel(i, j - 1) == Color.TRANSPARENT ||
//                        j < bmpH - 1 && bitmap.getPixel(i, j + 1) == Color.TRANSPARENT)) {
//                    path.addRect(i - 1, j - 1, i, j, Path.Direction.CCW); // 搜集边缘
//                    paint.setAntiAlias(true);
//                    paint.setColor(Color.WHITE);
//                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
//                    paint.setStrokeWidth(1.0f);
//                    ca.drawPath(path, paint); // 画出边缘
//                }
//            }
//        }
//
//        ca.drawBitmap(bitmap, 1, 1, paint);
//
//        return strokeBitmap;
//    }
//
//    Bitmap getBitmap(Drawable drawable) {
//        if (drawable instanceof BitmapDrawable) {
//            return ((BitmapDrawable) drawable).getBitmap();
//        } else if (drawable instanceof NinePatchDrawable) {
//            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
//                    drawable.getIntrinsicHeight(),
//                    drawable.getOpacity() !=
//                            PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
//                            : Bitmap.Config.RGB_565);
//            Canvas canvas = new Canvas(bitmap);
//            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
//                    drawable.getIntrinsicHeight());
//            drawable.draw(canvas);
//
//            bitmap = getStrokeBitmap(bitmap);
//
//            return bitmap;
//        } else {
//            return null;
//        }
//    }

    /**
     * 播放按钮事件监听器
     */
    private class OnPlayClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (isPlaying()) {
                getMediaClient().pause(); //正在播放时，发出暂停播放指令
            } else {
                getMediaClient().play(); //没有播放时，发出播放指令
            }
        }
    }

    /**
     * 语音拖动条改变事件监听器
     */
    private class OnAudioSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                getMediaClient().seekTo(progress); //定位媒体播放位置
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    /**
     * 同步按钮单击事件监听器
     */
    private class OnSynchronizationButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            getBook().setSynchronizationEnable(!getBook().synchronizationEnable());
            getMediaClient().refresh(); //刷新媒体客户端
        }
    }

    /**
     * 全屏按钮单击事件监听器
     */
    private class OnFullScreenButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            showContent(getBook().getFullScreenLinkType());
        }
    }

    /**
     * 音量按钮单击事件监听器
     */
    private class OnVolumeButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            OptionManager.showVolumeDialog(getContext()); //显示音量对话框
        }
    }

    /**
     * 目录按钮单击事件监听器
     */
    private class OnCatalogButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            showCatalog();
        }
    }

    /**
     * 图标按钮单击事件监听器
     */
    private class OnIconClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            showContent(getBook().getMediaIconLinkType());
        }
    }

    /**
     * 标题单击事件监听器
     */
    private class OnTitleClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            showContent(getBook().getMediaTitleLinkType());
        }
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
         * 媒体客户端
         */
        private MediaClient mediaClient;

        /**
         * 书ID
         */
        private int bookID;

        /**
         * 书目录ID
         */
        private int catalogID;

        /**
         * 内容ID
         */
        private int contentID;

        /**
         * 是否正在播放
         */
        private boolean isPlaying;

        /**
         * 是否正在显示目录
         */
        private boolean catalogShowing;
    }

    /**
     * 私有界面类
     */
    private class UI {
        /**
         * 语音位置文本框
         */
        private TextView audioPositionTextView;

        /**
         * 语音长度文本框
         */
        private TextView audioLengthTextView;

        /**
         * 语音拖动条
         */
        private SeekBar audioSeekBar;

        /**
         * 图标
         */
        private ImageView iconImageView;

        /**
         * 页号文本框
         */
        private TextView pageTextView;

        /**
         * 标题文本框
         */
        private TextView titleTextView;

        /**
         * 播放按钮
         */
        private Button playButton;

        /**
         * 同步按钮
         */
        private Button synchronizationButton;

        /**
         * 全屏按钮
         */
        private Button fullScreenButton;

        /**
         * 音量按钮
         */
        private Button volumeButton;

        /**
         * 目录按钮
         */
        private Button catalogButton;
    }
}
