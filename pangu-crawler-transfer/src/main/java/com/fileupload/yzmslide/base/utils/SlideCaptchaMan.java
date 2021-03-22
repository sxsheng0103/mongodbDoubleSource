package com.fileupload.yzmslide.base.utils;

import com.fileupload.yzmslide.base.log.Log;
import com.fileupload.yzmslide.base.screen.Screenshot;
import okhttp3.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import javax.net.ssl.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 湖南参考的调用方式（前提是页面以最大化方式打开）：
 * SlideCaptchaMan.create()
 * .setDriver(chromeDriver)
 * .setBasePngCss("img.yidun_jigsaw")
 * .setCaptchaJpgCss("img.yidun_bg-img")
 * .setSliderBarCss(".yidun_slider")
 * .setDiffEdgeOnly(false)
 * .setDeltaMax(false)
 * .setSlideOnce(true)
 * .setTopOffset(115) // 这个是浏览器内容部分距离顶部的像素值，可调整
 * .setLeftOffset(0)
 * .perform();
 * 安徽参考的调用方式（前提是页面以最大化方式打开）：
 * SlideCaptchaMan.create()
 * .setDriver(chromeDriver)
 * .setBasePngCss("img.captcha_slider_image_slider")
 * .setCaptchaJpgCss("img.captcha_slider_image_background")
 * .setSliderBarCss(".captcha_slider_bar")
 * .setDiffEdgeOnly(false)
 * .setDeltaMax(false)
 * .setSlideOnce(false)
 * .setTopOffset(115) // 这个是浏览器内容部分距离顶部的像素值，可调整
 * .setLeftOffset(0)
 * .perform();
 * 江苏参考的调用方式（前提是页面以最大化方式打开）：
 * SlideCaptchaMan.create()
 * .setDriver(chromeDriver)
 * .setBasePngCss("img.yidun_jigsaw")
 * .setCaptchaJpgCss("img.yidun_bg-img")
 * .setSliderBarCss("div.yidun_slider")
 * .setDiffEdgeOnly(true)
 * .setDiffEdgePixel(4)
 * .setDeltaMax(true)
 * .setSlideOnce(true)
 * .setTopOffset(115) // 这个是浏览器内容部分距离顶部的像素值，可调整
 * .setLeftOffset(0)
 * .perform();
 */
public class SlideCaptchaMan {

    private WebDriver driver;

    private int waitTimeout = 60;

    private String domain;

    private String basePngCss;

    private Function<WebElement, String> basePngUrlGetter;

    private String captchaJpgCss;

    private Function<WebElement, String> captchaJpgUrlGetter;

    private String sliderBarCss;

    private boolean diffEdgeOnly = false;

    private int diffEdgePixel = 3;

    private boolean deltaMax = false;

    private boolean slideOnce = true;

    private int topOffset = 0;

    private int leftOffset = 0;

    private int slideX = 0;

    private BiFunction<String, String, Integer> slideXGetter;

    private SlideCaptchaMan() {
    }

    private static final String LOG_PREFIX = "[slide-captcha-man] ";

    public static SlideCaptchaMan create() {
        Log.out(LOG_PREFIX + "create!");
        return new SlideCaptchaMan();
    }

    public SlideCaptchaMan setDriver(WebDriver driver) {
        this.driver = driver;
        return this;
    }

    public SlideCaptchaMan setWaitTimeout(int waitTimeout) {
        this.waitTimeout = waitTimeout;
        return this;
    }

    public SlideCaptchaMan setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public SlideCaptchaMan setBasePngCss(String basePngCss) {
        this.basePngCss = basePngCss;
        return this;
    }

    public SlideCaptchaMan setBasePngUrlGetter(Function<WebElement, String> basePngUrlGetter) {
        this.basePngUrlGetter = basePngUrlGetter;
        return this;
    }

    public SlideCaptchaMan setCaptchaJpgCss(String captchaJpgCss) {
        this.captchaJpgCss = captchaJpgCss;
        return this;
    }

