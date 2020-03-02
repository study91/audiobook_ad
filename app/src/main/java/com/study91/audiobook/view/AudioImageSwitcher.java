package com.study91.audiobook.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import com.study91.audiobook.R;
import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.BookMediaClient;
import com.study91.audiobook.book.BookMediaService;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookContent;
import com.study91.audiobook.file.FileFactory;
import com.study91.audiobook.file.IFile;
import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;

/**
 * 语音图片切换器
 */
public class AudioImageSwitcher extends ImageSwitcher {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public AudioImageSwitcher(Context context) {
        super(context);
        init(); //初始化
    }

    public AudioImageSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) { return; }
        init(); //初始化
    }

    @Override
    protected void onDetachedFromWindow() {
        //注销媒体客户端
        if (m.mediaClient != null) {
            getMediaClient().unregister();
        }

        super.onDetachedFromWindow();
    }

    /**
     * 初始化
     */
    private void init() {
        setFactory(new ImageViewFactory()); //设置工厂

        //设置布局参数
        setLayoutParams(new LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        getMediaClient().register(); //注册媒体客户端
    }

    /**
     * 获取媒体客户端
     * @return 媒体客户端
     */
    private BookMediaClient getMediaClient() {
        if (m.mediaClient == null) {
            m.mediaClient = new MediaClient(getContext());
        }

        return m.mediaClient;
    }

    /**
     * 获取全局书
     * @return 全局书
     */
    private IBook getBook() {
        return BookManager.getBook(getContext());
    }

    /**
     * 设置内容ID
     * @param contentID 内容ID
     */
    private void setContentID(int contentID) {
        m.contentID = contentID;
    }

    /**
     * 获取内容ID
     * @return 内容ID
     */
    private int getContentID() {
        return m.contentID;
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 媒体客户端
         */
        BookMediaClient mediaClient;

        /**
         * 内容ID
         */
        int contentID;
    }

    /**
     * 图片视图工厂
     */
    private class ImageViewFactory implements ViewSwitcher.ViewFactory {
        @Override
        public View makeView() {
            // 创建ImageView对象
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));

            return imageView; // 返回ImageView对象
        }
    }

    /**
     * 媒体客户端
     */
    private class MediaClient extends BookMediaClient {
        /**
         * 构造器
         *
         * @param context 应用程序上下文
         */
        public MediaClient(Context context) {
            super(context);
        }

        @Override
        public void setOnReceive(Intent intent) {
            int position = intent.getExtras().getInt(BookMediaService.VALUE_AUDIO_POSITION); //获取语音位置

            IBookContent content = getBook().getCurrentAudioCatalog().getAudioContent(position); //获取当前语音内容

            //如果图片有效，设置为当前显示的图片
            if (content != null && content.getContentID() != getContentID()) {
                if (getContentID() != 0) {
                    Animation inAnimation; //进入转场动画
                    Animation outAnimation; //移出转场动画

                    //获取原内容
                    IBookContent oldContent = BookManager.createBookContent(getContext(), getContentID());

                    if (content.getPage() == getBook().getFirstAudioCatalog().getPage() &&
                            oldContent.getCatalog().getPage() == getBook().getLastAudioCatalog().getPage()) {
                        //尾记录向后翻页到首记录
                        inAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.right_in); //右边进入
                        outAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.left_out); //左边移出
                    } else if (content.getPage() == getBook().getLastAudioCatalog().getPage() &&
                            oldContent.getCatalog().getPage() == getBook().getFirstAudioCatalog().getPage()) {
                        //首记录向前翻页到尾记录
                        inAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.left_in); //左边进入
                        outAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.right_out); //右边移出
                    } else if (content.getPage() > oldContent.getPage()) {
                        //向后翻页
                        inAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.right_in); //右边进入
                        outAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.left_out); //左边移出
                    } else {
                        //向前翻页
                        inAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.left_in); //左边进入
                        outAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.right_out); //右边移出
                    }

                    setInAnimation(inAnimation); //设置进入转场动画
                    setOutAnimation(outAnimation); //设置移出转场动画
                }

                IOption option = OptionManager.getOption(getContext()); //获取选项

                //创建文件
                IFile file = FileFactory.createFile(
                        getContext(),
                        option.getStorageType(),
                        content.getImageFilename());

                //创建内容Drawable
                Drawable contentDrawable = Drawable.createFromStream(file.getInputStream(), null);
                setImageDrawable(contentDrawable); //设置内容Drawable

                setContentID(content.getContentID()); //重置内容ID
            }
        }
    }
}