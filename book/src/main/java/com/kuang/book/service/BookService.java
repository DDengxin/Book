package com.kuang.book.service;

import com.kuang.book.entiy.BookContent;
import com.kuang.book.entiy.BookInfo;

import java.util.*;

public interface BookService {
    //翻页操作
    BookContent getPage(int id,String bookName,String page);
    //统计书籍的浏览量
    HashMap getViewCount(int id);




}