    public SlideCaptchaMan setCaptchaJpgUrlGetter(Function<WebElement, String> captchaJpgUrlGetter) {
        this.captchaJpgUrlGetter = captchaJpgUrlGetter;
        return this;
    }

    public SlideCaptchaMan setSliderBarCss(String sliderBarCss) {
        this.sliderBarCss = sliderBarCss;
        return this;
    }

    public SlideCaptchaMan setDiffEdgeOnly(boolean diffEdgeOnly) {
        this.diffEdgeOnly = diffEdgeOnly;
        return this;
    }

    public SlideCaptchaMan setDiffEdgePixel(int diffEdgePixel) {
        this.diffEdgePixel = diffEdgePixel;
        return this;
    }

    public SlideCaptchaMan setDeltaMax(boolean deltaMax) {
        this.deltaMax = deltaMax;
        return this;
    }

    public SlideCaptchaMan setSlideOnce(boolean slideOnce) {
        this.slideOnce = slideOnce;
        return this;
    }


    public SlideCaptchaMan setTopOffset(int topOffset) {
        this.topOffset = topOffset;
        return this;
    }

    public SlideCaptchaMan setLeftOffset(int leftOffset) {
        this.leftOffset = leftOffset;
        return this;
    }

    public SlideCaptchaMan setSlideX(int slideX) {
        this.slideX = slideX;
        return this;
    }

    public SlideCaptchaMan setSlideXGetter(BiFunction<String, String, Integer> slideXGetter) {
        this.slideXGetter = slideXGetter;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "driver=" + driver +
                ", waitTimeout=" + waitTimeout +
                ", domain=" + domain +
                ", basePngCss='" + basePngCss + '\'' +
                ", basePngUrlGetter='" + basePngUrlGetter + '\'' +
                ", captchaJpgCss='" + captchaJpgCss + '\'' +
                ", captchaJpgUrlGetter='" + captchaJpgUrlGetter + '\'' +
                ", sliderBarCss='" + sliderBarCss + '\'' +
                ", diffEdgeOnly=" + diffEdgeOnly +
                ", diffEdgePixel=" + diffEdgePixel +
                ", deltaMax=" + deltaMax +
                ", slideOnce=" + slideOnce +
                ", topOffset=" + topOffset +
                ", leftOffset=" + leftOffset +
                ", slideX=" + slideX +
                ", slideXGetter=" + slideXGetter +
                '}';
    }

    public static final String USER_HOME = System.getProperty("user.home");
    public static final String PKCS_DIR = USER_HOME + File.separator + "pangu-pkcs";
    public static final String CAPTCHA_DIR = USER_HOME + File.separator + "captcha7";
    private static final String _CAPTCHA_DATE_DIR = CAPTCHA_DIR + File.separator + "~D~";
    private static final String _BASE_PNG_FILE = _CAPTCHA_DATE_DIR + File.separator + "base.png";
    private static final String _CAPTCHA_JPG_FILE = _CAPTCHA_DATE_DIR + File.separator + "captcha_img.jpg";
    private static final String _CAPTCHA_JPG0_FILE = _CAPTCHA_DATE_DIR + File.separator + "captcha_img0_~T~.jpg";

