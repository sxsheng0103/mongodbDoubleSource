package com.pangu.crawler.framework.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum CharsetEnum {
    UTF8(StandardCharsets.UTF_8),
    GBK(Charset.forName("GBK"));

    private Charset charset;

    CharsetEnum(Charset charset) {
        this.charset = charset;
    }

    public Charset getCharset() {
        return charset;
    }
}
