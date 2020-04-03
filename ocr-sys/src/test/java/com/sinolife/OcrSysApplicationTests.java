package com.sinolife;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sinolife.interfaces.TableDemo;
import com.sinolife.interfaces.TickeDemo;
import com.sinolife.properties.*;
import com.sinolife.service.Classify;
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
import javax.sound.midi.Soundbank;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
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
    private Classify classify;

    @Autowired
    private DistinguishService distinguishService;

    private static final MediaType TYPE = MediaType.parse("application/json");

    @Autowired
    private TickeDemo tickeDemo;

    @Autowired
    private TableDemo tableDemo;

    private Logger logger = LoggerFactory.getLogger(OcrSysApplicationTests.class);

    @Test
    void contextLoads() {
        HashMap<String, String> map = null;
        try {
            File file = new File("C:\\Users\\Administrator\\Desktop\\车船票\\定额发票\\574198916.jpg");
            byte[] bytes = FileUtils.readFileToByteArray(file);
            String base64string = Base64Utils.encodeToString(bytes);
            HashMap<String, String> parms = new HashMap<>();
            map = new HashMap<>();
            parms.put("image", base64string);
            parms.put("fileName", file.getName());
            String ocr = ocrService.ocr(parms);
            JSONObject jsonObject = JSONObject.parseObject(ocr);
            String item = jsonObject.getString("item");
            JSONArray jsonArray = JSONArray.parseArray(item);
            BASE64Decoder base64Decoder = new BASE64Decoder();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                byte[] images = base64Decoder.decodeBuffer(object.getString("image"));
                ByteArrayInputStream bais = new ByteArrayInputStream(images);
                BufferedImage bi1 = ImageIO.read(bais);
                File file1 = new File("D:\\图片\\" + file.getName().replace(".jpg", "") + i + ".jpg");
                ImageIO.write(bi1, "jpg", file1);
                System.out.println("发票类型： " + object.getString("type"));
                map.put("image", object.getString("image"));
                map.put("type", object.getString("type"));
            }
            String distinguish = distinguishService.distinguish(map);
            System.out.println(distinguish);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }


    @Test
    void shiBie1() {
        try {
            File file = new File("C:\\Users\\Administrator\\Desktop\\车船票\\表格素材\\368620150.jpg");
            byte[] bytes = FileUtils.readFileToByteArray(file);
            String base64string = Base64Utils.encodeToString(bytes);
            Map<String, String> map = new HashMap<>();
            map.put("image", base64string);
            map.put("type", "手续费汇总计算表");
            String distinguish = distinguishService.distinguish(map);
            System.out.println(distinguish);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    void table() {
        Response response;
        String content = null;
        JSONObject object = new JSONObject();
        long start = System.currentTimeMillis();
        try {
            File file = new File("C:\\Users\\Administrator\\Desktop\\车船票\\表格素材\\368620150.jpg");
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
            logger.info("调用接口耗时：" + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    void vat() {
        Response response;
        String content = null;
        JSONObject object = new JSONObject();
        long start = System.currentTimeMillis();
        try {
            File file = new File("C:\\Users\\Administrator\\Desktop\\车船票\\单张票据\\增值税发票\\562042065.jpg");
            byte[] bytes = FileUtils.readFileToByteArray(file);
            String base64string = Base64Utils.encodeToString(bytes);
            JSONObject json = new JSONObject();
            json.put("image", base64string);
            json.put("return_key_list", new ArrayList());
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
            logger.info("调用接口耗时：" + (System.currentTimeMillis() - start));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @Test
    void test01() throws IOException {
        File file = new File("C:\\Program Files\\feiq\\Recv Files\\华为接口识别率测试.wusihua(1)\\华为接口识别率测试.wusihua\\支付截图\\刷卡小票\\590469036.jpg");
        byte[] bytes = FileUtils.readFileToByteArray(file);
        String base64string = Base64Utils.encodeToString(bytes);
        HashMap<String, String> parms = new HashMap<>();
        parms.put("image", base64string);
        String distinguish = distinguishService.distinguish(parms);
        System.out.println(distinguish);
    }

    @Test
    void test02() throws IOException {
        File file = new File("C:\\Users\\Administrator\\Desktop\\刷卡小票\\");
        File[] files = file.listFiles();
        for (File fs : files) {
            logger.info(fs.getName());
            byte[] bytes = FileUtils.readFileToByteArray(new File(fs.getPath()));
            String base64string = Base64Utils.encodeToString(bytes);

//            String content = http(base64string);

//            System.out.println(object);
        }


    }

    @Test
    void test03() throws IOException {
        File file = new File("C:\\Users\\Administrator\\Desktop\\问题图片\\中支.jpg");
        byte[] bytes = FileUtils.readFileToByteArray(file);
        String base64string = Base64Utils.encodeToString(bytes);
        HashMap<String, String> parms = new HashMap<>();
        parms.put("image", base64string);
        String distinguish = distinguishService.distinguish(parms);
        System.out.println(distinguish);
    }


    @Test
    void huiyifeiyong() throws IOException {
        File file = new File("C:\\Users\\Administrator\\Desktop\\问题图片\\总分.jpg");
        byte[] bytes = FileUtils.readFileToByteArray(file);
        String base64string = Base64Utils.encodeToString(bytes);
        JSONObject object = tableDemo.huiYiFeiYong(base64string);
        System.out.println(object);

    }

    @Test
    void caigoudan() throws IOException {
        File file = new File("C:\\Users\\Administrator\\Desktop\\测试图片\\0009\\591835142.jpg");
        byte[] bytes = FileUtils.readFileToByteArray(file);
        String base64string = Base64Utils.encodeToString(bytes);
        JSONObject jsonObject = tableDemo.purchaseRequisition("采购申请单(中支公司适用)", base64string);
         System.out.println(jsonObject);
    }

    @Test
    void qingdan() throws IOException {
        File file = new File("C:\\Users\\Administrator\\Desktop\\测试图片\\0015\\理赔查勘费用清单（分支公司适用）1.jpg");
        byte[] bytes = FileUtils.readFileToByteArray(file);
        String base64string = Base64Utils.encodeToString(bytes);
        JSONObject jsonObject = tableDemo.claimsSurvey(base64string);
        System.out.println(jsonObject);
    }
    @Test
    void idenCode() throws IOException {

       // File file = new File("D:\\tempFile\\1.png");
        File file = new File("D:\\tempFile\\验证码\\验证码\\平安验证码\\4.png");
        byte[] bytes = FileUtils.readFileToByteArray(file);
        String base64string = Base64Utils.encodeToString(bytes);
        Map<String,String> paraMap = new HashMap<String,String>();
        paraMap.put("image", base64string);
        paraMap.put("quickMod", "true");
        String  jsonObject = ocrService.generalText(paraMap);
        System.out.println(jsonObject);
    }

    @Test
    void shuakaxiaopiao() throws IOException {
        File file = new File("C:\\Program Files\\feiq\\Recv Files\\华为接口识别率测试.wusihua(1)\\华为接口识别率测试.wusihua\\支付截图\\刷卡小票\\590469036.jpg");
        byte[] bytes = FileUtils.readFileToByteArray(file);
        String base64string = Base64Utils.encodeToString(bytes);
        JSONObject jsonObject = tickeDemo.brushCard(base64string);
//        JSONObject jsonObject = tickeDemo.WeChatBills(base64string);
        System.out.println(jsonObject);
    }
    @Test
    void fileFor() throws IOException {
        Response response;

//        JSONObject object = new JSONObject();
        File file = new File("C:\\Users\\Administrator\\Desktop\\刷卡小票\\微信账单\\");
        File[] files = file.listFiles();
        JSONObject json = new JSONObject();

        FileWriter writer;
        for (File fs : files) {
            String content = null;
            byte[] bytes = FileUtils.readFileToByteArray(new File(fs.getPath()));
            String base64string = Base64Utils.encodeToString(bytes);
            json.put("image", base64string);
            json.put("detect_direction",true);
//            JSONObject jsonObject = tickeDemo.brushCard(base64string);
            RequestBody body = RequestBody.create(TYPE, json.toJSONString());
            OkHttpClient httpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(2000, TimeUnit.SECONDS)
                    .readTimeout(2000, TimeUnit.SECONDS).build();
            Request request = new Request.Builder()
                    .url("http://192.168.25.25:8601")//刷卡小票
                    .post(body)
                    .build();
            response = httpClient.newCall(request).execute();
            content = response.body().string();
            logger.warn(fs.getName());

            String  contentName = fs.getName().replace(".jpg",".json");

            try {
                writer = new FileWriter("C:\\Users\\Administrator\\Desktop\\图片json文件\\"+contentName);
                writer.write("");//清空原文件内容
                writer.write(content);
                logger.warn(contentName+"写入成功");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

//            String content = http(base64string);

//            System.out.println(object);
        }
    }
}