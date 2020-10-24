package com.pangu.crawler.transfer.com.objfillsize;

import com.alibaba.fastjson.JSONObject;
import org.apache.lucene.util.RamUsageEstimator;
import org.openqa.selenium.WebDriver;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RetryBusinessStrategy {


    /**
     * 定时检查缓存对象占用内存大小
     */
    public static void testHeapSize(long l) throws IOException, ClassNotFoundException {
        //第三方工具计算
        //计算指定对象及其引用树上的所有对象的综合大小，单位字节
        Map map = new HashMap();
        long sizeOf1 =RamUsageEstimator.sizeOf(map);
        //计算指定对象本身在堆空间的大小，单位字节
//        long sizeOf2 = RamUsageEstimator.shallowSizeOf(map);
        //计算指定对象及其引用树上的所有对象的综合大小，返回可读的结果，如：2KB
//        String sizeOf3 =  RamUsageEstimator.humanSizeOf(map);
//        long size = SizeOfAgent.fullSizeOf(exceptionBusinessDetailMap);
        //            ObjectShallowSize.sizeOf(exceptionBusinessDetailMap)
        FileOutputStream os = new FileOutputStream("d:"+ File.separator+"use.out");
        ObjectOutputStream o = new ObjectOutputStream(os);
        o.writeObject(map);
        o.flush();
        map.clear();
        FileInputStream input = new FileInputStream("d:"+File.separator+"use.out");
        ObjectInputStream objInput = new ObjectInputStream(input);
        map = (ConcurrentHashMap<String, List<Map<String,String>>>)objInput.readObject();
    }
}
