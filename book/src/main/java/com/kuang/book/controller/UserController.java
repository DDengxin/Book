package com.kuang.book.controller;

import com.kuang.book.entiy.BookUsers;
import com.kuang.book.mapper.BookMapper;
import com.kuang.book.mapper.UserMapper;
import com.kuang.book.service.BookService;
import com.kuang.book.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;


@Controller
public class UserController {



    @Autowired
    UserMapper userMapper;

    @Autowired
    UserService userService;

    @Autowired
    BookMapper bookMapper;


    @GetMapping("/test")
    public String test(){
        return "test";
    }


    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "login";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }



    @GetMapping("/unauth")
    @ResponseBody
    public String xxx(){
        return "12334";
    }


    @PostMapping("/userSign")
    public String userSign(@RequestParam String email,
                        @RequestParam String password,
                        Model model,
                        HttpSession session) {
        //获取当前用户
        Subject subject = SecurityUtils.getSubject();
        //封装用户的登录数据
        UsernamePasswordToken token = new UsernamePasswordToken(email,password);
        try {
            //执行登录方法，会调用doGetAuthenticationInfo认证方法,如果不存在异常就说明登录成功了
            subject.login(token);
//            BookUsers user = userMapper.getUser(username);
            BookUsers user = userService.userLogin(email);
            session.setAttribute("uid",user.getId());
            session.setAttribute("username",user.getUsername());
            session.setAttribute("email",user.getEmail());
            if (user.getAuth()==1){
                session.setAttribute("auth","超级用户");
            }else {
                session.setAttribute("auth","普通用户");
            }
            return "redirect:/index";
        }catch (UnknownAccountException e){ //UnknownAccountException异常表示用户不存在
            model.addAttribute("msg","用户名不存在");
            return "login";
        }catch (IncorrectCredentialsException e){
            model.addAttribute("msg","密码错误");//IncorrectCredentialsException表示密码不存在
            return "login";
        }
    }

    @PostMapping("/userRegister")
    public String userRegister(@RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String username,
                                Model model) {
        HashMap<String,String> map = userService.userRegister(email, password, username);
        String msg = map.get("msg");

        if (msg.equals("邮箱已被注册")){
            model.addAttribute("msg",msg);
            return "register";
        }
        else if (msg.equals("用户名已存在")){
            model.addAttribute("msg",msg);
            return "register";
        }
        else if (msg.equals("注册成功")){
            model.addAttribute("msg",msg);
            return "register";

        }
        else {
            return "服务器内部错误";
        }

        }
    }

