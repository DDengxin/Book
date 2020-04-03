package com.sinolife.interfaces.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sinolife.interfaces.TickeDemo;
import com.sinolife.properties.*;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class TickeDemoImpl implements TickeDemo {

    @Autowired
    private VatProperties vatProperties;

    @Autowired
    private TaxiProperties taxiProperties;

    @Autowired
    private TrainProperties trainProperties;

    @Autowired
    private TollProperties tollProperties;

    @Autowired
    private LicenseProperties licenseProperties;

    @Autowired
    private FlightProperties flightProperties;

    @Autowired
    private QuotaProperties quotaProperties;

    @Autowired
    private MvsProperties mvsProperties;

    @Autowired
    private ImageProperties imageProperties;

    @Autowired
    private CflProperties cflProperties;

    @Autowired
    private TableProperties tableProperties;

    private static final MediaType TYPE = MediaType.parse("application/json; charset=utf-8");

    private Logger logger = LoggerFactory.getLogger(TickeDemoImpl.class);


    /**
     * 网银支付截图
     * @param imageBase64
     * @return
     */
    public  JSONObject internetBanking(String imageBase64) {
        JSONObject jsonObject = JSONObject.parseObject(textHttp(imageBase64));
        JSONObject result = jsonObject.getJSONObject("result");
        JSONObject job = new JSONObject(true);
        JSONArray words_block_list = result.getJSONArray("words_block_list");

        Map<String, String> map= new HashMap<String, String>();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < words_block_list.size(); i++) {
            JSONObject object = words_block_list.getJSONObject(i);
            //金额
            if (object.getString("words_result").contains("￥") && object.getString("words_result").contains("-"))
                map.put("price", strFindNum(object.getString("words_result")));
            //交易时间
            try {
                Date parse = simpleDateFormat.parse(object.getString("words_result"));
                map.put("date", parse.toLocaleString());
            } catch (ParseException e) {
            }
            //收款方
            if (object.getString("words_result").contains("收款方"))
                map.put("payee", words_block_list.getJSONObject(i+1).getString("words_result"));
        }
        map.put("type", "Internet_banking");
        String jsonString = JSONObject.toJSONString(map);
        if (jsonString!= null ){
            job = JSONObject.parseObject(jsonString);
        }
        return job;
    }

    /**
     * 刷卡小票(brush_card_ticket)
     * @param base64string
     * @return
     */
    public  JSONObject brushCard(String base64string){
        JSONObject jsonObject = new JSONObject(true);
        String http = textHttp(base64string);
        JSONObject parseObject = JSONObject.parseObject(http);
        JSONObject result = parseObject.getJSONObject("result");
        JSONArray words_block_list = result.getJSONArray("words_block_list");
        jsonObject.put("type","brush_card_ticket");
        for (int i = 0; i < words_block_list.size(); i++) {
            String words = words_block_list.getJSONObject(i).getString("words_result");
            String all = words.replace(" ", "");
            if (words.contains("名称")) {
                String replace = all.replace("：", "");
                if (jsonObject.getString("merchant_name") == null) {
                    if (replace.endsWith("称")) {
                        jsonObject.put("merchant_name", words_block_list.getJSONObject(i + 1).getString("words_result"));
                    } else {
                        int indexOf = replace.indexOf("称");
                        jsonObject.put("merchant_name", replace.substring(indexOf+1));
                    }
                }
            }
            if (words.contains("终端")){

                if (words.endsWith("：")){
                    jsonObject.put("terminal_numb", words_block_list.getJSONObject(i + 1).getString("words_result"));

                }else{
                    int indexOf = words.indexOf("：");
                    String substring = words.substring(indexOf, words.length());
                    String temStr =  substring.replace("：","").replace(" ","");
                    String word = "";
                    for(int ii=0;ii<temStr.length();ii++){

                        if((temStr.charAt(ii)>=48 && temStr.charAt(ii)<=57 ) || (temStr.charAt(ii)>=65 && temStr.charAt(ii)<=90)){
                            word+=temStr.charAt(ii);
                        }else {
                            break;
                        }
                    }
                    jsonObject.put("terminal_numb",word);
//                    if (words.length() > (indexOf+8))
//                    jsonObject.put("terminal_numb",words.substring(indexOf,(indexOf+9)).replace("：",""));
                }
            }

            if ((words.contains("2019") || words.contains("2020") || words.contains("2021") || words.contains("2022")) & words.length()>9 & !(words.contains("班次号"))& !(words.contains("单号"))) {
                logger.warn(words);
                System.out.println(words.contains("单号"));
                if (jsonObject.getString("date") == null) {
                    int indexOf = all.indexOf("2");
                    if (all.length()>(indexOf+9)) {
                        jsonObject.put("date", all.substring(indexOf, (indexOf + 10)).replace("/", "-"));
                    }
                }
            }
            if ((words.contains(".0") || words.contains(".5")) && !(words.contains("201") || words.contains("202") ||words.contains("先证号") ||words.contains("批次号"))) {
                if (jsonObject.getString("amount") == null) {
                    String replace = all.replace("元", "");
                    String replaceAll = replace.replaceAll("[\u2E80-\u9FA5]", "");
                    String a = replaceAll.replace("￥", "");
                    String b = a.replace(":", "");
                    String c = b.replace("：", "");
                    String amount = c.replaceAll("[a-zA-Z]", "").replace("（）","");
                    jsonObject.put("amount", amount);
                }
            }

        }
        return jsonObject;
    }


    /**
     * 银行回单
     * @param base64string
     * @return
     */
    public  JSONObject bankErceipt(String base64string) {
        JSONObject job = new JSONObject(true);
        JSONObject jsonObject = JSONObject.parseObject(textHttp(base64string));
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray words_block_list = result.getJSONArray("words_block_list");
        Map<String, String> map= new HashMap<String, String>();;

        while (map.isEmpty()) {
            for (int i = 0; i < words_block_list.size(); i++) {
                JSONObject object = words_block_list.getJSONObject(i);
                String words_result = object.getString("words_result");
                if (words_result.contains("中国工商银行")) {
                    map=gsBank(words_block_list);
                    break;
                }
                if (words_result.contains("江苏银行")) {
                    map=jsBank(words_block_list);
                    break;
                }
                if (words_result.contains("招商银行")) {
                    map=zsBank(words_block_list);
                    break;
                }
                if (words_result.contains("中国银行")) {
                    map=zgBank(words_block_list);
                    break;
                }
                if (words_result.contains("中国农业银行")) {
                    map=nyBank(base64string);
                    break;
                }
                if (words_result.contains("中国邮政储蓄")) {
                    map=yzBank(base64string);
                    break;
                }
            }
        }
        map.put("type", "bank_erceipt");
        String jsonString = JSONObject.toJSONString(map);
        if (jsonString!= null ){
             job = JSONObject.parseObject(jsonString);
        }
        return job;
    }


    /**
     * 工商银行
     */
    private  Map<String, String> gsBank(JSONArray words_block_list){
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < words_block_list.size(); i++) {
            JSONObject object = words_block_list.getJSONObject(i);
            //账号
            if (object.getString("words_result").contains("付款人账号")) {
                int index=object.getString("words_result").lastIndexOf("：");
                if (index!=-1)
                    map.put("amount", object.getString("words_result").substring(index+1));
            }
            //金额
            if (object.getString("words_result").contains("小写")) {
                int index=object.getString("words_result").lastIndexOf("：");
                if (index!=-1)
                    map.put("price", object.getString("words_result").substring(index+1).replace("元", ""));
            }
        }
        return map;
    }

    /**
     * 江苏银行
     */
    private  Map<String, String> jsBank(JSONArray words_block_list){
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < words_block_list.size(); i++) {
            JSONObject object = words_block_list.getJSONObject(i);
            //账号
            if (object.getString("words_result").contains("付款人账号"))
                map.put("amount", words_block_list.getJSONObject(i+1).getString("words_result"));

            //金额
            if (object.getString("words_result").contains("小写")) {
                int index=object.getString("words_result").lastIndexOf("：");
                if (index!=-1) {
                    map.put("price", object.getString("words_result").substring(index+1).replace("元", ""));
                }
            }
        }
        return map;
    }

    /**
     * 招商银行
     */
    private  Map<String, String> zsBank(JSONArray words_block_list){
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < words_block_list.size(); i++) {
            JSONObject object = words_block_list.getJSONObject(i);
            //账号
            if (object.getString("words_result").contains("付款账号")) {
                int index=object.getString("words_result").lastIndexOf("：");
                if (index!=-1)
                    map.put("amount", object.getString("words_result").substring(index+1));
            }
            //金额
            if (object.getString("words_result").contains("金额")) {
                int index=object.getString("words_result").lastIndexOf("CNY");
                if (index!=-1)
                    map.put("price", object.getString("words_result").substring(index+3).replace(",", ""));
            }
        }
        return map;
    }

    /**
     * 中国银行
     */
    private  Map<String, String> zgBank(JSONArray words_block_list){
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < words_block_list.size(); i++) {
            JSONObject object = words_block_list.getJSONObject(i);
            //账号
            if (object.getString("words_result").contains("付款人账号")) {
                int index=object.getString("words_result").lastIndexOf("：");
                if (index!=-1)
                    map.put("amount", object.getString("words_result").substring(index+1));
            }
            //金额
            if (object.getString("words_result").contains("金额")) {
                int index=object.getString("words_result").lastIndexOf("CNY");
                if (index!=-1)
                    map.put("price", object.getString("words_result").substring(index+3).replace(",", ""));
            }
        }
        return map;
    }

    /**
     * 农业银行
     */
    private  Map<String, String> nyBank(String base) {
        Map<String, String> map = new HashMap<String, String>();
        JSONObject parseObject = JSONObject.parseObject(tableHttp(base));
        JSONObject result = parseObject.getJSONObject("result");
        JSONArray words_region_list = result.getJSONArray("words_region_list");
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject jsonObject = words_region_list.getJSONObject(i);
            if (jsonObject.getString("type").equals("table")) {
                JSONArray words_block_list = jsonObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    if (object.getString("words").equals("付款方")) {
                        map.put("amount", words_block_list.getJSONObject(j+2).getString("words"));
                    }
                    if (object.getString("words").contains("大写")) {
                        map.put("price", words_block_list.getJSONObject(j+2).getString("words"));
                    }
                }
            }
        }
        return map;
    }

    /**
     * 邮政储蓄
     */
    private  Map<String, String> yzBank(String base) {
        Map<String, String> map = new HashMap<String, String>();
        JSONObject parseObject = JSONObject.parseObject(tableHttp(base));
        JSONObject result = parseObject.getJSONObject("result");
        JSONArray words_region_list = result.getJSONArray("words_region_list");
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject jsonObject = words_region_list.getJSONObject(i);
            if (jsonObject.getString("type").equals("table")) {
                JSONArray words_block_list = jsonObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    String rows = object.getString("rows");
                    String columns = object.getString("columns");
                    if (rows.equals("[3,4,5]") && columns.equals("[2,3,4]")) {
                        String replaceAll = object.getString("words").replaceAll("[\u2E80-\u9FA5]", "");
                        map.put("amount", replaceAll);
                    }
                    if (rows.equals("[7,8]") && columns.equals("[2,3,4,5,6,7,8,9]")) {
                        int index = object.getString("words").indexOf("：");
                        if (index!=-1) {
                            int end = object.getString("words").indexOf("大");
                            if (end!=-1) {
                                map.put("price", object.getString("words").substring(index+1, end));
                            }
                        }
                    }
                }
            }
        }
        return map;
    }


    /**
     * 支付宝账单
     */
    public  JSONObject AliPayBills(String base64string) {
        JSONObject jsonObject = JSONObject.parseObject(textHttp(base64string));

        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray words_block_list = result.getJSONArray("words_block_list");

        Map<String, String> map = new HashMap<String, String>();
        JSONObject job = new JSONObject(true);
        job.put("type","ali_pay_bills");

        for (int i = 0; i < words_block_list.size(); i++) {
            JSONObject object = words_block_list.getJSONObject(i);
            //金额
            if (object.getString("words_result").startsWith("-") && object.getString("words_result").contains(".")) {
                map.put("price",object.getString("words_result").replace("-", "").replace(",", ""));
                //商户简称
                if (!"".equals(object.getString("words_result"))) {
                    map.put("product_short_name", words_block_list.getJSONObject(i-1).getString("words_result"));
                }
            }
            //订单号
            if (object.getString("words_result").equals("订单号")) {
                map.put("order_number",words_block_list.getJSONObject(i+1).getString("words_result"));
            }
            //商品说明
            if (object.getString("words_result").equals("商品说明")) {
                if (!words_block_list.getJSONObject(i+2).getString("words_result").equals("查看订单详情 >")
                        &&!words_block_list.getJSONObject(i+2).getString("words_result").equals("收货地址")
                        &&!words_block_list.getJSONObject(i+2).getString("words_result").equals("创建时间")
                        &&!words_block_list.getJSONObject(i+2).getString("words_result").contains("查看购物详情")) {
                    //有多行
                    String product_desc=words_block_list.getJSONObject(i+1).getString("words_result")+words_block_list.getJSONObject(i+2).getString("words_result");
                    map.put("product_desc", product_desc);
                }else {
                    map.put("product_desc", words_block_list.getJSONObject(i+1).getString("words_result"));
                }
            }
        }

        //商品说明2
        if (!map.containsKey("product_desc")) {
            for (int i = 0; i < words_block_list.size(); i++) {
                JSONObject object = words_block_list.getJSONObject(i);
                if (object.getString("words_result").contains("商品说明")) {
                    int start = object.getString("words_result").indexOf("说明")+2;
                    map.put("product_desc", object.getString("words_result").substring(start));
                }
            }
        }
        String jsonString = JSONObject.toJSONString(map);
        if (jsonString!=null){
            job = JSONObject.parseObject(jsonString);
        }
        return job;
    }


    /**
     * 微信账单
     */
    public  JSONObject WeChatBills(String base64string) {
        JSONObject jsonObject = JSONObject.parseObject(textHttp(base64string));
        JSONObject job = new JSONObject(true);
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray words_block_list = result.getJSONArray("words_block_list");

        Map<String, String> map = new HashMap<String, String>();
        job.put("type","we_chat_bills");
        Boolean falg=false;
        for (int i = 0; i < words_block_list.size(); i++) {
            JSONObject object = words_block_list.getJSONObject(i);
            // 商品
            if (object.getString("words_result").equals("商品")) {
                map.put("product", words_block_list.getJSONObject(i + 1).getString("words_result"));
            }
            // 商户全称
            if (object.getString("words_result").equals("商户全称")) {
                if (!words_block_list.getJSONObject(i + 2).getString("words_result").equals("支付时间")) {
                    String product_fullname="";
                    product_fullname=words_block_list.getJSONObject(i + 1).getString("words_result");
                    product_fullname+=words_block_list.getJSONObject(i + 2).getString("words_result");
                    map.put("product_fullname", product_fullname);
                }else {
                    map.put("product_fullname", words_block_list.getJSONObject(i + 1).getString("words_result"));
                }
            }
            // 交易单号
            if (object.getString("words_result").equals("交易单号")) {
                if (!words_block_list.getJSONObject(i + 2).getString("words_result").equals("商户单号")) {
                    String number="";
                    number=words_block_list.getJSONObject(i + 1).getString("words_result");
                    number+=words_block_list.getJSONObject(i + 2).getString("words_result");
                    map.put("number", number);
                }else {
                    map.put("number", words_block_list.getJSONObject(i + 1).getString("words_result"));
                }

            }
            // 金额
            if (object.getString("words_result").startsWith("-") && object.getString("words_result").contains(".")) {
                map.put("price", object.getString("words_result").replace("-", ""));
                try {
                    if (words_block_list.getJSONObject(i - 2).getString("words_result")!=null) {
                        if (words_block_list.getJSONObject(i - 2).getString("words_result").length()>20) {
                            //有上一行
                            String product_short_name="";
                            product_short_name=words_block_list.getJSONObject(i - 2).getString("words_result");
                            product_short_name+=words_block_list.getJSONObject(i - 1).getString("words_result");
                            map.put("product_short_name", product_short_name);
                        }else {
                            //其余。。
                            map.put("product_short_name", words_block_list.getJSONObject(i - 1).getString("words_result"));
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    //没有上一行
                    map.put("product_short_name", words_block_list.getJSONObject(i - 1).getString("words_result"));
                }catch (Exception e){

                }
            }
            //找商户单号
            if (object.getString("words_result").contains("商户单号")) {
                falg=true;
            }
        }

        //商户单号
        if (falg) {
            for (int i = 0; i < words_block_list.size(); i++) {
                JSONObject object = words_block_list.getJSONObject(i);
                String reg = "^[A-Za-z0-9]+$";
                if (object.getString("words_result").matches(reg)) {
                    if (map.get("number")!=object.getString("words_result")) {
                        map.put("no", object.getString("words_result"));
                    }

                }
            }
        }
        String jsonString = JSONObject.toJSONString(map);
        if (jsonString!=null){
            job = JSONObject.parseObject(jsonString);
        }
        return job;
    }

    /**
     * 船票识别
     * @param base64string
     * @return
     */
    public JSONObject shipHttp(String base64string){
        JSONObject jsonObject = new JSONObject(true);
        String http = textHttp(base64string);
        JSONObject parseObject = JSONObject.parseObject(http);
        JSONObject result = parseObject.getJSONObject("result");
        JSONArray words_block_list = result.getJSONArray("words_block_list");
        jsonObject.put("type","ship_invoice");
        jsonObject.put("departure_station","");
        jsonObject.put("destination_station","");
        jsonObject.put("name","");
        for (int i = 0; i < words_block_list.size(); i++) {
            String words = words_block_list.getJSONObject(i).getString("words_result");
            String all = words.replace(" ", "");
//            String numb = all.replaceAll("[\u2E80-\u9FA5]", "");
//            if (numb.length() == 12){
//                jsonObject.put("code", numb);
//            }
//            if (numb.length() == 8){
//                jsonObject.put("number", numb);
//            }
            if (words.contains("发票代码") || words.contains("代码")){
                if (jsonObject.getString("code") == null) {
                    if (words.length() > 15) {
                        int indexOf = words.indexOf("码");
                        jsonObject.put("code", all.substring((indexOf + 1), (indexOf + 13)));
                    } else {
                        jsonObject.put("code", words_block_list.getJSONObject(i + 1).getString("words"));
                    }
                }
            }
            if (words.contains("发票号码") || words.contains("号码")){
                if (words.length()>6){
                    int indexOf = words.indexOf("码");
                    if (jsonObject.getString("number") == null) {
                        jsonObject.put("number", all.substring((indexOf + 1), (indexOf + 9)));
                    }
                }
            }
            if ((words.contains("2019") || words.contains("2020") || words.contains("2021") || words.contains("2022")) && words.length()>9) {
                if (jsonObject.getString("date") == null) {
                    if (words.contains("年")) {
                        int indexOf = all.indexOf("2");
                        String substring = all.substring(indexOf, (indexOf + 10));
                        String a = substring.replace("年", "-");
                        String b = a.replace("月", "-");
                        String c = b.replace("日", " ");
                        jsonObject.put("date", c);
                    } else {
                        int indexOf = all.indexOf("2");
                        if (all.length()>(indexOf+9)) {
                            jsonObject.put("date", all.substring(indexOf, (indexOf + 10)).replace(".", "-"));
                        }
                    }
                }
            }
            if ((words.contains(".0") || words.contains(".5")) && !(words.contains("201") || words.contains("202"))){
                String replace = all.replace("元", "");
                String replaceAll = replace.replaceAll("[\u2E80-\u9FA5]", "");
                String a = replaceAll.replace("：", "");
                String b = a.replace("（", "");
                String c = b.replace("）", "");
                String amount = c.replace("￥", "");
                int indexOf = amount.indexOf(".");
                if (jsonObject.getString("amount") == null){
                    jsonObject.put("amount",amount.substring(0,(indexOf+2)));
                }
            }
//            else if (words.length()<4){
//                String replace = words.replace("元", "");
//                String replaceAll = replace.replace("￥", "");
//                String a = replaceAll.replace("：", "");
//                String b = a.replace("（", "");
//                String amount = b.replace("）", "");
//                if (checkStrIsNumb(amount)) {
//                    amount = words+".00";
//                    if ((amount.contains(".0") || amount.contains(".5")) && !(amount.contains("201") || amount.contains("202"))){
//                        if (jsonObject.getString("amount") == null) {
//                            jsonObject.put("amount", amount);
//                        }
//                    }
//                }
//            }

        }

        return jsonObject;
    }




    /**
     * 通用机打发票识别
     * @param base64string
     * @return
     */
    public JSONObject printedHttp(String base64string){
        JSONObject jsonObject = new JSONObject(true);
        String http = textHttp(base64string);
        JSONObject parseObject = JSONObject.parseObject(http);
        JSONObject result = parseObject.getJSONObject("result");
        JSONArray words_block_list = result.getJSONArray("words_block_list");
        jsonObject.put("type","printed_invoice");
        jsonObject.put("payer_name","");
        for (int i = 0; i < words_block_list.size(); i++) {
            String words = words_block_list.getJSONObject(i).getString("words_result");
            String all = words.replace(" ", "");
            if (words.contains("发票代码") || words.contains("代码")){
                int indexOf = words.indexOf("码");
                if (all.length()>(indexOf+10)) {
                    jsonObject.put("code", all.substring((indexOf + 1), (indexOf + 13)));
                }
            }
            if (words.contains("发票号码")){
                if (jsonObject.getString("number") == null) {
                    if (words.length() > 5) {
                        int indexOf = words.indexOf("码");
                        jsonObject.put("number", all.substring((indexOf + 1), (indexOf + 9)));
                    } else {
                        jsonObject.put("number", words_block_list.getJSONObject(i + 1).getString("words_result"));
                    }
                }
            }
            if ((words.contains("2019") || words.contains("2020") || words.contains("2021") || words.contains("2022")) && words.length()<20) {
                if (jsonObject.getString("date") == null) {
                    if (words.contains("年")) {
                        int indexOf = all.indexOf("2");
                        String substring = all.substring(indexOf, (indexOf + 10));
                        String a = substring.replace("年", "-");
                        String b = a.replace("月", "-");
                        String c = b.replace("日", " ");
                        jsonObject.put("date", c);
                    } else {
                        int indexOf = all.indexOf("2");
                        if (all.length()>(indexOf+9)){
                            jsonObject.put("date", all.substring(indexOf, (indexOf + 10)).replace(".", "-"));
                        }
                    }
                }
            }
            if ((words.contains(".0") || words.contains(".5") || words.contains(".8"))
                  && !(words.contains("201") || words.contains("202")) && words.length()<12){
                String replace = all.replace("元", "");
                String replaceAll = replace.replaceAll("[\u2E80-\u9FA5]", "");
                String amount = replaceAll.replace("￥", "");
                jsonObject.put("amount",amount);
            }
            if (words.contains("公司")){
                if (jsonObject.getString("payer_name").equals("")){
                    if (words.contains("：")){
                        int indexOf = words.indexOf("：");
                        jsonObject.put("payer_name",words.substring(indexOf+1));
                    }else {
                        jsonObject.put("payer_name",words.replace(" ",""));
                    }

                }
            }
        }

        return jsonObject;
    }


    /**
     * 客运车票识别
     * @param base64string
     * @return
     */
    public JSONObject passengerHttp(String base64string){
        JSONObject jsonObject = new JSONObject(true);
        String http = imageHttp(base64string);
        JSONObject parseObject = JSONObject.parseObject(http);
        JSONObject result = parseObject.getJSONObject("result");
        JSONArray words_block_list = result.getJSONArray("words_block_list");
        jsonObject.put("type","passenger_invoice");
        jsonObject.put("departure_station","");
        jsonObject.put("destination_station","");
        jsonObject.put("name","");
        for (int i = 0; i < words_block_list.size(); i++) {
            String words = words_block_list.getJSONObject(i).getString("words");
            String all = words.replace(" ", "");
            if (words.contains("发票代码") || words.contains("代码")){
                if (words.length()>15){
                    int indexOf = words.indexOf("码");
                    String replace = all.replace("：", "");
                    jsonObject.put("code",replace.substring((indexOf+1),(indexOf+13)));
                }else {
                    jsonObject.put("code",words_block_list.getJSONObject(i+1).getString("words"));
                }
            }
            if (words.contains("发票号码") || words.contains("号码")){
                if (words.length()>6){
                    int indexOf = words.indexOf("码");
                    String replace = all.replace("：", "");
                    jsonObject.put("number",replace.substring((indexOf+1),(indexOf+9)));
                }else {
                    jsonObject.put("number",words_block_list.getJSONObject(i+1).getString("words"));
                }

            }
            if ((words.contains("2019") || words.contains("2020") || words.contains("2021") || words.contains("2022")) && words.length()>9) {
                if (jsonObject.getString("date") == null) {
                    if (words.contains("年")) {
                        int indexOf = all.indexOf("2");
                        String substring = all.substring(indexOf, (indexOf + 10));
                        String a = substring.replace("年", "-");
                        String b = a.replace("月", "-");
                        String c = b.replace("日", " ");
                        jsonObject.put("date", c);
                    } else {
                        int indexOf = all.indexOf("2");
                        if (all.length()>(indexOf+9)) {
                            jsonObject.put("date", all.substring(indexOf, (indexOf + 10)).replace(".", "-"));
                        }
                    }
                }
            }
            if ((words.contains(".0") || words.contains(".5")) && !(words.contains("201") || words.contains("202"))){
                String replace = all.replace("元", "");
                String replaceAll = replace.replaceAll("[\u2E80-\u9FA5]", "");
                String amount = replaceAll.replace("：", "");
                if (jsonObject.getString("amount") == null){
                    jsonObject.put("amount",amount);
                }
            }else if (words.length()<4  && i > 8){
                String replace = words.replace("元", "");
                String amount = replace.replace("￥", "");
                if (checkStrIsNumb(amount)  &&  amount.length() >1) {
                    amount = amount+".00";
                    if ((amount.contains(".0") || amount.contains(".5")) && !(amount.contains("201") || amount.contains("202"))){
                        if (jsonObject.getString("amount") == null) {
                            jsonObject.put("amount", amount);
                        }
                    }
                }
            }

        }

        return jsonObject;
    }



    /**
     * 增值税发票
     * @param base64string
     * @return
     */
    public JSONObject vatHttp(String base64string) {
        String result = null;
        Response response = null;
        JSONObject object = new JSONObject(true);
        JSONObject jsonObject = new JSONObject(true);
            try {
                if (base64string != null) {
                    JSONObject json = new JSONObject();
                    json.put("image", base64string);
                    json.put("return_key_list",new ArrayList());
                    RequestBody body = RequestBody.create(TYPE, json.toJSONString());
                    OkHttpClient httpClient = new OkHttpClient().newBuilder()
                            .connectTimeout(60 * 1000, TimeUnit.SECONDS)
                            .readTimeout(5 * 60 * 1000, TimeUnit.SECONDS)
                            .writeTimeout(5 * 60 * 1000, TimeUnit.SECONDS).build();
                    Request request = new Request.Builder()
                            .url("http://" + vatProperties.getHost() + ":" + vatProperties.getPort())
                            .post(body)
                            .build();
                    response = httpClient.newCall(request).execute();
                    result = response.body().string();
                    jsonObject = JSONObject.parseObject(result);
                    String replace = jsonObject.toJSONString().replace("￥", "");
                    JSONObject job = JSONObject.parseObject(replace);
                    object = job.getJSONObject("result");
                    if (object != null){
                        object.remove("confidence");
                        if (object.getString("seller_seal").contains("发票专用章")){
                            object.put("seller_seal",1);
                        }else {
                            object.put("seller_seal",0);
                        }
                        if (object.getString("supervision_seal").contains("国家税务总局")){
                            object.put("supervision_seal",61);
                        }else {
                            object.put("supervision_seal",0);
                        }
                    }
                    if (object == null){
                        object = jsonObject;
                    }
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }finally {
                if (response != null) {
                    response.close();
                }
            }
                return object;
    }


    /**
     * 出租车发票识别
     * @param base64string
     * @return
     */
    public JSONObject taxiHttp(String base64string) {
        String result = null;
        Response response = null;
        JSONObject object = new JSONObject(true);
        JSONObject jsonObject = new JSONObject(true);
        try {
            if (base64string != null) {
                JSONObject json = new JSONObject();
                json.put("image", base64string);
                RequestBody body = RequestBody.create(TYPE, json.toJSONString());
                OkHttpClient httpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(60 * 1000, TimeUnit.SECONDS)
                        .readTimeout(5 * 60 * 1000, TimeUnit.SECONDS)
                        .writeTimeout(5 * 60 * 1000, TimeUnit.SECONDS).build();
                Request request = new Request.Builder()
                        .url("http://" + taxiProperties.getHost() + ":" + taxiProperties.getPort())
                        .post(body)
                        .build();
                response = httpClient.newCall(request).execute();
                result = response.body().string();
                jsonObject = JSONObject.parseObject(result);
                String replace = jsonObject.toJSONString().replace("￥", "");
                JSONObject job = JSONObject.parseObject(replace);
                object = job.getJSONObject("result");
                object.put("type","taxi_invoice");
                if (object != null){
                    object.remove("confidence");
                }
                if (object == null){
                    object = jsonObject;
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }finally {
            if (response != null) {
                response.close();
            }
        }
        return object;
    }


    /**
     * 车辆通行费识别
     * @param base64string
     * @return
     */
    public JSONObject tollHttp(String base64string) {
        String result = null;
        Response response = null;
        JSONObject object = new JSONObject(true);
        JSONObject jsonObject = new JSONObject(true);
        try {
            if (base64string != null) {
                JSONObject json = new JSONObject();
                json.put("image", base64string);
                RequestBody body = RequestBody.create(TYPE, json.toJSONString());
                OkHttpClient httpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(60 * 1000, TimeUnit.SECONDS)
                        .readTimeout(5 * 60 * 1000, TimeUnit.SECONDS)
                        .writeTimeout(5 * 60 * 1000, TimeUnit.SECONDS).build();
                Request request = new Request.Builder()
                        .url("http://" + tollProperties.getHost() + ":" + tollProperties.getPort())
                        .post(body)
                        .build();
                response = httpClient.newCall(request).execute();
                result = response.body().string();
                jsonObject = JSONObject.parseObject(result);
                String replace = jsonObject.toJSONString().replace("￥", "");
                JSONObject job = JSONObject.parseObject(replace);
                object = job.getJSONObject("result");
                object.put("type","toll_invoice");
                if (object != null){;
                    object.remove("confidence");
                }
                if (object == null){
                    object = jsonObject;
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }finally {
            if (response != null) {
                response.close();
            }
        }
        return object;
    }

    /**
     * 火车票识别
     * @param base64string
     * @return
     */
    public JSONObject trainHttp(String base64string) {
        String result = null;
        Response response = null;
        JSONObject object = new JSONObject(true);
        JSONObject jsonObject = new JSONObject(true);
        try {
            if (base64string != null) {
                JSONObject json = new JSONObject();
                json.put("image", base64string);
                RequestBody body = RequestBody.create(TYPE, json.toJSONString());
                OkHttpClient httpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(60 * 1000, TimeUnit.SECONDS)
                        .readTimeout(5 * 60 * 1000, TimeUnit.SECONDS)
                        .writeTimeout(5 * 60 * 1000, TimeUnit.SECONDS).build();
                Request request = new Request.Builder()
                        .url("http://" + trainProperties.getHost() + ":" + trainProperties.getPort())
                        .post(body)
                        .build();
                response = httpClient.newCall(request).execute();
                result = response.body().string();
                jsonObject = JSONObject.parseObject(result);
                String replace = jsonObject.toJSONString().replace("￥", "");
                JSONObject job = JSONObject.parseObject(replace);
                object = job.getJSONObject("result");
                object.put("type","train_ticket");
                if (object != null){
                    object.remove("confidence");
                }
                if (object == null){
                    object = jsonObject;
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }finally {
            if (response != null) {
                response.close();
            }
        }
        return object;
    }

    /**
     * 定额发票识别
     * @param base64string
     * @return
     */
    public JSONObject quotaHttp(String base64string) {
        String result = null;
        Response response = null;
        JSONObject object = new JSONObject(true);
        JSONObject jsonObject = new JSONObject(true);
        try {
            if (base64string != null) {
                JSONObject json = new JSONObject();
                json.put("image", base64string);
                RequestBody body = RequestBody.create(TYPE, json.toJSONString());
                OkHttpClient httpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(60 * 1000, TimeUnit.SECONDS)
                        .readTimeout(5 * 60 * 1000, TimeUnit.SECONDS)
                        .writeTimeout(5 * 60 * 1000, TimeUnit.SECONDS).build();
                Request request = new Request.Builder()
                        .url("http://" + quotaProperties.getHost() + ":" + quotaProperties.getPort())
                        .post(body)
                        .build();
                response = httpClient.newCall(request).execute();
                result = response.body().string();
                jsonObject = JSONObject.parseObject(result);
                String replace = jsonObject.toJSONString().replace("￥", "");
                JSONObject job = JSONObject.parseObject(replace);
                object = job.getJSONObject("result");
                object.put("type","quota_invoice");
                if (object != null){
                    object.remove("confidence");
                }
                if (object == null){
                    object = jsonObject;
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }finally {
            if (response != null) {
                response.close();
            }
        }
        return object;
    }

    /** ￥
     * 机动车销售发票识别
     * @param base64string
     * @return
     */
    public JSONObject mvsHttp(String base64string) {
        String result = null;
        Response response = null;
        JSONObject object = new JSONObject(true);
        JSONObject jsonObject = new JSONObject(true);
        try {
            if (base64string != null) {
                JSONObject json = new JSONObject();
                json.put("image", base64string);
                RequestBody body = RequestBody.create(TYPE, json.toJSONString());
                OkHttpClient httpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(60 * 1000, TimeUnit.SECONDS)
                        .readTimeout(5 * 60 * 1000, TimeUnit.SECONDS)
                        .writeTimeout(5 * 60 * 1000, TimeUnit.SECONDS).build();
                Request request = new Request.Builder()
                        .url("http://" + mvsProperties.getHost() + ":" + mvsProperties.getPort())
                        .post(body)
                        .build();
                response = httpClient.newCall(request).execute();
                result = response.body().string();
                jsonObject = JSONObject.parseObject(result);
                String replace = jsonObject.toJSONString().replace("￥", "");
                JSONObject job = JSONObject.parseObject(replace);
                object = job.getJSONObject("result");
                object.put("type","mvs_invoice");
                if (object != null){
                    object.remove("confidence");
                }
                if (object == null){
                    object = jsonObject;
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }finally {
            if (response != null) {
                response.close();
            }
        }
        return object;
    }

    /**
     * 飞机行程单识别
     * @param base64string
     * @return
     */
    public JSONObject flightHttp(String base64string) {
        String result = null;
        Response response = null;
        JSONObject object = new JSONObject(true);
        JSONObject jsonObject = new JSONObject(true);
        try {
            if (base64string != null) {
                JSONObject json = new JSONObject();
                json.put("image", base64string);
                RequestBody body = RequestBody.create(TYPE, json.toJSONString());
                OkHttpClient httpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(60 * 1000, TimeUnit.SECONDS)
                        .readTimeout(5 * 60 * 1000, TimeUnit.SECONDS)
                        .writeTimeout(5 * 60 * 1000, TimeUnit.SECONDS).build();
                Request request = new Request.Builder()
                        .url("http://" + flightProperties.getHost() + ":" + flightProperties.getPort())
                        .post(body)
                        .build();
                response = httpClient.newCall(request).execute();
                result = response.body().string();
                jsonObject = JSONObject.parseObject(result);
                String replace = jsonObject.toJSONString().replace("￥", "");
                JSONObject job = JSONObject.parseObject(replace);
                object = job.getJSONObject("result");
                object.put("type","flight_itinerary");
                if (object != null){
                    object.remove("confidence");
                    object.put("fare",object.getString("fare").replaceAll("[a-zA-Z]", "").replace(" ",""));
                    object.put("total",object.getString("total").replaceAll("[a-zA-Z]", "").replace(" ",""));
                    object.put("caac_development_fund",object.getString("caac_development_fund").replaceAll("[a-zA-Z]", "").replace(" ",""));
                }
                if (object == null){
                    object = jsonObject;
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }finally {
            if (response != null) {
                response.close();
            }
        }
        return object;
    }


    /**
     * 营业执照识别
     * @param base64string
     * @return
     */
    public JSONObject licenseHttp(String base64string) {
        String result = null;
        Response response = null;
        JSONObject object = new JSONObject(true);
        JSONObject jsonObject = new JSONObject(true);
        try {
            if (base64string != null) {
                JSONObject json = new JSONObject();
                json.put("image", base64string);
                RequestBody body = RequestBody.create(TYPE, json.toJSONString());
                OkHttpClient httpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(60 * 1000, TimeUnit.SECONDS)
                        .readTimeout(5 * 60 * 1000, TimeUnit.SECONDS)
                        .writeTimeout(5 * 60 * 1000, TimeUnit.SECONDS).build();
                Request request = new Request.Builder()
                        .url("http://" + licenseProperties.getHost() + ":" + licenseProperties.getPort())
                        .post(body)
                        .build();
                response = httpClient.newCall(request).execute();
                result = response.body().string();
                jsonObject = JSONObject.parseObject(result);
                object = jsonObject.getJSONObject("result");
                if (object != null){
                    object.remove("confidence");
                }
                if (object == null){
                    object = jsonObject;
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }finally {
            if (response != null) {
                response.close();
            }
        }
        return object;
    }


    /**
     * 网络图片识别方法
     */
    public String imageHttp(String base64string) {
        String result = null;
        Response response = null;
        try {
            if (base64string != null) {
                JSONObject json = new JSONObject();
                json.put("image", base64string);
                json.put("detect_direction",true);
                RequestBody body = RequestBody.create(TYPE, json.toJSONString());
                OkHttpClient httpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(60 * 1000, TimeUnit.SECONDS)
                        .readTimeout(5 * 60 * 1000, TimeUnit.SECONDS)
                        .writeTimeout(5 * 60 * 1000, TimeUnit.SECONDS).build();
                Request request = new Request.Builder()
                        .url("http://" + imageProperties.getHost() + ":" + imageProperties.getPort())
                        .post(body)
                        .build();
                response = httpClient.newCall(request).execute();
                result = response.body().string();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }


    /**
     * 文字识别方法
     */
    public String textHttp(String base64string) {
        String result = null;
        Response response = null;
        try {
            if (base64string != null) {
                JSONObject json = new JSONObject();
                json.put("image", base64string);
                json.put("detect_direction",true);
                RequestBody body = RequestBody.create(TYPE, json.toJSONString());
                OkHttpClient httpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(2000, TimeUnit.SECONDS)
                        .readTimeout(2000, TimeUnit.SECONDS).build();
                Request request = new Request.Builder()
                        .url("http://"+cflProperties.getHost()+":"+cflProperties.getPort())
                        .post(body)
                        .build();
                response = httpClient.newCall(request).execute();

                result = response.body().string().replaceAll("\r","");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    /**
     * 发送通用表格识别请求
     *
     * @param base64string
     * @return
     */
    public String tableHttp(String base64string) {
        Response response = null;
        String content = null;
        try {
            JSONObject json = new JSONObject();
            json.put("image", base64string);
            RequestBody body = RequestBody.create(TYPE, json.toJSONString());
            OkHttpClient httpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(2000, TimeUnit.SECONDS)
                    .readTimeout(2000, TimeUnit.SECONDS).build();
            Request request = new Request.Builder()
                    .url("http://" + tableProperties.getHost() + ":" + tableProperties.getPort())
                    .post(body)
                    .build();
            response = httpClient.newCall(request).execute();
            content = response.body().string();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return content;
    }

    /**
     * 字符串找金额带.
     * @param str
     * @return 数字
     */
    private  String strFindNum(String str) {
        char [] nu=str.toCharArray();
        StringBuffer num=new StringBuffer();
        for (int i = 0; i < nu.length; i++) {
            if (Integer.valueOf(nu[i])>=48 && Integer.valueOf(nu[i])<=57
                    || Integer.valueOf(nu[i])==46) {
                num.append(nu[i]);
            }
        }
        return num.toString();
    }


    /**
     * 校验一个字符串中是否全是数字的方法
     * @param s
     * @return
     */
    private Boolean checkStrIsNumb(String s){
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))){
                return false;
            }
        }
        return true;
    }
}

