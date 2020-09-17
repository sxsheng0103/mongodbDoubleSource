package com.pangu.crawler.transfer.enums;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author sheng.ding
 * @Date 2020/7/29 12:20
 * @Version 1.0
 **/
public class FormMap {
    //附加税
    public static final String fjs_cswhjss = "cswhjss";
    public static final String fjs_jyffj = "jyffj";
    public static final String fjs_dfjyfj = "dfjyfj";
    public static final String fjs_sljszxsy = "sljszxsy";
    public static final String fjs_cswhjssxfsfj = "cswhjssxfsfj";
    public static final String fjs_jyffjxfsfj = "jyffjxfsfj";
    public static final String fjs_dfjyfjxfsfj = "dfjyfjxfsfj";
    public static final String fjs_sbxxGridlbVO = "sbxxGridlbVO";
    //增值税
    public static final String zzs_zzssyyybnsrzb = "zzssyyybnsrzb";
    public static final String zzs_zzssyyybnsr01bqxsqkmxb = "zzssyyybnsr01bqxsqkmxb";
    public static final String zzs_zzssyyybnsr02bqjxsemxb = "zzssyyybnsr02bqjxsemxb";
    public static final String zzs_zzssyyybnsr03ysfwkcxmmx = "zzssyyybnsr03ysfwkcxmmx";
    public static final String zzs_zzssyyybnsr04bqjxsemxb = "zzssyyybnsr04bqjxsemxb";
    public static final String zzs_zzsjmssbmxb = "zzsjmssbmxb";
    public static final String zzs_dxqyfzjgzzshznsxxcdd = "dxqyfzjgzzshznsxxcdd";
    public static final String zzs_qtkspzmxb = "qtkspzmxb";
    public static final String zzs_hznsqyzzsfpb = "hznsqyzzsfpb";
    public static final String zzs_dkdjsstyjksdkqd = "dkdjsstyjksdkqd";
    public static final String zzs_ysfwkcxmqd = "ysfwkcxmqd";
    public static final String zzs_lhfjs = "lhfjs";
    public static final String zzs_dlqyzzsxxsehjxsecdd = "dlqyzzsxxsehjxsecdd";
    public static final String zzs_hznsqytycdd = "hznsqytycdd";
    public static final String zzs_zzsybnsrfjs = "zzsybnsr&fjs";
    //企业所得税A类
    public static final String asss_a200000Ywbd = "a200000Ywbd";
    public static final String asss_a201010Ywbd = "a201010Ywbd";
    public static final String asss_a201020Ywbd = "a201020Ywbd";
    public static final String asss_a201030Ywbd = "a201030Ywbd";
    public static final String asss_a202000Ywbd = "a202000Ywbd";
    public static final String asss_jmqycgwgqyxxbgb = "jmqycgwgqyxxbgb";
    public static final String asss_jscgtzrgqysdsdynsbab = "jscgtzrgqysdsdynsbab";

    //企业会计制度
    public static final String cwbb_lrbData = "lrbData";
    public static final String cwbb_xjllData = "xjllData";
    public static final String cwbb_zcfzData = "zcfzData";
    public static final String cwbb_syzqyData = "syzqyData";
    public static final String cwbb_files = "files";


    public static  LinkedHashMap<String,String> fjsforms = new LinkedHashMap<String,String>();
    public static  LinkedHashMap<String,String> zzsybnsrforms = new LinkedHashMap<String,String>();
    public static  LinkedHashMap<String,String> qysdsayjbforms = new LinkedHashMap<String,String>();
    public static  LinkedHashMap<String,String> cwbbqykjzdforms = new LinkedHashMap<String,String>();
    public static  LinkedHashMap<String,String> cwbbqykjzzyzxforms = new LinkedHashMap<String,String>();
    public static  LinkedHashMap<String,String> cwbbqykjzzwzxforms = new LinkedHashMap<String,String>();


