package com.kuang.book.controller;


import com.kuang.book.entiy.BookContent;
import com.kuang.book.entiy.BookInfo;
import com.kuang.book.mapper.BookMapper;
import com.kuang.book.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BookController {
    @Autowired
    BookMapper bookMapper;
    @Autowired
    BookService bookService;
    @Autowired
    @Qualifier("redisTemplates")
    RedisTemplate redisTemplate;


    @GetMapping("/index")
    public String index(Model model) {
        HashMap<String, List<BookInfo>> descTypeBook;
        List<BookInfo> AllBook = bookMapper.getAllBook();

        if (redisTemplate.opsForHash().entries("descBook").get(0)!=null){
            descTypeBook = (HashMap<String, List<BookInfo>>) redisTemplate.opsForHash().values("descBook");
        }else {
            descTypeBook = bookService.getDescAllType();
        }

        if (redisTemplate.opsForValue().get("AllBook")==null){
            AllBook = bookMapper.getAllBook();
            redisTemplate.opsForValue().set("AllBook",AllBook);
        }
        model.addAttribute("xuanhuan", descTypeBook.get("玄幻"));
        model.addAttribute("wuxia", descTypeBook.get("武侠"));
        model.addAttribute("lishi", descTypeBook.get("历史"));
        model.addAttribute("xiuxian",descTypeBook.get("修仙"));
        model.addAttribute("dushi", descTypeBook.get("都市"));
        model.addAttribute("books", AllBook);
        return "index";
    }

    @GetMapping("/read")
    public String read(HttpServletRequest request, Model model) {
        int id = Integer.parseInt(request.getParameter("id"));
        String bookName = request.getParameter("bookName");
        //如果用户点击了收藏按钮
        if (request.getParameter("uid")!=null){
            if (request.getParameter("uid").equals("null")){
                return "login";
            }
            Boolean aBoolean = bookService.addCollectionBook(request.getParameter("uid"), request.getParameter("id"));
            if (aBoolean==true){
                model.addAttribute("msg","添加成功");
            }else {
                model.addAttribute("msg","该书籍已存在");
            }
        }

        BookInfo currenBook = bookMapper.getBookId(id); //获取当前用户点击的书籍
        List<BookContent> bookTitleItem = bookMapper.getTitle(bookName); //遍历当前页面所有的标题
        Integer viewCount = bookService.getViewCount(id);//每次访问浏览量+1
        model.addAttribute("viewCount",viewCount);
        model.addAttribute("bookInfo", currenBook);
        model.addAttribute("bookContentItem", bookTitleItem);
        return "read";
    }

    @GetMapping("/content")
    public String content(HttpServletRequest request, Model model) {
        String bookName = request.getParameter("bookName");
        String id = request.getParameter("id");
        BookContent currentBook = bookMapper.getContent(id, bookName);
        //翻页
        if (currentBook==null){
            BookContent allbook = bookMapper.getContent("1", bookName);
            model.addAttribute("currentBook",allbook);
            return "content";
        }
        model.addAttribute("currentBook",currentBook);
        return "content";


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


