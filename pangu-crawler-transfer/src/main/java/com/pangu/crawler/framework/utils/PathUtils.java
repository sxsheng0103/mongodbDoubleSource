/*
package com.pangu.crawler.framework.utils;

import com.alibaba.fastjson.JSON;
import com.pangu.crawler.framework.http.HttpManager;
import com.pangu.crawler.framework.request.RequestBean;
import com.pangu.crawler.framework.resource.ResourceReader;
import com.pangu.crawler.framework.service.ServiceFirstArg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class PathUtils {

    private static final String USER_HOME = System.getProperty("user.home");

    private static final String PKCS = USER_HOME + File.separator + "pangu-pkcs";

    private static final String CACHE = USER_HOME + File.separator + "pangu-cache";

    private static final String IMAGE = USER_HOME + File.separator + "pangu-image";

    private static final Logger logger = LoggerFactory.getLogger(PathUtils.class);

    static {
        File dir = new File(PKCS);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new RuntimeException("[" + dir + "] mkdir failed!");
            } else {
                logger.info("[" + dir + "] mkdir success!");
            }
        } else {
            logger.info("[" + dir + "] have exist!");
        }
        dir = new File(CACHE);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new RuntimeException("[" + dir + "] mkdir failed!");
            } else {
                logger.info("[" + dir + "] mkdir success!");
            }
        } else {
            logger.info("[" + dir + "] have exist!");
        }
        dir = new File(IMAGE);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new RuntimeException("[" + dir + "] mkdir failed!");
            } else {
                logger.info("[" + dir + "] mkdir success!");
            }
        } else {
            logger.info("[" + dir + "] have exist!");
        }
    }

    public static File fileInPkcs(@NotNull String name) {
        return new File(PKCS + File.separator + name);
    }

    public static File fileInCache(@NotNull String name) {
        return new File(CACHE + File.separator + name);
    }

    public static File fileInImage(@NotNull String name) {
        return new File(IMAGE + File.separator + name);
    }

    */
/**
     * 获取验证码
     *
     * @param firstArg
     * @throws Exception
     *//*

    public static String postToken(ServiceFirstArg firstArg, Resource loginResource)
            throws Exception {
        return reqBase64(loginResource, "yzm", firstArg, null, body -> body);
    }


    */
/**
     * 直接填base64Code
     *//*

    public static String parseYzmByBase64Str(String base64Str,String area,
                                             String taxNo,String captchaType,String captchaMinLength, String captchaMaxLength) {
        String res = "";
        try {

            URL url = new URL("http://39.105.160.146:8909/api/receive/receiveImg");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置请求方式
            connection.setRequestMethod("POST");
            // 设置是否向HttpURLConnection输出
            connection.setDoOutput(true);
            // 设置是否从httpUrlConnection读入
            connection.setDoInput(true);
            // 设置是否使用缓存
            connection.setUseCaches(false);
            //设置参数类型是json格式
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            connection.connect();
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("area", area);
            paramMap.put("base64Code", base64Str);
            paramMap.put("taxNo", taxNo);
            paramMap.put("captchaType", captchaType);
            paramMap.put("captchaMinLength", captchaMinLength);
            paramMap.put("captchaMaxLength", captchaMaxLength);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            writer.write(JSON.toJSONString(paramMap));
            writer.close();
            //读取URLConnection的响应
            InputStream ins = connection.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            while (true) {
                int rc = ins.read(buf);
                if (rc <= 0) {
                    break;
                } else {
                    bout.write(buf, 0, rc);
                }
            }
            ins.close();
            //结果输出
            res = new String(bout.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    */
/**
     * req 只解析header 有返回值
     * @param resource
     * @param key
     * @param firstArg
     * @param urlParams
     * @param function
     * @return
     * @throws Exception
     *//*

    public static String reqBase64(Resource resource,
                                   String key,
                                   ServiceFirstArg firstArg,
                                   Map<String, String> urlParams,
                                   Function<String, String> function) throws Exception {
        String trace = firstArg.getTrace();
        String nsrsbh = firstArg.getNsrsbh();
        HttpManager httpManager = firstArg.getHttpManager();
        RequestBean requestBean = ResourceReader.readRequest(trace, key, resource)
                .parseHeader(urlParams);
        AtomicReference<String> ret = new AtomicReference<>("");
        httpManager.processBase64(trace, nsrsbh, requestBean, (httpHead, body) -> {
            //Base64ToImage(body);
            ret.set(function.apply(body));
        });
        return ret.get();
    }
}
*/
