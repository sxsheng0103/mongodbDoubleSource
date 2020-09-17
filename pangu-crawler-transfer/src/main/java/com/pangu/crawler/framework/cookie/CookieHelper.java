/*
package com.pangu.crawler.framework.cookie;

import com.pangu.crawler.framework.host.HostBean;
import com.pangu.crawler.framework.utils.CharsetEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CookieHelper {

    private final static Logger logger = LoggerFactory.getLogger(CookieHelper.class);

    */
/**
     * 解析HTTP响应头中的Set-Cookie。
     *
     * @param currentHostName 此Http请求响应对应的HostName
     * @param cookieSequences cookie条目列表，列表中示例如下：
     *                        key=value; Expires=Tue, 15 Jan 2013 21:47:38 GMT; Domain=www.demo.com; Path=/query; secure
     * @return
     * @throws Exception
     *//*

    public static Map<CookieKey, List<String>> parseHttpResponseCookie(@NotNull String trace,
                                                                       @NotNull String currentHostName,
                                                                       @NotNull List<String> cookieSequences) throws Exception {
        logger.info("[{}] - parseHttpResponseCookie start! currentHostName = {}, cookieSequences = {}",
                trace, currentHostName, cookieSequences);
        Map<CookieKey, List<String>> cookies = new HashMap<>();
        if (cookieSequences == null || cookieSequences.isEmpty()) {
            return cookies;
        }
        Pattern domainPattern = Pattern.compile("^ *Domain=");
        Pattern pathPattern = Pattern.compile("^ *Path=");
        Pattern securePattern = Pattern.compile("^ *secure *$");
        for (String cookieSequence : cookieSequences) {
            String key = "", value = "";
            List<String> hostNames = null;
            String path = null;
            String secure = null;
            String[] items = cookieSequence.split(";");
            for (int i = 0; i < items.length; i++) {
                String item = items[i];
                if (i == 0) {
                    // key=value部分
                    int index = item.indexOf("=");
                    if (index > 0) {
                        key = item.substring(0, index);
                        if (index < item.length() - 1) {
                            value = item.substring(index + "=".length());
                        }
                    } else {
                        key = item;
                    }
                } else {
                    boolean match = false;
                    if (!match && hostNames == null) {
                        Matcher matcher = domainPattern.matcher(item);
                        if (matcher.find()) {
                            match = true;
                            String domain = item.replace(matcher.group(), "");
                            List<String> hostNamesTemp = new ArrayList<>();
                            HostBean.instance.getHostURLs().forEach((hostName, hostURL) -> {
                                // 按照Cookie规范，例如Domain=.demo.com的值可同时用于www.demo.com和res.demo.com类似的情况。
                                // 为了适用该规范情况，根据Domain的值匹配至相应的hostName中。
                                if (hostURL.endsWith(domain)) {
                                    hostNamesTemp.add(hostName);
                                }
                            });
                            if (!hostNamesTemp.contains(currentHostName)) {
                                String currentHostURL = HostBean.instance.getHostURL(currentHostName);
                                throw new Exception("parse http response cookie error : domain not match current host! " +
                                        "current host name is " + currentHostName +
                                        ", current host url is " + currentHostURL +
                                        ", cookie item is " + item);
                            }
                            hostNames = hostNamesTemp;
                        }
                    }
                    if (!match && path == null) {
                        Matcher matcher = pathPattern.matcher(item);
                        if (matcher.find()) {
                            match = true;
                            path = item.replace(matcher.group(), "");
                        }
                    }
                    if (!match && secure == null) {
                        Matcher matcher = securePattern.matcher(item);
                        if (matcher.find()) {
                            match = true;
                            secure = "true";
                        }
                    }
                }
            }
            if (hostNames == null) {
                hostNames = Collections.singletonList(currentHostName);
            }
            if (path == null) {
                path = "/";
            }
            if (secure == null) {
                secure = "false";
            }
            String finalValue = value;
            for (String hostName : hostNames) {
                CookieKey cookieKey = new CookieKey(key, hostName, path, secure);
                cookies.compute(cookieKey, (ck, list) -> {
                    if (list == null || list.isEmpty()) {
                        list = new ArrayList<>();
                        list.add(finalValue);
                        return list;
                    } else {
                        list.add(finalValue);
                        return list;
                    }
                });
            }
        }
        logger.info("[{}] - parseHttpResponseCookie end! currentHostName = {}, cookieSequences = {}",
                trace, currentHostName, cookieSequences);
        return cookies;
    }

    */
