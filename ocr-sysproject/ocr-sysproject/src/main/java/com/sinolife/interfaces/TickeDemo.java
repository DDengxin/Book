package com.sinolife.interfaces;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface TickeDemo {

    //增值税发票识别方法
    JSONObject vatHttp(String base64string);
    //出租车发票识别方法
    JSONObject taxiHttp(String base64string);
    //客运车票识别方法
    JSONObject passengerHttp(String base64string);
    //车辆通行费识别方法
    JSONObject tollHttp(String base64string);
    //火车票识别方法
    JSONObject trainHttp(String base64string);
    //定额发票识别方法
    JSONObject quotaHttp(String base64string);
    //机动车销售发票识别方法
    JSONObject mvsHttp(String base64string);
    //飞机行程单识别方法
    JSONObject flightHttp(String base64string);
    //营业执照识别方法
    JSONObject licenseHttp(String base64string);
    //通用机打发票识别
    JSONObject printedHttp(String base64string);
    //船票识别
    JSONObject shipHttp(String base64string);
    //微信凭证识别
    JSONObject WeChatBills(String base64string);
    //支付宝凭证识别
    JSONObject AliPayBills(String base64string);
    //银行回单识别
    JSONObject bankErceipt(String base64string);
    //刷卡小票
    JSONObject brushCard(String base64string);
    //网银支付截图
    JSONObject internetBanking(String imageBase64);
}
