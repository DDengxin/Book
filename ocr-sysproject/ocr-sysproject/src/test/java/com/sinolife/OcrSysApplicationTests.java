package com.sinolife;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sinolife.interfaces.TableDemo;
import com.sinolife.interfaces.TickeDemo;
import com.sinolife.properties.*;
import com.sinolife.service.DistinguishService;
import com.sinolife.service.OcrService;
import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Base64Utils;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class OcrSysApplicationTests {

    @Autowired
    private CflProperties cflProperties;

    @Autowired
    private OcrService ocrService;

    @Autowired
    private DistinguishService distinguishService;

    private static final MediaType TYPE = MediaType.parse("application/json");

    @Autowired
    private TickeDemo tickeDemo;

    @Autowired
    private TableDemo tableDemo;

    private   Logger logger = LoggerFactory.getLogger(OcrSysApplicationTests.class);

    @Test
    void contextLoads() throws IOException {
        File file = new File("C:\\Users\\ThinkPad\\Desktop\\ocr-sysproject\\ocr-sysproject\\2.jpg");

        byte[] bytes = FileUtils.readFileToByteArray(file); //将图片file转换为byte数组
        String base64string = Base64Utils.encodeToString(bytes);//通过Base64工具类将byte数组进行编码并返回字符串对象
        HashMap<String,String> parms = new HashMap<>();
        HashMap<String,String> map = new HashMap<>();
        parms.put("image",base64string);//将图片与对应的文件放入hashmap中
        parms.put("fileName",file.getName());
        String ocr = ocrService.ocr(parms);
        JSONObject jsonObject = JSONObject.parseObject(ocr);
        String item = jsonObject.getString("item");
        JSONArray jsonArray = JSONArray.parseArray(item);
        BASE64Decoder base64Decoder = new BASE64Decoder();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
//            byte[] images = base64Decoder.decodeBuffer(object.getString("image"));
//            ByteArrayInputStream bais = new ByteArrayInputStream(images);
//            BufferedImage bi1 = ImageIO.read(bais);
//            File file1 = new File("D:\\图片\\"+file.getName().replace(".jpg","")+i+".jpg");
//            ImageIO.write(bi1,"jpg",file1);
            System.out.println("发票类型： "+object.getString("type"));
            map.put("image",object.getString("image"));
            map.put("type",object.getString("type"));
        }
            String distinguish = distinguishService.distinguish(map);
            System.out.println(distinguish);
//        logger.info(ocr);



    }
