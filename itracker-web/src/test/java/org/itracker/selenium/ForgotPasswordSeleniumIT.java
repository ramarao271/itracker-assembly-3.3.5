package org.itracker.selenium;

import org.itracker.model.util.UserUtilities;
import org.itracker.services.ITrackerServices;
import org.junit.Test;
import org.openqa.selenium.By;
import org.subethamail.wiser.WiserMessage;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.itracker.Assert.*;

/**
 * Verifies the ability retrieve/reset forgotten password.
 *
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class ForgotPasswordSeleniumIT extends AbstractSeleniumTestCase {
    /**
     * 1. Go to the Login Page.
     * 2. Leave all input fields (login and password) empty.
     * 3. Click Login button.
     * 4. Wait for page reload.
     * 5. Check that "Login is required" and "Last Name is required"
     * message appeared.
     */
    @Test
    public void testIfBothRequired() throws Exception {
        log.info("running testIfBothRequired");
        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        assertElementPresent(By.name("forgotpassword")).click();
        waitForPageToLoad();

        assertElementPresent(By.name("login")).clear();
        assertElementPresent(By.name("lastName")).clear();

        assertElementNotPresent(By.id("pageErrors"));

        assertElementPresent(By.id("btn-submit")).click();
        waitForPageToLoad();

        assertElementTextEquals("Login is required\n" +
                "Last Name is required",
                By.id("pageErrors"));
    }

    /**
     * 1. Go to the Login Page.
     * 2. Type something into Last Name input field but leave Login empty.
     * 3. Click Login button.
     * 4. Wait for page reload.
     * 5. Check that "Login is required" message has appeared.
     */
    @Test
    public void testIfLoginRequired() throws Exception {
        log.info("running testIfLoginRequired");
        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        driver.findElement(By.name("forgotpassword")).click();
        waitForPageToLoad();

        assertElementPresent(By.name("login"));
        assertElementPresent(By.name("lastName")).sendKeys("user");

        assertElementPresent(By.id("btn-submit")).click();
        waitForPageToLoad();

        assertElementTextEquals("Login is required", By.id("pageErrors"));
    }

    /**
     * 1. Go to the Login Page.
     * 2. Type something into Login input field but leave Last Name empty.
     * 3. Click Login button.
     * 4. Wait for page reload.
     * 5. Check that "Last Name is required" message has appeared.
     */
    @Test
    public void testIfLastNameRequired() throws Exception {
        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        driver.findElement(By.name("forgotpassword")).click();
        waitForPageToLoad();

        assertElementPresent(By.name("login")).sendKeys("user");
        assertElementPresent(By.name("lastName"));

        assertElementPresent(By.id("btn-submit")).click();
        waitForPageToLoad();

        assertElementTextEquals("Last Name is required", By.id("pageErrors"));

    }

    /**
     * 1. Go to the Login Page.
     * 2. Click "Forgot Password" link.
     * 3. Wait for page reload.
     * 4. Type valid login into Login input field.
     * 5. Type valid last name into Last Name input field.
     * 6. Click "Submit" button.
     * 7. Wait for page reload.
     * 8. Check that you was forwarded back to the Login Page.
     * 9. Obtain email message from locally started mail server.
     * 10. Extract a new password from there.
     * 11. Redo a usual login procedure with a new password and make sure
     * you can authorize to the system with it.
     */
    @Test
    public void testRetrievingForgottenPassword() throws Exception {
        log.info("running testRetrievingForgottenPassword");
        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        final String newPassword;

        driver.findElement(By.name("forgotpassword")).click();
        waitForPageToLoad();

        assertElementPresent(By.name("login")).sendKeys("user_test1");
        assertElementPresent(By.name("lastName")).sendKeys("user lastname");

        int received = wiser.getMessages().size();

        assertElementPresent(By.id("btn-submit")).click();
        waitForPageToLoad();

        assertElementPresent(By.name("login"));
        assertElementPresent(By.name("password"));
        assertEquals("wiser.receivedEmailSize", received + 1, wiser.getMessages().size());
        final WiserMessage smtpMessage = wiser.getMessages().get(received);
        final String smtpMessageBody = (String) smtpMessage.getMimeMessage().getContent();
        log.debug("testRetrievingForgottenPassword, received:\n " + smtpMessageBody);

        assertTrue(smtpMessageBody.contains("Password: "));
        newPassword = extractPassword(smtpMessageBody);
        String newPwEnc = ((ITrackerServices) applicationContext.getBean("itrackerServices"))
                .getUserService().getUser(3).getPassword();

        assertEquals("new password", newPwEnc, UserUtilities.encryptPassword(newPassword));

        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);
        login("user_test1", newPassword);

        assertElementPresent(By.name("id"));
    }

    private String extractPassword(String string) {
        string = string.replaceAll("\n|\r", "");
        Matcher match = Pattern.compile(".*Password: (\\w*).*").matcher(string);
        if (match.matches()) {
            MatchResult r = match.toMatchResult();
            if (r.groupCount() > 0) {
                return r.group(1);
            }
        }
        return "";
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
