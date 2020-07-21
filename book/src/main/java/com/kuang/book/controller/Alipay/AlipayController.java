package com.kuang.book.controller.Alipay;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.kuang.book.service.UserService;
import com.kuang.book.utils.AlipayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class AlipayController {

    @Autowired
    UserService userService;

    @RequestMapping("/test")
    public String test(){
        return "test";
    }
    @RequestMapping("/test2")
    public String test2(){
        return "500";
    }

    @RequestMapping("/test1")
    public String success(){
        return "success";
    }


    @PostMapping("/alipay/gopay")
    public void gopay(HttpServletRequest request, HttpServletResponse response)throws Exception {
        String str = request.getParameter("order_numbers");
        int i = str.lastIndexOf(",");
        String order_nums = str.substring(0, i);
        String [] strArr= order_nums.split(",");
        String order = userService.orderNumTotal(strArr);

        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
        request.setCharacterEncoding("UTF-8");
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = order;
        //付款金额，必填
        String total_amount = request.getParameter("WIDtotal_amount").replace("￥","");
        //订单名称，必填
        String subject = request.getParameter("WIDsubject");
        //商品描述，可空
        String body = request.getParameter("WIDbody");
        //用户唯一id号
        String uid = request.getParameter("uid");

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"uid\":\""+uid+"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //若想给BizContent增加其他可选请求参数，以增加自定义超时时间参数timeout_express来举例说明
        //alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
        //		+ "\"total_amount\":\""+ total_amount +"\","
        //		+ "\"subject\":\ ""+ subject +"\","
        //		+ "\"body\":\""+ body +"\","
        //		+ "\"timeout_express\":\"10m\","
        //		+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        //请求参数可查阅【电脑网站支付的API文档-alipay.trade.page.pay-请求参数】章节

        //请求
        String head = "<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'></head>";
        String result = alipayClient.pageExecute(alipayRequest).getBody();

        String buttom ="<body></body></html>";
        //输出
        response.setHeader("content-type","text/html; charset=utf-8");
        response.getWriter().println(head+result+buttom);
    }


}
