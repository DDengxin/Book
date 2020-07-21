package com.kuang.book.entiy;

import java.io.Serializable;

public class BookUsersOrder{
    //订单号
    private String order_number;
    //价格
    private String order_price;
    //用户
    private String order_user;
    //商品名称
    private String order_name;

    public BookUsersOrder(String order_number, String order_price, String order_user, String order_name) {
        this.order_number = order_number;
        this.order_price = order_price;
        this.order_user = order_user;
        this.order_name = order_name;
    }

    public BookUsersOrder() {
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getOrder_price() {
        return order_price;
    }

    public void setOrder_price(String order_price) {
        this.order_price = order_price;
    }

    public String getOrder_user() {
        return order_user;
    }

    public void setOrder_user(String order_user) {
        this.order_user = order_user;
    }

    public String getOrder_name() {
        return order_name;
    }

    public void setOrder_name(String order_name) {
        this.order_name = order_name;
    }
}
