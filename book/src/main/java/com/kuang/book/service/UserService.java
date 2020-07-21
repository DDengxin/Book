package com.kuang.book.service;

import com.kuang.book.entiy.BookUsers;
import com.kuang.book.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;


public interface UserService {
    @Autowired


    BookUsers userLogin(String phone);

    String orderNumTotal(String[] orderArr);


}
