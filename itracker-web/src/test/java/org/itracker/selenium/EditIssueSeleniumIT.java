package org.itracker.selenium;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.subethamail.wiser.WiserMessage;

import static org.itracker.Assert.assertEquals;
import static org.itracker.Assert.assertTrue;

/**
 * Verifies the functionality of Edit Issue page.
 *
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class EditIssueSeleniumIT extends AbstractSeleniumTestCase {

    /**
     * Editing one of tickets.
     * 1. Enter the system with some particular user (admin_test1).
     * 2. Goto Projects List page.
     * 3. Click "View" link for some project (test_name).
     * 4. Click "View" link for selected issue (no 1).
     * 5. Click "Edit" link.
     * 6. Update the description for this issue.
     * 7. Submit the form.
     * 8. Check if we got email notification about modifications.
     * 9. Being at "View Issues" page, right after saving updated issues,
     * check that no more issues with old description is here, but
     * new description has appeared.
     */
    @Test
    public void testEditIssue1FromViewIssue() throws Exception {
        log.info("running testEditIssue1FromViewIssue");
        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        login("admin_test1", "admin_test1");

        driver.findElement(By.name("listprojects")).click();
        waitForPageToLoad();

        // Click view issue link (usually it's named "View").
        assertElementPresent(By.xpath("//tr[starts-with(@id, 'project.2')]" +
                "/td[normalize-space(text())='test_name']/.." +
                "/td//a[1]")).click();
        waitForPageToLoad();

        assertElementPresent(By.xpath("//tr[starts-with(@id, 'issue.1')]" +
                "/td[contains(text(),'1')]/.." +
                "/td[normalize-space(text())='test_description']/.." +
                "/td[contains(normalize-space(text()),'A. admin lastname')]/.." +
                "/td//a[1]")).click();
        waitForPageToLoad();

        assertElementPresent(By.cssSelector(".actions a.edit")).click();
        waitForPageToLoad();

        assertElementPresent(By.name("description")).sendKeys(" (updated)");
        assertElementPresent(By.name("ownerId"));

        int received = wiser.getMessages().size();
        assertElementPresent(By.xpath("//input[@type='submit']")).click();
        waitForPageToLoad();

        assertEquals("wiser.receivedEmailSize", received + 1, wiser.getMessages().size());
        final WiserMessage smtpMessage = wiser.getMessages().get(received);
        final String smtpMessageBody = (String) smtpMessage.getMimeMessage().getContent();

        assertTrue(smtpMessageBody.contains("test_description (updated)"));

        assertElementPresent(By.id("issues"));
        assertElementCountEquals(4, By.xpath("//tr[starts-with(@id, 'issue.')]"));

        assertTrue(driver.getPageSource().contains("test_description (updated)"));

        assertElementNotPresent(By.xpath("//td[normalize-space(text())='test_description']"));
        assertElementPresent(By.xpath("//td[normalize-space(text())='test_description (updated)']"));
    }

    /**
     * Editing one of tickets.
     * 1. Enter the system with some particular user (admin_test1).
     * 2. Goto Projects List page.
     * 3. Click "View" link for some project (test_name).
     * 4. Click "Edit" link for selected issue (no 1).
     * 5. Update the description for this issue.
     * 6. Submit the form.
     * 7. Check if we got email notification about modifications.
     * 8. Being at "View Issues" page, right after saving updated issues,
     * check that no more issues with old description is here, but
     * new description has appeared.
     */
    @Test
    public void testEditIssue1FromIssueList() throws Exception {
        log.info("running testEditIssue1FromIssueList");
        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        login("admin_test1", "admin_test1");

        driver.findElement(By.name("listprojects")).click();
        waitForPageToLoad();

        // Click view issue link (usually it's named "View").
        assertElementPresent(By.xpath("//tr[@id='project.2']" +
                "/td[normalize-space(text())='test_name']/.." +
                "/td//a[1]"))
                .click();
        waitForPageToLoad();

        assertElementPresent(By.xpath("//tr[@id='issue.1']"+
                "/td[contains(text(),'1')]/.." +
                "/td[normalize-space(text())='test_description']/.." +
                "/td//a[2]"))
                .click();
        waitForPageToLoad();

        assertElementPresent(By.name("description")).sendKeys(" (updated)");
        assertElementPresent(By.name("ownerId"));

        int received = wiser.getMessages().size();

        assertElementPresent(By.xpath("//input[@type='submit']")).click();
        waitForPageToLoad();

        assertEquals("wiser.receivedEmailSize", received + 1, wiser.getMessages().size());
        final WiserMessage smtpMessage = wiser.getMessages().get(received);
        final String smtpMessageBody = (String) smtpMessage.getMimeMessage().getContent();
        log.debug("testEditIssue1FromIssueList, received:\n " + smtpMessageBody);

        assertTrue(smtpMessageBody.contains("test_description (updated)"));

        assertElementPresent(By.id("issues"));
        assertElementCountEquals(4, By.xpath("//*[starts-with(@id, 'issue.')]"));
        assertElementNotPresent(By.xpath("//*[starts-with(@id, 'issue.')]" +
                "/td[contains(text(),'1')]/.." +
                "/td[normalize-space(text())='test_description']/.." +
                "/td[contains(normalize-space(text()),'A. admin lastname')]"));
        assertElementPresent(By.xpath("//*[starts-with(@id, 'issue.')]" +
                "/td[contains(text(),'1')]/.." +
                "/td[normalize-space(text())='test_description (updated)']/.." +
                "/td[contains(normalize-space(text()),'A. admin lastname')]"));
    }

    /**
     * Move a ticket to another project.
     * 1. Enter the system with some particular user (admin_test1).
     * 2. Goto Projects List page.
     * 3. Click "View" link for some project (test_name).
     * 4. Click "View" link for selected issue (no 1).
     * 5. Click "Move" link.
     * 6. Select a project (test_name2) where we gonna move a ticket.
     * 7. Since test_name2 was an empty project, and now we have a single
     * item there, we check that it has appeared at "View Issues"
     * page for test_name2 project.
     */
    @Test
    public void testMoveIssue1() throws Exception {
        log.info("running testMoveIssue1");
        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        login("admin_test1", "admin_test1");

        log.info("logged in at URL: " + driver.getCurrentUrl());
        assertElementPresent(By.name("listprojects")).click();
        waitForPageToLoad();

        log.info("loaded projects-list with URL: " + driver.getCurrentUrl());
        // Click view issue link (usually it's named "View").
        assertElementPresent(By.xpath("//tr[starts-with(@id,'project.')]" +
                "/td[normalize-space(text())='test_name']/.." +
                "/td//a[1]")).click();

        log.info("loaded project with URL: " + driver.getCurrentUrl());
        waitForPageToLoad();

        assertElementPresent(By.xpath("//tr[starts-with(@id, 'issue.')]//a[@title='View Issue 1']")).click();
        waitForPageToLoad();

        log.info("loaded issue with URL: " + driver.getCurrentUrl());

        assertElementPresent(By.cssSelector(".actions a.moveIssue")).click();
        waitForPageToLoad();

        int received = wiser.getMessages().size();

        assertElementPresent(By.name("projectId"))
        .findElement(By.xpath("option[normalize-space(text())='test_name2']")).click();
        assertElementPresent(By.xpath("//input[@type='submit']")).click();

        waitForPageToLoad();
        log.info("moved issue with URL: " + driver.getCurrentUrl());

        // no message sent?
        assertEquals("wiser.receivedEmailSize", received, wiser.getMessages().size());
        assertElementPresent(By.cssSelector(".maincontent a.issuelist")).click();
        waitForPageToLoad();

        log.info("moved issue with URL: " + driver.getCurrentUrl());

        assertElementPresent(By.id("issues"));
        assertElementCountEquals(1, By.xpath("//tr[starts-with(@id, 'issue.')]"));
        assertElementPresent(By.xpath("//tr[starts-with(@id, 'issue.')]" +
                "/td[contains(text(),'1')]/.." +
                "/td[normalize-space(text())='test_description']/.." +
                "/td[contains(normalize-space(text()),'A. admin lastname')]"));
    }

    /**
     * Editing one of tickets (another issues).
     * 1. Enter the system with some particular user (admin_test1).
     * 2. Goto Projects List page.
     * 3. Click "View" link for some project (test_name).
     * 4. Click "View" link for selected issue (no 2).
     * 5. Click "Edit" link.
     * 6. Update the description for this issue.
     * 7. Submit the form.
     * 8. Check if we got email notification about modifications.
     * 9. Being at "View Issues" page, right after saving updated issues,
     * check that no more issues with old description is here, but
     * new description has appeared.
     */
    @Test
    public void testEditIssue2FromViewIssue() throws Exception {
        log.info("running testEditIssue2FromViewIssue");
        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        login("admin_test1", "admin_test1");

        assertElementPresent(By.name("listprojects")).click();
        waitForPageToLoad();

        // Click view issue link (usually it's named "View").
        assertElementPresent(By.xpath("//tr[starts-with(@id, 'project.')]" +
                "/td[normalize-space(text())='test_name']/.." +
                "/td//a[1]")).click();
        waitForPageToLoad();

        assertElementPresent(By.xpath("//tr[starts-with(@id, 'issue.2')]" +
                "/td[contains(text(),'2')]/.." +
                "/td[normalize-space(text())='test_description 2']/.." +
                "/td//a[1]"))
                .click();
        waitForPageToLoad();

        assertElementTextEquals("test_description 2",
                By.id("description"));
        assertElementTextEquals("admin firstname admin lastname",
                By.id("owner"));

        assertElementPresent(By.cssSelector(".actions a.edit"))
                .click();
        waitForPageToLoad();

        WebElement el = assertElementPresent(By.name("description"));
        el.clear();
        el.sendKeys("test_description 2 (updated)");
        assertElementPresent(By.name("ownerId"));

        int received = wiser.getMessages().size();

        assertElementPresent(By.xpath("//input[@type='submit']"))
        .click();
        waitForPageToLoad();
        assertEquals("wiser.receivedEmailSize", received + 1, wiser.getMessages().size());
        final WiserMessage smtpMessage = wiser.getMessages().get(received);
        final String smtpMessageBody = (String) smtpMessage.getMimeMessage().getContent();
        log.debug("testEditIssue2FromViewIssue, received:\n " + smtpMessageBody);

        assertTrue(smtpMessageBody.contains("test_description 2 (updated)"));

        assertElementPresent(By.id("issues"));
        assertElementCountEquals(4, By.xpath("//*[starts-with(@id, 'issue.')]"));

        assertElementNotPresent(By.xpath("//tr[starts-with(@id, 'issue.')]" +
                "/td[normalize-space(text())='2']/.." +
                "/td[normalize-space(text())='test_description 2']/.." +
                "/td[contains(normalize-space(text()),'A. admin lastname')]"));

        assertElementPresent(By.xpath("//tr[starts-with(@id, 'issue.')]" +
                "/td[contains(text(),'2')]/.." +
                "/td[normalize-space(text())='test_description 2 (updated)']/.." +
                "/td[contains(normalize-space(text()),'A. admin lastname')]"));
    }

    @Override
    protected String[] getDataSetFiles
            () {
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
