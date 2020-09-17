package com.pangu.crawler.transfer.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.pangu.crawler.framework.utils.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author sheng.ding
 * @Date 2020/8/21 17:27
 * @Version 1.0
 **/
public class RuleDataFilterUtils {
    /**
     * @param rules
     * @return
     * @description 获取有效的json报文
     */
    public static Object getValidJsonBefore(List<String> rules) {
        //重组josn报文json,这里只有有效值
        String key = "";
        String value = "";
        String temprule = "";
        Object resultData = new JSONObject();
        for (String rule : rules) {
            if (rule.indexOf("//") != -1) {
                temprule = rule.substring(0, rule.indexOf("//"));
            } else {
                temprule = rule;
            }
            key = temprule.substring(0, temprule.indexOf("="));
            value = temprule.substring(temprule.indexOf("=") + 1);
            JSONPath.set(resultData, key, value);
        }
        return resultData;
    }

    private static Pattern pattern = Pattern.compile("\\s*|\t|\r|\n");

    /**
     * @param rules
     * @return
     * @description 规则分割规则方案
     */
    public static Map<String, List<String>> splitGroupByTableRules(List<String> rules) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        List<String> listRules = new ArrayList<String>();
        String prefix = "";
        Collections.reverse(rules);
        for (String rule : rules) {
            Matcher m = pattern.matcher(rule);
            rule = m.replaceAll("");
            if (rule == null || StringUtils.isEmpty(rule) || rule.startsWith("//") || rule.startsWith("## version") || (rule.startsWith("##") && rule.contains("version"))) {
                continue;
            }
            if (rule.indexOf("//") != -1) {
                if (rule.substring(0, rule.indexOf("//")).replaceAll("\\s*", "").endsWith("--")) {
                    continue;
                }
            } else if (rule.replaceAll("\\s*", "").endsWith("--")) {
                continue;
            }
            if (rule.startsWith("prefix=") && rule.indexOf("//") != -1) {
                String ruleconfig = rule.substring(0, rule.indexOf("//"));
                prefix = ruleconfig.substring(ruleconfig.indexOf("=") + 1);
            } else if (rule.startsWith("prefix=")) {
                prefix = rule.substring(rule.indexOf("=") + 1);
            }
            if (!rule.startsWith("prefix=")) {
                listRules.add(rule);
            }
            if (rule.startsWith("prefix=")) {
                map.put(prefix, listRules);
                listRules = new ArrayList<String>();
            }
        }
        if (map.isEmpty()) {
            map.put(" ", listRules);
        }
        return map;
    }

    /**
     * @param rules
     * @return
     * @description 规则分割规则方案(自适应组合识别行之后按照索引找列)
     */
    public static Map<String, List<String>> splitGroupMatchColsByTableRules(List<String> rules) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        List<String> listRules = new ArrayList<String>();
        String prefix = "";
        Collections.reverse(rules);
        for (String rule : rules) {
            if (StringUtils.isEmpty(rule) || rule.startsWith("//") || rule.replace(" ", "").startsWith("##version")|| (rule.startsWith("##") && rule.contains("version"))) {
                continue;
            }
            if (rule.indexOf("//") != -1) {
                if (rule.substring(0, rule.indexOf("//")).replaceAll("\\s*", "").endsWith("--")) {
                    continue;
                }
            } else if (rule.replaceAll("\\s*", "").endsWith("--")) {
                continue;
            }
            if (rule.startsWith("matchtrprefix=") && rule.indexOf("//") != -1) {
                String ruleconfig = rule.substring(0, rule.indexOf("//"));
                prefix = ruleconfig.substring(ruleconfig.indexOf("=") + 1);
            } else if (rule.startsWith("matchtrprefix=")) {
                prefix = rule.substring(rule.indexOf("=") + 1);
            }
            if (!rule.startsWith("matchtrprefix=")) {
                listRules.add(rule);
            }
            if (rule.startsWith("matchtrprefix=")) {
                map.put(prefix, listRules);
                listRules = new ArrayList<String>();
            }
        }
        if (map.isEmpty()) {
            map.put(" ", listRules);
        }
        return map;
    }




    /**
     * @param rules
     * @return
     * @description html固定行+动态行增加数据获取实现
     */
    public static Map<String,Object> splitGroupByFormElement(List<String> rules) {
        Map<String,Object> result = new HashMap<String,Object>();
        Map<String,String> rulemap = new HashMap<String,String>();
        List<Map<String,String>> listRuleMaps = new ArrayList<Map<String,String>>();
        String prefix = "";
        CountDownLatch cdl= new CountDownLatch(6);
        StringBuilder errorgroup = new StringBuilder("");
        String ruletag = null;
        for (String rule : rules) {
            Matcher m = pattern.matcher(rule);
            rule = m.replaceAll("");
            if (rule == null || StringUtils.isEmpty(rule) || rule.startsWith("//") || rule.startsWith("## version") || (rule.startsWith("##") && rule.contains("version"))) {
                continue;
            }
            if (rule.indexOf("//") != -1) {
                if (rule.substring(0, rule.indexOf("//")).replaceAll("\\s*", "").endsWith("--")) {
                    continue;
                }
            }
            if (rule.replaceAll("\\s*", "").endsWith("--")||rule.replaceAll("\\s*", "").startsWith("groupstart")) {
                rulemap = new HashMap<String,String>();
                cdl= new CountDownLatch(6);
                ruletag= rule;
                continue;
            }else if(rule.replaceAll("\\s*", "").startsWith("groupend")){
                //结束一组规则生成组信息
                if(cdl.getCount()!=0){
                    errorgroup.append(ruletag+"转换文件组信息有误!组信息不全;");
                }else{
                    listRuleMaps.add(rulemap);
                }
                continue;
            }
            if(rule.indexOf("//")!=-1){
                rule=rule.substring(0,rule.indexOf("//"));
            }
            //每一组包含reportpath,documentpath,fixedtr,dynamictrstartpos,dynamictrreleations,dynamictrendpos,groupend1信息
            String[] entry = new String[2];
            if(rule.indexOf("=")!=-1){
                entry[0] = rule.split("=")[0];
                if(rule.split("=").length==1){
                    entry[1] = "";
                }else{
                    entry[1] = rule.split("=")[1];
                }
            }
            if(entry[0]!=null&&entry[0].equals("reportpath")){
                cdl.countDown();
                rulemap.put(entry[0],entry[1]);
            }else if(entry[0]!=null&&entry[0].equals("documentpath")){
                cdl.countDown();
                rulemap.put(entry[0],entry[1]);
            }else if(entry[0]!=null&&entry[0].equals("fixedtoptr")){
                cdl.countDown();
                rulemap.put(entry[0],entry[1]);
            }else if(entry[0]!=null&&entry[0].equals("fixedbottomtr")){
                cdl.countDown();
                rulemap.put(entry[0],entry[1]);
            }else if(entry[0]!=null&&entry[0].equals("dynamictrstartpos")){
                cdl.countDown();
                rulemap.put(entry[0],entry[1]);
            }else if(entry[0]!=null&&entry[0].equals("dynamictrreleations")){
                cdl.countDown();
                rulemap.put(entry[0],entry[1]);
            }else if(entry[0]!=null&&entry[0].equals("dynamictrendpos")){
                cdl.countDown();
                rulemap.put(entry[0],entry[1]);
            }
        }
        result.put("data",listRuleMaps);
        result.put("error",errorgroup.toString());
        return result;
    }
    /**
     * @description 查看tr是否符合需要查找的行
     * @param tr
     * @param marktds
     * @return
     */
    public static boolean checkValidTrElement(Element tr, String marktds){
        int index = -1;String tagValue = "";
        Elements tds = tr.getElementsByTag("td");
        for(String tdmark :marktds.split(",")){
            index = Integer.valueOf(tdmark.split("-")[0]);
            tagValue = tdmark.split("-")[1];

            if(tds.size()>=index){
                if(tds.get(index-1).wholeText().equals(tagValue)){
                }else{
                    return false;
                    //没有标识列不相同
                }
            }else{
                return false;
                //没有这个标识列索引
            }
        }
        return true;
    }


    /**
     * @param rules
     * @return
     * @description html通用分割器，规划中支持规则文件中定义starttransfertype endtransfertype作为分割后的标识
     * 普通类型为 nomallogical 直接转化所有规则
     * 布尔类型为 booleanlogical  特定处理选择框值
     * 动态匹配行获取 dynamicmatchrowlogical
     * 动态匹配多选项 dynamiclmatchmutilogical   对于企业所得税预缴方式等使用
     */
    public static Map<String,Object> dynamicSplitGroupHtmlRules(List<String> rules) {
        Queue queuerule = new ConcurrentLinkedQueue();
        Map<String,Object> result = new HashMap<String,Object>();
        Map<String,Object> rulemap = new HashMap<String,Object>();
        Map<String,Object> tempmap = new HashMap<String,Object>();
        List<Map<String,String>> listRuleMaps = new ArrayList<Map<String,String>>();
        List<String> listRules = new ArrayList<String>();
        String prefix = "";
        StringBuilder errorgroup = new StringBuilder("");
        String ruletag = "";
        String transfertype = "";
        CountDownLatch cdl= new CountDownLatch(6);
        for (String rule : rules) {
            Matcher m = pattern.matcher(rule);
            rule = m.replaceAll("");
            if (rule == null || StringUtils.isEmpty(rule) || rule.startsWith("//") || rule.startsWith("## version") || (rule.startsWith("##") && rule.contains("version"))) {
                continue;
            }
            if (rule.indexOf("//") != -1) {
                if (rule.substring(0, rule.indexOf("//")).replaceAll("\\s*", "").endsWith("--")) {
                    continue;
                }
            }
            if (rule.replaceAll("\\s*", "").startsWith("starttransfertype")) {
                cdl= new CountDownLatch(6);
                rulemap = new HashMap<String,Object>();
                tempmap = new HashMap<String,Object>();
                listRules= new ArrayList<String>();
                transfertype=rule.substring(rule.indexOf("=")+1);
                if(transfertype.indexOf("//")!=-1){
                    transfertype = transfertype.substring(0,transfertype.indexOf("//"));
                }
                ruletag= transfertype;
                continue;
            }else if(rule.replaceAll("\\s*", "").startsWith("endtransfertype")){
                //结束一组规则生成组信息
                if(ruletag.startsWith("nomallogical")){
                    rulemap.put("type","nomallogical");
                    rulemap.put("data",listRules);
                    queuerule.offer(rulemap);
                }else if(ruletag.startsWith("booleanlogical")){
                    rulemap.put("type","booleanlogical");
                    rulemap.put("data",listRules);
                    queuerule.offer(rulemap);
                }else if(ruletag.startsWith("dynamicmatchrowlogical")){
                    if(cdl.getCount()!=0){
                        errorgroup.append(ruletag+"转换文件组信息有误!组信息不全;");
                        break;
                    }else{
                        rulemap.put("type","dynamicmatchrowlogical");
                        rulemap.put("data",tempmap);
                        queuerule.offer(rulemap);
                    }
                    continue;
                }
                continue;
            }
            if(rule.indexOf("//")!=-1){
                rule=rule.substring(0,rule.indexOf("//"));
            }
            if(ruletag==null||StringUtils.isEmpty(ruletag)){
                errorgroup.append(ruletag+"转换文件组配置确实转换类型!以starttransfertype开头;");
            }
            if(ruletag.startsWith("nomallogical")){
                listRules.add(rule);
            }else if(ruletag.startsWith("booleanlogical")){
                listRules.add(rule);
            }else if(ruletag.startsWith("dynamicmatchrowlogical")){
                //每一组包含reportpath,documentpath,fixedtr,dynamictrstartpos,dynamictrreleations,dynamictrendpos,groupend1信息
                String[] entry = new String[2];
                if(rule.indexOf("=")!=-1){
                    entry[0] = rule.split("=")[0];
                    if(rule.split("=").length==1){
                        entry[1] = "";
                    }else{
                        entry[1] = rule.split("=")[1];
                    }
                }
                if(entry[0]!=null&&entry[0].equals("reportpath")){
                    cdl.countDown();
                    tempmap.put(entry[0],entry[1]);
                }else if(entry[0]!=null&&entry[0].equals("documentpath")){
                    cdl.countDown();
                    tempmap.put(entry[0],entry[1]);
                }else if(entry[0]!=null&&entry[0].equals("fixedtoptr")){
                    cdl.countDown();
                    tempmap.put(entry[0],entry[1]);
                }else if(entry[0]!=null&&entry[0].equals("fixedbottomtr")){
                    cdl.countDown();
                    tempmap.put(entry[0],entry[1]);
                }else if(entry[0]!=null&&entry[0].equals("dynamictrstartpos")){
                    cdl.countDown();
                    tempmap.put(entry[0],entry[1]);
                }else if(entry[0]!=null&&entry[0].equals("dynamictrreleations")){
                    cdl.countDown();
                    tempmap.put(entry[0],entry[1]);
                }else if(entry[0]!=null&&entry[0].equals("dynamictrendpos")){
                    cdl.countDown();
                    tempmap.put(entry[0],entry[1]);
                }
            }
        }
        result.put("data",queuerule);
        result.put("error",errorgroup.toString());
        return result;
    }

}
