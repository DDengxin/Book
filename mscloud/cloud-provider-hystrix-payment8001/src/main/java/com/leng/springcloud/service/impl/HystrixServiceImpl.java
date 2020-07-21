package com.leng.springcloud.service.impl;

import com.leng.springcloud.service.HystrixService;
import org.springframework.stereotype.Service;

@Service
public class HystrixServiceImpl implements HystrixService {
    @Override
    public String paymentInfoOK(Integer id) {
        return "线程池+"+Thread.currentThread().getName()+"OK"+id;
    }

    @Override
    public String paymentTimeOut(Integer id) {
        try {Thread.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
        return "线程池+"+Thread.currentThread().getName()+"TimeOut"+id;
    }
}
