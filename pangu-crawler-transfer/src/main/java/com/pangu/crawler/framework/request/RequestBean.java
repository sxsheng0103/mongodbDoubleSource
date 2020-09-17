/*
package com.pangu.crawler.framework.request;

import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.framework.utils.CharsetEnum;
import com.pangu.crawler.framework.utils.EncEnum;
import freemarker.ext.dom.NodeModel;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.function.BiFunction;

public class RequestBean {

    private String trace;

    private String key;

    private RequestHeaderPart header;

    private RequestBodyPart body;

    private CharsetEnum requestCharset = CharsetEnum.UTF8;

    private CharsetEnum responseCharset = CharsetEnum.UTF8;

    public RequestBean(@NotNull String trace) {
        this.trace = trace;
    }

    public RequestBean(@NotNull String trace,
                       @NotNull String key,
                       @NotNull String headerRaw,
                       @NotNull String bodyRaw) {
        this.trace = trace;
        this.key = key;
        this.header = new RequestHeaderPart(trace, key, headerRaw);
        this.body = new RequestBodyPart(trace, key, bodyRaw);
    }

    public RequestBean parseHeader() throws Exception {
        this.header.parse();
        return this;
    }

    public RequestBean parseHeader(Map<String, String> params) throws Exception {
        this.header.parse(params);
        return this;
    }

    public RequestBean parseBody() throws Exception {
        this.body.parse();
        return this;
    }
    
    public RequestBean parseBody(String content) throws Exception {
        this.body.parse(content);
        return this;
    }

    public RequestBean parseBody(Map<String, String> params) throws Exception {
        this.body.parse(params);
        return this;
    }

    public RequestBean bodyFtlProcess(String xmlJsonBw) throws Exception {
        this.body.ftlProcess(xmlJsonBw, requestCharset, null, null);
        return this;
    }

    public RequestBean bodyJsonFtlProcess(String jsonBw,
                                          BiFunction<String, CharsetEnum, JSONObject> prepareJson) throws Exception {
        this.body.ftlProcess(jsonBw, requestCharset, prepareJson, null);
        return this;
    }

    public RequestBean bodyXmlFtlProcess(String xmlBw,
                                         BiFunction<String, CharsetEnum, NodeModel> prepareXml) throws Exception {
        this.body.ftlProcess(xmlBw, requestCharset, null, prepareXml);
        return this;
    }

    public RequestBean bodyPrefix(String prefix) {
        this.body.prefix(prefix);
        return this;
    }

    public RequestBean bodySuffix(String suffix) {
        this.body.suffix(suffix);
        return this;
    }

    public RequestBean bodyEncode(EncEnum enc) throws Exception {
        this.body.encode(enc, requestCharset);
        return this;
    }

    public RequestBean build4Redirect(String location) {
        RequestBean requestBean4Redirect = new RequestBean(trace);
        requestBean4Redirect.key = this.key;
        requestBean4Redirect.header = this.header.build4Redirect(location);
        requestBean4Redirect.body = this.body;
        requestBean4Redirect.requestCharset = this.requestCharset;
        requestBean4Redirect.responseCharset = this.responseCharset;
        return requestBean4Redirect;
    }

    public String getTrace() {
        return trace;
    }

    public String getKey() {
        return key;
    }

    public RequestHeaderPart getHeader() {
        return header;
    }

    public RequestBodyPart getBody() {
        return body;
    }

    public CharsetEnum getRequestCharset() {
        return requestCharset;
    }

    public RequestBean setRequestCharset(CharsetEnum requestCharset) {
        this.requestCharset = requestCharset;
        return this;
    }

    public CharsetEnum getResponseCharset() {
        return responseCharset;
    }

    public RequestBean setResponseCharset(CharsetEnum responseCharset) {
        this.responseCharset = responseCharset;
        return this;
    }

    @Override
    public String toString() {
        return "RequestBean{" +
                "trace='" + trace + '\'' +
                ", key='" + key + '\'' +
                ", header=" + header +
                ", body=" + body +
                ", requestCharset=" + requestCharset +
                ", responseCharset=" + responseCharset +
                '}';
    }
}
*/
