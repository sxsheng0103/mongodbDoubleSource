package com.pangu.crawler.transfer.enums;

import java.util.LinkedHashMap;

/**
 * @Author sheng.ding
 * @Date 2020/8/13 11:39
 * @Version 1.0
 **/
public enum SchedualEnum {

    fixrate("1",1,"固定频率执行"),
    fixtime("2",2,"固定时间");

    private String item;
    private int value;
    private String info;
    SchedualEnum(String item,int value,String info){
        this.item = item;
        this.value = value;
        this.info = info;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }}
