package com.fileupload.iec.app.commons.cache;

import org.apache.commons.lang3.StringUtils;

public enum CacheTimeUnitEnum {
    SECOND("S"), MINUTE("M"), HOUR("H"), DAY("D");

    private CacheTimeUnitEnum(String unit) {
        if (!StringUtils.isAlphanumeric(unit)) {
            throw new RuntimeException("CacheTimeUnitEnum instance error! unit must is alpha or numeric! unit = " + unit);
        }
        if (unit.length() > SIZE) {
            throw new RuntimeException("CacheTimeUnitEnum instance error! unit length too long! unit = " + unit + ", unit.length = " + unit.length() + ", SIZE = " + SIZE);
        }
        if (unit.length() < SIZE) {
            unit = StringUtils.leftPad(unit, SIZE, ' ');
        }
        this.unit = unit;
    }

    private String unit;

    public String getUnit() {
        return unit;
    }

    public static final int SIZE = 2;

    public static CacheTimeUnitEnum getEnum(String unit) {
        if (unit == null || unit.isEmpty()) {
            return null;
        }
        if (unit.length() < SIZE) {
            unit = StringUtils.leftPad(unit, SIZE, ' ');
        }
        CacheTimeUnitEnum[] values = CacheTimeUnitEnum.values();
        for (int i = 0; i < values.length; i++) {
            if (unit.equals(values[i].getUnit())) {
                return values[i];
            }
        }
        return null;
    }

    public static long getTimeout(CacheTimeNumEnum timeNumEnum, CacheTimeUnitEnum timeUnitEnum) {
        if (timeNumEnum == null || timeUnitEnum == null) {
            return -1L;
        }
        if (timeUnitEnum == SECOND) {
            return timeNumEnum.getValue() * 1000L;
        } else if (timeUnitEnum == MINUTE) {
            return timeNumEnum.getValue() * 60 * 1000L;
        } else if (timeUnitEnum == HOUR) {
            return timeNumEnum.getValue() * 60 * 60 * 1000L;
        } else if (timeUnitEnum == DAY) {
            return timeNumEnum.getValue() * 24 * 60 * 60 * 1000L;
        }
        return -1L;
    }
}