package org.itracker.selenium;

import org.junit.Test;
import org.openqa.selenium.By;

import java.io.IOException;
import static org.itracker.Assert.*;

/**
 * Verifies security issues - that user, leaving a system cannot
 * access its content anymore.
 *
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class LogoutSeleniumIT extends AbstractSeleniumTestCase {
    /**
     * Verifies the successful login case with valid login/password.
     * <p/>
     * 0. Exit all available http sessions.
     * 1. Open some page inside the system.
     * 2. Verify, that browser was forwarded to login page.
     * 3. Enters correct login and password into appropriate input fields.
     * 4. Clicks "Login" button.
     * 5. Waits for page reload.
     * 6. Verifies if page contains ticket id input field, which means we
     * are inside an application.
     * 7. Verify if page contains Logout link and click it.
     * 8. After page refresh, verify that it's login page.
     * 9. Again, try to access some page inside the system.
     * 10. Verify, that browser has been forwarded to login page again.
     */
    @Test
    public void testLoginAndLogout() throws IOException {
        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath + "/portalhome.do");

        assertElementNotPresent(By.name("id"));
        assertElementPresent(By.name("login")).sendKeys("user_test1");
        //TODO not use username as password!
        assertElementPresent(By.name("password")).sendKeys("user_test1");
        assertElementPresent(By.id("btn-login")).click();

        waitForPageToLoad();

        assertElementPresent(By.name("id"));
        assertElementPresent(By.name("logoff")).click();
        waitForPageToLoad();

        assertElementNotPresent(By.name("id"));
        assertElementPresent(By.name("login"));
        assertElementPresent(By.name("password"));
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath + "/portalhome.do");

        assertElementNotPresent(By.name("id"));
        assertElementPresent(By.name("login"));
        assertElementPresent(By.name("password"));
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
