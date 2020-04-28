package com.kuang.book.service.impl;

import com.kuang.book.entiy.BookContent;
import com.kuang.book.entiy.BookInfo;
import com.kuang.book.mapper.BookMapper;
import com.kuang.book.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    BookMapper bookMapper;
    @Autowired
    @Qualifier("redisTemplates")
    private RedisTemplate redisTemplate;

    @Override
    public Integer getViewCount(int id) {
        //浏览量自增+1
        redisTemplate.opsForHash().increment("viewCount", "viewCount:id:"+id, 1);
        Integer viewCount = (Integer) redisTemplate.opsForHash().entries("viewCount").get("viewCount:id:" + id);
        return viewCount;
    }


    @Override
    public HashMap<String, List<BookInfo>> getDescAllType() {
        List<BookInfo> descAllBook = bookMapper.getDescAllBook();
        HashMap<String, List<BookInfo>> map = new HashMap<>();
        List<BookInfo> xh = new ArrayList<>();
        List<BookInfo> wx = new ArrayList<>();
        List<BookInfo> ds = new ArrayList<>();
        List<BookInfo> xx = new ArrayList<>();
        List<BookInfo> ls = new ArrayList<>();

        for (int i=0;i<descAllBook.size();i++){
            if (descAllBook.get(i).getType().equals("玄幻")){
                xh.add(descAllBook.get(i));
            }
            else if (descAllBook.get(i).getType().equals("武侠")){
                wx.add(descAllBook.get(i));
            }
            else if (descAllBook.get(i).getType().equals("都市")){
                ds.add(descAllBook.get(i));
            }
            else if (descAllBook.get(i).getType().equals("修仙")){
                xx.add(descAllBook.get(i));
            }
            else if (descAllBook.get(i).getType().equals("历史")){
                ls.add(descAllBook.get(i));
            }
        }
        map.put("玄幻",xh);
        map.put("历史",ls);
        map.put("武侠",wx);
        map.put("修仙",xx);
        map.put("都市",ds);
        redisTemplate.opsForHash().put("descBook","xuanhuan",xh);
        redisTemplate.opsForHash().put("descBook","lishi",ls);
        redisTemplate.opsForHash().put("descBook","wuxia",wx);
        redisTemplate.opsForHash().put("descBook","xiuxian",xx);
        redisTemplate.opsForHash().put("descBook","dushi",ds);
        return map;
    }
}
