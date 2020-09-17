package com.pangu.crawler.framework.exception;

public class ZuofeiNeedConfirmException extends PanicException {
    public ZuofeiNeedConfirmException(String trace, String nsrsbh) {
        super(trace, nsrsbh, null);
    }
}
