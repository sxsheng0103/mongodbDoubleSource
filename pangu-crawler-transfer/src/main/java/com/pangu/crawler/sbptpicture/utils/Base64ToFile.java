package com.pangu.crawler.sbptpicture.utils;



import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
@Slf4j
public class Base64ToFile {
    private Base64ToFile() {
    }

    public static final int FILE_COUNT_IN_CWBB = 5;

    public static String get(InputStream inputStream) throws Exception {
        InputStream fis = null;
        String result = "";
        try {

            byte[] bytes = new byte[0];
            try {//(FileInputStream fileInputStream = new FileInputStream(file))
                while (true) {
                    byte[] readedBytes = new byte[1024];
                    int readedCount = inputStream.read(readedBytes);
                    if (readedCount <= 0) {
                        break;
                    }
                    byte[] newBytes = new byte[bytes.length + readedCount];
                    System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
                    System.arraycopy(readedBytes, 0, newBytes, bytes.length, readedCount);
                    bytes = newBytes;
                }
            } catch (Exception e) {
                log.error( "get base64  fail!");
            }
            result = Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.error("读取文件内容异常!");
        }finally{
            try {
                if(fis!=null){
                    fis.close();//强制关闭输入流
                }

            } catch (IOException e) {
                log.error("关闭文件流异常!");
            }finally {
                return  result;
            }
        }
    }
}