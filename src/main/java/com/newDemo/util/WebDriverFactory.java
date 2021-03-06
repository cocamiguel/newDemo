package com.newDemo.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

/**
 * Created by mike on 1/31/16.
 * nice factory class get from github
 */


public class WebDriverFactory {

	/* Browsers constants */
	public static final String CHROME = "chrome";
	public static final String FIREFOX = "firefox";
	public static final String OPERA = "opera";
	public static final String INTERNET_EXPLORER = "ie";
    public static final String SAFARI = "safari";

	/* Platform constants */
	public static final String WINDOWS = "windows";
	public static final String ANDROID = "android";
	public static final String XP = "xp";
	public static final String VISTA = "vista";
	public static final String MAC = "mac";
	public static final String LINUX = "linux";
        
	private WebDriverFactory(){}

	public static WebDriver getInstance(String gridHubUrl, Browser browser,	String username, String password) {

		WebDriver webDriver = null;

		DesiredCapabilities capability = new DesiredCapabilities();
		String browserName = browser.getName();
		capability.setJavascriptEnabled(true);

		if (gridHubUrl == null || gridHubUrl.length() == 0) {
			return getInstance(browserName, username, password);
		}

		if (CHROME.equals(browserName)) {
			capability = DesiredCapabilities.chrome();
		} else if (FIREFOX.equals(browserName)) {
			capability = DesiredCapabilities.firefox();
			FirefoxProfile ffProfile = new FirefoxProfile();

			if (username != null && password != null) {
				ffProfile.setPreference("network.http.phishy-userpass-length", 255);
				capability.setCapability(FirefoxDriver.PROFILE, ffProfile);
			}
			
			capability.setCapability(CapabilityType.TAKES_SCREENSHOT, true);
		} else if (INTERNET_EXPLORER.equals(browserName)) {
			capability = DesiredCapabilities.internetExplorer();
			capability.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
		} else if (OPERA.equals(browserName)) {
			capability = DesiredCapabilities.operaBlink();
        } else if (SAFARI.equals(browserName)) {
            capability = DesiredCapabilities.safari();
		} else if (ANDROID.equals(browserName)) {
			capability = DesiredCapabilities.android();
		} else {
				webDriver = new HtmlUnitDriver(true);
				return webDriver;
		}

		capability = setVersionAndPlatform(capability, browser.getVersion(),
				browser.getPlatform());

		try {
			webDriver = new RemoteWebDriver(new URL(gridHubUrl), capability);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return webDriver;
	}

	public static WebDriver getInstance(String browser, String username, String password) {
		WebDriver webDriver = null;

		if (CHROME.equals(browser)) {
			setChromeDriver();
			webDriver = new ChromeDriver();
		} else if (FIREFOX.equals(browser)) {
			FirefoxProfile ffProfile = new FirefoxProfile();
			if (username != null && password != null) {
				ffProfile.setPreference("network.http.phishy-userpass-length", 255);
			}

			webDriver = new FirefoxDriver(ffProfile);
			webDriver.manage().window().maximize();

		} else if (INTERNET_EXPLORER.equals(browser)) {
            isSupportedPlatform(browser);
			webDriver = new InternetExplorerDriver();

		} else if (SAFARI.equals(browser)) {
            isSupportedPlatform(browser);
            webDriver = new SafariDriver();

    } else if (ANDROID.equals(browser)) {
      webDriver = new RemoteWebDriver(DesiredCapabilities.android());
    } else {
			webDriver = new HtmlUnitDriver(true);
		}
		return webDriver;
	}

	private static DesiredCapabilities setVersionAndPlatform(
			DesiredCapabilities capability, String version, String platform) {
		if (MAC.equalsIgnoreCase(platform)) {
			capability.setPlatform(Platform.MAC);
		} else if (LINUX.equalsIgnoreCase(platform)) {
			capability.setPlatform(Platform.LINUX);
		} else if (XP.equalsIgnoreCase(platform)) {
			capability.setPlatform(Platform.XP);
		} else if (VISTA.equalsIgnoreCase(platform)) {
			capability.setPlatform(Platform.VISTA);
		} else if (WINDOWS.equalsIgnoreCase(platform)) {
			capability.setPlatform(Platform.WINDOWS);
		} else if (ANDROID.equalsIgnoreCase(platform)) {
			capability.setPlatform(Platform.ANDROID);
		} else {
			capability.setPlatform(Platform.ANY);
		}

		if (version != null) {
			capability.setVersion(version);
		}
		return capability;
	}

	private static void setChromeDriver() {
		String os = System.getProperty("os.name").toLowerCase().substring(0, 3);
		String chromeBinary = "src/main/resources/drivers/chrome/chromedriver"
				+ (os.equals("win") ? ".exe" : "");
		System.setProperty("webdriver.chrome.driver", chromeBinary);
	}

    private static void isSupportedPlatform(String browser) {
        boolean is_supported = true;
        Platform current = Platform.getCurrent();
        if (INTERNET_EXPLORER.equals(browser)) {
            is_supported = Platform.WINDOWS.is(current);
        } else if (SAFARI.equals(browser)) {
            is_supported = Platform.MAC.is(current) || Platform.WINDOWS.is(current);
        }
        assert is_supported : "Platform is not supported by " + browser.toUpperCase() + " browser";
    }
}
