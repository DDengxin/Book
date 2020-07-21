package com.kuang.book.controller;

import com.kuang.book.entiy.BookContent;
import com.kuang.book.mapper.BookMapper;
import com.kuang.book.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PayController {
    @Autowired
    BookMapper bookMapper;
    @Autowired
    BookService bookService;

    @GetMapping("/pay/message")
    public String message(){
        return "message";
    }



    @GetMapping("/pay")
    public String bookPay(String tid, String uid, String bid, Model model){
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
    @ResponseBody
    @PostMapping("/pay/deal")
    public String payDeal(String uid,String bid,String tid){
        //用户未登录
        if(uid.equals("null")){
            return "login";
        }
        String result = bookService.payBook(uid, bid, tid);
        //支付成功
        if (result.equals("支付成功")){
            return "success";
        }
        //余额不足
        else if (result.equals("余额不足")){
            return "fail";
            //支付异常，事务回滚
        }else {
            return "error";
        }
    }


}
