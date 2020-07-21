package com.wxapp.shoppingapp.entiy;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Swiper {
    private String image_src;
    private String open_type;
    private int goods_id;
    private String navigator_url;



}
