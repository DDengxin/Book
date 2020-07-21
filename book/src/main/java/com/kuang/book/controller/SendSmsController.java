package com.kuang.book.controller;
import com.kuang.book.entiy.BookUsers;
import com.kuang.book.mapper.UserMapper;
import com.kuang.book.service.SendService;
import com.kuang.book.service.UserService;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@CrossOrigin
public class SendSmsController {
    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;
    @Autowired
    private SendService sendService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @ResponseBody
    @PostMapping(value = "/send")
    public String code(String phone) {

//        String code = redisTemplate.opsForValue().get(phone);
//        if (!StringUtils.isEmpty(code)){
//            return "验证码未过期";
//        }
        String code = UUID.randomUUID().toString().substring(0, 4);
        HashMap<String,Object> map = new HashMap<>();
        map.put("code",code);
        boolean isSend = sendService.send(phone,"SMS_189762050",map);
        if (isSend){
            redisTemplate.opsForValue().set(phone,code,30, TimeUnit.MINUTES);
            return "发送成功";

        }else {
            return "发送失败";
        }
    }
    @PostMapping("/phone/verification")
    public String phoneVerification(String inputCode,String phone){
        try {
        if (userMapper.getUser(phone)!=null){
            return "exist";
        }
        String code = redisTemplate.opsForValue().get(phone);
        if (inputCode.equals(code)){
            return "success";
        }
            return "false";
        }catch (Exception e){
            return "error";
        }

    }





    }

