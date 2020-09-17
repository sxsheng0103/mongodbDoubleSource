package com.pangu.crawler.framework.exception;

import javax.validation.constraints.NotNull;

public class PanicException extends RuntimeException {

    private String trace;

    private String nsrsbh;

    private Exception cause;

    private String controller;

    public PanicException(@NotNull String trace, @NotNull String nsrsbh, @NotNull Exception cause) {
        this.trace = trace;
        this.nsrsbh = nsrsbh;
        this.cause = cause;
    }

    public String getTrace() {
        return trace;
    }

    public String getNsrsbh() {
        return nsrsbh;
    }

    @Override
    public Exception getCause() {
        return cause;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }
}
