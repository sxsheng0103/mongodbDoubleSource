package com.fileupload.captcha;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Demo {

//    public static void main_(String[] args) throws Exception {
//        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsDevice device = environment.getDefaultScreenDevice();
//        DisplayMode[] displayModes = device.getDisplayModes();
//        for (DisplayMode displayMode : displayModes) {
//            System.out.println("Available display mode : [" + displayMode.getWidth() + " , " + displayMode.getHeight() + " , " + displayMode.getBitDepth() + " , " + displayMode.getRefreshRate() + "]");
//        }
//    }

    private static final Pattern[] patterns = new Pattern[]{
            // Pattern.compile("[1-9]\\d*"),
            Pattern.compile("[^0-9,][1-9]\\d*\\.\\d*|[^0-9,]0\\.\\d*[1-9]\\d*"),
            // Pattern.compile("[1-9][0-9]{0,2},([0-9][0-9][0-9],)*[0-9]{3}"),
            Pattern.compile("[^0-9,][1-9][0-9]{0,2},([0-9][0-9][0-9],)*[0-9]{3}\\.\\d*|[^0-9,]0\\.\\d*[1-9]\\d*")
    };

    public static void main(String[] args) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(new File("C:\\Users\\Admin\\Desktop\\data1607593876597.csv")),
                        StandardCharsets.UTF_8
                )
        );
        Map<String, BigDecimal> result = new TreeMap<>();
        int rnum = 1;
        Map<String, Integer> index = new HashMap<>();
        String line = bufferedReader.readLine();
        String[] titles = line.split(",");
        int count = titles.length;
        for (int i = 0; i < titles.length; i++) {
            index.put(titles[i], i);
        }
        List<BigDecimal> bigDecimalList = new ArrayList<>();
        while (true) {
            rnum++;
            line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            while (!line.endsWith("\"")) {
                rnum++;
                String lineNext = bufferedReader.readLine();
                if (lineNext == null) {
                    System.out.println("rnum = " + rnum);
                    System.out.println("line = " + line);
                    throw new Exception("line end error!");
                }
                line += lineNext;
            }
            String temp = line;
            while (temp.contains("\",\"")) {
                temp = line.replace("\",\"", "\",,,\"");
            }
            String[] datas = temp.split(",,,");
            if (count != datas.length) {
                System.out.println("rnum = " + rnum);
                System.out.println("count = " + count);
                System.out.println("line = " + line);
                System.out.println("temp = " + temp);
                for (int i = 0; i < datas.length; i++) {
                    System.out.println("[" + i + "] = " + datas[i]);
                }
                throw new Exception("line error!");
            }
            String sz = datas[index.get("\"sz\"")];
            String area = datas[index.get("\"area\"")];
            String token = datas[index.get("\"token\"")];
            String nsrsbh = datas[index.get("\"nsrsbh\"")];
            String create_time = datas[index.get("\"create_time\"")];
            // String update_time = datas[index.get("\"update_time\"")];
            String update_time = "";
            String result_msg = datas[index.get("\"result_msg\"")];
            if (!"\"unicom2019\"".equalsIgnoreCase(token)) {
                continue;
            }
            String month = create_time.substring("\"".length(), "\"yyyy-MM".length());
//            if (!"2020-11".equalsIgnoreCase(month)) {
//                continue;
//            }
//            if (!"\"zzsybnsr\"".equalsIgnoreCase(sz)) {
//                continue;
//            }
            if (!"\"beijing\"".equalsIgnoreCase(area)) {
                continue;
            }
            String message = String.format("%s, %s, %s, %s, %s", token, nsrsbh, create_time, update_time, result_msg);
            System.out.println("message = " + message);
            result.putIfAbsent(month, BigDecimal.ZERO);
            for (Pattern pattern : patterns) {
                Matcher matcher = pattern.matcher(result_msg);
                while (matcher.find()) {
                    System.out.println("pattern = " + pattern);
                    BigDecimal bigDecimal = result.get(month);
                    for (int i = 0; i <= matcher.groupCount(); i++) {
                        String group = matcher.group(i);
                        if (group == null || group.trim().isEmpty()) {
                            continue;
                        }
                        System.out.print(group + " " + i + " ");
                        while (group.contains(",")) {
                            group = group.replace(",", "");
                        }
                        group = group.substring(1);
                        BigDecimal decimal = new BigDecimal(group);
                        bigDecimalList.add(decimal);
//                        if (decimal.compareTo(BigDecimal.valueOf(5000000)) > 0) {
//                            System.out.print(" ||| " + nsrsbh + " ::: " + create_time + " : " + decimal);
//                        }
                        bigDecimal = bigDecimal.add(decimal);
                    }
                    System.out.println("");
                    result.put(month, bigDecimal);
                }
            }
        }
        bigDecimalList.sort(BigDecimal::compareTo);
        bigDecimalList.forEach(System.out::println);
        System.out.println("result = " + result);
        AtomicReference<BigDecimal> total = new AtomicReference<>(BigDecimal.ZERO);
        result.values().forEach(bigDecimal -> total.set(total.get().add(bigDecimal)));
        System.out.println("total = " + total);
        bufferedReader.close();
    }
}
