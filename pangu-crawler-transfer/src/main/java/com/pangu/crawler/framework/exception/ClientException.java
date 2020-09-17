package com.pangu.crawler.framework.exception;

public class ClientException extends PanicException {
    public ClientException(String trace, String nsrsbh, Exception cause) {
        super(trace, nsrsbh, cause);
    }
}
