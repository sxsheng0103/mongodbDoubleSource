package com.fileupload.yzmslide.cache;

public class CacheGetResult {

    public static final CacheGetResult DONT_HAVE = new CacheGetResult(false, null);

    public static final CacheGetResult NULL = new CacheGetResult(true, null);

    public static final CacheGetResult EMPTY = new CacheGetResult(true, "");

    private boolean have;

    private String data;

    private CacheGetResult(boolean have, String data) {
        this.have = have;
        this.data = data;
    }

    public CacheGetResult(String data) {
        this.have = true;
        this.data = data;
    }

    public boolean isHave() {
        return have;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "CacheGetResult [have=" + have + ", data=" + data + "]";
    }
}