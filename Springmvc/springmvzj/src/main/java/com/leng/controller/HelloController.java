package com.leng.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {

    @RequestMapping("/demo")
    public String demo(Model model){
        model.addAttribute("msg","you are so good");
        return "demo";
    }

}
