package com.wxapp.shoppingapp.service;

import com.alibaba.fastjson.JSONArray;
import com.wxapp.shoppingapp.entiy.Categories;

import java.util.List;

public interface ShopService {
    JSONArray getFloordata();
    JSONArray getCatitems();
    List<Categories> getCategories();
}
