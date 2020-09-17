package com.pangu.crawler.transfer.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum AreaEnum{
    guizhou("guizhou", "贵州"),
    fujian("fujian", "福建"),
    beijing("beijing", "北京"),
    gansu("gansu", "甘肃"),
    zhejiang("zhejiang", "浙江"),
    yunnan("yunnan", "云南"),
    hainan("hainan", "海南"),
    sichuan("sichuan", "四川"),
    ningxia("ningxia", "宁夏"),
    jilin("jilin", "吉林"),
    liaoning("liaoning", "辽宁"),
    qinghai("qinghai", "青海"),
    shaanxi("shaanxi", "陕西"),
    jiangsu("jiangsu", "江苏"),
    shanxi("shanxi", "山西"),
    chongqing("chongqing", "重庆"),
    jiangxi("jiangxi", "江西"),
    shandong("shandong", "山东"),
    guangdong("guangdong", "广东"),
    hunan("hunan", "湖南"),
    anhui("anhui", "安徽"),
    qingdao("qingdao", "青岛"),
    xiamen("xiamen", "厦门"),
    dalian("dalian", "大连"),
    ningbo("ningbo", "宁波"),
    shenzhen("shenzhen", "深圳"),
    neimeng("neimeng", "内蒙"),
    shanghai("shanghai", "上海"),
    xinjiang("xinjiang", "新疆"),
    xizang("xizang", "西藏"),
    henan("henan", "河南"),
    guangxi("guangxi", "广西"),
    hebei("hebei", "河北"),
    heilongjiang("heilongjiang", "黑龙江"),
    hubei("hubei", "湖北"),
    tianjin("tianjin", "天津");

    private String code;

    private String name;

    AreaEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "AreaEnum{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public static Map<String,String> getMapDq(){
        Map<String,String> map  = new HashMap<String,String>();
        for(AreaEnum e: AreaEnum.values()){
            map.put(e.getCode(),e.getName());
        }
        return map;
    }

    public static Map<String,String> getMapDq(String[] codes){
        Map<String,String> map  = new HashMap<String,String>();
        for(AreaEnum e: AreaEnum.values()){
            if(Arrays.asList(codes).contains(e.getCode())){
                map.put(e.getCode(),e.getName());
            }
        }
        return map;
    }
}
