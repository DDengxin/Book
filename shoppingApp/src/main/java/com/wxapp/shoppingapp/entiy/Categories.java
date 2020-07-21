package com.wxapp.shoppingapp.entiy;

import com.wxapp.shoppingapp.commons.Result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class Categories implements Serializable {
    private int cat_id;
    private String cat_name;
    private int cat_pid;
    private int cat_level;
    private boolean cat_deleted;
    private String cat_icon;
    private Object children;

    public static Categories getChildren(Categories categories,Object children){
        categories.setChildren(children);
        return categories;

    }
}
