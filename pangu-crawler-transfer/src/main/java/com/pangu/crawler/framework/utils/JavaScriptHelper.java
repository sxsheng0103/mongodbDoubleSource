package com.pangu.crawler.framework.utils;

import jdk.nashorn.api.scripting.JSObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.HashMap;
import java.util.Map;

public class JavaScriptHelper {

    public static String random() {
        try {
            return expression("Math.random()");
        } catch (Exception e) {
            return String.valueOf(Math.random());
        }
    }

    public static String expression(String expression) throws Exception {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");
        return String.valueOf(scriptEngine.eval(expression));
    }

    public static String variable(String variableName, String variableDefines) throws Exception {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");
        scriptEngine.eval(variableDefines);
        return String.valueOf(scriptEngine.get(variableName));
    }

    public static String function(String functionName, String functionEntity, Object... args) throws Exception {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");
        JSObject jsObject = (JSObject) scriptEngine.eval(functionEntity);
        return String.valueOf(jsObject.call(functionName, args));
    }

    public static String getJsParamValue(String html, String key) throws Exception {

        Document document = Jsoup.parse(html);
        Elements e = document.getElementsByTag("script");
        for (Element element : e) {
            String[] data = element.data().split("var");
            for (String variable : data) {
                if (variable.contains("=")) {
                    if (variable.contains(key)) {
                        String[] kvp = variable.split("=");
                        return (kvp[1].contains(";")?kvp[1].split(";")[0]:kvp[1]).replace("\"","");
                    }
                }
            }
        }
        return null;
    }

}
