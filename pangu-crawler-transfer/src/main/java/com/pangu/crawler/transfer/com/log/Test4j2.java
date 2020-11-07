package com.pangu.crawler.transfer.com.log;



import lombok.extern.log4j.Log4j2;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Test4j2 extends  Thread{
    protected static Logger logger = Logger.getLogger(Test4j2.class);
    public static void main(String[] args) throws IOException, InterruptedException {
        /*new Thread(() -> {
            logger.info("info");
            logger.debug("debug");
            logger.error("error");
        }).start();
        new Thread(() -> {
            log.info("info");
            log.debug("debug");
            log.error("error");
        }).start();*/
    }
}
