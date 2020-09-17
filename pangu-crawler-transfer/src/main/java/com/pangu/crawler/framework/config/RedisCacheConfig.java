//package com.pangu.crawler.framework.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.EnvironmentAware;
//import org.springframework.core.env.Environment;
//import org.springframework.stereotype.Component;
//
//@Component
//@ConfigurationProperties(prefix = "pangu.cache.redis")
//public class RedisCacheConfig extends CacheBaseConfig implements InitializingBean, EnvironmentAware {
//
//    private static final Logger logger = LoggerFactory.getLogger(RedisCacheConfig.class);
//
//    private Environment environment;
//
//    @Override
//    public String toString() {
//        String host = environment.getProperty("spring.redis.host");
//        String port = environment.getProperty("spring.redis.port");
//        String password = environment.getProperty("spring.redis.password");
//        String timeout = environment.getProperty("spring.redis.timeout");
//        return "RedisCacheConfig{" +
//                super.toString() +
//                ", host='" + host + '\'' +
//                ", port='" + port + '\'' +
//                ", password='" + password + '\'' +
//                ", timeout='" + timeout + '\'' +
//                '}';
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        logger.info("{}", this);
//    }
//
//    @Override
//    public void setEnvironment(Environment environment) {
//        this.environment = environment;
//    }
//}
