package com.sinolife.service;

import com.sinolife.util.StringUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sinolife.interfaces.TableDemo;
import com.sinolife.interfaces.TickeDemo;
import com.sinolife.properties.CfgProperties;
import com.sinolife.properties.CflProperties;
import com.sinolife.properties.ImageProperties;
import com.sinolife.sf.esb.EsbMethod;
import com.sinolife.util.Base64ImageUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;



@Service
@EnableConfigurationProperties(value = CfgProperties.class)
public class OcrService {

    private  static final Logger logger= LoggerFactory.getLogger(OcrService.class);

    @Autowired
    private  CfgProperties cfgProperties;

    @Autowired
    private ImageProperties imageProperties;
    @Autowired
    private CflProperties cflProperties;
    @Autowired
    private Classify classify;

    @Autowired
    private TableDemo tableDemo;

    @Autowired
    private TickeDemo tickeDemo;
    /**
     * @param paraMap
     *  image 图像数据base64编码 （图片不超过10M 最小边不小于15像素，最长边不超过4096）
     *  quickMod 快速模式开关（针对单行文字图片，且文字占比超过50%） true 打开，false关闭 默认关闭
     * @return
     */
    public String generalText(Map<String,String> paraMap){
        logger.warn("generalText--------start");
        JSONObject resultJson = new JSONObject();
        String image =  paraMap.get("image");
        String quickMod =  (String) paraMap.get("quickMod");
        if(!"true".equals(quickMod)){
            quickMod = "false";
        }
        Response response = null;
        String  result  ="";
        if(StringUtil.isEmpty(image)){
            resultJson.put("code","01");
            resultJson.put("error_msg","传入的图像数据为空");
        }else{
            JSONObject json = new JSONObject();
            json.put("image", image);
            json.put("quick_mode", quickMod);
            RequestBody body = RequestBody.create(TYPE, json.toJSONString());
            OkHttpClient httpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(60 * 1000, TimeUnit.SECONDS)
                    .readTimeout(5 * 60 * 1000, TimeUnit.SECONDS)
                    .writeTimeout(5 * 60 * 1000, TimeUnit.SECONDS).build();
            Request request = new Request.Builder()
                    .url("http://" + cflProperties.getHost() + ":" + cflProperties.getPort())
                    .post(body)
                    .build();
            try{
                response = httpClient.newCall(request).execute();
                result = response.body().string();
            }catch( IOException e){
                resultJson.put("code","01");
                resultJson.put("error_msg","调用底层OCR模型异常");
            }
            if (result.contains("error")){
                JSONObject  j=JSONObject.parseObject(result);
                logger.warn("generalOcr error----code:"+ j.getString("error_code")+",msg:"+j.getString("error_msg"));
                resultJson.put("error_code",j.getString("error_code"));
                resultJson.put("error_msg",j.getString("error_msg"));
            }else {
                resultJson =JSONObject.parseObject(result);
            }
        }
        logger.warn("generalText--------end");
        return resultJson.toJSONString();
    }
    @EsbMethod(esbServiceId = "com.sinolife.ocr.esb.general")
    public String generalOcr(Map<String,Object> paraMap){
        logger.warn("generalOcr--------start");
        JSONObject resultJson = new JSONObject();
        String image = (String) paraMap.get("image");
        Response response = null;
        String  result  ="";
        if(StringUtil.isEmpty(image)){
            resultJson.put("code","01");
            resultJson.put("error_msg","传入的图像数据为空");
        }else{
            JSONObject json = new JSONObject();
            json.put("image", image);
            RequestBody body = RequestBody.create(TYPE, json.toJSONString());
            OkHttpClient httpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(60 * 1000, TimeUnit.SECONDS)
                    .readTimeout(5 * 60 * 1000, TimeUnit.SECONDS)
                    .writeTimeout(5 * 60 * 1000, TimeUnit.SECONDS).build();
            Request request = new Request.Builder()
                    .url("http://" + imageProperties.getHost() + ":" + imageProperties.getPort())
                    .post(body)
                    .build();
            try{
                response = httpClient.newCall(request).execute();
                result = response.body().string();
            }catch( IOException e){
                resultJson.put("code","01");
                resultJson.put("error_msg","璋冪敤OCR搴曞眰鎺ュ彛澶辫触");
            }
            if (result.contains("error")){

                JSONObject  j=JSONObject.parseObject(result);
                logger.warn("generalOcr error----code:"+ j.getString("error_code")+",msg:"+j.getString("error_msg"));
                resultJson.put("error_code",j.getString("error_code"));
                resultJson.put("error_msg",j.getString("error_msg"));
            }else {
                resultJson =JSONObject.parseObject(result);
            }
        }
        logger.warn("generalOcr--------end");
        return resultJson.toJSONString();
    }

