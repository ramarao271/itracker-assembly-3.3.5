package org.itracker.selenium;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class SeleniumManager {
    private final static String PROPERTY_SELENIUM_BROWSER = "selenium.browser";
    private final static String PROPERTY_SELENIUM_HOST = "selenium.host";
    private final static String PROPERTY_SELENIUM_NO_SERVER = "selenium.no.server";
    private final static String PROPERTY_SELENIUM_PORT = "selenium.port";
    private final static String PROPERTY_SELENIUM_SMTP_PORT = "selenium.smtp.port";
    private final static String PROPERTY_APPLICATION_HOST = "application.host";
    private final static String PROPERTY_APPLICATION_PORT = "application.port";
    private final static String PROPERTY_APPLICATION_PATH = "application.path";


    private final static String PROPERTY_SELENIUM_BROWSER_DEFAULT = "firefox";
    private final static String
            PROPERTY_SELENIUM_HOST_DEFAULT = "localhost";
    private final static String
            PROPERTY_SELENIUM_PORT_DEFAULT = "5555";
    private final static String
            PROPERTY_APPLICATION_HOST_DEFAULT = "localhost";
    private final static String
            PROPERTY_APPLICATION_PORT_DEFAULT = "8888";
    private final static String
            PROPERTY_APPLICATION_PATH_DEFAULT = "itracker";
    private final static String
            PROPERTY_SELENIUM_SMTP_PORT_DEFAULT = "2525";


    private static WebDriver driver = null;

    private static String seleniumHost = null;
    private static Integer seleniumPort = null;
    private static Boolean seleniumNoServer = null;
    private static String seleniumBrowser = null;

    private static String applicationHost = null;
    private static Integer applicationPort = null;
    private static String applicationPath = null;
    private static Integer smtpPort = null;

    private static final Logger log = Logger.getLogger(SeleniumManager.class);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (null != SeleniumManager.driver) {
                    driver.close();
                }
            }
        });
    }

    static {
        final InputStream inputStream = SeleniumManager.class
                .getResourceAsStream("SeleniumManager.properties");
        final Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        seleniumBrowser =
                properties.getProperty(PROPERTY_SELENIUM_BROWSER, PROPERTY_SELENIUM_BROWSER_DEFAULT);
        seleniumNoServer =
                Boolean.valueOf(properties.getProperty(PROPERTY_SELENIUM_NO_SERVER, "true"));
        seleniumHost =
                properties.getProperty(PROPERTY_SELENIUM_HOST, PROPERTY_SELENIUM_HOST_DEFAULT);
       try {
          seleniumPort =
                  Integer.valueOf(properties.getProperty(PROPERTY_SELENIUM_PORT, PROPERTY_SELENIUM_PORT_DEFAULT));
       } catch (Exception e) {
          seleniumPort = Integer.valueOf(PROPERTY_SELENIUM_PORT_DEFAULT);
       }
       applicationHost =
                properties.getProperty(PROPERTY_APPLICATION_HOST, PROPERTY_APPLICATION_HOST_DEFAULT);
       try {
          applicationPort =
                  Integer.valueOf(properties.getProperty(PROPERTY_APPLICATION_PORT, PROPERTY_APPLICATION_PORT_DEFAULT));
       } catch (Exception e) {
          applicationPort = Integer.valueOf(PROPERTY_APPLICATION_PORT_DEFAULT);
       }
       applicationPath =
                properties.getProperty(PROPERTY_APPLICATION_PATH, PROPERTY_APPLICATION_PATH_DEFAULT);
       try {
          smtpPort =
                  Integer.valueOf(properties.getProperty(PROPERTY_SELENIUM_SMTP_PORT, PROPERTY_SELENIUM_SMTP_PORT_DEFAULT));
       } catch (Exception e) {
          smtpPort = Integer.valueOf(PROPERTY_SELENIUM_SMTP_PORT_DEFAULT);
       }
    }


    public static synchronized WebDriver getWebDriver() throws IOException {


        if (null == driver) {

            if (!getSeleniumNoServer()) {
                DesiredCapabilities capabilities = DesiredCapabilities.htmlUnitWithJs();
                if (getSeleniumBrowser().equals("firefox")) {
                    capabilities = DesiredCapabilities.firefox();
                } else if (getSeleniumBrowser().equals("edge")) {
                    capabilities = DesiredCapabilities.edge();
                } else if (getSeleniumBrowser().equals("ie")) {
                    capabilities = DesiredCapabilities.internetExplorer();
                } else if (getSeleniumBrowser().equals("chrome")) {
                    capabilities = DesiredCapabilities.chrome();
                } else if (getSeleniumBrowser().equals("safari")) {
                    capabilities = DesiredCapabilities.safari();
                }
                URL remoteURL = new URL("http://" + getSeleniumHost() + ":" + getSeleniumPort());
                capabilities.setJavascriptEnabled(true);
                driver = new RemoteWebDriver(remoteURL, capabilities);
            } else {

                if (getSeleniumBrowser().equals("firefox")) {
                    DesiredCapabilities capabilities = DesiredCapabilities.firefox();
                    driver = new FirefoxDriver(capabilities);
                } else if (getSeleniumBrowser().equals("edge")) {
                    DesiredCapabilities capabilities = DesiredCapabilities.edge();
                    driver = new EdgeDriver(capabilities);
                } else if (getSeleniumBrowser().equals("ie")) {
                    DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
                    driver = new InternetExplorerDriver(capabilities);
                } else if (getSeleniumBrowser().equals("chrome")) {
                    DesiredCapabilities capabilities = DesiredCapabilities.chrome();
                    driver = new ChromeDriver(capabilities);
                } else if (getSeleniumBrowser().equals("safari")) {
                    DesiredCapabilities capabilities = DesiredCapabilities.safari();
                    driver = new SafariDriver(capabilities);
                } else {
                    DesiredCapabilities capabilities = DesiredCapabilities.htmlUnitWithJs();
                    driver = new HtmlUnitDriver(capabilities);
                }
            }


        }

        return driver;

    }

    /**
     * This will initialize a new selenium session for this test scope.
     */
    protected static void closeSession(WebDriver driver) {
        if (log.isDebugEnabled()) {
            log.debug("closeSession: " + driver);
        }
        driver.manage().deleteAllCookies();
    }

    public static Boolean getSeleniumNoServer() {
        return seleniumNoServer;
    }

    public static String getSeleniumHost() {
        return seleniumHost;
    }

    public static int getSeleniumPort() {
        return seleniumPort;
    }

    public static int getSMTPPort() {
        return smtpPort;
    }

    public static String getSeleniumBrowser() {
        return seleniumBrowser;
    }

    public static String getApplicationHost() {
        return applicationHost;
    }

    public static int getApplicationPort() {
        return applicationPort;
    }

    public static String getApplicationPath() {
        return applicationPath;
    }
}
