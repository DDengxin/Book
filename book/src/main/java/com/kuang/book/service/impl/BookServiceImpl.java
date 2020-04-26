package com.kuang.book.service.impl;

import com.kuang.book.entiy.BookContent;
import com.kuang.book.mapper.BookMapper;
import com.kuang.book.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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


    /*
        书籍翻页
    */
    @Override
    public BookContent getPage(int id, String bookName, String page) {
        try {

            if (page.equals("next")) {
                String tid = Integer.toString(id + 1);
                BookContent content = bookMapper.getContent(tid, bookName);
                int nextExise = content.getTid();
                return content;
            } else {
                String tid = Integer.toString(id - 1);
                BookContent content = bookMapper.getContent(tid, bookName);
                int nextExise = content.getTid();
                return content;
            }
        } catch (NullPointerException e) {
            return bookMapper.getContent("1", bookName);
        }
    }



    @Override
    public HashMap getViewCount(int id) {
        //浏览量自增+1
        redisTemplate.opsForValue().increment("viewCount:id:"+id);
        //获取浏览量
        Integer count = (Integer) redisTemplate.opsForValue().get("viewCount:id:" + id);
        HashMap  viewCount = new HashMap();
        viewCount.put(id,count);
        return viewCount;
    }

}







/*
    书籍类型进行月票排行
 */

//    @Override
//    public HashMap<String, ArrayList<BookInfo>> typeBookRank(HashMap<String, ArrayList<BookInfo>> bookSortMap) {
//        //查询redis中所有的数据，使用zset进行排序，id顺序为月票从大到小排列，并用linkHashMap返回
//        LinkedHashMap<String, Integer> ticketMap = RedisUtil.SortTicket();
//        HashMap<String,ArrayList<BookInfo>> resultMap =new HashMap();
//        ArrayList<BookInfo> xuanhuan = new ArrayList<>();
//        ArrayList<BookInfo> lishi = new ArrayList<>();
//        ArrayList<BookInfo> dushi = new ArrayList<>();
//        ArrayList<BookInfo> wuxia = new ArrayList<>();
//        ArrayList<BookInfo> xiuxian = new ArrayList<>();
//
//        Set<String> set = ticketMap.keySet();
//        //遍历redis获取的中所有的key--->id:x:xxx
//        for (String value:set){
//            //将id:x:xx字符分割为id
//            int id = Integer.parseInt(value.substring(0, value.lastIndexOf(":")).replace("id:",""));
//            // 分割type
//            String type = value.substring(value.lastIndexOf(":")).replace(":","");
//            //当类型为等于玄幻时
//            if (type.equals("xh")) {
//                //获取玄幻类型的所有集合
//                ArrayList<BookInfo> xh = bookSortMap.get("xuanhuan");
//                //遍历玄幻类型的集合，
//                for (int i = 0; i<xh.size(); i++) {
//                    if(xh.get(i).getId()==id){
//                        xuanhuan.add(xh.get(i));
//                    }
//                }
//            }
//            else if (type.equals("ls")){
//                ArrayList<BookInfo> ls = bookSortMap.get("lishi");
//                for (int i = 0; i<ls.size(); i++) {
//                    if(ls.get(i).getId()==id){
//                        lishi.add(ls.get(i));
//                    }
//                }
//            }
//            else if (type.equals("ds")){
//                ArrayList<BookInfo> ds = bookSortMap.get("dushi");
//                for (int i = 0; i<ds.size(); i++) {
//                    if(ds.get(i).getId()==id){
//                        dushi.add(ds.get(i));
//                    }
//                }
//            }
//            else if (type.equals("wx")){
//                ArrayList<BookInfo> wx = bookSortMap.get("wuxia");
//                for (int i = 0; i<wx.size(); i++) {
//                    if(wx.get(i).getId()==id){
//                        wuxia.add(wx.get(i));
//                    }
//                }
//            }
//            else if (type.equals("xx")){
//                ArrayList<BookInfo> xx = bookSortMap.get("xiuxian");
//                for (int i = 0; i<xx.size(); i++) {
//                    if(xx.get(i).getId()==id){
//                        xiuxian.add(xx.get(i));
//                    }
//                }
//            }
//
//        }
//        if (xuanhuan.size()!=0){
//            resultMap.put("xuanhuan",xuanhuan);
//        }
//        else if (xiuxian.size()!=0) {
//            resultMap.put("xiuxian", xiuxian);
//        }
//        else if (lishi.size()!=0) {
//            resultMap.put("lishi", lishi);
//        }
//        else if (dushi.size()!=0) {
//            resultMap.put("dushi", dushi);
//        }
//        else if (wuxia.size()!=0) {
//            resultMap.put("wuxia", wuxia);
//        }
//        return resultMap;
//    }
//
//
//}

