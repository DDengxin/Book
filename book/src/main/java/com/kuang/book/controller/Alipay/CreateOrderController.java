package com.kuang.book.controller.Alipay;

import com.kuang.book.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Controller


public class CreateOrderController {
    @Autowired
    UserMapper userMapper;

    @ResponseBody
    @PostMapping("/alipay/createOrder")
    public String order(String order_name,String order_user,String order_price){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        String newDate=sdf.format(new Date());
        StringBuilder result= new StringBuilder();
        Random random=new Random();
        for(int i=0;i<3;i++){
            result.append(random.nextInt(10));
        }
        userMapper.addUserOrder(order_user,order_name.replace("1.",""),newDate+result,order_price.replace("元",""));
        return newDate + result;

    }

}
