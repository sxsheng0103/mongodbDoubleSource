package com.pangu.crawler.transfer.com.mongo.configuration;

/**
 *  开启mongo多数据源
 *  不能用sit作为后缀，不能修改配置文件名称，只能使用bootstrap开头
 * @Author sheng.ding
 * @Date 2020/9/5 13:35
 * @Version 1.0
 **/

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({MongoMultiDataSourceRegistrar.class})
public @interface EnableMongoMultiDataSource {

}
