package com.wxapp.shoppingapp.entiy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FloorData implements Serializable {
    private String name;
    private String image_src;
    private String image_width;
    private String open_type;
    private String navigator_url;


}
