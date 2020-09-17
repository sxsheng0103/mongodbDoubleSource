package com.pangu.crawler.transfer.enums;

import java.util.*;



public enum FormEnum{

    // 附加税
    fjs("fjs",FormMap.fjsforms),
    // 增值税一般纳税人
    zzsybnsr("zzsybnsr",FormMap.zzsybnsrforms),
    // 企业所得税A月季报
    qysdsayjb("qysdsayjb", FormMap.qysdsayjbforms),
    // 财务报表（企业会计制度）
    cwbbqykjzd("cwbbqykjzd", FormMap.cwbbqykjzdforms),
    // 财务报表（企业会计准则（适用于已执行新金融准则、新收入准则和新租赁准则））
    cwbbqykjzzyzx("cwbbqykjzzyzx", FormMap.cwbbqykjzzyzxforms),
    // 财务报表（企业会计准则（适用于未执行新金融准则、新收入准则和新租赁准则））
    cwbbqykjzzwzx("cwbbqykjzzwzx",FormMap.cwbbqykjzzwzxforms);
    private String szcode;
    private  LinkedHashMap<String,String>  forms;

    public String getSzCode() {
        return szcode;
    }

    public LinkedHashMap<String,String> getForms() {
        return forms;
    }


    FormEnum(String szcode, LinkedHashMap<String,String>  forms) {
        this.szcode = szcode;
        this.forms = forms;
    }

    public static synchronized FormEnum getMapForms(String szcode){
        for(FormEnum formEnum:FormEnum.values()){
            if(formEnum.getSzCode().equals(szcode)){
                 FormMap.initFormsBySzcode(formEnum);
                return formEnum;
            }
            continue;
        }
        return null;
    }
}
