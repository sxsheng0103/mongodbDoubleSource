package com.pangu.crawler.framework.exception;

public class ShenbaoNeedConfirmException extends PanicException {
    public ShenbaoNeedConfirmException(String trace, String nsrsbh) {
        super(trace, nsrsbh, null);
    }
}
