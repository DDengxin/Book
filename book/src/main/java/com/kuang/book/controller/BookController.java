package com.kuang.book.controller;
import com.kuang.book.entiy.BookContent;
import com.kuang.book.entiy.BookInfo;
import com.kuang.book.entiy.BookUsers;
import com.kuang.book.mapper.BookMapper;
import com.kuang.book.mapper.UserMapper;
import com.kuang.book.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


@Controller
public class BookController {
    @Autowired
    BookMapper bookMapper;
    @Autowired
    BookService bookService;
    @Autowired
    @Qualifier("redisTemplates")
    RedisTemplate redisTemplate;
    @Autowired
    UserMapper userMapper;



    @GetMapping(value = {"/","/index"})
    public String index(Model model) {
        HashMap<String, List<BookInfo>> descTypeBook;
        List<BookInfo> AllBook;

        if (redisTemplate.opsForHash().entries("descBook").get("xuanhuan")!=null){
            descTypeBook = (HashMap<String, List<BookInfo>>) redisTemplate.opsForHash().entries("descBook");
        }else {
            descTypeBook = bookService.getDescAllType();
        }
        if (redisTemplate.opsForValue().get("AllBook")!=null){
            AllBook = (List<BookInfo>) redisTemplate.opsForValue().get("AllBook");

        }else {
            AllBook = bookMapper.getAllBook();
            redisTemplate.opsForValue().set("AllBook",AllBook);
//            redisTemplate.expire("AllBook",60*5, TimeUnit.SECONDS);
        }
        model.addAttribute("xuanhuan", descTypeBook.get("xuanhuan"));
        model.addAttribute("wuxia", descTypeBook.get("wuxia"));
        model.addAttribute("lishi", descTypeBook.get("lishi"));
        model.addAttribute("xiuxian",descTypeBook.get("xiuxian"));
        model.addAttribute("dushi", descTypeBook.get("dushi"));
        model.addAttribute("books", AllBook);

        return "index";
    }

    @GetMapping("/pay/order")
    public String order(){
        return "order";
    }

    @GetMapping("/read")
    public String read(HttpServletRequest request, Model model) {
        String bid = request.getParameter("id");
        String uid = request.getParameter("uid");

        if (!(uid.equals("null"))){
            String userBook = bookMapper.userGetBook(uid, bid);
            if (userBook==null){
                model.addAttribute("msg","收藏书籍");
            }
            else {
                model.addAttribute("msg","取消收藏");
            }
        }else {
            model.addAttribute("msg","收藏书籍");
        }

        Integer viewCount = bookService.getViewCount(bid);//每次访问浏览量+1
        BookInfo currenBook = bookMapper.getBookId(bid); //获取用户当前点击的书籍
        List<BookContent> bookTitleItem = bookMapper.getTitle(bid); //遍历当前页面所有的标题

        model.addAttribute("viewCount",viewCount);
        model.addAttribute("bookInfo", currenBook);
        model.addAttribute("bookContentItem", bookTitleItem);
        return "read";
    }

    @PostMapping("/read/collect")
    @ResponseBody
    public String bookCollect(String uid,String bid,String action){
        if (uid.equals("")){
            return "login";
        }
        else if (action.equals("收藏书籍")){
            bookMapper.userAddBook(uid, bid);
            return "addSuccess";
        }

        bookMapper.userDelBook(uid, bid);
        return "delSuccess";
    }



    @GetMapping("/content")
    public String content(HttpServletRequest request, Model model) {
        String bid = request.getParameter("id");
        String tid = request.getParameter("tid");
        String uid = request.getParameter("uid");
        String result = bookService.getAuthContent(bid, tid, uid);

        if (result.equals("free")){
            BookContent currentBook = bookMapper.getContent(tid, bid);
            model.addAttribute("currentBook",currentBook);
            return "content";
        }
        else if (result.equals("pay")){
            return "redirect:/pay?tid="+tid+"&uid="+uid+"&bid="+bid;
        }
        else {
            return "404";
        }
    }






    @GetMapping("/chargeMoney")
    public String chargeMoney(HttpServletRequest request,Model model){
        String uid = request.getParameter("uid");
        double myMoney = userMapper.getUserMoney(uid);
        BookUsers userInfo = userMapper.getUserName(uid);
        model.addAttribute("myMoney",myMoney);
        model.addAttribute("userInfo",userInfo);
        return "charge_money";
    }



    @GetMapping("sortBook")
    public String bookSort(HttpServletRequest request,Model model){
        String type = request.getParameter("type");
        String spage = request.getParameter("page");
        if(spage==null){
            spage="1";
        }
        int page = Integer.parseInt(spage);
        int count =0;
        //获取底部的页码
        List<BookInfo> scoreBook = bookMapper.getScoreBook(type);
        for (int size=scoreBook.size();size>0;){
            size-=4;
            count+=1;
        }
        //获取分页后的内容
        List<BookInfo> pageBook = bookService.getPageBook(type, page);
        model.addAttribute("pageBook",pageBook);
        System.out.println(pageBook.size());
        model.addAttribute("scoreBook",scoreBook);
        model.addAttribute("page",count);
        return "sort_book";
    }

    @GetMapping("/selfInfo")
    public String selfInfo(HttpServletRequest request,Model model){
        if (request.getParameter("uid").equals("null")){
            return "login";
        }

        //获取用户当前页的所有书籍
        String spage = request.getParameter("page");
        int page = Integer.parseInt(spage);
        String uid = request.getParameter("uid");//获取当前用户
        List<BookInfo> books = bookService.getCollectionBook(uid, page); //返回分页后的集合对象

        //通过用户书籍的数量，来判断底部共有多少页
        int bookCount = bookMapper.getCollectionCount(uid);
        int count=0;//页面底部的页码数量
        for (int size = bookCount;size>0;){
            size-=6;
            count+=1;
        }
        //获取用户金额
        double userMoney = userMapper.getUserMoney(uid);
        model.addAttribute("myMoney",userMoney);
        model.addAttribute("page",count);
        Collections.reverse(books);
        model.addAttribute("books",books);
        return "self_info";
    }

    @PostMapping("/search")
    public String searchBook(String words,Model model){
        List<BookInfo> result = bookMapper.getSearchBook(words);
        if (result.size()==0){
            model.addAttribute("msg","未搜到相关信息");
            return "search";
        }
        model.addAttribute("books",result);
        return "search";

    }



}


