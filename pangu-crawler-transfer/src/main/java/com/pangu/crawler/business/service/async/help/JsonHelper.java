package com.pangu.crawler.business.service.async.help;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Optional;
import java.util.function.Supplier;

public class JsonHelper {
    private JsonHelper() {
    }

    /**
     * 如果jpath对应的值为null，该方法返回Optional.empty。
     *
     * @see Optional#empty()
     */
    public static Optional<String> jpathEval(JSONObject jsonObject, String jpath) {
        Object object = JSONPath.eval(jsonObject, jpath);
        if (object == null) {
            return Optional.empty();
        }
        return Optional.of(object.toString());
    }

    /**
     * 该方法在jpath对应的值非空（既不能是null，而且trim后也不能为空串）时才返回true！
     */
    public static boolean jpathExist(JSONObject jsonObject, String jpath) {
        Object object = JSONPath.eval(jsonObject, jpath);
        return object != null && !object.toString().trim().isEmpty();
    }

    public static boolean jpathSet(JSONObject jsonObject, String jpath, Object value) {
        return JSONPath.set(jsonObject, jpath, value);
    }



    public static JSONObject parse(String json) throws Exception {
        return JSONObject.parseObject(json, Feature.AllowComment, Feature.OrderedField);
    }

    public static String str(JSONObject jsonObject) {
        return jsonObject.toString(SerializerFeature.PrettyFormat);
    }
}
