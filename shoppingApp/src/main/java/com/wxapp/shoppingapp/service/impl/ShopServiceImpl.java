package com.wxapp.shoppingapp.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wxapp.shoppingapp.entiy.Categories;
import com.wxapp.shoppingapp.entiy.Catitems;
import com.wxapp.shoppingapp.entiy.CommonsName;
import com.wxapp.shoppingapp.entiy.FloorData;
import com.wxapp.shoppingapp.mapper.ShopMapper;
import com.wxapp.shoppingapp.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {
    @Autowired
    ShopMapper shopMapper;


    @Override
    public JSONArray getFloordata() {
        List<FloorData> floorData = shopMapper.getFloorData();
        List<FloorData> floorTitle = shopMapper.getFloorTitle();
        JSONArray jsonArray = new JSONArray();

        for (int j = 0; j < 3; j++) {

            JSONObject jsonObject = new JSONObject();
            ArrayList arrayList = new ArrayList();
            jsonObject.put("floor_title",floorTitle.get(j));
            for (int i = 0; i < 5;) {
                if (arrayList.size()<5){
                    FloorData data = floorData.get(i);
                    arrayList.add(data);
                    floorData.remove(floorData.get(i));
                }else {
                    break;
                }
            }
            jsonObject.put("product_list",arrayList);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JSONArray getCatitems() {
        JSONArray jsonArray = new JSONArray();

        Catitems catitems = shopMapper.getCatitems();

        jsonArray.add(catitems);
        List<CommonsName> commonsName = shopMapper.getCommonsName();

        jsonArray.addAll(commonsName);
        return jsonArray;
    }

    @Override
    public List<Categories> getCategories() {
        List<Categories> categories = shopMapper.getCategories();

        for (int i = 0; i < categories.size(); i++) {
            JSONArray jsonArray = new JSONArray();
            if (i <2) {
                jsonArray.add(categories.get(i + 1));
                categories.get(i).setChildren(jsonArray);

            } else {
                categories.remove(1);
                categories.remove(1);
            }
        }
        return categories;
    }
}
