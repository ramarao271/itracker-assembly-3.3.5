package org.itracker.selenium;

import com.google.common.base.Function;
import org.apache.log4j.Logger;
import org.itracker.AbstractDependencyInjectionTest;
import org.junit.Before;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.FluentWait;
import org.subethamail.wiser.Wiser;

import java.util.concurrent.TimeUnit;

import static org.itracker.Assert.*;

/**
 * It is a base class for all Selenium-based test cases.
 * It performs initialization of Selenium client part and
 * retrieves some generally-used parameters like hose application
 * is running at, port, context.
 *
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public abstract class AbstractSeleniumTestCase
        extends AbstractDependencyInjectionTest {
    public final static String SE_TIMEOUT = "2000";
    protected static final Wiser wiser;

    protected WebDriver driver;
    protected String applicationHost;
    protected int applicationPort;
    protected String applicationPath;
    protected String applicationURL;
    Logger log = Logger.getLogger(getClass());

    static {
        wiser = new Wiser(SeleniumManager.getSMTPPort());
        wiser.start();
        Logger.getLogger(AbstractSeleniumTestCase.class).info("started wiser on " + wiser.getServer().getPort());

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    wiser.stop();
                    Logger.getLogger(getClass()).info("stopped wiser " + wiser);
                } catch (RuntimeException e) {
                    Logger.getLogger(getClass()).warn("could not stop running wiser: " + wiser);
                    Logger.getLogger(getClass()).debug("exception caught", e);
                }
            }
        });
    }

    @Before
    public void seleniumStart() throws Exception {
        super.onSetUp();
        driver = SeleniumManager.getWebDriver();
        assertNotNull("driver", driver);
        driver.manage().timeouts().implicitlyWait(Long.valueOf(SE_TIMEOUT), TimeUnit.MILLISECONDS);
        applicationHost = SeleniumManager.getApplicationHost();
        applicationPort = SeleniumManager.getApplicationPort();
        applicationPath = SeleniumManager.getApplicationPath();
        applicationURL = "http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath;
    }

    final WebElement assertElementPresent(By by) {
        assertTrue(driver.getCurrentUrl() + " " + by + " present", isElementPresent(by));
        log.debug("element present by " + by);
        return driver.findElement(by);
    }

    final void assertElementNotPresent(By by) {
        assertFalse(driver.getCurrentUrl() + " " + by + " present", isElementPresent(by));
        log.debug("element not present by " + by);
    }

    final WebElement assertElementTextEquals(String expected, By by) {
        WebElement el = assertElementPresent(by);
        assertEquals(driver.getCurrentUrl() + " " + by, expected, el.getText());
        log.debug("text equals '" + expected + "' by " + by);
        return el;
    }

    final void assertElementCountEquals(int expected, By by) {
        assertEquals(driver.getCurrentUrl() + " " + by + " size", expected, driver.findElements(by).size());
        log.debug("element count '" + expected + "' by " + by);
    }


    /**
     * This will initialize a new selenium session for this test scope.
     */
    protected void closeSession() {
        SeleniumManager.closeSession(driver);
    }

    /**
     * Assert being on login page and enter the credentials.
     * <p>Success will be asserted with <code>itracker</code> Cookie present.</p>
     */
    protected final void login(final String username, final String password) {

        log.debug("login called with " + username + ", " + password);

        assertElementPresent(By.name("login")).sendKeys(username);
        assertElementPresent(By.name("password")).sendKeys(password);
        assertElementPresent(By.id("btn-login")).click();
        waitForPageToLoad();
        assertElementPresent(By.name("lookupForm"));

        log.debug("loginUser, logged in " + username + ", cookies: " + driver.manage().getCookies());
    }

    private boolean isElementPresent(By by) {
      try {
        driver.manage().timeouts().implicitlyWait(200, TimeUnit.MILLISECONDS);
        driver.findElement(by);
        return true;
      } catch (NoSuchElementException e) {
         if (log.isDebugEnabled())
            log.debug("isElementPresent: not present " + by + "\n in:\n<"
                    + driver.getCurrentUrl() + ">\n"
                    + driver.getPageSource());
        return false;
      } finally {
          driver.manage().timeouts().implicitlyWait(Long.valueOf(SE_TIMEOUT), TimeUnit.MILLISECONDS);
      }

    }
    protected void waitForPageToLoad() {
        waitForPageToLoad(Long.valueOf(SE_TIMEOUT));
    }
    protected void waitForPageToLoad(long timeout) {

        new FluentWait<>(driver)
                .withTimeout(timeout, TimeUnit.MILLISECONDS)
                .pollingEvery(200, TimeUnit.MILLISECONDS)
                .until(new Function<WebDriver, Boolean>() {
                    @Override
                    public Boolean apply(WebDriver webDriver) {
                        try {
                          Object result = ((JavascriptExecutor) driver).executeScript(
                              "return 'complete' == document.readyState;");

                          if (result != null && result instanceof Boolean && (Boolean) result) {
                            return true;
                          }
                        } catch (Exception e) {
                          // Possible page reload. Fine
                        }
                        return false;
                    }
                });
    }

}
