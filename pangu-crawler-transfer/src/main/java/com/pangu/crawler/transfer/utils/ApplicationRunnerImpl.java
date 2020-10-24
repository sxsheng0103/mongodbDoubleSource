package com.pangu.crawler.transfer.utils;

import com.pangu.crawler.transfer.service.TransferRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * @author dingsheng
 * createTime 2018-11-07 22:37
 **/
@Component
public class ApplicationRunnerImpl implements ApplicationRunner {
    @Autowired
    TransferRuleService transferRuleService;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        transferRuleService.initBootSchedual();
    }
}