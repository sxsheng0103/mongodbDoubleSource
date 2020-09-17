package com.pangu.crawler.framework.cookie;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 联合主键：key，hostName，path
 */
public class CookieKey {
    final private String key;

    final private String hostName;

    final private String path;

    final private String secure;

    private CookieKey(String[] values) {
        this(values[0], values[1], values[2], values[3]);
    }

    public CookieKey(String key, String hostName, String path, String secure) {
        this.key = key;
        this.hostName = hostName;
        this.path = path;
        this.secure = secure;
    }

    public static CookieKey parse(@NotNull String s) throws Exception {
        if (s == null || s.isEmpty()) {
            throw new Exception("cookie key parse param is empty!");
        }
        List<String> matchGroups = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[.*?]");
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            matchGroups.add(matcher.group());
        }
        int matchGroupSize = matchGroups.size();
        int fieldsLength = CookieKey.class.getDeclaredFields().length;
        if (matchGroupSize != fieldsLength) {
            throw new Exception("cookie key parse fail : match groups size error! match size : " + matchGroupSize
                    + ", wanted size : " + fieldsLength + ", param : " + s);
        }
        String sTemp = s;
        for (String matchGroup : matchGroups) {
            sTemp = sTemp.replace(matchGroup, "");
        }
        if (!sTemp.isEmpty()) {
            throw new Exception("cookie key parse fail : param has extra useless chars! param : " + s
                    + ", match groups : " + matchGroups);
        }
        String[] values = matchGroups.stream().map(v -> v.substring(1, v.length() - 1)).toArray(String[]::new);
        for (String value : values) {
            if (value.contains("[") || value.contains("]")) {
                throw new Exception("cookie key parse fail : [] error! param : " + s);
            }
        }
        return new CookieKey(values);
    }

    public String getKey() {
        return key;
    }

    public String getHostName() {
        return hostName;
    }

    public String getPath() {
        return path;
    }

    public String getSecure() {
        return secure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CookieKey cookieKey = (CookieKey) o;
        return Objects.equals(key, cookieKey.key) &&
                Objects.equals(hostName, cookieKey.hostName) &&
                Objects.equals(path, cookieKey.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, hostName, path);
    }

    @Override
    public String toString() {
        return String.format("[%s][%s][%s][%s]", key, hostName, path, secure);
    }

    @Override
    public CookieKey clone() {
        return new CookieKey(this.key, this.hostName, this.path, this.secure);
    }
}
