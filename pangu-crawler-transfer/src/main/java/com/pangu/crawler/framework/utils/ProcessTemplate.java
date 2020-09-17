/*
package com.pangu.crawler.framework.utils;

import com.pangu.crawler.framework.http.HttpManager;
import com.pangu.crawler.framework.request.RequestBean;
import com.pangu.crawler.framework.resource.ResourceReader;
import com.pangu.crawler.framework.service.ServiceFirstArg;
import org.springframework.core.io.Resource;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

*/
/**
 * @author liuhx
 * @desc 封装特定格式的http请求
 * @date 2019/7/11
 **//*

public class ProcessTemplate {

    */
/**
     * req 携带body，不携带freemarker报文映射， 有返回值
     * @param resource
     * @param key
     * @param firstArg
     * @param urlParams
     * @param bodyParams
     * @param function 自定义处理且返回处理结果
     * @throws Exception
     *//*

    public static String reqWithBodyFunc(Resource resource,
                                   String key,
                                   ServiceFirstArg firstArg,
                                   Map<String, String> urlParams,
                                   Map<String, String> bodyParams,
                                   Function<String, String> function) throws Exception {
        String trace = firstArg.getTrace();
        String nsrsbh = firstArg.getNsrsbh();
        HttpManager httpManager = firstArg.getHttpManager();
        RequestBean requestBean = ResourceReader.readRequest(trace, key, resource)
                .parseHeader(urlParams)
                .parseBody(bodyParams);
        AtomicReference<String> ret = new AtomicReference<>();
        httpManager.processText(trace, nsrsbh, requestBean, (httpHead, body) -> {
           ret.set(function.apply(body));
        });
        return ret.get();
    }


    */
/**
     * req 携带body，不携带freemarker报文映射， 冇返回值
     * @param resource
     * @param key
     * @param firstArg
     * @param urlParams
     * @param bodyParams
     * @param consumer 消费响应体
     * @throws Exception
     *//*

    public static void reqWithBodyComsumer(Resource resource,
                                   String key,
                                   ServiceFirstArg firstArg,
                                   Map<String, String> urlParams,
                                   Map<String, String> bodyParams,
                                   Consumer<String> consumer) throws Exception {
        String trace = firstArg.getTrace();
        String nsrsbh = firstArg.getNsrsbh();
        HttpManager httpManager = firstArg.getHttpManager();
        RequestBean requestBean = ResourceReader.readRequest(trace, key, resource)
                .parseHeader(urlParams)
                .parseBody(bodyParams);
        httpManager.processText(trace, nsrsbh, requestBean, (httpHead, body) -> {
            if (consumer != null) {
                consumer.accept(body);
            }
        });
    }


    */
/**
     * req 携带body,且携带报文映射 有返回值，返回值为String
     *
     * @param resource
     * @param key
     * @param firstArg
     * @param urlParams
     * @param bodyParams
     * @param function
     * @return
     * @throws Exception
     *//*

    public static String reqWithBodyMappingFtl(Resource resource,
                                     String key,
                                     ServiceFirstArg firstArg,
                                     Map<String, String> urlParams,
                                     Map<String, String> bodyParams,
                                     String data,
                                     Function<String, String> function) throws Exception {
        String trace = firstArg.getTrace();
        String nsrsbh = firstArg.getNsrsbh();
        HttpManager httpManager = firstArg.getHttpManager();
        RequestBean requestBean = ResourceReader.readRequest(trace, key, resource)
                .parseHeader(urlParams)
                .parseBody(bodyParams)
                .bodyFtlProcess(data);
        AtomicReference<String> ret = new AtomicReference<>();
        httpManager.processText(trace, nsrsbh, requestBean, (httpHead, body) -> {
            ret.set(function.apply(body));
        });
        return ret.get();
    }


    */
/**
     * req 只解析http Header   不消费响应体
     * @param resource
     * @param key
     * @param firstArg
     * @param urlParams
     * @throws Exception
     *//*

    public static void req(Resource resource,
                           String key,
                           ServiceFirstArg firstArg,
                           Map<String, String> urlParams) throws Exception {
        String trace = firstArg.getTrace();
        String nsrsbh = firstArg.getNsrsbh();
        HttpManager httpManager = firstArg.getHttpManager();
        RequestBean requestBean = ResourceReader.readRequest(trace, key, resource)
                .parseHeader(urlParams);
        httpManager.processText(trace, nsrsbh, requestBean);
    }


    */
/**
     * req 只解析http Header
     *
     * @param resource  http resource
     * @param key       key
     * @param firstArg  必备参数
     * @param urlParams urlParams 可为空
     * @param consumer  消费消息
     * @throws Exception
     *//*

    public static void req(Resource resource,
                           String key,
                           ServiceFirstArg firstArg,
                           Map<String, String> urlParams,
                           Consumer<String> consumer) throws Exception {
        String trace = firstArg.getTrace();
        String nsrsbh = firstArg.getNsrsbh();
        HttpManager httpManager = firstArg.getHttpManager();
        RequestBean requestBean = ResourceReader.readRequest(trace, key, resource)
                .parseHeader(urlParams);
        httpManager.processText(trace, nsrsbh, requestBean, (httpHead, body) -> {
            if (consumer != null) {
                consumer.accept(body);
            }
        });
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

    public static String req(Resource resource,
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
        httpManager.processText(trace, nsrsbh, requestBean, (httpHead, body) -> {
            ret.set(function.apply(body));
        });
        return ret.get();
    }


}
*/
