package com.pangu.crawler.transfer.com.mongo.configuration;

/**
 * mongo多数据源属性配置类
 * @Author sheng.ding
 * @Date 2020/9/5 13:33
 * @Version 1.0
 **/

public class MongoConfig {

    private String user;

    private String password;

    private String uri;

    private boolean enabled = false;


    private String host;

    private Integer port;

    private String database;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
