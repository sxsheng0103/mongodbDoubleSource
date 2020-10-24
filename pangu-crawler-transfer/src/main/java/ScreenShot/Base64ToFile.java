package ScreenShot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64;

public class Base64ToFile {
    private Base64ToFile() {
    }

    public static final int FILE_COUNT_IN_CWBB = 5;

    public static String get(File file) throws Exception {
        if (file == null) {
            throw new IllegalArgumentException("file args of Base64ToFile get is null!");
        }
        if (!file.exists()) {
            throw new Exception("file of Base64ToFile get not exists : ");
        }
        byte[] bytes = new byte[0];
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            while (true) {
                byte[] readedBytes = new byte[1024];
                int readedCount = fileInputStream.read(readedBytes);
                if (readedCount <= 0) {
                    break;
                }
                byte[] newBytes = new byte[bytes.length + readedCount];
                System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
                System.arraycopy(readedBytes, 0, newBytes, bytes.length, readedCount);
                bytes = newBytes;
            }
        } catch (Exception e) {
            throw new Exception("get base64 from [" + file + "] fail!");
        }
        try {
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new Exception("base64 encode fail!");
        }
    }

    public static File save(String fileName, String base64Content) throws Exception {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("fileName args of Base64ToFile save is empty!");
        }
        if (base64Content == null) {
            throw new IllegalArgumentException("base64Content args of Base64ToFile save is null!");
        }
        String USER_HOME = System.getProperty("user.home");
        String SCREENS = USER_HOME + File.separator + "pangu-screens";
        String FILES = USER_HOME + File.separator + "pangu-files";
        File file = new File(FILES + File.separator + fileName);
        if (file == null) {
            throw new Exception("[" + fileName + "] error in Base64ToFile save!");
        }
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(base64Content);
        } catch (Exception e) {
            throw new Exception("base64 decode fail!");
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
        } catch (Exception e) {
            throw new Exception("save base64 to [" + fileName + "] fail!");
        }
        return file;
    }
}