    @EsbMethod(esbServiceId = "com.sinolife.ocr.esb.incision")
    public String ocr(Map<String,String> parms) {
        JSONObject resultJson = new JSONObject();
        JSONArray arr = new JSONArray();
        JSONObject j = new JSONObject();
        long start = System.currentTimeMillis();
        try {
            //鍒ゆ柇鏄惁鏄崟寮犺〃鍗曪紝濡傛灉鏄〃鍗曪紝鑾峰彇琛ㄥ崟绫诲瀷  0430
            String tableType = this.classify.ify(parms.get("image"));
            //濡傛灉涓嶆槸琛ㄥ崟锛岃繘琛屽垎鍓叉搷浣�
            if (tableType==null){
                String http = http(parms.get("image"));
                if (http.contains("error")){
                    j=JSONObject.parseObject(http);
                    resultJson.put("error_code",j.getString("error_code"));
                    resultJson.put("error_msg",j.getString("error_msg"));
                }else {
                    j=JSONObject.parseObject(http);
                    if (j!=null){
                        Map<String, String> incision = incision(j, parms.get("image"));
                        Iterator<String> iterator = incision.keySet().iterator();
                        while (iterator.hasNext()) {
                            JSONObject object=new JSONObject();
                            String base = iterator.next();
                            String type = incision.get(base);
                            if (type.contains("vat")){
                                JSONObject vatHttp = tickeDemo.vatHttp(base);
                                if (vatHttp!=null  && !vatHttp.toJSONString().contains("error")){
                                    String types = vatHttp.getString("type");
                                    object.put("image",base);
                                    object.put("type",types);
                                    arr.add(object);
                                    logger.warn("鍗庝负鏂瑰垎绫绘帴鍙ｈ瘑鍒嚭绫诲瀷");
                                }
                            }else if (type.contains("other")) {
                                String otherType = classify.other(base);
                                object.put("image",base);
                                object.put("type",otherType);
                                arr.add(object);
                            }else if (type.contains("quota")){
                                String quotatype = classify.quota(base);
                                object.put("image",base);
                                object.put("type",quotatype);
                                arr.add(object);
                            }else {
                                object.put("image",base);
                                object.put("type",type);
                                arr.add(object);
                                logger.warn("鍗庝负鏂瑰垎绫绘帴鍙ｈ瘑鍒嚭绫诲瀷");
                            }
                        }
                        resultJson.put("code",200);
                        resultJson.put("msg","success");
                    }
                }
            }else {
                JSONObject object=new JSONObject();
                object.put("type",tableType);
                object.put("image",parms.get("image"));
                arr.add(object);
                resultJson.put("code",200);
                resultJson.put("msg","success");
            }
            resultJson.put("item",arr);
        } catch (Exception e) {
            logger.error(e.getMessage());
            resultJson.put("error_code",403);
            resultJson.put("error_msg",e.getMessage());
        }
        logger.warn(parms.get("fileName")+"鍒嗙被鎵�鑰楁椂:"+(System.currentTimeMillis()-start));
        return resultJson.toJSONString();
    }

    /**
     * 鍒嗗壊鎿嶄綔
     * @param jsonObject
     * @param base
     * @return
     */
    private Map<String, String> incision(JSONObject jsonObject,String base) {
        Map<String,String> maps=new HashMap<>();
        long start = System.currentTimeMillis();
        try {
            JSONArray result = jsonObject.getJSONArray("result");
            if (result!=null){
                for (int i = 0; i < result.size(); i++) {
                    JSONObject object = result.getJSONObject(i);
                    Object[] locations = object.getJSONArray("location").toArray();
                    JSONArray first = JSONArray.parseArray(locations[0].toString());
                    JSONArray san = JSONArray.parseArray(locations[2].toString());
                    BufferedImage bufferedImage = Base64ImageUtil.Base64ToBufferedImage(base);
                    BufferedImage subImage = bufferedImage.getSubimage(
                            Integer.parseInt(first.getString(0)),
                            Integer.parseInt(first.getString(1)),
                            Integer.parseInt(san.getString(0)) - Integer.parseInt(first.getString(0)),
                            Integer.parseInt(san.getString(1)) - Integer.parseInt(first.getString(1))
                    );
                    String type = object.getString("type");
                    String subEncoding = Base64ImageUtil.BufferedImageToBase(subImage);
                    maps.put(subEncoding,type);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.warn("鍒嗗壊鍥剧墖鎿嶄綔鑰楁椂锛�"+(System.currentTimeMillis()-start));
        return  maps;
    }
    /**
     * 鍗庝负鍒嗗壊api
     * @param base64string
     * @return
     */
    private static final MediaType TYPE = MediaType.parse("application/json; charset=utf-8");
    private String http(String base64string) {
        String result = null;
        Response response = null;
        try {
            if (base64string!=null){
                JSONObject json = new JSONObject();
                json.put("image", base64string);
                RequestBody body = RequestBody.create(TYPE, json.toJSONString());
                OkHttpClient httpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(60 * 1000, TimeUnit.SECONDS)
                        .readTimeout(5 * 60 * 1000, TimeUnit.SECONDS)
                        .writeTimeout(5 * 60 * 1000,TimeUnit.SECONDS).build();
                Request request = new Request.Builder()
                        .url("http://"+cfgProperties.getHost()+":"+cfgProperties.getPort())
                        .post(body)
                        .build();
                response = httpClient.newCall(request).execute();
                result = response.body().string();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (response != null) {
                response.close();
            }
            if (result != null){
                logger.warn("璋冪敤鍗庝负鍒嗙被鎺ュ彛鎴愬姛");
            }
        }
        return result;
    }
}
