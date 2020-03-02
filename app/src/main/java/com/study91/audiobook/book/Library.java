package com.study91.audiobook.book;

import android.content.Context;
import android.database.Cursor;

import com.study91.audiobook.data.DataFactory;
import com.study91.audiobook.data.IData;

import java.util.ArrayList;
import java.util.List;

/**
 * 图书馆
 */
class Library implements ILibrary {
    Context mContext; //应用程序上下文

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public Library(Context context) {
        mContext = context;
    }
    
    @Override
    public List<IBook> getBookList() {
        List<IBook> bookList = null;

        IData data = null;
        Cursor cursor = null;

        try {
            data = DataFactory.createData(getContext()); //创建数据对象

            //查询字符串
            String sql = "SELECT [BookID] FROM [Book] ORDER BY [bookID]";
            cursor = data.query(sql); //查询数据

            if(cursor.getCount() > 0) {
                bookList = new ArrayList<>(); //实例化书列表
                for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    int bookID = cursor.getInt(cursor.getColumnIndex("BookID")); //书ID
                    IBook book = BookManager.createBook(getContext(), bookID); //创建书
                    bookList.add(book); //添加到列表
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(cursor != null) cursor.close(); //关闭数据指针
            if(data != null) data.close(); //关闭数据对象
        }

        return bookList;
    }

    @Override
    public List<IBook> getBookListWithout(int bookID) {
        List<IBook> bookList = null;

        IData data = null;
        Cursor cursor = null;

        try {
            data = DataFactory.createData(getContext()); //创建数据对象

            //查询字符串
            String sql = "SELECT [BookID] FROM [Book]" + " " +
                    "WHERE [BookID] <> " + bookID + " " +
                    "ORDER BY [bookID]";
            cursor = data.query(sql); //查询数据

            if(cursor.getCount() > 0) {
                bookList = new ArrayList<>(); //实例化书列表
                for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex("BookID")); //书ID
                    IBook book = BookManager.createBook(getContext(), id); //创建书
                    bookList.add(book); //添加到列表
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(cursor != null) cursor.close(); //关闭数据指针
            if(data != null) data.close(); //关闭数据对象
        }

        return bookList;
    }

    /**
     * 获取应用程序上下文
     */
    private Context getContext() {
        return mContext;
    }
}