/**
     * 解析缓存中读取的Cookie。
     *
     * @param cookieSequences 逗号分隔的cookie条目，示例如下：
     *                        [key0][default][/query][true]=value0;[key1][default][/][true]=value1;...
     *                        等号左边的KEY部分参见com.pangu.crawler.framework.cookie.CookieKey.toString方法
     * @return
     * @throws Exception
     *//*

    public static Map<CookieKey, List<String>> parseCacheLoadedCookie(@NotNull String trace,
                                                                      @NotNull String cookieSequences) throws Exception {
        logger.info("[{}] - parseCacheLoadedCookie start! cookieSequences = {}", trace, cookieSequences);
        Map<CookieKey, List<String>> cookies = new HashMap<>();
        if (cookieSequences == null || cookieSequences.isEmpty()) {
            return cookies;
        }
        // 对应来自其它应用写入的Cookie进行解析。
        if (cookieSequences.startsWith("*")) {
            logger.info("[{}] - parseCacheLoadedCookie to parseHttpResponseCookie!", trace);
            byte[] decodeBytes = Base64.getDecoder().decode(cookieSequences.substring("*".length()));
            String decodeCookieSequences = new String(decodeBytes, CharsetEnum.UTF8.getCharset());
            return parseHttpResponseCookie(trace, "default",
                    Stream.of(decodeCookieSequences.split(";")).map(String::trim).collect(Collectors.toList()));
        }
        // 对应本程序写入的Cookie进行解析。
        for (String cookieSequence : cookieSequences.split(";")) {
            String key = "";
            String value = "";
            int index = cookieSequence.indexOf("=");
            if (index > 0) {
                key = cookieSequence.substring(0, index);
                if (index < cookieSequence.length() - 1) {
                    value = cookieSequence.substring(index + "=".length());
                }
            } else {
                key = cookieSequence;
            }
            String finalValue = value;
            CookieKey cookieKey = CookieKey.parse(key);
            cookies.compute(cookieKey, (ck, list) -> {
                if (list == null || list.isEmpty()) {
                    list = new ArrayList<>();
                    list.add(finalValue);
                    return list;
                } else {
                    list.add(finalValue);
                    return list;
                }
            });
        }
        logger.info("[{}] - parseCacheLoadedCookie end! cookieSequences = {}", trace, cookieSequences);
        return cookies;
    }

    public static String sequenceForRequest(@NotNull String trace, @NotNull String url, @NotNull Map<CookieKey, List<String>> cookies) {
        logger.info("[{}] - sequenceForRequest start! url = {}, cookies = {}", trace, url, cookies);
        List<String> cookieList = new ArrayList<>();
        cookies.forEach(((cookieKey, list) -> {
            String hostURL = HostBean.instance.getHostURL(cookieKey.getHostName());
            String path = cookieKey.getPath();
            if ("/".equals(path)) {
                path = "";
            }
            while (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            String cookieURL0 = hostURL + path;
            String cookieURL1 = hostURL + path + "/";
            String cookieURL2 = hostURL + path + "?";
            boolean urlMatch = url.endsWith(cookieURL0) || url.startsWith(cookieURL1) || url.startsWith(cookieURL2);
            boolean secure = "true".equalsIgnoreCase(cookieKey.getSecure());
            boolean secureMatch = !secure || url.toLowerCase().startsWith("https");
            if (urlMatch && secureMatch) {
                list.forEach(e -> cookieList.add(cookieKey.getKey() + "=" + e));
            }
        }));
        logger.info("[{}] - sequenceForRequest end! url = {}, cookies = {}", trace, url, cookies);
        return String.join(";", cookieList);
    }

    public static String sequenceForCache(@NotNull String trace, @NotNull Map<CookieKey, List<String>> cookies) {
        logger.info("[{}] - sequenceForCache start! cookies = {}", trace, cookies);
        List<String> cookieList = new ArrayList<>();
        cookies.forEach((k, v) -> v.forEach(e -> cookieList.add(k + "=" + e)));
        logger.info("[{}] - sequenceForCache end! cookies = {}", trace, cookies);
        return String.join(";", cookieList);
    }
}
*/
