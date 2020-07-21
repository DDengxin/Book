package com.wxapp.shoppingapp.commons;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class Result implements Serializable {


    private Object message;
    private Object meta;

    public static Result succ(Object message, String msg, int status){
        Result result = new Result();
        HashMap map = new HashMap<>();
        map.put("msg",msg);
        map.put("status",status);
        result.setMeta(map);
        result.setMessage(message);
        return result;
    }
}
