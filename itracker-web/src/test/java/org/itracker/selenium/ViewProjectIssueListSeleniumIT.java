package org.itracker.selenium;

import org.junit.Test;
import org.openqa.selenium.By;

import java.io.IOException;

/**
 * Verifies the functionality of per-project issues list page.
 *
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class ViewProjectIssueListSeleniumIT extends AbstractSeleniumTestCase {
    /**
     * 1. Login into the application with a specific user (admin_test1).
     * 2. Goto Project List page.
     * 3. Select project "test_name" and click "View" link for it.
     * 4. At the "View Issue" page, compare all issues to the data we
     * expect.
     */
    @Test
    public void testViewProjectIssueList() throws IOException {
        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                        + applicationPath);

        login("admin_test1", "admin_test1");

        driver.findElement(By.name("listprojects")).click();
        waitForPageToLoad();

        // Click view issue link (usually it's named "View").
        assertElementPresent(By.xpath("//*[starts-with(@id, 'project.')]" +
                "/td[normalize-space(text())='test_name']/.." +
                "/td/a[1]")).click();

        waitForPageToLoad();

        assertElementCountEquals(4, By.xpath("//*[starts-with(@id, 'issue.')]"));

        assertElementPresent(By.xpath("//*[starts-with(@id, 'issue.1')]" +
                "/td[contains(text(),'1')]/.." +
                "/td[normalize-space(text())='test_description']/.." +
                "/td[contains(text(),'A. admin lastname')]"));

        assertElementPresent(By.xpath("//*[starts-with(@id, 'issue.2')]" +
                "/td[contains(text(),'2')]/.." +
                "/td[normalize-space(text())='test_description 2']/.." +
                "/td[contains(text(),'A. admin lastname')]"));

        assertElementPresent(By.xpath("//*[starts-with(@id, 'issue.3')]" +
                "/td[contains(text(),'3')]/.." +
                "/td[normalize-space(text())='test_description 3']/.." +
                "/td[contains(text(),'A. admin lastname')]"));

        assertElementPresent(By.xpath("//*[starts-with(@id, 'issue.4')]" +
                "/td[contains(text(),'4')]/.." +
                "/td[normalize-space(text())='test_description 4']/.." +
                "/td[contains(text(),'A. admin lastname')]"));
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
