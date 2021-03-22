package com.fileupload.yzmslide.captcha;
import com.fileupload.yzmslide.base.log.Log;
import com.fileupload.yzmslide.base.screen.Screenshot;
import com.fileupload.yzmslide.base.utils.SlideCaptchaMan;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;

public class CaptchaDemo {

    private static final int Y_OFFSET = 115;

    public static void main(String[] args) throws Exception {
        char s = '0';
        while (s != 'N' && s != 'A' && s != 'H' && s != 'J' && s != '1') {
            try {
                System.out.print("\r\n内蒙古(N)，安徽(A)，湖南(H)，江苏(J)，退出(1):");
                s = (char) System.in.read();
                if (s == 'N') {
                    s = '0';
                    System.out.print("\r\n内蒙古(N)开始！");
                    ChromeDriver chromeDriver = null;
                    try {
                        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
                        ChromeOptions options = new ChromeOptions();
                        chromeDriver = new ChromeDriver(options);
                        chromeDriver.setLogLevel(Level.ALL);
                        chromeDriver.manage().window().maximize();
                        neimenggu(chromeDriver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (chromeDriver != null) {
                            chromeDriver.quit();
                            System.out.println("chrome quit done!");
                        }
                    }
                    try {
                        Thread.sleep(60 * 1000);
                    } catch (InterruptedException e) {
                    }
                    System.out.println("chrome quit over!");
                    System.out.print("\r\n内蒙古(N)结束！");
                } else if (s == 'A') {
                    s = '0';
                    System.out.print("\r\n安徽(A)开始！");
                    ChromeDriver chromeDriver = null;
                    try {
                        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
                        ChromeOptions options = new ChromeOptions();
                        chromeDriver = new ChromeDriver(options);
                        chromeDriver.setLogLevel(Level.ALL);
                        chromeDriver.manage().window().maximize();
                        anhui(chromeDriver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (chromeDriver != null) {
                            chromeDriver.quit();
                            System.out.println("chrome quit done!");
                        }
                    }
                    try {
                        Thread.sleep(60 * 1000);
                    } catch (InterruptedException e) {
                    }
                    System.out.println("chrome quit over!");
                    System.out.print("\r\n安徽(A)结束！");
                } else if (s == 'H') {
                    s = '0';
                    System.out.print("\r\n湖南(H)开始！");
                    ChromeDriver chromeDriver = null;
                    try {
                        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
                        ChromeOptions options = new ChromeOptions();
                        chromeDriver = new ChromeDriver(options);
                        chromeDriver.setLogLevel(Level.ALL);
                        chromeDriver.manage().window().maximize();
                        hunan(chromeDriver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (chromeDriver != null) {
                            chromeDriver.quit();
                            System.out.println("chrome quit done!");
                        }
                    }
                    try {
                        Thread.sleep(60 * 1000);
                    } catch (InterruptedException e) {
                    }
                    System.out.println("chrome quit over!");
                    System.out.print("\r\n湖南(H)结束！");
                } else if (s == 'J') {
                    s = '0';
                    System.out.print("\r\n江苏(J)开始！");
                    ChromeDriver chromeDriver = null;
                    try {
                        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
                        ChromeOptions options = new ChromeOptions();
                        chromeDriver = new ChromeDriver(options);
                        chromeDriver.setLogLevel(Level.ALL);
                        chromeDriver.manage().window().maximize();
                        jiangsu(chromeDriver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (chromeDriver != null) {
                            chromeDriver.quit();
                            System.out.println("chrome quit done!");
                        }
                    }
                    try {
                        Thread.sleep(3 * 1000);
                    } catch (InterruptedException e) {
                    }
                    System.out.println("chrome quit over!");
                    System.out.print("\r\n江苏(J)结束！");
                } else if (s == '1') {
                    System.out.print("\r\n退出!!!");
                    break;
                }
            } catch (Exception e) {
                // NOTHING
            }
        }
    }

    private static void neimenggu(WebDriver chromeDriver) throws Exception {
        chromeDriver.get("https://etax.neimenggu.chinatax.gov.cn");
        while (true) {
            char cc = 'N';
            while (cc != 'Y' && cc != 'B') {
                try {
                    System.out.print("\r\n滑块出现请输入Y，退出循环请输入B:");
                    cc = (char) System.in.read();
                } catch (Exception e) {
                    // NOTHING
                }
            }
            if (cc == 'Y') {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                try {
                    String cmd = String.format("CMD /C START %s win activate title \"国家税务总局内蒙古自治区电子税务局 - Google Chrome\"",
                            Screenshot.NIRCMD_FILE);
                    Runtime.getRuntime().exec(cmd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                chromeDriver.manage().window().maximize();
                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                }
                try {
                    WebDriverWait wait = new WebDriverWait(chromeDriver, 5);
                    WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.close")));
                    btn.click();
                } catch (Exception e) {
                }
                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                }
                try {
                    WebDriverWait wait = new WebDriverWait(chromeDriver, 5);
                    WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#fd_div_ningxia > div > div.pop_div_title > a")));
                    btn.click();
                } catch (Exception e) {
                }
                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                }
                try {
                    WebDriverWait wait = new WebDriverWait(chromeDriver, 5);
                    WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#newLogin > a.loing > img")));
                    btn.click();
                } catch (Exception e) {
                }
                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                }
                SlideCaptchaMan.create()
                        .setDriver(chromeDriver)
                        .setDomain("https://etax.neimenggu.chinatax.gov.cn")
                        .setBasePngCss("#img")
                        .setCaptchaJpgCss("#container")
                        .setCaptchaJpgUrlGetter(webElement -> {
                            String style = webElement.getAttribute("style");
                            int index = style.indexOf("url(");
                            int start = style.indexOf("(\"", index) + "(\"".length();
                            int end = style.indexOf("\")", index);
                            return "https://etax.neimenggu.chinatax.gov.cn/" + style.substring(start, end);
                        })
                        .setSliderBarCss("#dragbutton > input")
                        .setDiffEdgeOnly(false)
                        .setDeltaMax(false)
                        .setSlideOnce(true)
                        .setTopOffset(Y_OFFSET)
                        .setLeftOffset(0)
                        .setSlideXGetter((basePngFile, captchaJpgFile) -> {
                            try {
                                WebDriverWait wait = new WebDriverWait(chromeDriver, 5);
                                WebElement bg2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bg2")));
                                Point location = bg2.getLocation();
                                System.out.println("location = " + location);
                                String style = bg2.getAttribute("style");
                                final String BG2_SCREENSHOT_FILE = "C:\\Users\\Admin\\captcha7\\captcha.jpg";
                                int widthIndex = style.toLowerCase().indexOf("width:") + "width:".length();
                                int width = Integer.parseInt(style.substring(widthIndex, style.indexOf("px;", widthIndex)).trim());
                                int heightIndex = style.toLowerCase().indexOf("height:") + "height:".length();
                                int height = Integer.parseInt(style.substring(heightIndex, style.indexOf("px;", heightIndex)).trim());
                                boolean screenshot = Screenshot.screenshot(BG2_SCREENSHOT_FILE, location.getX(), Y_OFFSET + location.getY(), width, height);
                                System.out.println("screenshot = " + screenshot);
                                final int POSITION_FIXED_X = 11;
                                final int POSITION_START_Y = 9;
                                final int POSITION_END_Y = 110;
                                BufferedImage basePngImage = ImageIO.read(new File(basePngFile));
                                int basePngImageWidth = basePngImage.getWidth();
                                int basePngImageHeight = basePngImage.getHeight();
                                System.out.println("basePngImageWidth = " + basePngImageWidth);
                                System.out.println("basePngImageHeight = " + basePngImageHeight);
                                for (int y = POSITION_START_Y; y < POSITION_END_Y; y++) {
                                    
                                }
                                return 99;
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        })
                        .perform();
            } else {
                break;
            }
        }
    }

    private static void hunan(WebDriver chromeDriver) throws Exception {
        chromeDriver.get("https://etax.hunan.chinatax.gov.cn/wsbs/toLogin.do");
        while (true) {
            char cc = 'N';
            while (cc != 'Y' && cc != 'B') {
                try {
                    System.out.print("\r\n滑块出现请输入Y，退出循环请输入B:");
                    cc = (char) System.in.read();
                } catch (Exception e) {
                    // NOTHING
                }
            }
            if (cc == 'Y') {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                try {
                    String cmd = String.format("CMD /C START %s win activate title \"国家税务总局湖南省电子税务局 - Google Chrome\"",
                            Screenshot.NIRCMD_FILE);
                    Runtime.getRuntime().exec(cmd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                chromeDriver.manage().window().maximize();
                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                }
                SlideCaptchaMan.create()
                        .setDriver(chromeDriver)
                        .setDomain("https://etax.hunan.chinatax.gov.cn/")
                        .setBasePngCss("img.yidun_jigsaw")
                        .setCaptchaJpgCss("img.yidun_bg-img")
                        .setSliderBarCss(".yidun_slider")
                        .setDiffEdgeOnly(false)
                        .setDeltaMax(false)
                        .setSlideOnce(true)
                        // .setSlideOnce(false)
                        .setTopOffset(Y_OFFSET)
                        .setLeftOffset(0)
                        .perform();
            } else {
                break;
            }
        }
    }

    private static void anhui(WebDriver chromeDriver) throws Exception {
        chromeDriver.get("https://etax.anhui.chinatax.gov.cn/cas/login");
        WebDriverWait wait = new WebDriverWait(chromeDriver, 60);
        while (true) {
            char cc = 'N';
            while (cc != 'Y' && cc != 'B') {
                try {
                    System.out.print("\r\n滑块出现请输入Y，退出循环请输入B:");
                    cc = (char) System.in.read();
                } catch (Exception e) {
                    // NOTHING
                }
            }
            if (cc == 'Y') {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                try {
                    String cmd = String.format("CMD /C START %s win activate title \"国家税务总局安徽省电子税务局 - Google Chrome\"",
                            Screenshot.NIRCMD_FILE);
                    Runtime.getRuntime().exec(cmd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                chromeDriver.manage().window().maximize();
                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                }
//                WebElement login = wait.until(ExpectedConditions.elementToBeClickable(By.id("login")));
//                login.click();
//                WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
//                WebElement password = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
//                username.clear();
//                username.sendKeys("91341300744877243T");
//                password.clear();
//                password.sendKeys("120015");
//                login = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#fm3 button")));
//                login.click();
                SlideCaptchaMan.create()
                        .setDriver(chromeDriver)
                        .setDomain("https://etax.anhui.chinatax.gov.cn")
                        .setBasePngCss("img.captcha_slider_image_slider")
                        .setCaptchaJpgCss("img.captcha_slider_image_background")
                        .setSliderBarCss(".captcha_slider_bar")
                        .setDiffEdgeOnly(false)
                        .setDeltaMax(false)
                        .setSlideOnce(false)
                        .setTopOffset(Y_OFFSET)
                        .setLeftOffset(0)
                        .perform();
            } else {
                break;
            }
        }
    }

    private static void jiangsu(WebDriver chromeDriver) throws Exception {
        chromeDriver.get("https://etax.jiangsu.chinatax.gov.cn/sso/login");
        WebDriverWait wait = new WebDriverWait(chromeDriver, 60);
        while (true) {
            char cc = 'N';
            while (cc != 'Y' && cc != 'B') {
                try {
                    System.out.print("\r\n滑块出现请输入Y，退出循环请输入B:");
                    cc = (char) System.in.read();
                } catch (Exception e) {
                    // NOTHING
                }
            }
            if (cc == 'Y') {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                try {
                    String cmd = String.format("CMD /C START %s win activate title \"国家税务总局江苏省电子税务局 - Google Chrome\"",
                            Screenshot.NIRCMD_FILE);
                    Runtime.getRuntime().exec(cmd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                chromeDriver.manage().window().maximize();
                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                }
//                try {
//                    Robot robot = new Robot();
//                    robot.delay(200);
//                    robot.mouseMove(1200, 310);
//                    robot.mousePress(KeyEvent.BUTTON1_MASK);
//                    robot.delay(300);
//                    robot.mouseRelease(KeyEvent.BUTTON1_MASK);
//                    robot.delay(200);
//                } catch (Exception e) {
//                }
//                String loginButtonSelector = "#channel > div.header > div.login > div.go_login";
//                WebElement loginButtonElement = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(loginButtonSelector)));
//                loginButtonElement.click();
                SlideCaptchaMan.create()
                        .setDriver(chromeDriver)
                        .setDomain("https://etax.jiangsu.chinatax.gov.cn")
                        .setBasePngCss("img.yidun_jigsaw")
                        .setCaptchaJpgCss("img.yidun_bg-img")
                        .setSliderBarCss("div.yidun_slider")
                        .setDiffEdgeOnly(true)
                        .setDiffEdgePixel(4)
                        .setDeltaMax(true)
                        .setSlideOnce(true)
                        .setTopOffset(Y_OFFSET)
                        .setLeftOffset(0)
                        .perform();
            } else {
                break;
            }
        }
    }
}
