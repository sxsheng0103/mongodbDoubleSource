package com.pangu.crawler.framework.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liuhx
 * @desc StringUtils
 * @date 2019/6/3
 **/
public class StringUtils {

    /**
     * 缺位则前面补领
     *
     * @param number 数字
     * @param len    数字总长度 ,实际长度大于等于此数字时，不进行干预，否则进行干预
     * @return
     */
    public static String wantPreString(int number, int len) {
        StringBuilder numStr = new StringBuilder(String.valueOf(number));
        int diffLen = len - numStr.length();
        if (diffLen > 0) {
            for (int i = 0; i < diffLen; i++) {
                numStr.insert(0, "0");
            }
        }
        return numStr.toString();
    }


    public static boolean isBlank(String str) {
        return org.apache.commons.lang3.StringUtils.isBlank(str);
    }


    public static boolean isNotBlank(String str) {
        return org.apache.commons.lang3.StringUtils.isNotBlank(str);
    }


    public static boolean isEmpty(String str) {
        return org.apache.commons.lang3.StringUtils.isEmpty(str);
    }


    public static boolean isNotEmpty(String str) {
        return org.apache.commons.lang3.StringUtils.isNotEmpty(str);
    }

    /**
     * 替换所有间隙
     *
     * @param origin
     * @return
     */
    public static String replaceInterval(String origin) {
        return origin.replaceAll("\r|\n|\\s", "");
    }

    /**
     * 首字母大写
     * @param str
     * @return
     */
    public static String captureStr(String str) {
        char[] cs = str.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }


    public static String smallLetter(String str) {
        return str.toLowerCase();
    }


    public static String findMiddle(String origin, String start, String end) {
        String ret = "";
        Pattern pattern = Pattern.compile(start + "(.*?)" + end);
        Matcher matcher = pattern.matcher(origin);
        while (matcher.find()) {
            ret = matcher.group(1);
        }
        return ret;
    }

}
