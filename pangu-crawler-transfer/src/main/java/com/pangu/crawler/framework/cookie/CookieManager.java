package com.pangu.crawler.framework.cookie;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CookieManager {

    private static final ThreadLocal<Map<CookieKey, List<String>>> cookies = new CookieManagerThreadLocal();

    @Deprecated
    public synchronized static String getCookie(@NotNull String nsrsbh, @NotNull String key) {
        return getCookie(key).orElse(null);
    }

    public synchronized static Optional<String> getCookie(@NotNull String key) {
        Optional<List<String>> cookieList = getCookieList(key);
        return cookieList.map(list -> list.get(0));
    }

    /**
     * 根据CookieKey中的key、hostName、path查询。
     * 这三个参数可以传入null，如果传入null，则表示忽略该查询条件。
     *
     * @param key
     * @param hostName
     * @param path
     * @return
     */
    public synchronized static Optional<String> getCookie(String key, String hostName, String path) {
        Optional<List<String>> cookieList = getCookieList(key, hostName, path);
        return cookieList.map(list -> list.get(0));
    }

    public synchronized static Optional<List<String>> getCookieList(@NotNull String key) {
        return getCookieList(key, null, null);
    }

    /**
     * 根据CookieKey中的key、hostName、path查询。
     * 这三个参数可以传入null，如果传入null，则表示忽略该查询条件。
     *
     * @param key
     * @param hostName
     * @param path
     * @return
     */
    public synchronized static Optional<List<String>> getCookieList(String key, String hostName, String path) {
        try {
            return getCookieList(cookieKey -> key == null || cookieKey.getKey().equals(key)
                    && hostName == null || (cookieKey.getHostName().equals(hostName)
                    && path == null || cookieKey.getPath().equals(path)));
        } catch (Exception e) {
            throw new RuntimeException("too many cookie keys matched! key = " + key +
                    ", hostName = " + hostName +
                    ", path = " + path +
                    ". all cookies : " + e.getMessage());
        }
    }

    private synchronized static Optional<List<String>> getCookieList(Predicate<CookieKey> predicate) throws Exception {
        Map<CookieKey, List<String>> cookieLists = getCookieLists();
        List<CookieKey> cookieKeys = cookieLists.keySet().stream().filter(predicate).collect(Collectors.toList());
        // 如果没有匹配到。
        if (cookieKeys.isEmpty()) {
            return Optional.empty();
        }
        // 可能匹配出多个，但不允许匹配出多个！
        if (cookieKeys.size() > 1) {
            throw new Exception(String.valueOf(cookieLists));
        }
        // 有且仅有一个匹配。
        CookieKey cookieKey = cookieKeys.get(0);
        List<String> list = cookieLists.get(cookieKey);
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(list);
        }
    }

    @Deprecated
    public static synchronized void putCookie(@NotNull String nsrsbh, @NotNull String key, @NotNull String value) {
        putCookie(new CookieKey(key, "default", "/", "false"), Collections.singletonList(value));
    }

    public static synchronized void putCookie(@NotNull CookieKey cookieKey, @NotNull List<String> list) {
        cookies.get().put(cookieKey, Collections.unmodifiableList(list));
    }

    @Deprecated
    public static synchronized Map<String, String> getCookies(@NotNull String nsrsbh) {
        Map<String, String> result = new HashMap<>();
        getCookies().forEach((k, v) -> result.put(k.getKey(), v));
        return result;
    }

    public static synchronized Map<CookieKey, String> getCookies() {
        Map<CookieKey, String> result = new HashMap<>();
        getCookieLists().forEach((k, v) -> result.put(k, v == null || v.isEmpty() ? null : v.get(0)));
        return result;
    }

    public static synchronized Map<CookieKey, List<String>> getCookieLists() {
        Map<CookieKey, List<String>> result = new HashMap<>();
        cookies.get().forEach((k, v) -> {
            if (v == null || v.isEmpty()) {
                result.put(k, null);
            } else {
                result.put(k, new ArrayList<>(v));
            }
        });
        return result;
    }

    public static synchronized Map<CookieKey, List<String>> clearCookies() {
        Map<CookieKey, List<String>> map = cookies.get();
        cookies.remove();
        return map;
    }

    private static  class CookieManagerThreadLocal extends ThreadLocal<Map<CookieKey, List<String>>> {
        @Override
        protected Map<CookieKey, List<String>> initialValue() {
            return new HashMap<>();
        }
    }
}