    public boolean perform() {
        Log.out(LOG_PREFIX + "perform start : " + this + "!");
        String today = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
        String captchaDateDir = _CAPTCHA_DATE_DIR.replace("~D~", today);
        String basePngFile = _BASE_PNG_FILE.replace("~D~", today);
        String captchaJpgFile = _CAPTCHA_JPG_FILE.replace("~D~", today);
        String captchaJpg0File = _CAPTCHA_JPG0_FILE.replace("~D~", today);
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            WebDriverWait wait = new WebDriverWait(driver, waitTimeout);
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            WebElement sliderBar = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(sliderBarCss)));
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            Robot robot = new Robot();
            Rectangle sliderBarRect = sliderBar.getRect();
            int pressX = sliderBarRect.getX() + leftOffset + sliderBarRect.width / 2;
            int pressY = sliderBarRect.getY() + topOffset + sliderBarRect.getHeight() / 2;
            robot.mouseMove(pressX, pressY);
            robot.mousePress(KeyEvent.BUTTON1_MASK);
            robot.delay(1000);
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            File captchaDirFile = new File(CAPTCHA_DIR);
            if (!captchaDirFile.exists() && !captchaDirFile.mkdir()) {
                throw new Exception(CAPTCHA_DIR + " mkdir fail!");
            }
            File captchaDateDirFile = new File(captchaDateDir);
            if (!captchaDateDirFile.exists() && !captchaDateDirFile.mkdir()) {
                throw new Exception(captchaDateDir + " mkdir fail!");
            }
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            WebElement basePng = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(basePngCss)));
            WebElement captchaJpg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(captchaJpgCss)));
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            String basePngUrl;
            if (basePngUrlGetter == null) {
                basePngUrl = basePng.getAttribute("src");
            } else {
                basePngUrl = basePngUrlGetter.apply(basePng);
            }
            Log.out(LOG_PREFIX + "basePngUrl = " + basePngUrl);
            download(basePngUrl, basePngFile);
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            String captchaJpgUrl;
            if (captchaJpgUrlGetter == null) {
                captchaJpgUrl = captchaJpg.getAttribute("src");
            } else {
                captchaJpgUrl = captchaJpgUrlGetter.apply(captchaJpg);
            }
            Log.out(LOG_PREFIX + "captchaJpgUrl = " + captchaJpgUrl);
            download(captchaJpgUrl, captchaJpgFile);
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            BufferedImage basePngImage = ImageIO.read(new File(basePngFile));
            BufferedImage captchaJpgImage = ImageIO.read(new File(captchaJpgFile));
            PixelRange basePngPixelRange = basePngPixelRange(basePngImage);
            PixelRange captchaJpgPixelRange = captchaJpgPixelRange(captchaJpgImage);
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if (slideX > 0) {
                Log.out(LOG_PREFIX + "slideX(A) = " + slideX);
            } else {
                if (slideXGetter != null) {
                    slideX = slideXGetter.apply(basePngFile, captchaJpgFile);
                    Log.out(LOG_PREFIX + "slideX(B) = " + slideX);
                } else {
                    slideX = diff(executor, basePngPixelRange, captchaJpgPixelRange);
                    Log.out(LOG_PREFIX + "slideX(C) = " + slideX);
                }
            }
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            int[] steps = new int[]{3, 4, 3, 7, 4, 3, 4, 6};
            int restX = slideX;
            while (true) {
                int step = Math.min(restX, steps[(int) (Math.round(Math.random() * 100) % steps.length)]);
                pressX += step;
                robot.mouseMove(pressX, pressY);
                robot.delay((int) (Math.round(Math.random() * 100) % step) * 10);
                restX -= step;
                if (restX <= 0) {
                    break;
                }
            }
            robot.delay(1000);
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if (!slideOnce) {
                Point captchaJpgLocation = captchaJpg.getLocation();
                Log.out(LOG_PREFIX + "captchaJpgLocation = " + captchaJpgLocation);
                int screenshotX = captchaJpgLocation.getX() + leftOffset;
                int screenshotY = captchaJpgLocation.getY() + topOffset;
                int screenshotWidth = captchaJpgPixelRange.width;
                int screenshotHeight = captchaJpgPixelRange.height;
                String screenshotFilename = captchaJpg0File.replace("~T~", String.valueOf(System.nanoTime()));
                Log.out(LOG_PREFIX + "screenshotFilename = " + screenshotFilename);
                int screenshotCount = 0;
                while (true) {
                    screenshotCount++;
                    if (screenshotCount >= 10) {
                        Log.out(LOG_PREFIX + "screenshort count overflow(10)!");
                        break;
                    }
                    Log.out(LOG_PREFIX + "screenshort [" + screenshotCount + "] start!");
                    File f = new File(screenshotFilename);
                    if (f.exists() && !f.delete()) {
                        screenshotFilename = captchaJpg0File.replace("~T~", String.valueOf(System.nanoTime()));
                        Log.out(LOG_PREFIX + "changed screenshotFilename is " + screenshotFilename);
                    }
                    boolean screenshot = Screenshot.screenshot(screenshotFilename, screenshotX, screenshotY, screenshotWidth, screenshotHeight);
                    if (!screenshot) {
                        throw new Exception("screenshot fail!");
                    }
                    Log.out(LOG_PREFIX + "read file to buffered image start! file = " + screenshotFilename);
                    BufferedImage image = ImageIO.read(Files.newInputStream(Paths.get(screenshotFilename)));
                    Log.out(LOG_PREFIX + "read file to buffered image end! file = " + screenshotFilename);
                    int x = diff(executor, basePngPixelRange, captchaJpgPixelRange(image));
                    if (x == slideX) {
                        Log.out(LOG_PREFIX + "screenshort [" + screenshotCount + "] end and success!");
                        break;
                    } else {
                        pressX += slideX - x;
                        robot.mouseMove(pressX, pressY);
                        robot.delay((int) (Math.round(Math.random() * 10)));
                    }
                    Log.out(LOG_PREFIX + "screenshort [" + screenshotCount + "] end!");
                }
            }
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            robot.delay((int) (Math.random() * 200) + 100);
            robot.mouseRelease(KeyEvent.BUTTON1_MASK);
        } catch (Exception e) {
            Log.error(e, LOG_PREFIX + "perform fail : " + this + "!");
            return false;
        } finally {
            executor.shutdown();
        }
        Log.out(LOG_PREFIX + "perform success : " + this + "!");
        return true;
    }

    private OkHttpClient getOkHttpClient() throws Exception {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }
        X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{trustManager}, null);
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        return new OkHttpClient.Builder().sslSocketFactory(sslSocketFactory, trustManager).build();
    }

    private void register() throws Exception {
        File dir = new File(PKCS_DIR);
        if (!dir.exists() && !dir.mkdir()) {
            throw new Exception(PKCS_DIR + " mkdir fail!");
        }
        if (domain == null || !domain.toLowerCase().startsWith("https://")) {
            throw new Exception(domain + " error in pkcs register!");
        }
        File file = new File(PKCS_DIR + File.separator + MD5Util.getMd5(domain));
        if (!file.exists()) {
            load(file, domain);
        }
        System.setProperty("javax.net.ssl.trustStore", file.getPath());
    }

    private void load(File file, String url) throws Exception {
        String host = url.substring("https://".length());
        int port = 443;
        char[] passPhrase = "changeit".toCharArray();
        File cacerts = new File(System.getProperty("java.home")
                + File.separator + "lib"
                + File.separator + "security"
                + File.separator + "cacerts");
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream inputStream = new FileInputStream(cacerts)) {
            keyStore.load(inputStream, passPhrase);
        }
        SSLContext context = SSLContext.getInstance("TLS");
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        X509TrustManager defaultTrustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
        SavingTrustManager savingTrustManager = new SavingTrustManager(defaultTrustManager);
        context.init(null, new TrustManager[]{savingTrustManager}, null);
        SSLSocketFactory factory = context.getSocketFactory();
        try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port)) {
            socket.setSoTimeout(60 * 1000);
            socket.startHandshake();
        } catch (Exception e) {
            // 忽略！
        }
        X509Certificate[] chain = savingTrustManager.chain;
        if (chain == null) {
            throw new Exception("Could not obtain server certificate chain!");
        }
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        for (X509Certificate cert : chain) {
            sha1.update(cert.getEncoded());
            md5.update(cert.getEncoded());
        }
        keyStore.setCertificateEntry(host + "-1", chain[0]);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            keyStore.store(fileOutputStream, passPhrase);
        }
    }

    private static class SavingTrustManager implements X509TrustManager {

        private final X509TrustManager trustManager;

        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager trustManager) {
            this.trustManager = trustManager;
        }

        public X509Certificate[] getAcceptedIssuers() {
            throw new UnsupportedOperationException();
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            this.chain = chain;
            this.trustManager.checkServerTrusted(chain, authType);
        }
    }

    private void download(String url, String path) throws Exception {
        Log.out(LOG_PREFIX + "download start : " + url);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Request request = new Request.Builder().url(url).addHeader("Connection", "close").build();
        if (url.toLowerCase().startsWith("https://")) {
            try {
                register();
            } catch (Exception e) {
                // TODO
            }
        }
        Call call = getOkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] all = new byte[0];
                byte[] buf = new byte[512];
                InputStream inputStream = null;
                FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
                try {
                    inputStream = response.body().byteStream();
                    int read = 0;
                    do {
                        read = inputStream.read(buf);
                        if (read < 0) {
                            break;
                        }
                        if (read == 0) {
                            continue;
                        }
                        byte[] allTemp = all;
                        all = new byte[allTemp.length + read];
                        System.arraycopy(allTemp, 0, all, 0, allTemp.length);
                        System.arraycopy(buf, 0, all, allTemp.length, read);
                    } while (read >= 0);
                    fileOutputStream.write(all);
                    fileOutputStream.flush();
                } catch (Exception e) {
                    Log.error(e, LOG_PREFIX + "download error : " + url);
                    if (e instanceof IOException) {
                        throw e;
                    } else {
                        throw new IOException(e.getMessage());
                    }
                } finally {
                    try {
                        if (inputStream != null)
                            inputStream.close();
                    } catch (Exception e) {
                    }
                    try {
                        if (fileOutputStream != null)
                            fileOutputStream.close();
                    } catch (Exception e) {
                    }
                }
                Log.out(LOG_PREFIX + "download end : " + url);
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.error(e, LOG_PREFIX + "download fail : " + url);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    private static final long LARGE = 1000000000000L;
    private static final Comparator<Integer> INTEGER_COMPARATOR = Comparator.comparingInt(Integer::intValue);

    private PixelRange basePngPixelRange(BufferedImage image) throws Exception {
        Log.out(LOG_PREFIX + "basePngPixelRange start!");
        PixelRange pixelRange = new PixelRange();
        int imageMinX = image.getMinX();
        int imageMinY = image.getMinY();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        List<Integer> xFilledRange = new ArrayList<>();
        List<Integer> yFilledRange = new ArrayList<>();
        pixelRange.pixels = new long[imageWidth][imageHeight][];
        TreeMap<Integer, TreeSet<Integer>> regionPoints = new TreeMap<>(INTEGER_COMPARATOR);
        for (int x = imageMinX; x < imageWidth; x++) {
            TreeSet<Integer> xlineY = new TreeSet<>(INTEGER_COMPARATOR);
            for (int y = imageMinY; y < imageHeight; y++) {
                int argb = image.getRGB(x, y);
                int a = (argb & 0xff000000) >> 24; // a
                int r = (argb & 0xff0000) >> 16; // r
                int g = (argb & 0xff00) >> 8; // g
                int b = (argb & 0xff); // b
                pixelRange.pixels[x][y] = new long[]{r * LARGE, g * LARGE, b * LARGE, a};
                if ((r != 0 || g != 0 || b != 0) && a != 0) {
                    xFilledRange.add(x);
                    yFilledRange.add(y);
                    xlineY.add(y);
                }
            }
            if (!xlineY.isEmpty()) {
                regionPoints.put(x, xlineY);
            }
        }
        if (diffEdgeOnly) {
            TreeMap<Integer, TreeSet<Integer>> edgePoints = new TreeMap<>();
            NavigableSet<Integer> xSet = regionPoints.navigableKeySet();
            int minX = xSet.pollFirst().intValue();
            int maxX = xSet.pollLast().intValue();
            for (Integer x : xSet) {
                if (x <= minX + diffEdgePixel || x >= maxX - diffEdgePixel) {
                    edgePoints.put(x, regionPoints.get(x));
                } else {
                    TreeSet<Integer> xEdgeY = new TreeSet<>(INTEGER_COMPARATOR);
                    TreeSet<Integer> xlineY = regionPoints.get(x);
                    int minY = xlineY.pollFirst().intValue();
                    int maxY = xlineY.pollLast().intValue();
                    for (Integer y : xlineY) {
                        if (y <= minY + diffEdgePixel || y >= maxY - diffEdgePixel) {
                            xEdgeY.add(y);
                        }
                    }
                    edgePoints.put(x, xEdgeY);
                }
            }
            pixelRange.points.putAll(edgePoints);
        } else {
            pixelRange.points.putAll(regionPoints);
        }
        int edge = deltaMax ? -1 : 1;
        xFilledRange.parallelStream().mapToInt(Integer::intValue).min().ifPresent(e -> pixelRange.minX = Math.max(e + edge, 0));
        yFilledRange.parallelStream().mapToInt(Integer::intValue).min().ifPresent(e -> pixelRange.minY = Math.max(e + edge, 0));
        xFilledRange.parallelStream().mapToInt(Integer::intValue).max().ifPresent(e -> pixelRange.maxX = e - edge);
        yFilledRange.parallelStream().mapToInt(Integer::intValue).max().ifPresent(e -> pixelRange.maxY = e - edge);
        pixelRange.width = pixelRange.maxX - pixelRange.minX;
        pixelRange.height = pixelRange.maxY - pixelRange.minY;
        Log.out(LOG_PREFIX + "basePngPixelRange end : " + pixelRange);
        return pixelRange;
    }

    private PixelRange captchaJpgPixelRange(BufferedImage image) throws Exception {
        Log.out(LOG_PREFIX + "captchaJpgPixelRange start!");
        PixelRange pixelRange = new PixelRange();
        int imageMinX = image.getMinX();
        int imageMinY = image.getMinY();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        pixelRange.pixels = new long[imageWidth][imageHeight][];
        for (int y = imageMinY; y < imageHeight; y++) {
            for (int x = imageMinX; x < imageWidth; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb & 0xff0000) >> 16; // r
                int g = (rgb & 0xff00) >> 8; // g
                int b = (rgb & 0xff); // b
                pixelRange.pixels[x][y] = new long[]{r * LARGE, g * LARGE, b * LARGE};
            }
        }
        pixelRange.minX = imageMinX + 1;
        pixelRange.minY = imageMinY + 1;
        pixelRange.maxX = imageMinX + imageWidth - 1;
        pixelRange.maxY = imageMinY + imageHeight - 1;
        pixelRange.width = pixelRange.maxX - pixelRange.minX;
        pixelRange.height = pixelRange.maxY - pixelRange.minY;
        Log.out(LOG_PREFIX + "captchaJpgPixelRange end : " + pixelRange);
        return pixelRange;
    }

    private int diff(ExecutorService executor, PixelRange basePngPixelRange, PixelRange captchaJpgPixelRange) throws Exception {
        Log.out(LOG_PREFIX + "diff start!");
        int deltaY = 5;
        List<Future<CalcValue>> futures = new ArrayList<>();
        for (int x = captchaJpgPixelRange.minX; x <= captchaJpgPixelRange.maxX - basePngPixelRange.width; x++) {
            for (int y = Math.max(captchaJpgPixelRange.minY, basePngPixelRange.minY - deltaY);
                 y <= Math.min(basePngPixelRange.minY + deltaY, captchaJpgPixelRange.maxY - basePngPixelRange.height);
                 y++) {
                final int finalX = x;
                final int finalY = y;
                Future<CalcValue> future = executor.submit(() -> new CalcValue(
                        finalX, finalY, calc(finalX, finalY, captchaJpgPixelRange, basePngPixelRange)));
                futures.add(future);
            }
        }
        List<CalcValue> values = new ArrayList<>();
        for (Future<CalcValue> future : futures) {
            values.add(future.get());
        }
        CalcValue calcValue = null;
        BigInteger value = null;
        for (CalcValue cv : values) {
            if (value == null) {
                value = cv.value;
            }
            if (deltaMax) {
                if (cv.value.compareTo(BigInteger.ZERO) > 0 && cv.value.compareTo(value) >= 0) {
                    value = cv.value;
                    calcValue = cv;
                }
            } else {
                if (cv.value.compareTo(BigInteger.ZERO) > 0 && cv.value.compareTo(value) <= 0) {
                    value = cv.value;
                    calcValue = cv;
                }
            }
        }
        Log.out(LOG_PREFIX + "diff calc value : " + calcValue);
        int result = -1;
        if (calcValue != null) {
            result = calcValue.x;
        }
        Log.out(LOG_PREFIX + "diff end : " + result);
        return result;
    }

    private BigInteger calc(int xStart, int yStart, PixelRange captchaJpg, PixelRange basePng) {
        List<Long> rDeltas = new ArrayList<>();
        List<Long> gDeltas = new ArrayList<>();
        List<Long> bDeltas = new ArrayList<>();
        for (int basePngX = basePng.minX, captchaJpgX = xStart; basePngX <= basePng.maxX; basePngX++, captchaJpgX++) {
            for (int basePngY = basePng.minY, captchaJpgY = yStart; basePngY <= basePng.maxY; basePngY++, captchaJpgY++) {
                if (basePng.points.containsKey(basePngX) && basePng.points.get(basePngX).contains(basePngY)) {
                    long[] captchaJpgRgb = captchaJpg.pixels[captchaJpgX][captchaJpgY];
                    long[] basePngRgba = basePng.pixels[basePngX][basePngY];
                    if ((basePngRgba[0] != 0 || basePngRgba[1] != 0 || basePngRgba[2] != 0) && basePngRgba[3] != 0) {
                        rDeltas.add(delta(captchaJpgRgb[0], basePngRgba[0]));
                        gDeltas.add(delta(captchaJpgRgb[1], basePngRgba[1]));
                        bDeltas.add(delta(captchaJpgRgb[2], basePngRgba[2]));
                    }
                }
            }
        }
        long rDeltaAvg = avg(rDeltas);
        long gDeltaAvg = avg(gDeltas);
        long bDeltaAvg = avg(bDeltas);
        BigInteger rDeltaAvg2 = avg2(rDeltas, rDeltaAvg);
        BigInteger gDeltaAvg2 = avg2(gDeltas, gDeltaAvg);
        BigInteger bDeltaAvg2 = avg2(bDeltas, bDeltaAvg);
        return rDeltaAvg2.add(gDeltaAvg2).add(bDeltaAvg2);
    }

    private BigInteger avg2(List<Long> deltas, long avg) {
        return deltas.stream().mapToLong(Long::longValue)
                .mapToObj(delta -> BigInteger.valueOf(delta - avg).multiply(BigInteger.valueOf(delta - avg)))
                .reduce(BigInteger.ZERO, BigInteger::add);
    }

    private long avg(List<Long> deltas) {
        return deltas.stream().mapToLong(Long::longValue).sum() / deltas.size();
    }

    private long delta(long i0, long i1) {
        return i0 >= i1 ? i0 - i1 : i1 - i0;
    }

    private static class CalcValue {
        private final int x;

        private final int y;

        private final BigInteger value;

        public CalcValue(int x, int y, BigInteger value) {
            this.x = x;
            this.y = y;
            this.value = value;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ") = " + value;
        }
    }

    private static class PixelRange {
        private int minX;

        private int maxX;

        private int minY;

        private int maxY;

        private int width;

        private int height;

        private long[][][] pixels;

        private final TreeMap<Integer, TreeSet<Integer>> points = new TreeMap<>(INTEGER_COMPARATOR);

        @Override
        public String toString() {
            return "{" + "minX=" + minX + ", maxX=" + maxX
                    + ", minY=" + minY + ", maxY=" + maxY
                    + ", width=" + width + ", height=" + height
                    + ", points.size=" + points.size() + '}';
        }
    }
}