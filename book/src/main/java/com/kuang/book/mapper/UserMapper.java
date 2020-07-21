package com.kuang.book.mapper;

import com.kuang.book.entiy.BookUsers;
//import com.kuang.book.entiy.BookUsersOrder;
import com.kuang.book.entiy.BookUsersOrder;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
@Mapper
public interface UserMapper {
    BookUsers getUser(String phone);

    double getUserMoney(String uid);

    int updateUserMoney(HashMap map);

    int addUserPayBook(String uid,String bid,String tid);

    String getUserPayBook(String uid,String bid,String tid);

    int addUser(String phone,String password,String username);

    BookUsers getUserName(String uid);

    int addUserOrder(String order_user, String order_name, String order_number, String order_price);

    List<BookUsersOrder> getUserOrder(String uid);

    int updateOrderTotal(String order_number,String order_total);

    int deleteOrderTotal(String order);

    String getOrderUserID(String out_trade_no);

    List getUserTid(String uid,String bid);

}
