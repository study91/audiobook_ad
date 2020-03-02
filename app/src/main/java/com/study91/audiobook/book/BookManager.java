package com.study91.audiobook.book;

import android.content.Context;
import android.widget.PopupWindow;

import com.study91.audiobook.config.ConfigFactory;
import com.study91.audiobook.config.IConfig;
import com.study91.audiobook.data.DataFactory;
import com.study91.audiobook.data.IData;
import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;

/**
 * 书管理器
 */
public class BookManager {
    /**
     * 目录弹出窗口
     */
    public static PopupWindow CatalogPopupWindow;

    /**
     * 设置书
     * @param context 应用程序上下文
     */
    public static void setBook(Context context, int bookID) {
        if (bookID != M.book.getBookID()) {
            IData data = null; //声明数据对象

            try {
                data = DataFactory.createData(context); //创建数据对象
                IOption option = OptionManager.getOption(context); //获取全局选项

                //更新字符串
                String sql = "UPDATE [Option] " +
                        "SET [BookID] = " + bookID + " " +
                        "WHERE [OptionID] = " + option.getOptionID();

                data.execute(sql); //执行更新
                M.book = createBook(context, bookID); //创建书
            } finally {
                //关闭数据对象
                if (data != null) {
                    data.close();
                }
            }
        }
    }

    /**
     * 获取书
     * @param context 应用程序上下文
     * @return 书
     */
    public static IBook getBook(Context context) {
        //如果全局书为空值，创建选项书ID的书
        if (M.book == null) {
            IOption option = OptionManager.getOption(context);
            if (option.canChooseBook()) { //能选书
                M.book = createBook(context, option.getBookID());
            } else { //不能选书
                IConfig config = ConfigFactory.getConfig(context);
                M.book = createBook(context, config.getBookID());
            }
        }

        return M.book;
    }

    /**
     * 创建图书馆
     * @param context 应用程序上下文
     * @return 图书馆
     */
    public static ILibrary createLibrary(Context context) {
        return new Library(context);
    }

    /**
     * 创建书
     * @param context 应用程序上下文
     * @param bookID 书ID
     * @return 书
     */
    static IBook createBook(Context context, int bookID) {
        return new Book(context, bookID);
    }

    /**
     * 创建书目录
     * @param catalogID 目录ID
     * @return 书目录
     */
    static IBookCatalog createBookCatalog(Context context, int catalogID) {
        return new BookCatalog(context, catalogID);
    }

    /**
     * 创建书内容
     * @param context 应用程序上下文
     * @param contentID 内容ID
     * @return 书内容
     */
    public static IBookContent createBookContent(Context context, int contentID) {
        return new BookContent(context, contentID);
    }

    /**
     * 创建音乐
     * @param context 应用程序上下文
     * @param musicID 音乐ID
     * @return 书音乐
     */
    static IBookMusic createBookMusic(Context context, int musicID) {
        return new BookMusic(context, musicID);
    }

    /**
     * 私有全局类
     */
    private static class M {
        /**
         * 书
         */
        static IBook book;
    }
}
