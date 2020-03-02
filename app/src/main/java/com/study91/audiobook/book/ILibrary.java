package com.study91.audiobook.book;

import java.util.List;

/**
 * 图书馆接口
 */
public interface ILibrary {
    /**
     * 获取书列表
     */
    List<IBook> getBookList();

    /**
     * 获取书列表除了书ID以外的所有书列表
     * @param bookID 书ID
     */
    List<IBook> getBookListWithout(int bookID);
}
