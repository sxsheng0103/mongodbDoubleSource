/*
package com.pangu.crawler.framework.resource;

import com.pangu.crawler.framework.request.RequestBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceReader {

    public static final String LINE_SEP = " '!-!@!-!\" ";

    private static final Logger logger = LoggerFactory.getLogger(ResourceReader.class);

    private static final ConcurrentHashMap<String, String[]> requestRawsMap = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, String> resourceJoinedLineMap = new ConcurrentHashMap<>();

    public static RequestBean readRequest(@NotNull String trace,
                                          @NotNull String key,
                                          @NotNull Resource resource,
                                          Resource... bodyFtlResources) throws Exception {
        logger.info("[{{}] - read request resource start! key = {}, resource = {}, bodyFtlResources = {}",
                trace, key, resource, Arrays.toString(bodyFtlResources));
        String resourceName = resource.getFilename();
        String resourceKeyName = resourceName + "#" + key;
        if (requestRawsMap.containsKey(resourceKeyName)) {
            // 如果曾经缓存
            String[] raws = requestRawsMap.get(resourceKeyName);
            logger.info("[{}] - {} get cached raws!", trace, resourceKeyName);
            RequestBean requestBean = new RequestBean(trace, resourceKeyName, raws[0], raws[1]);
            if (logger.isDebugEnabled()) {
                logger.debug("[{{}] - read request resource end! key = {}, resource = {}, bodyFtlResources = {}, requestBean = {}",
                        trace, key, resource, Arrays.toString(bodyFtlResources), requestBean);
            } else {
                logger.info("[{{}] - read request resource end! key = {}, resource = {}, bodyFtlResources = {}",
                        trace, key, resource, Arrays.toString(bodyFtlResources));
            }
            return requestBean;
        }
        String joinedLine;
        if (resourceJoinedLineMap.containsKey(resourceName)) {
            // 如果曾经缓存
            joinedLine = resourceJoinedLineMap.get(resourceName);
            logger.info("[{}] - {} get cached joinedLine!", trace, resourceName);
        } else {
            // 读取资源文件，并进行拼接
            List<String> lines = readResource(resource);
            joinedLine = LINE_SEP + String.join(LINE_SEP, lines) + LINE_SEP;
            logger.info("[{}] - {} read joinedLine and cache : {}", trace, resourceName, joinedLine);
            // 放入缓存
            resourceJoinedLineMap.put(resourceName, joinedLine);
        }
        String openKeyForCheck = "//--" + key + "--//";
        String closeKeyForCheck = "///---" + key + "---///";
        String openKeyForMatch = LINE_SEP + openKeyForCheck + LINE_SEP;
        String closeKeyForMatch = LINE_SEP + closeKeyForCheck + LINE_SEP;
        // 匹配KEY包含的部分
        Pattern keyPattern = Pattern.compile(openKeyForMatch + "(.*?)" + closeKeyForMatch);
        List<String> keyWrapList = new ArrayList<>();
        String joinedLineTemp = joinedLine;
        while (true) {
            Matcher keyMatcher = keyPattern.matcher(joinedLineTemp);
            if (!keyMatcher.find()) {
                break;
            } else {
                String content = keyMatcher.group();
                keyWrapList.add(content);
                joinedLineTemp = joinedLineTemp.replace(content, LINE_SEP + LINE_SEP);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] - after match key wrap list is {}! resource = {}, key = {}", trace, keyWrapList, resource, key);
        }
        // 判断内容
        // KEY包含的部分只能是1或2个
        int size = keyWrapList.size();
        if (size != 1 && size != 2) {
            throw new Exception("resource contain key size error! resource = " + resource
                    + ", key = " + key + ", size = " + size);
        }
        // KEY包含的部分之外不能再出现KEY。
        joinedLineTemp = joinedLine;
        for (String keyWrap : keyWrapList) {
            joinedLineTemp = joinedLineTemp.replace(keyWrap, LINE_SEP + LINE_SEP);
        }
        if (joinedLineTemp.contains(openKeyForCheck)) {
            throw new Exception("resource contain unmatched open key *! resource = " + resource + ", key = " + key);
        }
        if (joinedLineTemp.contains(closeKeyForCheck)) {
            throw new Exception("resource contain unmatched close key *! resource = " + resource + ", key = " + key);
        }
        // 剔除KEY包含的各部分中的开始KEY标识和结束KEY标识
        keyWrapList = keyWrapList.stream()
                .map(keyWrap -> keyWrap.substring(openKeyForMatch.length()))
                .map(keyWrap -> keyWrap.substring(0, keyWrap.length() - closeKeyForMatch.length()))
                .collect(Collectors.toList());
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] - after trim key wrap list is {}! resource = {}, key = {}", trace, keyWrapList, resource, key);
        }
        // KEY包含的部分之内也不能再出现KEY。
        for (String keyWrap : keyWrapList) {
            if (keyWrap.contains(openKeyForCheck)) {
                throw new Exception("resource contain unmatched open key #! resource = " + resource + ", key = " + key);
            }
            if (keyWrap.contains(closeKeyForCheck)) {
                throw new Exception("resource contain unmatched close key #! resource = " + resource + ", key = " + key);
            }
        }
        // 解析HEADER和BODY部分
        String headerRaw = "";
        String bodyRaw = "";
        for (String keyWrap : keyWrapList) {
            boolean isHeader = false;
            for (HttpMethod method : HttpMethod.values()) {
                if (keyWrap.toUpperCase().startsWith(method.name().toUpperCase() + " ")) {
                    isHeader = true;
                    break;
                }
            }
            if (isHeader) {
                if (headerRaw.isEmpty()) {
                    headerRaw = keyWrap;
                } else {
                    throw new Exception("resource contain key, but header part more than one! resource = " + resource
                            + ", key = " + key);
                }
            } else {
                if (bodyRaw.isEmpty()) {
                    bodyRaw = keyWrap;
                } else {
                    throw new Exception("resource contain key, but body part more than one! resource = " + resource
                            + ", key = " + key);
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] - header raw is {}, body raw is {}! resource = {}, key = {}",
                    trace, headerRaw, bodyRaw, resource, key);
        }
        // BODY部分替换
        if (!bodyRaw.isEmpty()) {
            Pattern ftlPattern = Pattern.compile("@(\\{)(\\w+.ftl)(})");
            // 转换回分行形式
            List<String> bodyRawLines;
            if (bodyRaw.contains(LINE_SEP)) {
                bodyRawLines = Arrays.asList(bodyRaw.split(LINE_SEP));
            } else {
                bodyRawLines = Collections.singletonList(bodyRaw);
            }
            List<String> bodyRawLinesWithReplacements = new ArrayList<>();
            // 依次处理每行
            for (String bodyRawLine : bodyRawLines) {
                // 要求每行最多一个替换部分
                String replacement = "";
                Matcher ftlMatcher = ftlPattern.matcher(bodyRawLine);
                while (ftlMatcher.find()) {
                    if (!replacement.isEmpty()) {
                        throw new Exception("resource contain key, but body part have one more replacement in line!"
                                + " resource = " + resource + ", key = " + key + ", replacement = " + replacement);
                    }
                    replacement = ftlMatcher.group();
                }
                // 该行没有需替换部分
                if (replacement.isEmpty()) {
                    bodyRawLinesWithReplacements.add(bodyRawLine);
                    continue;
                }
                // 确定替换内容中每一行的前后追加部分
                String linePrefix = null;
                String lineSufix = null;
                int startIndex = bodyRawLine.indexOf(replacement);
                int endIndex = startIndex + replacement.length();
                if (startIndex > 0) {
                    linePrefix = bodyRawLine.substring(0, startIndex);
                }
                if (endIndex < bodyRawLine.length()) {
                    lineSufix = bodyRawLine.substring(endIndex);
                }
                String finalLinePrefix = linePrefix;
                String finalLineSufix = lineSufix;
                // 剔除变量占位符部分
                String replacementName = replacement.replace("@{", "").replace("}", "");
                // 找到替换的资源文件
                Set<Resource> resourceSet = Stream.of(bodyFtlResources)
                        .filter(res -> res.getFilename().equals(replacementName))
                        .collect(Collectors.toSet());
                // 判断对应的替换资源文件有且仅有一个
                if (resourceSet.isEmpty()) {
                    throw new Exception("resource contain key, but body part replacement not found related resource!"
                            + " resource = " + resource + ", key = " + key + ", replacement = " + replacement);
                }
                if (resourceSet.size() > 1) {
                    throw new Exception("resource contain key, but body part replacement found many related resources!"
                            + " resource = " + resource + ", key = " + key + ", replacement = " + replacement
                            + ", related resources = " + resourceSet);
                }
                // 添加替换文件的内容
                resourceSet.stream().map(res -> {
                    List<String> lines;
                    try {
                        lines = readResource(res, finalLinePrefix, finalLineSufix);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return lines;
                }).forEach(bodyRawLinesWithReplacements::addAll);
            }
            // 重新拼接
            bodyRaw = String.join(LINE_SEP, bodyRawLinesWithReplacements);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] - after replace header raw is {}, body raw is {}! resource = {}, key = {}",
                    trace, headerRaw, bodyRaw, resource, key);
        }
        String[] raws = new String[]{headerRaw, bodyRaw};
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] - {} read raws and cache : {}", trace, resourceKeyName, Arrays.toString(raws));
        }
        // 放入缓存
        requestRawsMap.put(resourceKeyName, raws);
        RequestBean requestBean = new RequestBean(trace, resourceKeyName, raws[0], raws[1]);
        if (logger.isDebugEnabled()) {
            logger.debug("[{{}] - read request resource end! key = {}, resource = {}, bodyFtlResources = {}, requestBean = {}",
                    trace, key, resource, Arrays.toString(bodyFtlResources), requestBean);
        } else {
            logger.info("[{{}] - read request resource end! key = {}, resource = {}, bodyFtlResources = {}",
                    trace, key, resource, Arrays.toString(bodyFtlResources));
        }
        return requestBean;
    }

    private static List<String> readResource(@NotNull Resource resource) throws Exception {
        return readResource(resource, null, null);
    }

    private static List<String> readResource(@NotNull Resource resource,
                                             String linePrefix, String lineSufix) throws Exception {
        linePrefix = (linePrefix == null ? "" : linePrefix);
        lineSufix = (lineSufix == null ? "" : lineSufix);
        List<String> lines = new ArrayList<>();
        try (BufferedReader br =
                     new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            for (line = br.readLine(); line != null; line = br.readLine()) {
                // 剔除空行和注释行
                if (line.trim().length() > 0 && !line.trim().startsWith("#")) {
                    lines.add(linePrefix + line + lineSufix);
                }
            }
        }
        if (lines.isEmpty()) {
            throw new Exception("resource is empty! resource = " + resource);
        }
        return lines;
    }

    public static List<String> resource(@NotNull Resource resource) throws Exception {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br =
                     new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            for (line = br.readLine(); line != null; line = br.readLine()) {
                lines.add(line);
            }
        }
        if (lines.isEmpty()) {
            throw new Exception("resource is empty! resource = " + resource);
        }
        return lines;
    }
}
*/
