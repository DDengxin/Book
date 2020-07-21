package com.kuang.book;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
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
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static org.apache.commons.codec.binary.StringUtils.newString;
import static org.apache.http.util.EncodingUtils.getBytes;


@RunWith(SpringRunner.class)
@SpringBootTest
public class BookApplicationTests {

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
    public void test() {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4G4BrpiZvFF8k69KPEW4", "HDZ3NdGhoBNO0q5ZBpyAV64gPEHZQp");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendService");

        request.putQueryParameter("TemplateCode", "SMS_189762050");
        request.putQueryParameter("SignName", "黄金书屋科技");
        request.putQueryParameter("PhoneNumber", "15607486434");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }


    }

    //随机增加redis数据
    @Test
    public void add() {
        for (int i = 0; i < 50; i++) {

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
    public void test2() {

        int count = 0;
        for (int i = 110; i < 130; i++) {
            Random random = new Random();
            int ticket = random.nextInt(15000 - 5000 + 1) + 5000;
            int score = random.nextInt(10 - 5 + 1) + 5;
            int score1 = random.nextInt(9 - 1 + 1) + 1;
            count++;
            int x = bookMapper.addBook("" + i, "联盟之侠客行" + count, "打个第五局回来", "/images/彼岸/中国上海城市夜景4K壁纸_彼岸图网.jpg", "肆意挥洒激情的游戏人生，打破现实框架的无尽幻想！", "游戏", "连载中", "" + ticket, "" + score + "." + score1);
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
                System.out.println("id：" + bookId.getId() + "--->" + bookId.getBname() + "--->第" + tid + "章节" + "插入成功");
            }
            id++;
        }
    }

    @Test
    public void search() throws UnsupportedEncodingException {

    }


}