    public static  LinkedHashMap<String,String>  initFormsBySzcode(FormEnum formEnum){
        if(formEnum.getForms() == null||formEnum.getForms().size()<=0){
            if(formEnum.getSzCode().equals("fjs")){
                formEnum.getForms().put(fjs_cswhjss,fjs_cswhjss);
                formEnum.getForms().put(fjs_jyffj,fjs_jyffj);
                formEnum.getForms().put(fjs_dfjyfj,fjs_dfjyfj);
                formEnum.getForms().put(fjs_sljszxsy,fjs_sljszxsy);
                formEnum.getForms().put(fjs_cswhjssxfsfj,fjs_cswhjssxfsfj);
                formEnum.getForms().put(fjs_jyffjxfsfj,fjs_jyffjxfsfj);
                formEnum.getForms().put(fjs_dfjyfjxfsfj,fjs_dfjyfjxfsfj);
                formEnum.getForms().put(fjs_sbxxGridlbVO,fjs_sbxxGridlbVO);
            }else if(formEnum.getSzCode().equals("zzsybnsr")){
                formEnum.getForms().put(zzs_zzssyyybnsrzb,zzs_zzssyyybnsrzb);
                formEnum.getForms().put(zzs_zzssyyybnsr01bqxsqkmxb,zzs_zzssyyybnsr01bqxsqkmxb);
                formEnum.getForms().put(zzs_zzssyyybnsr02bqjxsemxb,zzs_zzssyyybnsr02bqjxsemxb);
                formEnum.getForms().put(zzs_zzssyyybnsr03ysfwkcxmmx,zzs_zzssyyybnsr03ysfwkcxmmx);
                formEnum.getForms().put(zzs_zzssyyybnsr04bqjxsemxb,zzs_zzssyyybnsr04bqjxsemxb);
                formEnum.getForms().put(zzs_zzsjmssbmxb,zzs_zzsjmssbmxb);
                formEnum.getForms().put(zzs_dxqyfzjgzzshznsxxcdd,zzs_dxqyfzjgzzshznsxxcdd);
                formEnum.getForms().put(zzs_qtkspzmxb,zzs_qtkspzmxb);
                formEnum.getForms().put(zzs_hznsqyzzsfpb,zzs_hznsqyzzsfpb);
                formEnum.getForms().put(zzs_dkdjsstyjksdkqd,zzs_dkdjsstyjksdkqd);
                formEnum.getForms().put(zzs_ysfwkcxmqd,zzs_ysfwkcxmqd);
                formEnum.getForms().put(zzs_lhfjs,zzs_lhfjs);
                formEnum.getForms().put(zzs_dlqyzzsxxsehjxsecdd,zzs_dlqyzzsxxsehjxsecdd);
                formEnum.getForms().put(zzs_hznsqytycdd,zzs_hznsqytycdd);
                formEnum.getForms().put(zzs_zzsybnsrfjs,zzs_zzsybnsrfjs);
            }else if(formEnum.getSzCode().equals("qysdsayjb")){
                formEnum.getForms().put(asss_a200000Ywbd,asss_a200000Ywbd);
                formEnum.getForms().put(asss_a201010Ywbd,asss_a201010Ywbd);
                formEnum.getForms().put(asss_a201020Ywbd,asss_a201020Ywbd);
                formEnum.getForms().put(asss_a201030Ywbd,asss_a201030Ywbd);
                formEnum.getForms().put(asss_a202000Ywbd,asss_a202000Ywbd);
                formEnum.getForms().put(asss_jmqycgwgqyxxbgb,asss_jmqycgwgqyxxbgb);
                formEnum.getForms().put(asss_jscgtzrgqysdsdynsbab,asss_jscgtzrgqysdsdynsbab);
            }else if(formEnum.getSzCode().equals("cwbbqykjzd")){
                formEnum.getForms().put(cwbb_lrbData,cwbb_lrbData);
                formEnum.getForms().put(cwbb_xjllData,cwbb_xjllData);
                formEnum.getForms().put(cwbb_zcfzData,cwbb_zcfzData);
                formEnum.getForms().put(cwbb_syzqyData,cwbb_syzqyData);
                formEnum.getForms().put(cwbb_files,cwbb_files);
            }else if(formEnum.getSzCode().equals("cwbbqykjzzyzx")){
                formEnum.getForms().put(cwbb_lrbData,cwbb_lrbData);
                formEnum.getForms().put(cwbb_xjllData,cwbb_xjllData);
                formEnum.getForms().put(cwbb_zcfzData,cwbb_zcfzData);
                formEnum.getForms().put(cwbb_syzqyData,cwbb_syzqyData);
                formEnum.getForms().put(cwbb_files,cwbb_files);
            }else if(formEnum.getSzCode().equals("cwbbqykjzzwzx")){
                formEnum.getForms().put(cwbb_lrbData,cwbb_lrbData);
                formEnum.getForms().put(cwbb_xjllData,cwbb_xjllData);
                formEnum.getForms().put(cwbb_zcfzData,cwbb_zcfzData);
                formEnum.getForms().put(cwbb_syzqyData,cwbb_syzqyData);
                formEnum.getForms().put(cwbb_files,cwbb_files);
            }
            return formEnum.getForms();
        }else{
            return formEnum.getForms();
        }
    }
}
