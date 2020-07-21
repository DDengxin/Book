package com.wxapp.shoppingapp.entiy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Catitems implements Serializable {
    private String name;
    private String image_src;
    private String open_type;
    private String navigator_url;
}
