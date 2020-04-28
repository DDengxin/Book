package com.kuang.book;
import com.kuang.book.entiy.BookInfo;
import com.kuang.book.entiy.BookUsers;
import com.kuang.book.mapper.BookMapper;
import com.kuang.book.mapper.UserMapper;
import com.kuang.book.service.BookService;
import org.apache.shiro.crypto.hash.Hash;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.awt.*;
import java.util.*;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class BookApplicationTests  {

    @Autowired
    BookMapper bookMapper;

    @Autowired
    UserMapper userMapper;
    @Autowired
    BookService bookService;

    @Autowired
    @Qualifier("redisTemplates")
    private RedisTemplate redisTemplate;
    @Test
    public void test(){
        BookUsers bookUsers = new BookUsers(1,"xxx","1234","@qq",1);
//        redisTemplate.opsForZSet().add("monthTicket","id:2",10000);
//        redisTemplate.opsForZSet().add("monthTicket","id:3",7892);
        //查询集合user中0-1000000的区间，并排序输出
        Set users = redisTemplate.opsForZSet().reverseRangeByScoreWithScores("monthTicket", 0, 10000000);
        //获取set个数
       System.out.println(users.size());
        //变量set集合
        Iterator<ZSetOperations.TypedTuple<String>> iterator =users.iterator();
        while (iterator.hasNext()){
            ZSetOperations.TypedTuple<String> typedTuple = iterator.next();
            Object value = typedTuple.getValue();
            double score = typedTuple.getScore();
            int scores = (int)score;
            System.out.println("获取RedisZSetCommands.Tuples的区间值:" + value + "---->" + scores );
        }


    }

//随机增加redis数据
    @Test
    public void add(){
        for (int i=0;i<50;i++){
            redisTemplate.opsForHash().put("viewCount","viewCount:id:"+i,0);
            System.out.println(redisTemplate.opsForHash().values("viewCount").get(i));
        }


//        Set users = redisTemplate.opsForZSet().reverseRangeByScoreWithScores("monthTicket", 0, 10000000);
//        //变量set集合
//        Iterator<ZSetOperations.TypedTuple<String>> iterator =users.iterator();
//        while (iterator.hasNext()){
//            ZSetOperations.TypedTuple<String> typedTuple = iterator.next();
//            Object value = typedTuple.getValue();
//            double score = typedTuple.getScore();
//            int scores = (int)score;
//            System.out.println("获取RedisZSetCommands.Tuples的区间值:" + value + "---->" + scores );
//        }

    }
// 分类测试
@Test
    public void test2(){

    System.out.println(redisTemplate.opsForHash().entries("sb").get(0));
    }
}

