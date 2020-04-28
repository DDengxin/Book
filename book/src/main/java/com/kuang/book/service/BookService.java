package com.kuang.book.service;

import com.kuang.book.entiy.BookContent;
import com.kuang.book.entiy.BookInfo;

import java.util.*;

public interface BookService {

    //统计书籍的浏览量
    Integer getViewCount(int id);

    HashMap<String,List<BookInfo>> getDescAllType();




}
