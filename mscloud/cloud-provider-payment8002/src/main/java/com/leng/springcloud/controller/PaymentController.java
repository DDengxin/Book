package com.leng.springcloud.controller;


import com.leng.springcloud.enties.CommonResult;
import com.leng.springcloud.enties.Payment;
import com.leng.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
public class PaymentController {
    @Value("${server.port}")
    private String port;

    @Resource
    private PaymentService paymentService;

    @PostMapping("/payment/create")
    public CommonResult create(@RequestBody Payment payment){
        int result = paymentService.create(payment);
        log.info("*******插入结果："+result);

        if (result>0){
            return new CommonResult(200,"插入数据成功"+port,result);

        }else {
            return new CommonResult(444,null);
        }
    }

    @GetMapping("/payment/get/{id}")
    public CommonResult getPaymentById(@PathVariable("id") Long id){
        Payment payment = paymentService.getPaymentById(id);
        log.info("*******插入结果："+payment);

        if (payment!=null){
            return new CommonResult(200,"查询成功"+port,payment);

        }else {
            return new CommonResult(444,"查询内容为空"+port,null);
        }
    }
    @GetMapping("/test")
    public String test(){
        return "sadasd";
    }


}
