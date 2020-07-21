package com.wxapp.shoppingapp.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wxapp.shoppingapp.commons.Result;
import com.wxapp.shoppingapp.entiy.*;
import com.wxapp.shoppingapp.mapper.ShopMapper;
import com.wxapp.shoppingapp.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.lang.StringEscapeUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController

public class ShopController {
    @Autowired
    ShopMapper shopMapper;

    @Autowired
    ShopService shopService;

    @GetMapping("/get/swiper")
    public Result getSwiper(){

        List<Swiper> swiper = shopMapper.getSwiper();
        return Result.succ(swiper,"获取成功",200);
    }


    @GetMapping("/get/catitems")
    public Result getCatitems(){
        JSONArray jsonArray = shopService.getCatitems();
        return Result.succ(jsonArray,"成功",200);
    }

    @GetMapping("/get/floordata")
    public Result getFloorData(){

        JSONArray jsonArray = shopService.getFloordata();
        return Result.succ(jsonArray,"成功",200);

    }
    @GetMapping("/get/categories")
    public Result getCagtegories() {
        List<Categories> categories = shopService.getCategories();
        return Result.succ(categories, "成功", 200);

    }
}
