package com.pangu.crawler.transfer.enums;

import java.util.*;

public enum SzEnum{


    // 增值税一般纳税人
    zzsybnsr("zzsybnsr","增值税一般纳税人"),
    // 附加税
    fjs("fjs", "附加税"),

    // 增值税小规模查账征收
//    zzsxgmczzs("zzsxgmczzs", "增值税小规模查账征收"),
    // 附加税小规模
    //fjsxgm("fjsxgm", "附加税小规模"),
    // 增值税小规模核定征收
//    zzsxgmhdzs("zzsxgmhdzs", "增值税小规模核定征收"),
    // 企业所得税A月季报
    qysdsayjb("qysdsayjb", "企业所得税A月季报"),
    // 财务报表（企业会计制度）
    cwbbqykjzd("cwbbqykjzd", "财务报表（企业会计制度）"),
    // 财务报表（企业会计准则（适用于已执行新金融准则、新收入准则和新租赁准则））
    cwbbqykjzzyzx("cwbbqykjzzyzx", "财务报表(企业会计准则已执行)"),
    // 财务报表（企业会计准则（适用于未执行新金融准则、新收入准则和新租赁准则））
    cwbbqykjzzwzx("cwbbqykjzzwzx","财务报表(企业会计准则未执行)");
    /*  // 财务报表（小企业会计准则）
    cwbbxqykjzz("cwbbxqykjzz", "财务报表（小企业会计准则）"),
  // 印花税
    yhsyspzdraqhzjn("yhsyspzdraqhzjn", "印花税"),
    // 文化建设费
    whsyjsf("whsyjsf", "文化建设费"),
    //印花税
    yhs("yhs", "印花税"),
    //通用申报（工会经费）
    tysb("tysb", "通用申报（工会经费）");*/

    private String code;
    private String name;


    SzEnum(String code, String name) {
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
        return "SzEnum{" +
                "code='" + code + '\'' +
                ", ame='" + name + '\'' +
                '}';
    }


    public static Map<String,String> getMapSz(){
        Map<String,String> map  = new HashMap<String,String>();
        for(SzEnum e: SzEnum.values()){
            map.put(e.getCode(),e.getName());
        }
        return map;
    }

    public static Map<String,String> getMapSz(String[] szs){
        Map<String,String> map  = new HashMap<String,String>();
        for(SzEnum e: SzEnum.values()){
            if(Arrays.asList(szs).contains((e.getCode()))){
                map.put(e.getCode(),e.getName());
            }
        }
        return map;
    }
}
