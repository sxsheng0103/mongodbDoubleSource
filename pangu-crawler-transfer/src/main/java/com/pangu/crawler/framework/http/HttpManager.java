/*
package com.pangu.crawler.framework.http;

import com.pangu.crawler.framework.config.HttpConfig;
import com.pangu.crawler.framework.cookie.CookieHelper;
import com.pangu.crawler.framework.cookie.CookieKey;
import com.pangu.crawler.framework.cookie.CookieManager;
import com.pangu.crawler.framework.host.HostBean;
import com.pangu.crawler.framework.request.RequestBean;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Component
public class HttpManager {

    public static final String LOCATIONS = "location_urls";

    private static final Logger logger = LoggerFactory.getLogger(HttpManager.class);

    @Autowired
    private HttpConfig httpConfig;

    @Override
    public String toString() {
        return "HttpManager{" + httpConfig + '}';
    }

    public void processBase64(@NotNull String trace,
                              @NotNull String nsrsbh,
                              @NotNull RequestBean requestBean) throws Exception {
        process(false, trace, nsrsbh, requestBean, null, null);
    }

    public void processBase64(@NotNull String trace,
                              @NotNull String nsrsbh,
                              @NotNull RequestBean requestBean,
                              @NotNull Function<RequestBean, byte[]> requestBodyForPost,
                              @NotNull BiConsumer<Map<String, String>, String> responseConsumer) throws Exception {
        process(false, trace, nsrsbh, requestBean, requestBodyForPost, responseConsumer);
    }

    public void processBase64(@NotNull String trace,
                              @NotNull String nsrsbh,
                              @NotNull RequestBean requestBean,
                              @NotNull Function<RequestBean, byte[]> requestBodyForPost) throws Exception {
        process(false, trace, nsrsbh, requestBean, requestBodyForPost, null);
    }

    public void processBase64(@NotNull String trace,
                              @NotNull String nsrsbh,
                              @NotNull RequestBean requestBean,
                              @NotNull BiConsumer<Map<String, String>, String> responseConsumer) throws Exception {
        process(false, trace, nsrsbh, requestBean, null, responseConsumer);
    }

    public void processText(@NotNull String trace,
                            @NotNull String nsrsbh,
                            @NotNull RequestBean requestBean) throws Exception {
        process(true, trace, nsrsbh, requestBean, null, null);
    }

    public void processText(@NotNull String trace,
                            @NotNull String nsrsbh,
                            @NotNull RequestBean requestBean,
                            @NotNull Function<RequestBean, byte[]> requestBodyForPost,
                            @NotNull BiConsumer<Map<String, String>, String> responseConsumer) throws Exception {
        process(true, trace, nsrsbh, requestBean, requestBodyForPost, responseConsumer);
    }

    public void processText(@NotNull String trace,
                            @NotNull String nsrsbh,
                            @NotNull RequestBean requestBean,
                            @NotNull Function<RequestBean, byte[]> requestBodyForPost) throws Exception {
        process(true, trace, nsrsbh, requestBean, requestBodyForPost, null);
    }

    public void processText(@NotNull String trace,
                            @NotNull String nsrsbh,
                            @NotNull RequestBean requestBean,
                            @NotNull BiConsumer<Map<String, String>, String> responseConsumer) throws Exception {
        process(true, trace, nsrsbh, requestBean, null, responseConsumer);
    }

    private void process(@NotNull boolean respText,
                         @NotNull String trace,
                         @NotNull String nsrsbh,
                         @NotNull RequestBean requestBean,
                         Function<RequestBean, byte[]> requestBodyForPost,
                         BiConsumer<Map<String, String>, String> responseConsumer) throws Exception {
        List<String> redirectLocations = new ArrayList<>();
        if (requestBean.getHeader().getMethod() == GET) {
            httpGet(respText, trace, nsrsbh, requestBean, responseConsumer, redirectLocations);
        } else if (requestBean.getHeader().getMethod() == POST) {
            httpPost(respText, trace, nsrsbh, requestBean, requestBodyForPost, responseConsumer, redirectLocations);
        } else {
            throw new Exception("method error : " + requestBean.getHeader().getMethod());
        }
    }

    private void httpGet(boolean respText, String trace, String nsrsbh, RequestBean requestBean,
                         BiConsumer<Map<String, String>, String> responseConsumer,
                         List<String> redirectLocations) throws Exception {
        logger.info("[{{}] - http get start!", trace);
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "[{{}] - http get arg : respText[{}], trace[{}], nsrsbh[{}], requestBean[{}], responseConsumer[{}], redirectLocations[{}]",
                    trace, respText, trace, nsrsbh, requestBean, responseConsumer, redirectLocations);
        }
        HttpURLConnection connection = null;
        try {
            //忽略证书 start
            // URL url = new URL(null, requestBean.getHeader().getUrl(), new sun.net.www.protocol.https.Handler());
            SSLContext sslcontext = SSLContext.getInstance("SSL","SunJSSE");
            sslcontext.init(null, new TrustManager[]{new MyX509TrustManager()}, new java.security.SecureRandom());
            URL url = new URL(requestBean.getHeader().getUrl());
            HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
                public boolean verify(String s, SSLSession sslsession) {
                    System.out.println("WARNING: Hostname is not matched for cert.");
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
            //忽略证书 end
            connection = (HttpURLConnection) url.openConnection();
            connectionConfiguration(trace, requestBean, connection);
            logger.info("[{{}] - http get request start! url = {}", trace, url);
            connection.connect();
            int responseCode = connection.getResponseCode();
            logger.info("[{{}] - http get response code is {}!", trace, responseCode);
            if (responseCode == 200) {
                parseAndPutCookies(trace, connection, requestBean);
                responseProcess(false, respText, trace, nsrsbh, requestBean, responseConsumer, redirectLocations, connection);
            } else if (responseCode == 302 || responseCode == 301 || responseCode == 303) {
                parseAndPutCookies(trace, connection, requestBean);
                logger.info("[{{}] - http get redirect start!", trace);
                String location = connection.getHeaderField("Location");
                logger.info("[{{}] - http get redirect original location is {}", trace, location);
                if (!location.toLowerCase().startsWith("http")) {
                    location = HostBean.instance.getHostURL(requestBean.getHeader().getHostName()) + location;
                }
                logger.info("[{{}] - http get redirect final location is {}", trace, location);
                redirectLocations.add(location);
                httpGet(respText,
                        trace + "-redirect",
                        nsrsbh,
                        requestBean.build4Redirect(location),
                        responseConsumer, redirectLocations);
                logger.info("[{{}] - http get redirect end!", trace);
            } else {
                // 虽然不进行应用逻辑处理，但仍然解析返回报文。
                parseAndPutCookies(trace, connection, requestBean);
                responseProcess(true, respText, trace, nsrsbh, requestBean, null, redirectLocations, connection);
                throw new Exception("response code error : " + responseCode);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        logger.info("[{{}] - http get end!", trace);
    }

    private void httpPost(boolean respText, String trace, String nsrsbh, RequestBean requestBean,
                          Function<RequestBean, byte[]> requestBodyForPost,
                          BiConsumer<Map<String, String>, String> responseConsumer,
                          List<String> redirectLocations) throws Exception {
        logger.info("[{{}] - http post start!", trace);
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "[{{}] - http post arg : respText[{}], trace[{}], nsrsbh[{}], requestBean[{}], " +
                            "requestBodyForPost[{}], " +
                            "responseConsumer[{}], " +
                            "redirectLocations[{}]",
                    trace, respText, trace, nsrsbh, requestBean, requestBodyForPost, responseConsumer, redirectLocations);
        }
        HttpURLConnection connection = null;
        try {
            //忽略证书 start
            // URL url = new URL(null, requestBean.getHeader().getUrl(), new sun.net.www.protocol.https.Handler());
            SSLContext sslcontext = SSLContext.getInstance("SSL","SunJSSE");
            sslcontext.init(null, new TrustManager[]{new MyX509TrustManager()}, new java.security.SecureRandom());
            URL url = new URL(requestBean.getHeader().getUrl());
            HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
                public boolean verify(String s, SSLSession sslsession) {
                    System.out.println("WARNING: Hostname is not matched for cert.");
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
            //忽略证书 end
            connection = (HttpURLConnection) url.openConnection();
            connectionConfiguration(trace, requestBean, connection);
            byte[] reqBodyBytes;
            if (requestBodyForPost != null) {
                reqBodyBytes = requestBodyForPost.apply(requestBean);
            } else {
                reqBodyBytes = requestBean.getBody().getContent().getBytes(requestBean.getRequestCharset().getCharset());
            }
            if (reqBodyBytes == null) {
                throw new Exception("http post error : request body bytes is null!");
            }
            String contentLength = String.valueOf(reqBodyBytes.length);
            connection.setRequestProperty("Content-Length", contentLength);
            logger.info("[{{}] - http post request start! url = {}, contentLength = {}", trace, url, contentLength);
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(reqBodyBytes);
                outputStream.flush();
                int responseCode = connection.getResponseCode();
                logger.info("[{{}] - http post response code is {}!", trace, responseCode);
                if (responseCode == 200) {
                    parseAndPutCookies(trace, connection, requestBean);
                    responseProcess(false, respText, trace, nsrsbh, requestBean, responseConsumer, redirectLocations, connection);
                } else if (responseCode == 302 || responseCode == 301) {
                    parseAndPutCookies(trace, connection, requestBean);
                    logger.info("[{{}] - http post redirect start!", trace);
                    String location = connection.getHeaderField("Location");
                    logger.info("[{{}] - http post redirect original location is {}", trace, location);
                    if (!location.toLowerCase().startsWith("http")) {
                        location = HostBean.instance.getHostURL(requestBean.getHeader().getHostName()) + location;
                    }
                    logger.info("[{{}] - http post redirect final location is {}", trace, location);
                    redirectLocations.add(location);
                    httpGet(respText,
                            trace + "-redirect",
                            nsrsbh,
                            requestBean.build4Redirect(location),
                            responseConsumer, redirectLocations);
                    logger.info("[{{}] - http post redirect end!", trace);
                } else {
                    // 虽然不进行应用逻辑处理，但仍然解析返回报文。
                    parseAndPutCookies(trace, connection, requestBean);
                    responseProcess(true, respText, trace, nsrsbh, requestBean, null, redirectLocations, connection);
                    throw new Exception("response code error : " + responseCode);
                }
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        logger.info("[{{}] - http post end!", trace);
    }

    private void connectionConfiguration(String trace,
                                         RequestBean requestBean,
                                         HttpURLConnection connection) throws Exception {
        logger.info("[{{}] - connection configuration start!", trace);
        connection.setRequestMethod(requestBean.getHeader().getMethod().name());
        requestBean.getHeader().headersConsumer(connection::setRequestProperty);
        connection.setRequestProperty("Cookie", "");  // 首先清理Cookie
        String cookies = CookieHelper.sequenceForRequest(
                trace, requestBean.getHeader().getUrl(), CookieManager.getCookieLists());
        logger.info("[{}] - request connection cookies = {}", trace, cookies);
        connection.setRequestProperty("Cookie", cookies);
        connection.setConnectTimeout(httpConfig.getConnectTimeout() * 1000);
        connection.setReadTimeout(httpConfig.getReadTimeout() * 1000);
        connection.setInstanceFollowRedirects(false);
        connection.setDefaultUseCaches(false);
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        logger.info("[{{}] - connection configuration end!", trace);
    }

    private void parseAndPutCookies(String trace, HttpURLConnection connection,
                                    RequestBean requestBean) throws Exception {
        logger.info("[{{}] - parse & put cookies only set by this response start!", trace);
        List<String> setCookieList = new ArrayList<>();
        List<String> setNormalCookie = connection.getHeaderFields().get("Set-Cookie");
        List<String> setUpCookie = connection.getHeaderFields().get("SET-COOKIE");
        if(CollectionUtils.isNotEmpty(setNormalCookie)){
            setCookieList.addAll(setNormalCookie);
        }
        if(CollectionUtils.isNotEmpty(setUpCookie)){
            setCookieList.addAll(setUpCookie);
        }
        logger.info("[{{}] - parse & put cookies only set by this response, [Set-Cookie] : {}", trace, setCookieList);
        Map<CookieKey, List<String>> cookies = CookieHelper.parseHttpResponseCookie(
                trace, requestBean.getHeader().getHostName(), setCookieList);
        cookies.forEach(CookieManager::putCookie);
        logger.info("[{{}] - parse & put cookies only set by this response end! this cookie : {}, all cookie : {}",
                trace, cookies, CookieManager.getCookieLists());
    }

    private void responseProcess(boolean errorResp, boolean respText, String trace, String nsrsbh, RequestBean requestBean,
                                 BiConsumer<Map<String, String>, String> responseConsumer,
                                 List<String> redirectLocations,
                                 HttpURLConnection connection) throws Exception {
        logger.info("[{{}] - response process start!", trace);
        Map<String, List<String>> responseHeaders = new HashMap<>();
        connection.getHeaderFields().forEach(responseHeaders::put);
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] - response header : [{}]", trace, responseHeaders);
        }
        byte[] responseBytes;
        logger.info("[{{}] - read response body start! errorResp = {}", trace, errorResp);
        if (errorResp) {
            try (InputStream inputStream = connection.getErrorStream()) {
                responseBytes = readResponseBytes(inputStream);
            }
        } else {
            try (InputStream inputStream = connection.getInputStream()) {
                responseBytes = readResponseBytes(inputStream);
            }
        }
        logger.info("[{{}] - read response body end! errorResp = {}, responseBytes.length = {}",
                trace, errorResp, responseBytes.length);
        StringBuilder responseBody = new StringBuilder();
        if (respText) {
            // 首先尝试用GZIP方式处理
            try {
                logger.info("[{{}] - try to process response body by gzip format start!", trace);
                byte[] ungzipBytes = new byte[0];
                try (GZIPInputStream gzipis = new GZIPInputStream(new ByteArrayInputStream(responseBytes))) {
                    while (true) {
                        byte[] readedBytes = new byte[1024];
                        int readedCount = gzipis.read(readedBytes);
                        if (readedCount <= 0) {
                            break;
                        }
                        byte[] newBytes = new byte[ungzipBytes.length + readedCount];
                        System.arraycopy(ungzipBytes, 0, newBytes, 0, ungzipBytes.length);
                        System.arraycopy(readedBytes, 0, newBytes, ungzipBytes.length, readedCount);
                        ungzipBytes = newBytes;
                    }
                }
                try (BufferedReader br = new BufferedReader(new InputStreamReader(
                        new ByteArrayInputStream(ungzipBytes), requestBean.getResponseCharset().getCharset()))) {
                    String line;
                    for (line = br.readLine(); line != null; line = br.readLine()) {
                        responseBody.append(line).append("\r\n");
                    }
                }
                logger.info("[{{}] - try to process response body by gzip format end!", trace);
            } catch (Exception e) {
                // 尝试用GZIP方式处理失败后，用TEXT方式处理
                logger.info("[{{}] - try to process response body by gzip format failed : {}", trace, e.getMessage());
                logger.info("[{{}] - try to process response body by text format start!", trace);
                try (BufferedReader br = new BufferedReader(new InputStreamReader(
                        new ByteArrayInputStream(responseBytes), requestBean.getResponseCharset().getCharset()))) {
                    String line;
                    for (line = br.readLine(); line != null; line = br.readLine()) {
                        responseBody.append(line).append("\r\n");
                    }
                }
                logger.info("[{{}] - try to process response body by text format end!", trace);
            }
        } else {
            responseBody.append(Base64.encodeBase64String(responseBytes));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] - response body : [{}]", trace, responseBody);
        }
        if (responseConsumer != null) {
            Map<String, String> headersForConsumer = new HashMap<>();
            responseHeaders.entrySet().stream()
                    .filter(entry -> !"Set-Cookie".equalsIgnoreCase(entry.getKey()))
                    .forEach(entry -> headersForConsumer.put(entry.getKey(), entry.getValue().get(0)));
            if (!redirectLocations.isEmpty()) {
                headersForConsumer.put(LOCATIONS, String.join(" ", redirectLocations));
            }
            logger.info("[{}] - response consumer start!", trace);
            if (logger.isDebugEnabled()) {
                logger.debug("[{}] - response consumer arg : headers[{}], body[{}]",
                        trace, headersForConsumer, responseBody);
            }
            responseConsumer.accept(Collections.unmodifiableMap(headersForConsumer), responseBody.toString());
            logger.info("[{}] - response consumer end!", trace);
        } else {
            logger.info("[{}] - response consumer none!", trace);
        }
        logger.info("[{{}] - response process end!", trace);
    }

    private byte[] readResponseBytes(InputStream inputStream) throws Exception {
        byte[] responseBytes = new byte[0];
        while (true) {
            byte[] readedBytes = new byte[1024];
            int readedCount = inputStream.read(readedBytes);
            if (readedCount <= 0) {
                break;
            }
            byte[] newBytes = new byte[responseBytes.length + readedCount];
            System.arraycopy(responseBytes, 0, newBytes, 0, responseBytes.length);
            System.arraycopy(readedBytes, 0, newBytes, responseBytes.length, readedCount);
            responseBytes = newBytes;
        }
        return responseBytes;
    }

    class MyX509TrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}
*/
