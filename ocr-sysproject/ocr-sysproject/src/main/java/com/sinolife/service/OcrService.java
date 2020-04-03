package com.sinolife.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sinolife.interfaces.TableDemo;
import com.sinolife.interfaces.TickeDemo;
import com.sinolife.properties.CfgProperties;
import com.sinolife.util.Base64ImageUtil;
//import com.sinolife.sf.esb.EsbMethod;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
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
    private Classify classify;

    @Autowired
    private TableDemo tableDemo;

    @Autowired
    private TickeDemo tickeDemo;


//    @EsbMethod(esbServiceId = "com.sinolife.ocr.esb.incision")
    public String ocr(Map<String,String> parms) {
        JSONObject resultJson = new JSONObject();
        JSONArray arr = new JSONArray();
        JSONObject j = new JSONObject();
        long start = System.currentTimeMillis();
        try {
            //判断是否是单张表单，如果是表单，获取表单类型  0430
            String tableType = this.classify.ify(parms.get("image"));
            //如果不是表单，进行分割操作
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
                                    logger.warn("华为方分类接口识别出类型");
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
                                logger.warn("华为方分类接口识别出类型");
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
            e.printStackTrace();
            resultJson.put("error_code",403);
            resultJson.put("error_msg",e.getMessage());
        }
        logger.warn(parms.get("fileName")+":"+(System.currentTimeMillis()-start));
        return resultJson.toJSONString();
    }

    /**
     * 分割操作
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
            e.printStackTrace();
        }
        logger.info("分割图片操作耗时："+(System.currentTimeMillis()-start));
        return  maps;
    }
     /**
     * 华为分割api
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
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        logger.info("OcrService http success");
        return result;
    }
}
