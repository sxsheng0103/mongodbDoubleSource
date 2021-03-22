package com.fileupload.yzmslide.base.log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Log {
    private Log() {
    }

    public static void out(String format, Object... args) {
        Date time = Calendar.getInstance().getTime();
        String prefix = "[INFO]" + String.format("[%s]",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS").format(time));
        String content = prefix + String.format(format, args);
        System.out.println(content);
    }

    public static void error(Exception e, String format, Object... args) {
        Date time = Calendar.getInstance().getTime();
        String prefix = "[ERROR]" + String.format("[%s]",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS").format(time));
        String content = prefix + String.format(format, args);
        System.out.println(content);
        e.printStackTrace(System.err);
    }
}
