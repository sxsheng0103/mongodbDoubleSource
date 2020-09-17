package com.pangu.crawler.framework.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pangu.crawler.framework.model.ResultBean;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@ControllerAdvice
public class ExceptionAdvice implements EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    private boolean isProd;

    private void logException(PanicException e) {
        if (!isProd) {
            logger.error(String.format("[%s] - %s request %s error!", e.getTrace(), e.getNsrsbh(), e.getController()), e);
        } else {
            Exception cause = e;
            if (e.getCause() != null) {
                cause = e.getCause();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(cause.getClass().getName());
            sb.append(" $$$$$$ ");
            String message = cause.getMessage();
            if (message != null && !message.isEmpty()) {
                while (message.contains("\r")) {
                    message = message.replace("\r", "-r-");
                }
                while (message.contains("\n")) {
                    message = message.replace("\n", "-n-");
                }
            }
            sb.append(message);
            sb.append(" ****** ");
            StackTraceElement[] stackTraceElements = cause.getStackTrace();
            for (int i = 0; i < stackTraceElements.length; i++) {
                sb.append(stackTraceElements[i].toString());
                sb.append(" ###### ");
            }
            logger.error("[{}] - {} request {} error - {}", e.getTrace(), e.getNsrsbh(), e.getController(), sb.toString());
        }
    }

    private String getExceptionCode(String name) {
        if (!name.contains("Exception")) {
            return name;
        }
        int startIndex = 0;
        if (name.contains(".")) {
            startIndex = name.lastIndexOf(".") + 1;
        }
        int endIndex = name.indexOf("Exception");
        String code = name.substring(startIndex, endIndex);
        char[] codeChars = code.toCharArray();
        List<Character> codeCharList = new ArrayList<>();
        for (char c : codeChars) {
            if (Character.isUpperCase(c)) {
                codeCharList.add('_');
                codeCharList.add(Character.toLowerCase(c));
            } else {
                codeCharList.add(c);
            }
        }
        StringBuilder sb = new StringBuilder();
        codeCharList.forEach(sb::append);
        return sb.toString();
    }

    private String fromException(PanicException e) {
        Exception cause = e;
        if (e.getCause() != null) {
            cause = e.getCause();
        }
        return ResultBean.FAIL(500,getExceptionCode(e.getClass().getName())).toString();
    }

    @ExceptionHandler(LoginFailException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody
    String loginFail(LoginFailException e) {
        logException(e);
        return fromException(e);
    }

    @ExceptionHandler(LoginExpiredException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody
    String loginExpired(LoginExpiredException e) {
        logException(e);
        return fromException(e);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody
    String notFound(NotFoundException e) {
        logException(e);
        return fromException(e);
    }

    @ExceptionHandler(PanicException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    String panic(PanicException e) {
        logException(e);
        return fromException(e);
    }

    @ExceptionHandler(BuShenbaoException.class)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    String buShenbao(BuShenbaoException e) {
        return fromException(e);
    }

    @ExceptionHandler(YiShenbaoWeiJiaokuanException.class)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    String yiShenbaoWeiJiaokuan(YiShenbaoWeiJiaokuanException e) {
        return fromException(e);
    }

    @ExceptionHandler(YiShenbaoYiJiaokuanException.class)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    String yiShenbaoYiJiaokuan(YiShenbaoYiJiaokuanException e) {
        return fromException(e);
    }

    @ExceptionHandler(WeiShenbaoWeiJiaokuanException.class)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    String weiShenbaoWeiJiaokuan(WeiShenbaoWeiJiaokuanException e) {
        return fromException(e);
    }

    @ExceptionHandler(ShenbaoNeedConfirmException.class)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    String shenbaoNeedConfirm(ShenbaoNeedConfirmException e) {
        return fromException(e);
    }

    @ExceptionHandler(ShenbaoBusinessFailException.class)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    String shenbaoBusinessFail(ShenbaoBusinessFailException e) {
        return fromException(e);
    }

    @ExceptionHandler(ZuofeiNeedConfirmException.class)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    String zuofeiNeedConfirm(ZuofeiNeedConfirmException e) {
        return fromException(e);
    }

    @Override
    public void setEnvironment(Environment environment) {
        String[] activeProfiles = environment.getActiveProfiles();
        isProd = activeProfiles != null && Stream.of(activeProfiles).anyMatch("prod"::equalsIgnoreCase);
    }
}
