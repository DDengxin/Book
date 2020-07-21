package com.wxapp.shoppingapp.entiy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FloorTitle implements Serializable {
    private String name;
    private String image_src;

}
