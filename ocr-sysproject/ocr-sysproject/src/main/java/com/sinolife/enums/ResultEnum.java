package com.sinolife.enums;

public enum ResultEnum {
    CODE200("200"), CODE403("403"),SUCCESS("success"),FAIL("图片base64为空");


    private String result;

    ResultEnum(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
