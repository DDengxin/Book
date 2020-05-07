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
//        BookUsers bookUsers = new BookUsers(1,"xxx","1234","@qq",1);
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

    int count=0;
    for (int i=110;i<130;i++){
        Random random  = new Random();
        int ticket = random.nextInt(15000-5000+1)+5000;
        int score = random.nextInt(10-5+1)+5;
        int score1 = random.nextInt(9-1+1)+1;
        count++;
        int x = bookMapper.addBook(""+i, "联盟之侠客行"+count, "打个第五局回来", "/images/彼岸/中国上海城市夜景4K壁纸_彼岸图网.jpg", "肆意挥洒激情的游戏人生，打破现实框架的无尽幻想！", "游戏", "连载中", ""+ticket, ""+score+"."+score1);
        System.out.println("插入成功");
    }



}

@Test
    public void addContent() {
    int id = 50;
    for (int j = id; j < 130; j++) {
        for (int tid = 1; tid < 21; tid++) {
            BookInfo bookId = bookMapper.getBookId(id + "");
            int z = bookMapper.addBookContent("" + bookId.getId(), "" + tid, "" + bookId.getBname(), "第" + tid + "章", "第" + tid + "章" + "内容", "0.00");
            System.out.println("id：" + bookId.getId() + "--->" + bookId.getBname() + "--->第"+tid+"章节"+"插入成功");
        }
        id++;
    }
}

@Test
    public void search(){
    List<BookInfo> result = bookMapper.getSearchBook("sadas");
    System.out.println(result);
    System.out.println(result.size());
    for (int i=0;i<result.size();i++){
        System.out.println(result.get(i).getBname());
    }
}

    }

