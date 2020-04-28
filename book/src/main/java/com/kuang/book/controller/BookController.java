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
        BookInfo currenBook = bookMapper.getBookId(id); //获取info对象
        List<BookContent> bookTitleItem = bookMapper.getTitle(bookName); //获取所有content对象
        Integer viewCount = bookService.getViewCount(id);
        model.addAttribute("viewCount",viewCount);//?
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
        int page =0;
        List<BookInfo> scoreBook = bookMapper.getScoreBook(type);
        for (int size=scoreBook.size();size>0;){
            size-=4;
            page+=1;
        }

        model.addAttribute("scoreBook",scoreBook);
        model.addAttribute("page",page);
        return "sort_book";
    }
}


