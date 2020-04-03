package com.sinolife;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class MyTest {

    @Test
    void JsonArrayForEache(){
        //对json数组进行遍历
        JSONObject json = new JSONObject();
        JSONObject json1 = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        json.put("id","1");
        json.put("name","张三");
        json.put("pwd","123456");

        json1.put("id","2");
        json1.put("name","李四");
        json1.put("pwd","456");
        jsonArray.add(json);
        jsonArray.add(json1);
        for(int i=0;i<jsonArray.size();i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            System.out.println(jsonObject); }


    }



    @Test
     void ArraySplit(){

        String x = "{jsonString:{\"name\":\"admin\",\"users\":[{\"age\":35,\"name\":\"guest\"},{\"age\":0,\"name\":\"root\"}]}}";

//        JSONArray array = new JSONArray();

        JSONObject jsonObject = JSONObject.parseObject(x);
        System.out.println(jsonObject);
        JSONObject jsonString = jsonObject.getJSONObject("jsonString");
        System.out.println(jsonString);
        JSONArray users = jsonString.getJSONArray("users");
        System.out.println(users);
        JSONObject jsonObject1 = users.getJSONObject(0);
        System.out.println(jsonObject1);

        System.out.println(jsonObject1.getString("name"));

//        array.add(jsonString1);
//        System.out.println(array);
//        for (int i=0;i<array.size();i++){
//            JSONObject jsons = array.getJSONObject(i);
//            System.out.println(jsons);
//        }
//





    }




}

