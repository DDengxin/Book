package com.kuang.book.mapper;

import com.kuang.book.entiy.BookUsers;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserMapper {
    BookUsers getUser(String username);
    int addUser();
}
