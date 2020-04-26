package com.kuang.book.utils;

import com.kuang.book.entiy.BookInfo;
import com.kuang.book.mapper.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;
import java.util.List;


@Configuration
public class RedisFindUtil {
    @Autowired
    private BookMapper mapper;

    @Autowired
    @Qualifier("redisTemplates")
    private RedisTemplate redis;

    private static BookMapper bookMapper;
    private static RedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        redisTemplate = redis;
        bookMapper = mapper;
    }

    public static List<BookInfo> getAllBook(){
        //如果redis中存在，直接获取
        if (redisTemplate.opsForValue().get("AllBook")!=null){
            List<BookInfo> allBook = (List<BookInfo>) redisTemplate.opsForValue().get("AllBook");
            return allBook;
            //如果不存在，查询数据库，并给redis赋值
        }else {
            List<BookInfo> allBook = bookMapper.getAllBook();
            redisTemplate.opsForValue().set("AllBook",allBook);
            return allBook;
        }
    }

    public static List<BookInfo> getDescAllBook(){
        if (redisTemplate.opsForValue().get("DescAllBook")!=null){
            List<BookInfo> descAllBook = (List<BookInfo>) redisTemplate.opsForValue().get("DescAllBook");
            return descAllBook;

        }else {
            List<BookInfo> descAllBook = bookMapper.getDescAllBook();
            redisTemplate.opsForValue().set("AllBook",descAllBook);
            return descAllBook;
        }
    }

    public static List<BookInfo> getDescTypeBook(String type,String name){
        if (redisTemplate.opsForValue().get("DescTypeBook:"+name)!=null){
            List<BookInfo> descTypeBook = (List<BookInfo>) redisTemplate.opsForValue().get("DescTypeBook:"+name);
            return descTypeBook;

        }else {
            List<BookInfo> descTypeBook = bookMapper.getDescTypeBook(type);
            redisTemplate.opsForValue().set("DescTypeBook:"+name,descTypeBook);
            return descTypeBook;
        }
    }

    //数据库与redis同步方法
    public void Datasync(){

    /*
    *数据库修改同步到redis业务代码
    */
    }
}
