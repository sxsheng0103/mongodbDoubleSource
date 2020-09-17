/*
package com.pangu.crawler.framework.cookie;

import com.pangu.crawler.framework.config.CookieConfig;
import com.pangu.crawler.framework.exception.LoginExpiredException;
//import com.pangu.crawler.framework.service.ServiceFirstArg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CookieFreshTimer implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(CookieFreshTimer.class);

    private static final AtomicLong AL = new AtomicLong(0);

    @Autowired
    private CookieConfig cookieConfig;

 */
/*   @Autowired(required = false)
    private CookieExpiredChecker cookieExpiredChecker;*//*


    private CopyOnWriteArraySet<String> nsrsbhSet = new CopyOnWriteArraySet<>();

    private ApplicationContext applicationContext;

    private Timer timer;

    public void addNsrsbh(String nsrsbh) {
        nsrsbhSet.add(nsrsbh);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (cookieConfig.isRefreshEnable()) {
            timer = new Timer("cookie-fresh-timer", true);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    String trace = "cookie-fresh-timer-" + AL.getAndIncrement();
                    logger.info("[{}] - run start!", trace);
                    if (!nsrsbhSet.isEmpty()) {
                        logger.info("[{}] - nsrsbh set not empty!", trace);
                        Iterator<String> iterator = nsrsbhSet.iterator();
                        while (iterator.hasNext()) {
                            String nsrsbh = iterator.next();
                            try {
                                logger.info("[{}] - nsrsbh fresh start by cookie-expired-checker.pre-check! nsrsbh = {}",
                                        trace, nsrsbh);
                               */
/* if (cookieExpiredChecker != null) {
                                    if (!cookieExpiredChecker.preCheck(applicationContext, ServiceFirstArg.cookie(trace, nsrsbh))) {
                                        throw new LoginExpiredException(trace, nsrsbh);
                                    }
                                } else {
                                    throw new Exception("cookieExpiredChecker is null!");
                                }*//*

                                logger.info("[{}] - nsrsbh fresh end by cookie-expired-checker.pre-check! nsrsbh = {}",
                                        trace, nsrsbh);
                            } catch (Exception e) {
                                logger.info("[{}] - nsrsbh fresh failed by cookie-expired-checker.pre-check! nsrsbh = {}",
                                        trace, nsrsbh);
                                logger.info("[{}] - remove nsrsbh from set! nsrsbh = {}", trace, nsrsbh);
                                nsrsbhSet.remove(nsrsbh);
                            }
                        }
                    } else {
                        logger.info("[{}] - nsrsbh set empty!", trace);
                    }
                    logger.info("[{}] - run end!", trace);
                }
            };
            timer.schedule(timerTask,
                    cookieConfig.getRefreshInternal() * 60 * 1000,
                    cookieConfig.getRefreshInternal() * 60 * 1000);
            logger.info("cookie fresh timer start!");
        } else {
            logger.info("cookie fresh timer not enable!");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
*/
