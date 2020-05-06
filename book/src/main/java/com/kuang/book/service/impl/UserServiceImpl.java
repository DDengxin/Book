package com.kuang.book.service.impl;

import com.kuang.book.entiy.BookUsers;
import com.kuang.book.mapper.UserMapper;
import com.kuang.book.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpSession;
import java.util.HashMap;


@Repository
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public BookUsers userLogin(String email) {
        return userMapper.getUser(email);

    }

    @Override
    public HashMap<String,String> userRegister(String email, String password, String username) {

        HashMap map = new HashMap();
        BookUsers user = userMapper.getUser(email);


        if (user!=null){
            map.put("msg","邮箱已被注册");
            return map;
        }
        else if (user.getUsername().equals(username)){
            map.put("msg","用户名已存在");
            return map;
        }
        else{
            userMapper.addUser(email,password,username);
            map.put("msg","注册成功");
            return map;
        }
    }
}
