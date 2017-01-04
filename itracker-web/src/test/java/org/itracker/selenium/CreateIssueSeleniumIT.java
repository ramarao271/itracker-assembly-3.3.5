package org.itracker.selenium;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.itracker.model.User;
import org.itracker.persistence.dao.UserDAO;
import org.junit.Test;
import org.openqa.selenium.By;
import org.subethamail.wiser.WiserMessage;

import static org.itracker.Assert.assertEquals;
import static org.itracker.Assert.assertTrue;

/**
 * Verifies the functionality of new issue creation.
 *
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class CreateIssueSeleniumIT extends AbstractSeleniumTestCase {

    private static final Logger log = Logger.getLogger(CreateIssueSeleniumIT.class);

    /**
     * 1. Login into the system with some particular user
     * (admin_test1 in our case, which already has 4 issues, 2 of them
     * are unassigned).
     * 2. Go to "Project List" page (by clicking "Project List" link).
     * 3. Click "Create" link for selected project.
     * 4. Fill issue fields with some data (we remember description
     * in our case, to check if new issue has appeared).
     * 5. After submitting new issue, we are at "View Issues" page and
     * check if new issue has appeared.
     * 6. Go to "Portal Home" page and check if new issue has appeared
     * in "Unassigned" area and "Created" area.
     */
    @Test
    public void testCreateUnassignedIssue() throws Exception {
        log.info(" running testCreateUnassignedIssue");
        closeSession();

        final String descriptionValue = "Issue to be unassigned.";
        final String historyValue = "Issue to be unassigned history.";

        driver.get(applicationURL);

        login("admin_test1", "admin_test1");

        // Click "Projects List".
        assertElementPresent(By.name("listprojects")).click();
        waitForPageToLoad();

        // Click issue creation link (usually it's named "Create").
        assertElementPresent(By.xpath("//tr[starts-with(@id, 'project.')]" +
                "/td[normalize-space(text())='test_name']/.." +
                "/td//a[2]"))
        .click();
        waitForPageToLoad();

        assertElementPresent(By.name("description"));
        assertElementPresent(By.name("ownerId"));
        assertElementPresent(By.name("creatorId"));
        assertElementPresent(By.name("severity"));
        assertElementPresent(By.name("versions"));

        assertElementPresent(By.name("description"))
                .sendKeys(descriptionValue);
        assertElementPresent(By.name("history"))
                .sendKeys(historyValue);

        log.debug(driver.getPageSource());

        assertElementPresent(By.cssSelector("select[name='ownerId'] option[value='-1']")).click();
        final UserDAO userDao = (UserDAO) applicationContext.getBean("userDAO");
        final User user = userDao.findByLogin("admin_test1");
        assertTrue(null != user);
        final long userId = user.getId();
        assertElementPresent(By.cssSelector("select[name='creatorId'] option[value='" + userId + "']"))
        .click();

        int received = wiser.getMessages().size();

        assertElementPresent(By.cssSelector("input[type='submit']"))
            .click();
        waitForPageToLoad();

        assertEquals("wiser.receivedEmailSize", received + 1, wiser.getMessages().size());
        final WiserMessage smtpMessage = wiser.getMessages().get(received);
        final String smtpMessageBody = (String) smtpMessage.getMimeMessage().getContent();


        log.debug("testCreateUnassignedIssue, received:\n" + smtpMessageBody);
        // as defined in jetty-env.xconf

        assertTrue("System URL in Message body, " + applicationURL + ", " + smtpMessageBody,
                StringUtils.containsIgnoreCase(smtpMessageBody, applicationURL));

        assertTrue("Description not contained in Message body, " + descriptionValue,
                smtpMessageBody.contains(descriptionValue));
        assertTrue("History not contained in Message body," + historyValue,
                smtpMessageBody.contains(historyValue));

        // Check that the total number of issues is 5 now (4 from db + 1 our).
        assertElementPresent(By.id("issues"));
        assertElementCountEquals(5, By.xpath("//*[starts-with(@id, 'issue.')]"));

        assertElementPresent(By.xpath("//tr[starts-with(@id, 'issue.')]" +
                "/td[normalize-space(text())=normalize-space('" + descriptionValue + "')]"));

        driver.get(applicationURL + "/portalhome.do");

        // Check that just created issue has appeared in "Unassigned" area.
        assertElementPresent(By.xpath("//tr[starts-with(@id,'unassignedIssue.')]" +
                "/td[normalize-space(text())='test_name']/.." +
                "/td[normalize-space(text())=normalize-space('" + descriptionValue + "')]"));

        // Check that just created issue has appeared in "Created" area.
        assertElementPresent(By.xpath("//tr[starts-with(@id,'createdIssue.')]" +
                "/td[normalize-space(text())='test_name']/.." +
                "/td[normalize-space(text())=normalize-space('" + descriptionValue + "')]"));

        // Check that number of watched items is 0.
        assertElementNotPresent(By.xpath("//*[starts-with(@id, 'watchedIssue.')]"));
    }

    /**
     * TODO
     */
    @Test
    public void testCreateAssignedIssue() throws Exception {

        final String descriptionValue = "Issue to be assigned.";
        final String historyValue = "Issue to be assigned history.";

        log.info(" running testCreateAssignedIssue");
        closeSession();

        driver.get(applicationURL);

        login("admin_test1", "admin_test1");

        // Clicking "Project List" link.
        assertElementPresent(By.name("listprojects"))
        .click();
        waitForPageToLoad();

        // Click issue creation link (usually it's named "Create").
        assertElementPresent(By.xpath("//tr[starts-with(@id, 'project.')]" +
                "/td[normalize-space(text())='test_name']/.." +
                "/td//a[2]"))
        .click();
        waitForPageToLoad();

        assertElementPresent(By.name("description"))
        .sendKeys(descriptionValue);
        assertElementPresent(By.name("ownerId"));
        assertElementPresent(By.name("creatorId"));
        assertElementPresent(By.name("severity"));
        assertElementPresent(By.name("versions"));
        assertElementPresent(By.name("history"))
        .sendKeys(historyValue);

        final UserDAO userDao = (UserDAO) applicationContext.getBean("userDAO");
        final User user = userDao.findByLogin("admin_test1");
        assertTrue("user #admin_test1", null != user);
        final long userId = user.getId();
        assertElementPresent(By.cssSelector("select[name='ownerId'] option[value='" + userId + "']"))
        .click();
        assertElementPresent(By.cssSelector("select[name='creatorId'] option[value='" + userId + "']"))
        .click();

        int received = wiser.getMessages().size();
        assertElementPresent(By.cssSelector("input[type='submit']"))
        .click();
        waitForPageToLoad();

        assertEquals("wiser.receivedEmailSize", received + 2, wiser.getMessages().size());
        final WiserMessage smtpMessage1 = wiser.getMessages().get(received);
        final WiserMessage smtpMessage2 = wiser.getMessages().get(received + 1);

        final String smtpMessageBody1 = (String) smtpMessage1.getMimeMessage().getContent();
        final String smtpMessageBody2 = (String) smtpMessage2.getMimeMessage().getContent();

        // Checking email notification for creator.
        log.debug("testCreateAssignedIssue, received:\n " + smtpMessageBody1);
        assertTrue(smtpMessageBody1.contains(descriptionValue));
        assertTrue(smtpMessageBody1.contains(historyValue));

        // Checking email notification for owner.
        log.debug("testCreateAssignedIssue, received2:\n " + smtpMessageBody2);
        assertTrue(smtpMessageBody2.contains(descriptionValue));
        assertTrue(smtpMessageBody2.contains(historyValue));
//
        // Checking that our new issue has appeared in "View Issues".
        assertElementPresent(By.id("issues"));
        assertElementCountEquals(5, By.xpath("//*[starts-with(@id, 'issue.')]"));

        assertElementPresent(By.xpath("//*[starts-with(@id, 'issue.')]" +
                "/td[normalize-space(text())=normalize-space('" + descriptionValue + "')]"));

        driver.get(applicationURL + "/portalhome.do");

        // Checking that our new issue has not appeared in "Unassigned" area.
        assertElementNotPresent(By.xpath("//tr[starts-with(@id,'unassignedIssue.')]" +
                "/td[normalize-space(text())='test_name']/.." +
                "/td[normalize-space(text())=normalize-space('" + descriptionValue + "')]"));

        // Checking that our new issue has appeared in "Created" area.
        assertElementPresent(By.xpath("//tr[starts-with(@id,'createdIssue.')]" +
                "/td[normalize-space(text())='test_name']/.." +
                "/td[normalize-space(text())=normalize-space('" + descriptionValue + "')]"));

        // Check that "Watched" area is still empty.
        assertElementNotPresent(By.xpath("//*[starts-with(@id, 'watchedIssue.')]"));
    }

    @Override
    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/languagebean_init_dataset.xml",
                "dataset/languagebean_dataset.xml",
                "dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/permissionbean_dataset.xml",
                "dataset/versionbean_dataset.xml",
                "dataset/issuebean_dataset.xml",
                "dataset/issueversionrel_dataset.xml",
                "dataset/issueactivitybean_dataset.xml",
                "dataset/issuehistorybean_dataset.xml"
        };
    }


}
