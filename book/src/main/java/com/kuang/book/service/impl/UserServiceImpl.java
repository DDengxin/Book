package com.kuang.book.service.impl;

import com.kuang.book.entiy.BookUsers;
import com.kuang.book.mapper.UserMapper;
import com.kuang.book.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;


@Repository
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public BookUsers userLogin(String phone) {
        return userMapper.getUser(phone);

    }

    @Override
    public String orderNumTotal(String[] orderArr) {
        if (orderArr.length==1){
            return orderArr[0];
        }else {
            //重新生成合成统一订单号
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
            String newDate=sdf.format(new Date());
            StringBuilder result= new StringBuilder();
            Random random=new Random();
            for(int i=0;i<3;i++){
                result.append(random.nextInt(10));
            }
            for (int j=0;j<orderArr.length;j++){
                userMapper.updateOrderTotal (orderArr[j],result+newDate);
            }
            return result+newDate;
        }

    }


}
