package com.pangu.crawler.transfer.com.log;

/**
 * @Author sheng.ding
 * @Date 2020/9/25 23:34
 * @Version 1.0
 **/
import java.io.File;
import java.io.IOException;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;

public class ThreadSeperateDailyRollingFileAppender extends DailyRollingFileAppender {
  public ThreadSeperateDailyRollingFileAppender() {}

  public ThreadSeperateDailyRollingFileAppender(Layout layout,String filename, String datePattern)
      throws IOException {
    super(layout, filename, datePattern);
  }
}