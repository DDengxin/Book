package com.wxapp.shoppingapp.mapper;

import com.wxapp.shoppingapp.entiy.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface ShopMapper {
   List<Swiper> getSwiper();

   Catitems getCatitems();

   List<CommonsName> getCommonsName();

   List<FloorData> getFloorData();

   List<FloorData> getFloorTitle();

   List<Categories> getCategories();

}
