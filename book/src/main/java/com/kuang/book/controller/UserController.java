package com.kuang.book.controller;

import com.kuang.book.entiy.BookUsers;
import com.kuang.book.entiy.BookUsersOrder;
import com.kuang.book.mapper.BookMapper;
import com.kuang.book.mapper.UserMapper;
import com.kuang.book.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;


@Controller
public class UserController {



    @Autowired
    UserMapper userMapper;

    @Autowired
    UserService userService;

    @Autowired
    BookMapper bookMapper;


    @Autowired
    RedisTemplate redisTemplate;

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
        try{
        session.invalidate();}
        catch (IllegalStateException e){
            return "/login";
        }
        return "/login";
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

    @ResponseBody
    @PostMapping("/user/add")
    public String userAdd(String password,String phone,String username) {
        try {
            userMapper.addUser(phone, password, username);
            return "success";
        } catch (DuplicateKeyException e){
            return "exist";
        }catch (DataIntegrityViolationException e){
            return "exist";
        }

        catch (Exception e){
            return "error";
        }
    }

    @ResponseBody
    @PostMapping("/user/login")
    public String userSign(@RequestParam String phone,
                        @RequestParam String password,
                        HttpSession session) {
        //获取当前用户
        Subject subject = SecurityUtils.getSubject();
        //封装用户的登录数据
        UsernamePasswordToken token = new UsernamePasswordToken(phone,password);
        try {
            //执行登录方法，会调用doGetAuthenticationInfo认证方法,如果不存在异常就说明登录成功了
            subject.login(token);

            BookUsers user = userService.userLogin(phone);
            session.setAttribute("uid",user.getId());
            session.setAttribute("username",user.getUsername());
            session.setAttribute("money",user.getMoney());
            if (user.getAuth()==1){
                session.setAttribute("auth","超级用户");
            }else {
                session.setAttribute("auth","普通用户");
            }
            return "success";
        }catch (UnknownAccountException e){ //UnknownAccountException异常表示用户不存在
            return "手机号不存在";
        }catch (IncorrectCredentialsException e){
            //IncorrectCredentialsException表示密码不存在
            return "密码错误";
        }
    }

    @GetMapping("/pay/orderInfo")
    public String orderSelfInfo(HttpServletRequest request,Model model){
        String uid = request.getParameter("uid");
        List<BookUsersOrder> user = userMapper.getUserOrder(uid);
        Collections.reverse(user);
        model.addAttribute("Goods",user);
        return "order";
    }
    @ResponseBody
    @PostMapping("/pay/order/del")
    public String order_del(String order){
        try {
            userMapper.deleteOrderTotal(order);
            return "success";
        }catch (Exception e){
            return "error";
        }



    }



}

