package org.itracker.selenium;

import org.junit.Test;
import org.openqa.selenium.By;

import java.io.IOException;
/**
 * Verifies the functionality of View Issue page.
 *
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class ViewIssueSeleniumIT extends AbstractSeleniumTestCase {
    /**
     * 1. Enter the system with admin_test1 user login.
     * 2. Goto "Projects List" page.
     * 3. For project "test_name", click "View" link.
     * 4. At "View Issues" page, for Issue 1, click "View" link.
     * 5. At appeared page (View Issue), compare description and
     * current owner to expected values.
     */
    @Test
    public void testViewIssue1() throws IOException {
        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        login("admin_test1", "admin_test1");

        assertElementPresent(By.name("listprojects")).click();

        waitForPageToLoad();

        // Click view issue link (usually it's named "View").
        assertElementPresent(By.xpath("//tr[@id='project.2']" +
                "/td[normalize-space(text())='test_name']/.." +
                "/td//a[1]")).click();

        waitForPageToLoad();

        assertElementPresent(By.xpath("//tr[@id='issue.1']" +
                "/td[contains(text(),'1')]/.." +
                "/td[normalize-space(text())='test_description']/.." +
                "/td//a[1]")).click();

        waitForPageToLoad();

        assertElementTextEquals("test_description", By.id("description"));
        assertElementTextEquals("admin firstname admin lastname", By.id("owner"));
    }

    /**
     * 1. Enter the system with admin_test1 user login.
     * 2. Goto "Projects List" page.
     * 3. For project "test_name", click "View" link.
     * 4. At "View Issues" page, for Issue 2, click "View" link.
     * 5. At appeared page (View Issue), compare description and
     * current owner to expected values.
     */
    @Test
    public void testViewIssue2() throws IOException {
        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        login("admin_test1", "admin_test1");

        assertElementPresent(By.name("listprojects")).click();
        waitForPageToLoad();

        // Click view issue link (usually it's named "View").
        assertElementPresent(By.xpath("//tr[@id='project.2']" +
                "/td[normalize-space(text())='test_name']/.." +
                "/td//a[1]")).click();
        waitForPageToLoad();

        assertElementPresent(By.xpath("//tr[@id='issue.2']" +
                "/td[contains(text(),'2')]/.." +
                "/td[normalize-space(text())='test_description 2']/.." +
                "/td//a[1]")).click();
        waitForPageToLoad();

        assertElementTextEquals("test_description 2", By.id("description"));
        assertElementTextEquals("admin firstname admin lastname", By.id("owner"));
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
                "dataset/issuebean_dataset.xml"
        };
    }

}
