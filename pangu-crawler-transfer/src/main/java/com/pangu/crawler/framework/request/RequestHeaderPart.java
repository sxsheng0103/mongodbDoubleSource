/*
package com.pangu.crawler.framework.request;

import com.pangu.crawler.framework.host.HostBean;
import com.pangu.crawler.framework.resource.ResourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class RequestHeaderPart {

    private static final Logger logger = LoggerFactory.getLogger(RequestHeaderPart.class);

    private String trace;

    private String key;

    private String raw;

    private HttpMethod method;

    private String hostName;

    private String hostURL;

    private String url;

    private Map<String, String> headers = new HashMap<>();

    public RequestHeaderPart(@NotNull String trace) {
        this.trace = trace;
    }

    public RequestHeaderPart(@NotNull String trace, @NotNull String key, @NotNull String raw) {
        this.trace = trace;
        this.key = key;
        this.raw = raw;
    }

    public RequestHeaderPart build4Redirect(String location) {
        RequestHeaderPart requestHeaderPart4Redirect = new RequestHeaderPart(trace);
        requestHeaderPart4Redirect.key = this.key;
        requestHeaderPart4Redirect.raw = this.raw;
        requestHeaderPart4Redirect.method = HttpMethod.GET;
        requestHeaderPart4Redirect.hostName = this.hostName;
        requestHeaderPart4Redirect.url = location;
        requestHeaderPart4Redirect.headers = this.headers;
        return requestHeaderPart4Redirect;
    }

    public void parse() throws Exception {
        parse(Collections.emptyMap());
    }

    public void parse(Map<String, String> params) throws Exception {
        logger.info("[{{}] - request header parse start!", trace);
        String[] lines;
        if (raw.contains(ResourceReader.LINE_SEP)) {
            lines = raw.split(ResourceReader.LINE_SEP);
        } else {
            lines = new String[]{raw};
        }
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] - request header parse, raw = {}, lines = {}, params = {}",
                    trace, raw, Arrays.toString(lines), params);
        }
        boolean first = true;
        for (String line : lines) {
            if (params != null && !params.isEmpty()) {
                String[] lineWrap = new String[]{line};
                params.forEach((k, v) -> {
                    String actualK = "[[" + k + "]]";
                    while (lineWrap[0].contains(actualK)) {
                        lineWrap[0] = lineWrap[0].replace(actualK, v);
                    }
                });
                line = lineWrap[0];
            }
            if (first) {
                if (!line.contains("HTTP/1.1")) {
                    throw new Exception("Only HTTP/1.1 Supported : " + line);
                }
                String[] firstParts = line.split(" ");
                this.method = HttpMethod.valueOf(firstParts[0].toUpperCase());
                String hostName = "default";
                String uri = firstParts[1];
                if (uri.startsWith("{")) {
                    hostName = uri.substring(1, uri.indexOf("}"));
                    uri = uri.substring(("{" + hostName + "}").length());
                }
                if (!uri.startsWith("/")) {
                    throw new Exception("http request header line error : " + line);
                }
                String hostURL = HostBean.instance.getHostURL(hostName);
                if (hostURL == null || hostURL.isEmpty()) {
                    throw new Exception("host url is empty : host name is " + hostName + ", line is " + line);
                }
                this.hostURL = hostURL;
                this.hostName = hostName;
                this.url = hostURL + uri;
                first = false;
            } else {
                String sep = ": ";
                int index = line.indexOf(sep);
                if (index <= 0) {
                    throw new Exception("header format error : " + line);
                }
                String headerKey = line.substring(0, index);
                if (!"cookie".equalsIgnoreCase(headerKey)) {
                    String headerValue = "";
                    if (index + sep.length() < line.length()) {
                        headerValue = line.substring(index + sep.length());
                    }
                    this.headers.put(headerKey, headerValue);
                }
            }
        }
        logger.info("[{{}] - request header parse end!", trace);
    }

    public String getTrace() {
        return trace;
    }

    public String getKey() {
        return key;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getHostName() {
        return hostName;
    }

    public String getHostURL() {
        return hostURL;
    }

    public String getUrl() {
        return url;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }
    
    public void setHeader(String key,String value) {
        headers.put(key, value);
    }

    public void headersConsumer(BiConsumer<String, String> consumer) {
        this.headers.forEach(consumer);
    }

    @Override
    public String toString() {
        return "RequestHeaderPart{" +
                "trace='" + trace + '\'' +
                ", key='" + key + '\'' +
                ", raw='" + raw + '\'' +
                ", method=" + method +
                ", hostName='" + hostName + '\'' +
                ", hostURL='" + hostURL + '\'' +
                ", url='" + url + '\'' +
                ", headers=" + headers +
                '}';
    }
}
*/
