package com.kuang.book.controller;


import com.kuang.book.entiy.BookContent;
import com.kuang.book.entiy.BookInfo;
import com.kuang.book.mapper.BookMapper;
import com.kuang.book.mapper.UserMapper;
import com.kuang.book.service.BookService;
import com.sun.deploy.net.URLEncoder;
import org.apache.shiro.web.util.RedirectView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
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

    @GetMapping("/read")
    public String read(HttpServletRequest request, Model model) {
        String bid = request.getParameter("id");
        String uid = request.getParameter("uid");
        if (!(uid.equals("null"))){
            String userBook = bookMapper.userGetBook(uid, bid);

            if (userBook==null){
                model.addAttribute("confirm","收藏书籍");
                model.addAttribute("msg","add");
            }
            else {
                model.addAttribute("cancel","取消收藏");
                model.addAttribute("msg","del");
            }
        }else {
            model.addAttribute("confirm","收藏书籍");
            model.addAttribute("msg","add");
        }

        Integer viewCount = bookService.getViewCount(bid);//每次访问浏览量+1
        BookInfo currenBook = bookMapper.getBookId(bid); //获取用户当前点击的书籍
        List<BookContent> bookTitleItem = bookMapper.getTitle(bid); //遍历当前页面所有的标题
        model.addAttribute("viewCount",viewCount);
        model.addAttribute("bookInfo", currenBook);
        model.addAttribute("bookContentItem", bookTitleItem);
        return "read";
    }

    @PostMapping("/read/add")
    public String readAdd(@RequestParam String uid, @RequestParam String bid){
        if (uid.equals("")){
            return "/login";
        }
        bookMapper.userAddBook(uid, bid);

        return "redirect:/read?id="+bid+"&uid="+uid;
    }

    @PostMapping("/read/del")
    public String readDel(@RequestParam String uid, @RequestParam String bid){
        bookMapper.userDelBook(uid, bid);
        return "redirect:/read?id="+bid+"&uid="+uid;
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
        else{
            return "redirect:/pay?tid="+tid+"&uid="+uid+"&bid="+bid;
        }
    }

    @GetMapping("/pay")
    public String bookPay(String tid,String uid,String bid,Model model){
        BookContent book = bookMapper.getContent(tid, bid);
        double bookPrice = book.getPrice();
        String bname = book.getBname();
        String title = book.getTitle();
        model.addAttribute("tid",tid);
        model.addAttribute("uid",uid);
        model.addAttribute("bid",bid);
        model.addAttribute("price",bookPrice);
        model.addAttribute("bname",bname);
        model.addAttribute("title",title.replace(" ",""));

        return "payBook";
    }

    @PostMapping("/pay/deal")
    public String payDeal(String uid,String bid,String tid,Model model){
        //用户未登录
        if(uid.equals("null")){
            return "login";
        }
        String result = bookService.payBook(uid, bid, tid);
        //支付成功
        if (result.equals("支付成功")){
            return "redirect:/content?tid="+tid+"&id="+bid+"&uid="+uid;
        }
        //余额不足
        else if (result.equals("余额不足")){
            double userMoney = userMapper.getUserMoney(uid);
            model.addAttribute("userMoney",userMoney);
            return "message";
         //支付异常，事务回滚
        }else {
            return null;
    }
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
        model.addAttribute("page",count);
        model.addAttribute("books",books);

        return "self_info";
    }
}