public static String getClass(Object object){
        return object.getClass().toString();
    }

    @Test
    void wenZi() {
        Response response;
        String content=null;
        long start = 0;
        JSONObject object = new JSONObject(true);
        try {
            File file = new File("C:\\Users\\Administrator\\Desktop\\车船票\\单张票据\\通用机打发票\\591086828.jpg");
            byte[] bytes = FileUtils.readFileToByteArray(file);
            String base64string = Base64Utils.encodeToString(bytes);
            start = System.currentTimeMillis();
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
            content = response.body().string();
            JSONObject jsonObject = JSONObject.parseObject(content);
            JSONObject result = jsonObject.getJSONObject("result");
            JSONArray words_block_list = result.getJSONArray("words_block_list");
            System.out.println(words_block_list);
            for (int i = 0; i < words_block_list.size(); i++) {
                object.put("words_result"+i+"",words_block_list.getJSONObject(i).getString("words_result"));
            }
            System.out.println(object);

//            JSONObject job = tickeDemo.brushCard(base64string);
//            System.out.println(job);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.warn("调用接口耗时："+(System.currentTimeMillis()-start));
    }

    @Test
    void tupian() {
        Response response;
        String content=null;
        long start = 0;
        JSONObject object = new JSONObject(true);
        JSONObject job = new JSONObject(true);
        try {
            File file = new File("C:\\Users\\ThinkPad\\Desktop\\ocr-sysproject\\ocr-sysproject\\图1.jpg");
            byte[] bytes = FileUtils.readFileToByteArray(file);
            String base64string = Base64Utils.encodeToString(bytes);
            start = System.currentTimeMillis();
            JSONObject json = new JSONObject();
            json.put("image", base64string);
            json.put("detect_direction",true);
            RequestBody body = RequestBody.create(TYPE, json.toJSONString());
            OkHttpClient httpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(2000, TimeUnit.SECONDS)
                    .readTimeout(2000, TimeUnit.SECONDS).build();
            Request request = new Request.Builder()
                    .url("http://192.168.25.25:8603")
                    .post(body)
                    .build();
            response = httpClient.newCall(request).execute();
            content = response.body().string();
            JSONObject jsonObject = JSONObject.parseObject(content);
            JSONObject result = jsonObject.getJSONObject("result");
            JSONArray words_block_list = result.getJSONArray("words_block_list");
            for (int i = 0; i < words_block_list.size(); i++) {
                object.put("words_result"+i+"",words_block_list.getJSONObject(i).getString("words"));
            }
            System.out.println(object);
//            job = tickeDemo.passengerHttp(base64string);
//            System.out.println(job);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.warn("调用接口耗时："+(System.currentTimeMillis()-start));


    }



    @Test
    void table(){
        Response response;
        String content=null;
        JSONObject object = new JSONObject();
        long start = System.currentTimeMillis();
        try {
            File file = new File("C:\\Users\\Administrator\\Desktop\\富德ocr\\富德ocr模板\\表格素材\\368618684.jpg");
            byte[] bytes = FileUtils.readFileToByteArray(file);
            String base64string = Base64Utils.encodeToString(bytes);
            JSONObject json = new JSONObject();
            json.put("image", base64string);
            RequestBody body = RequestBody.create(TYPE, json.toJSONString());
            OkHttpClient httpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(2000, TimeUnit.SECONDS)
                    .readTimeout(2000, TimeUnit.SECONDS).build();
            Request request = new Request.Builder()
                    .url("http://192.168.25.25:8602")
                    .post(body)
                    .build();
            response = httpClient.newCall(request).execute();
            content = response.body().string();
            System.out.println(JSONObject.parseObject(content));
//            object = tableDemo.shouXuFei(base64string);
//            System.out.println(object);
            logger.info("调用接口耗时："+(System.currentTimeMillis()-start));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Test
    void shiBie(){
        try {
            File file = new File("C:\\Users\\Administrator\\Desktop\\车船票\\单张票据\\通用机打发票\\591129217.jpg");
            byte[] bytes = FileUtils.readFileToByteArray(file);
            String base64string = Base64Utils.encodeToString(bytes);
            Map<String,String> map = new HashMap<>();
            map.put("image",base64string);
            map.put("type","printed");
            String distinguish = distinguishService.distinguish(map);
            System.out.println(distinguish);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void vat(){
        Response response;
        String content=null;
        JSONObject object = new JSONObject();
        long start = System.currentTimeMillis();
        try {
            File file = new File("C:\\Users\\Administrator\\Desktop\\车船票\\单张票据\\增值税发票\\562042065.jpg");
            byte[] bytes = FileUtils.readFileToByteArray(file);
            String base64string = Base64Utils.encodeToString(bytes);
            JSONObject json = new JSONObject();
            json.put("image", base64string);
            json.put("return_key_list",new ArrayList());
            RequestBody body = RequestBody.create(TYPE, json.toJSONString());
            OkHttpClient httpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(2000, TimeUnit.SECONDS)
                    .readTimeout(2000, TimeUnit.SECONDS).build();
            Request request = new Request.Builder()
                    .url("http://192.168.25.25:8609")
                    .post(body)
                    .build();
            response = httpClient.newCall(request).execute();
            content = response.body().string();
//            System.out.println(JSONObject.parseObject(content));
            JSONObject jsonObject = tickeDemo.vatHttp(base64string);
            System.out.println(jsonObject);
            logger.info("调用接口耗时："+(System.currentTimeMillis()-start));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Test
    void xiaCe(){
        String a ="( ( )     0. 北 省 路 客 运 发 票";
        String s = a.replaceAll(" ", "");
//        System.out.println(s);
//        System.out.println(s.contains("客运发票"));

//        String b = "RMB:1000.00";
//        String all = b.replaceAll("[a-zA-Z]", "");
//        System.out.println(all);

//        String c = "商户名称益阳市朝阳站发卡网点";
//        int indexOf = c.indexOf("称");
//        System.out.println(indexOf);
//        System.out.println(c.substring(indexOf));

//        String d = "33090219******4429";
//        String replace = d.replace(" ", "");
//        String replaceAll = replace.replaceAll("[\u2E80-\u9FA5]", "");
//        System.out.println(replaceAll+":"+(replaceAll.length()));

//        JSONObject jsonObject = new JSONObject(true);
//        jsonObject.put("payer_name","哈哈哈");
//        System.out.println(jsonObject.getString("payer_name") == null);
//        System.out.println("获取json中字段:"+(jsonObject.getString("payer_name").equals("")));

//        Map<String,String> map = new HashMap<>();
//        System.out.println(map.get("type"));

//        String e = "浙江分 公司 财务 部";
//        String[] split = e.split("\\s");
//        for (String s1 : split) {
//            System.out.println(s1);
//        }
//        System.out.println(split.length);

        String f = "合计：￥856.80元";
        System.out.println(f.length());

    }

    private Boolean checkStrIsNumb(String s){
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))){
                return false;
            }
        }
        return true;
    }

}
