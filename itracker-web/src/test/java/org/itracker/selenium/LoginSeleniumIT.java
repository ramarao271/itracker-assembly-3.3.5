package org.itracker.selenium;

import org.junit.Test;
import org.openqa.selenium.By;

import java.io.IOException;

/**
 * Verifies authorization to the system functionality.
 *
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class LoginSeleniumIT extends AbstractSeleniumTestCase {


    /**
     * Verifies the successful login case with valid login/password.
     * <p/>
     * 1. Opens login page of an application.
     * 2. Enters correct login and password into appropriate input fields.
     * 3. Clicks "Login" button.
     * 4. Waits for page reload.
     * 5. Verifies if page contains ticket id input field, which means we
     * are inside an application.
     */
    @Test
    public void testLoginSuccessDefaultAdmin() throws IOException {
        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        login("admin_test1", "admin_test1");

        assertElementPresent(By.name("id"));
    }

    /**
     * Verifies login failure case with invalid login/password.
     * <p/>
     * 1. Opens login page of an application.
     * 2. Enters incorrect login and password into appropriate input fields.
     * 3. Clicks "Login" button.
     * 4. Waits for page reload.
     * 5. Verifies if page contains ticket id input field, which means we
     * are inside an application.
     */
    @Test
    public void testLoginFailure() throws IOException {
        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        try {
            assertElementPresent(By.name("login")).sendKeys("wrong_login");
            assertElementPresent(By.name("password")).sendKeys("wrong_password");
            assertElementPresent(By.id("btn-login")).click();
        } catch (Exception e) {
             // fine
        } finally {
            waitForPageToLoad();
            assertElementNotPresent(By.name("id"));
        }

    }
    @Test
    public void testRememberMe() throws IOException {
        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath + "/module-preferences/editpreferencesform.do");

        assertElementPresent(By.name("login")).sendKeys("user_test1");
        assertElementPresent(By.name("password")).sendKeys("user_test1");
        assertElementPresent(By.name("saveLogin")).click();
        assertElementPresent(By.id("btn-login")).click();
        waitForPageToLoad();

        assertElementPresent(By.name("id"));
        // expire the current session
        driver.manage().deleteCookieNamed("JSESSIONID");

        assertElementPresent(By.cssSelector(
                "a[href='/" + applicationPath + "/module-searchissues/searchissuesform.do']"))
                .click();
        waitForPageToLoad();

        assertElementPresent(By.name("id"));
        assertElementNotPresent(By.name("login"));
        assertElementNotPresent(By.name("password"));
    }
    @Test
    public void testNotRememberMe() throws IOException {
        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath + "/module-preferences/editpreferencesform.do");

        assertElementPresent(By.name("login")).sendKeys("user_test1");
        assertElementPresent(By.name("password")).sendKeys("user_test1");
        assertElementPresent(By.id("btn-login")).click();
        waitForPageToLoad();

        assertElementPresent(By.name("id"));
        // expire the current session
        driver.manage().deleteCookieNamed("JSESSIONID");
        assertElementPresent(By.cssSelector(
                "a[href='/" + applicationPath + "/module-searchissues/searchissuesform.do']"))
                .click();
        waitForPageToLoad();

        assertElementPresent(By.name("login"));
        assertElementPresent(By.name("password"));
        assertElementNotPresent(By.name("id"));

    }

    @Override
    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/languagebean_init_dataset.xml",
                "dataset/languagebean_dataset.xml",
                "dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml"
        };
    }

}
