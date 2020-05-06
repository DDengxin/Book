package com.kuang.book.mapper;

import com.kuang.book.entiy.BookUsers;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
@Mapper
public interface UserMapper {
    BookUsers getUser(String email);

    double getUserMoney(String uid);
    int updateUserMoney(HashMap map);

    int addUserPayBook(String uid,String bid,String tid);

    String getUserPayBook(String uid,String bid,String tid);

    int addUser(String email,String password,String username);




}
