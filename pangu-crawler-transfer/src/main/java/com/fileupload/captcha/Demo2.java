package com.fileupload.captcha;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class Demo2 {

    public static void main(String[] args) throws Exception {
        Map<KEY, BigDecimal> result = new TreeMap<>();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(new File("C:\\Users\\Admin\\Desktop\\data1607682287444.csv")),
                        StandardCharsets.UTF_8
                )
        );
        int rnum = 1;
        Map<String, Integer> index = new HashMap<>();
        String line = bufferedReader.readLine();
        String[] titles = line.split(",");
        int count = titles.length;
        for (int i = 0; i < titles.length; i++) {
            index.put(titles[i], i);
        }
        while (true) {
            rnum++;
            line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            while (true) {
                if (line.endsWith(",\"\"")) {
                    break;
                }
                Pattern p = Pattern.compile("^\"[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}\"$");
                if (line.length() > "\"yyyy-mm-dd hh:mm:ss\"".length()) {
                    if (p.matcher(line.substring(line.length() - "\"yyyy-mm-dd hh:mm:ss\"".length())).find()) {
                        break;
                    }
                }
                rnum++;
                String lineNext = bufferedReader.readLine();
                while (true) {
                    if (lineNext == null) {
                        System.out.println("rnum = " + rnum);
                        System.out.println("line = " + line);
                        throw new Exception("line end error!");
                    }
                    if (lineNext.trim().isEmpty()) {
                        rnum++;
                        lineNext = bufferedReader.readLine();
                    } else {
                        break;
                    }
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
            sz = sz.substring(1, sz.length() - 1);
            String area = datas[index.get("\"area\"")];
            area = area.substring(1, area.length() - 1);
            String token = datas[index.get("\"token\"")];
            token = token.substring(1, token.length() - 1);
            String nsrsbh = datas[index.get("\"nsrsbh\"")];
            nsrsbh = nsrsbh.substring(1, nsrsbh.length() - 1);
            String create_time = datas[index.get("\"create_time\"")];
            create_time = create_time.substring(1, create_time.length() - 1);
            String ybtse = datas[index.get("\"ybtse\"")];
            ybtse = ybtse.substring(1, ybtse.length() - 1);
            if (!"unicom2019".equalsIgnoreCase(token)) {
                continue;
            }
            if (!"zzsybnsr".equalsIgnoreCase(sz) && !"fjs".equalsIgnoreCase(sz)) {
                continue;
            }
            String month = create_time.substring(0, "yyyy-MM".length());
            KEY key = new KEY(area, month, sz);
            result.putIfAbsent(key, BigDecimal.ZERO);
            BigDecimal bigDecimal = result.get(key);
            while (ybtse.contains(",")) {
                ybtse = ybtse.replace(",", "");
            }
            try {
                ybtse = ybtse.trim();
                if (ybtse.endsWith("元")) {
                    ybtse = ybtse.substring(0, ybtse.length() - 1);
                }
                bigDecimal = bigDecimal.add(new BigDecimal(ybtse));
            } catch (Exception e) {
                System.err.println(rnum + " nsrsbh = " + nsrsbh);
                System.err.println(rnum + " ybtse = " + ybtse);
                if(!ybtse.isEmpty() && !ybtse.contains("-")){
                    byte[] bytes = ybtse.getBytes(StandardCharsets.UTF_8);
                    System.out.println("bytes = " + Arrays.toString(bytes));
                }
            }
            result.put(key, bigDecimal);
        }
        result.forEach((k, v) -> System.out.println(k + " : " + v));
        bufferedReader.close();
    }

    private static class KEY implements Comparable<KEY> {
        private String area;

        private String month;

        private String sz;

        public KEY(String area, String month, String sz) {
            this.area = area;
            this.month = month;
            this.sz = sz;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            KEY key = (KEY) o;
            return Objects.equals(area, key.area) &&
                    Objects.equals(month, key.month) &&
                    Objects.equals(sz, key.sz);
        }

        @Override
        public int hashCode() {
            return Objects.hash(area, month, sz);
        }

        @Override
        public int compareTo(KEY o) {
            KEY o1 = this;
            KEY o2 = o;
            int compare = o1.area.compareTo(o2.area);
            if (compare != 0) {
                return compare;
            }
            compare = o1.month.compareTo(o2.month);
            if (compare != 0) {
                return compare;
            }
            compare = o1.sz.compareTo(o2.sz);
            if (compare != 0) {
                return compare;
            }
            return 0;
        }

        @Override
        public String toString() {
            return "[" + area + ']' +
                    "[" + month + ']' +
                    "[" + sz + ']';
        }
    }
}
