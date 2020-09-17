/*
package com.pangu.crawler.framework.host;

import com.pangu.crawler.framework.pkc.PKCManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;

public class HostBean {

    public static final HostBean instance = new HostBean();

    private static final Logger logger = LoggerFactory.getLogger(HostBean.class);

    private boolean inited = false;

    private String hostArea;

    private Map<String, String> hostURLs;

    public boolean initial(@NotNull String hostArea, @NotNull String hostURL) {
        return initial(hostArea, hostURL, true);
    }

    public boolean initial(@NotNull String hostArea, @NotNull Map<String, String> hostURLs) {
        return initial(hostArea, hostURLs, true);
    }

    public boolean initial(@NotNull String hostArea, @NotNull String hostURL, boolean pkcRegister) {
        return initial(hostArea, Collections.singletonMap("default", hostURL), pkcRegister);
    }

    public boolean initial(@NotNull String hostArea, @NotNull Map<String, String> hostURLs, boolean pkcRegister) {
        if (inited) {
            throw new RuntimeException("host can initial only once!");
        }
        logger.info("host bean initial start! area = {}. hostURLs = {}", hostArea, hostURLs);
        boolean[] result = new boolean[]{true};
        this.hostArea = hostArea;
        this.hostURLs = Collections.unmodifiableMap(hostURLs);
        if (pkcRegister) {
            hostURLs.forEach((k, v) -> {
                if (result[0] && v != null && v.toLowerCase().startsWith("https:")) {
                    logger.info("host bean initial : pkc register start! area = {}. hostName = {}, hostURL = {}",
                            hostArea, k, v);
                    try {
                        PKCManager.register(hostArea + "-" + k, v);
                        logger.info("host bean initial : pkc register end! area = {}. hostName = {}, hostURL = {}",
                                hostArea, k, v);
                    } catch (Exception e) {
                        logger.error("host bean initial : pkc register fail! area = {}. hostName = {}, hostURL = {}",
                                hostArea, k, v, e);
                        result[0] = false;
                    }
                }
            });
        }
        logger.info("host bean initial end! area = {}. hostURLs = {}, result = {}", hostArea, hostURLs, result[0]);
        this.inited = true;
        return result[0];
    }

    public String getHostArea() {
        if (!inited) {
            throw new RuntimeException("host not initial!");
        }
        return hostArea;
    }

    public String getHostURL() {
        if (!inited) {
            throw new RuntimeException("host not initial!");
        }
        return hostURLs.get("default");
    }

    public String getHostURL(@NotNull String hostName) {
        if (!inited) {
            throw new RuntimeException("host not initial!");
        }
        return hostURLs.get(hostName);
    }

    public Map<String, String> getHostURLs() {
        if (!inited) {
            throw new RuntimeException("host not initial!");
        }
        return hostURLs;
    }

    @Override
    public String toString() {
        return "HostBean{" +
                "hostArea='" + hostArea + '\'' +
                ", hostURLs='" + hostURLs + '\'' +
                '}';
    }
}
*/
