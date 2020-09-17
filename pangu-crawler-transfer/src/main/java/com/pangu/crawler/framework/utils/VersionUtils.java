package com.pangu.crawler.framework.utils;

public class VersionUtils {
    public static String version() {
        String defineVersion = "function defineVersion() {\n" +
                "    var now = new Date();\n" +
                "    var year = now.getFullYear();\n" +
                "    var month = now.getMonth()+1;\n" +
                "    var day = now.getDate();\n" +
                "    var hour = now.getHours();\n" +
                "    var minute = now.getMinutes();\n" +
                "    return year + '-' + month + '-' + day+ '-' + hour;\n" +
                "}";
        String version;
        try {
            version = JavaScriptHelper.function("defineVersion", defineVersion);
        } catch (Exception e) {
            version = "";
        }
        return version;
    }
}
