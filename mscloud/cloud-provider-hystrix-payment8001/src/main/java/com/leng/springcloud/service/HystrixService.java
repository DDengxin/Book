package com.leng.springcloud.service;

public interface HystrixService {
    String paymentInfoOK(Integer id);
    String paymentTimeOut(Integer id);
}
