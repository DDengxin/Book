package com.kuang.book.mapper;

import com.kuang.book.entiy.BookContent;
import com.kuang.book.entiy.BookInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
@Mapper
public interface BookMapper {
   //获取所有的bookinfo对象
   List<BookInfo> getAllBook();
   //获取单个bookinfo对象
   BookInfo getBookId(int id);
   //获取所有的章节、内容
   List<BookContent> getTitle(String i);
   BookContent getContent(String tid,String bookName);
   //获取所有书籍，按月票大小排序
   List<BookInfo> getDescAllBook();
   //获取按类型的评分大小
   List<BookInfo> getScoreBook(String type);
   List<BookInfo> getPageBook(HashMap map);

   //书籍加入用户收藏
   int userAddBook(String  uid,String bid);

   //获取用户收藏书籍的数量
   int getCollectionCount(String uid);

   //获取用户收藏的书籍
   List<BookInfo> getCollectionBook(HashMap map);
}
