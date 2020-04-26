package com.kuang.book.controller;


import com.kuang.book.entiy.BookContent;
import com.kuang.book.entiy.BookInfo;
import com.kuang.book.mapper.BookMapper;
import com.kuang.book.service.BookService;
import com.kuang.book.utils.RedisFindUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
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

    @GetMapping("/index")
    public String index(Model model) {
        List<BookInfo> allBook = RedisFindUtil.getAllBook();
        //分类排行
        List<BookInfo> xuanhuan = RedisFindUtil.getDescTypeBook("玄幻","xh");
        List<BookInfo> wuxia = RedisFindUtil.getDescTypeBook("武侠","wx");
        List<BookInfo> lishi = RedisFindUtil.getDescTypeBook("历史","ls");
        List<BookInfo> xiuxian = RedisFindUtil.getDescTypeBook("修仙","xx");
        List<BookInfo> dushi = RedisFindUtil.getDescTypeBook("都市","ds");
        model.addAttribute("xuanhuan", xuanhuan);
        model.addAttribute("wuxia", wuxia);
        model.addAttribute("lishi", lishi);
        model.addAttribute("xiuxian", xiuxian);
        model.addAttribute("dushi", dushi);
        model.addAttribute("books", allBook);
        return "index";
    }

    @GetMapping("/read")
    public String read(HttpServletRequest request, Model model) {
        int id = Integer.parseInt(request.getParameter("id"));
        String bookName = request.getParameter("bookName");
        BookInfo currenBook = bookMapper.getBookId(id); //获取info对象
        List<BookContent> bookTitleItem = bookMapper.getTitle(bookName); //获取所有content对象
        HashMap viewCount = bookService.getViewCount(id);
        model.addAttribute("viewCount",viewCount);//?
        model.addAttribute("bookInfo", currenBook);
        model.addAttribute("bookContentItem", bookTitleItem);
        return "read";
    }

    @GetMapping("/content")
    public String content(HttpServletRequest request, Model model) {
        String bookName = request.getParameter("bookName");
        String page = request.getParameter("page");
        String id = request.getParameter("id");
        int tid = Integer.parseInt(id);
        //翻页
        if (page !=null){
        BookContent currentBook = bookService.getPage(tid, bookName, page);
        model.addAttribute("currentBook",currentBook);
        return "content";
        }

        BookContent currentBook = bookMapper.getContent(id, bookName);
        model.addAttribute("currentBook",currentBook);
        return "content";


    }

    }


