package com.pangu.crawler.framework.mapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import static com.alibaba.fastjson.JSON.parseObject;

public class JsonHelper {
    private JsonHelper() {
    }

    private static final String NULL_FLAG = ":null";

    private static final String NULL_FLAG_REG = "(?i)" + NULL_FLAG;

    private static final String NULL_STR = ":\"N342184U902A6C8L9B990F11EBD552DE202L\"";

    public static JSONObject jsonStringToObject(String json) throws Exception {
        if (json == null) {
            throw new IllegalArgumentException("json is null in JsonHelper.jsonStringToObject!");
        }
        if (json.trim().isEmpty()) {
            throw new IllegalArgumentException("json is empty in JsonHelper.jsonStringToObject!");
        }
        try {
            return parseObject(json, Feature.AllowComment, Feature.OrderedField);
        } catch (Exception e) {
            throw new Exception("json string to object error!", e);
        }
    }

    public static String jsonObjectToString(JSONObject jsonObject, boolean withNullValue) throws Exception {
        if (jsonObject == null) {
            throw new IllegalArgumentException("jsonObject is null in JsonHelper.jsonObjectToString!");
        }
        try {
            if (withNullValue) {
                return jsonObject.toString(SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue);
            } else {
                return jsonObject.toString(SerializerFeature.PrettyFormat);
            }
        } catch (Exception e) {
            throw new Exception("json object to string error!", e);
        }
    }

    public static JSONObject encodeNullValue(JSONObject jsonObject) throws Exception {
        if (jsonObject == null) {
            throw new IllegalArgumentException("jsonObject is null in JsonHelper.encodeNullValue!");
        }
        try {
            String s = jsonObjectToString(jsonObject, true);
            if (Pattern.compile(NULL_FLAG_REG).matcher(s).find()) {
                s = s.replaceAll(NULL_FLAG_REG, NULL_STR);
                return jsonStringToObject(s);
            } else {
                return jsonObject;
            }
        } catch (Exception e) {
            throw new Exception("json encode null value error!", e);
        }
    }

    public static JSONObject decodeNullValue(JSONObject jsonObject) throws Exception {
        if (jsonObject == null) {
            throw new IllegalArgumentException("jsonObject is null in JsonHelper.decodeNullValue!");
        }
        try {
            String s = jsonObjectToString(jsonObject, true);
            if (s.contains(NULL_STR)) {
                s = s.replaceAll(NULL_STR, NULL_FLAG);
                return jsonStringToObject(s);
            } else {
                return jsonObject;
            }
        } catch (Exception e) {
            throw new Exception("json decode null value error!", e);
        }
    }

    public static SortedSet<String> mappingJPaths(JSONObject jsonObject) throws Exception {
        if (jsonObject == null) {
            throw new IllegalArgumentException("jsonObject is null in JsonHelper.mappingJPaths!");
        }
        TreeSet<String> jpaths = new TreeSet<>();
        try {
            Map<String, Object> paths = JSONPath.paths(jsonObject);
            for (Map.Entry<String, Object> entry : paths.entrySet()) {
                String path = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof JSONObject || value instanceof JSONArray) {
                    // SKIP
                } else {
                    path = path.substring(1);
                    String[] split0 = path.split("/");
                    String[] split1 = new String[split0.length * 2];
                    for (int i = 0; i < split0.length; i++) {
                        String split = split0[i];
                        split1[i * 2] = split;
                        split1[i * 2 + 1] = ".";
                        boolean isNumber = true;
                        try {
                            Integer.parseInt(split);
                        } catch (Exception e) {
                            isNumber = false;
                        }
                        if (isNumber) {
                            split1[i * 2 - 1] = "[";
                            split1[i * 2 + 1] = "].";
                        }
                    }
                    String[] split2 = new String[split1.length - 1];
                    System.arraycopy(split1, 0, split2, 0, split2.length);
                    path = String.join("", split2);
                    jpaths.add(path);
                }
            }
            return Collections.unmodifiableSortedSet(jpaths);
        } catch (Exception e) {
            throw new Exception("mapping jpaths error!", e);
        }
    }

    public static SortedSet<String> allJPaths(JSONObject jsonObject) throws Exception {
        if (jsonObject == null) {
            throw new IllegalArgumentException("jsonObject is null in JsonHelper.allJPaths!");
        }
        try {
            return Collections.unmodifiableSortedSet(new TreeSet<>(JSONPath.paths(jsonObject).keySet()));
        } catch (Exception e) {
            throw new Exception("all jpaths error!", e);
        }
    }
}
