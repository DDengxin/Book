package com.kuang.book.service;

import com.kuang.book.entiy.BookContent;
import com.kuang.book.entiy.BookInfo;

import java.util.*;

public interface BookService {

    //统计书籍的浏览量
    Integer getViewCount(int id);
    //获取所有书籍分类排行
    HashMap<String,List<BookInfo>> getDescAllType();
    //获取分页后的书籍
    List<BookInfo> getPageBook(String type,int page);



}
