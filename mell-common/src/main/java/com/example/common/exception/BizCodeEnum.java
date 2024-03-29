package com.example.common.exception;


public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VALID_EXCEPTION(10001,"参数格式校验失败"),
    SMS_CODE_EXCEPTION(10002,"短信验证获取频率过高，请稍后再试"),

    PRODUCT_EXCEPTION(11000,"商品上架异常");



    private int code;
    private String msg;
    BizCodeEnum(int code,String msg){
        this.code=code;
        this.msg=msg;
    }
    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
