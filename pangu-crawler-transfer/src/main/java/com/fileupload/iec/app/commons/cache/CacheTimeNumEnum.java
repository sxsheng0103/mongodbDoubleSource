package com.fileupload.iec.app.commons.cache;

import org.apache.commons.lang3.StringUtils;

public enum CacheTimeNumEnum {
    N1("1", 1), N2("2", 2), N3("3", 3), N4("4", 4), N5("5", 5), N6("6", 6), N7("7", 7), N8("8", 8), N9("9", 9), N10("a", 10), N11("b", 11), N12("c", 12), N13("d", 13), N14("e", 14), N15("f", 15), N16("g", 16), N17("h", 17), N18("i", 18), N19("j", 19), N20("k", 20), N21("l", 21), N22("m", 22), N23("n", 23), N24("o", 24), N25("p", 25), N26("q", 26), N27("r", 27), N28("s", 28), N29("t", 29), N30("u", 30), N31("v", 31), N32("w", 32), N33("x", 33), N34("y", 34), N35("z", 35);

    private CacheTimeNumEnum(String num, int value) {
        if (!StringUtils.isAlphanumeric(num)) {
            throw new RuntimeException("CacheTimeNumEnum instance error! num must is alpha or numeric! num = " + num);
        }
        if (num.length() > SIZE) {
            throw new RuntimeException("CacheTimeNumEnum instance error! num length too long! num = " + num + ", num.length = " + num.length() + ", SIZE = " + SIZE);
        }
        if (num.length() < SIZE) {
            num = StringUtils.leftPad(num, SIZE, ' ');
        }
        this.num = num;
        this.value = value;
    }

    private String num;

    private int value;

    public String getNum() {
        return num;
    }

    public int getValue() {
        return value;
    }

    public static final int SIZE = 2;

    public static CacheTimeNumEnum getEnum(String num) {
        if (num == null || num.isEmpty()) {
            return null;
        }
        if (num.length() < SIZE) {
            num = StringUtils.leftPad(num, SIZE, ' ');
        }
        CacheTimeNumEnum[] values = CacheTimeNumEnum.values();
        for (int i = 0; i < values.length; i++) {
            if (num.equals(values[i].getNum())) {
                return values[i];
            }
        }
        return null;
    }
}
