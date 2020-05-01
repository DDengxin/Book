package com.kuang.book.service;

import com.kuang.book.entiy.BookContent;
import com.kuang.book.entiy.BookInfo;

import java.util.*;

public interface BookService {

    //统计书籍的浏览量
    Integer getViewCount(int id);
    //获取所有书籍分类排行
    HashMap<String,List<BookInfo>> getDescAllType();
    //评分书籍分页
    List<BookInfo> getPageBook(String type,int page);

    //添加用户书籍收藏
    Boolean addCollectionBook(String uid,String bid);

    //用户收藏书籍分页显示
    List<BookInfo> getCollectionBook(String uid,int page);

}
