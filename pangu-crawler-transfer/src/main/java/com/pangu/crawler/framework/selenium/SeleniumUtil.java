/*
package com.pangu.crawler.framework.selenium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pangu.crawler.framework.cookie.CookieKey;
import com.pangu.crawler.framework.cookie.CookieLoader;

@Component
public class SeleniumUtil {
	
	static String chromePath;
	@Value("${browser.driver.chrome-path}")
	public void setChromePath(String chromePath) {
		SeleniumUtil.chromePath = chromePath;
	}
	
	
	static CookieLoader cookieLoader;
	@Autowired
	public void setCookie(CookieLoader cookie) {
		cookieLoader = cookie;
	}

	public static WebDriver loadChromeDriver(){
		// 加载驱动
		System.setProperty("webdriver.chrome.driver", chromePath);
		ChromeOptions options = new ChromeOptions();

//		options.addArguments("disable-extensions"); // disabling extensions
//		options.addArguments("disable-dev-shm-usage"); // overcome limited resource problems
//		options.addArguments("no-sandbox");
		 options.addArguments("start-maximized");
		// 加载驱动
		WebDriver driver = new ChromeDriver(options);
//		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		return driver;
	}
	
	public static void saveCookie(String trace,String nsrsbh,String nsrdq,WebDriver webdriver) {
		  Set<Cookie> cookieSet = webdriver.manage().getCookies();
	      Map<CookieKey, List<String>> cookies = new HashMap<>();
	        cookieSet.forEach(cookie -> {
	            String key = cookie.getName();
	            String value = cookie.getValue();
	            CookieKey cookieKey = new CookieKey(key, nsrdq, "/", "false");
	            cookies.compute(cookieKey, (ck, list) -> {
	                if (list == null) {
	                    list = new ArrayList<>();
	                    list.add(value);
	                    return list;
	                } else {
	                    list.add(value);
	                    return list;
	                }
	            });
	        });
	        cookieLoader.saveCookies(trace, nsrsbh, cookies);
//	        cookies.forEach(CookieManager::putCookie);
	}
	
	
	
	public static void closeDriver(WebDriver driver) {
		driver.close();
		driver.quit();
		driver=null;
	}
}
*/
